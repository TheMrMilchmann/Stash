/*
 * Copyright (c) 2022 Leon Linhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.themrmilchmann.stash;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * TODO doc
 *
 * @param <T>   the type of the secret
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public final class Secret<T> {

    private final AtomicLong locks = new AtomicLong();

    private final SecretSpec<T> spec;
    private final io.github.themrmilchmann.stash.Storage storage;
    private final Runnable onDispose;

    private final Object disposeLock = new Object();
    private boolean isDisposed;

    @Nullable private T data;

    Secret(SecretSpec<T> spec, Storage storage, Runnable onDispose, T value) {
        this.spec = spec;
        this.storage = storage;
        this.onDispose = onDispose;

        byte[] bytes = this.spec.getSerializer().serialize(value);

        try {
            this.storage.write(bytes);
        } finally {
            Arrays.fill(bytes, (byte) 0);
        }
    }

    /**
     * Acquires a {@link Lock} that holds onto this secret. An unreleased lock
     * may be used to read from and write to a secret.
     *
     * <p>Unused secrets are stored in a {@link Storage}. When the first lock
     * that is holding onto a secret is acquired, the secret is read from
     * storage into memory. It is released from memory again when the last lock
     * is released.</p>
     *
     * @return  a new lock that holds onto this secret
     *
     * @throws IllegalStateException    if the secret was already disposed
     *
     * @implNote    This implementation uses a reference-counting algorithm that
     *              implicitly caps the number of available locks for a secret
     *              at {@code 2^31 - 1}.
     *
     * @since   0.1.0
     */
    public Lock acquire() {
        synchronized (this.disposeLock) {
            if (this.isDisposed) throw new IllegalStateException();

            long locks = this.locks.getAndIncrement();
            if (locks < 0) throw new IllegalStateException("Too many locks");

            if (locks == 0) {
                byte[] bytes = this.storage.read();

                try {
                    this.data = this.spec.getSerializer().deserialize(bytes);
                } finally {
                    Arrays.fill(bytes, (byte) 0);
                }
            }

            return new Lock();
        }
    }

    void dispose(@Nullable Runnable onDispose) {
        synchronized (this.disposeLock) {
            if (this.isDisposed) return;

            try {
                this.storage.dispose();
            } finally {
                this.isDisposed = true;
                Objects.requireNonNullElse(onDispose, this.onDispose).run();
            }
        }
    }

    /**
     * TODO doc
     *
     * @since   0.1.0
     */
    public final class Lock implements AutoCloseable {

        private final Object releaseLock = new Object();
        private boolean isReleased;

        private Lock() {}

        /**
         * {@link #release() Releases} this lock.
         *
         * @since   0.1.0
         */
        @Override
        public void close() {
            this.release();
        }

        /**
         * Disposes the secret that this lock holds onto.
         *
         * @since   0.1.0
         */
        public void dispose() {
            Secret.this.dispose(null);
        }

        /**
         * TODO doc
         *
         * @return
         *
         * @since   0.1.0
         */
        public Optional<T> get() {
            // TODO figure out locking

            synchronized (this.releaseLock) {
                return Optional.ofNullable(Secret.this.data);
            }
        }

        /**
         * TODO doc
         *
         * @param value
         *
         * @since   0.1.0
         */
        public void set(T value) {
            synchronized (Secret.this.disposeLock) {
                if (Secret.this.isDisposed) throw new IllegalStateException();

                synchronized (this.releaseLock) {
                    if (this.isReleased) throw new IllegalStateException();

                    Secret.this.data = Objects.requireNonNull(value);
                }
            }
        }

        /**
         * TODO doc
         *
         * @param supplier
         *
         * @return  whether the secret's value was updated
         *
         * @since   0.1.0
         */
        public boolean setIfAcquired(Supplier<T> supplier) {
            synchronized (Secret.this.disposeLock) {
                if (Secret.this.isDisposed) return false;

                synchronized (this.releaseLock) {
                    if (this.isReleased) return false;

                    Secret.this.data = supplier.get();
                    return true;
                }
            }
        }

        /**
         * Releases this lock.
         *
         * TODO doc
         *
         * @since   0.1.0
         */
        public void release() {
            synchronized (Secret.this.disposeLock) {
                if (Secret.this.isDisposed) return;

                synchronized (this.releaseLock) {
                    if (this.isReleased) return;
                    this.isReleased = true;

                    long locks = Secret.this.locks.decrementAndGet();
                    assert (locks >= 0);

                    if (locks == 0) {
                        try {
                            byte[] bytes = Secret.this.spec.getSerializer().serialize(Secret.this.data);

                            try {
                                Secret.this.storage.write(bytes);
                            } finally {
                                Arrays.fill(bytes, (byte) 0);
                            }
                        } finally {
                            Secret.this.data = null;
                        }
                    }
                }
            }
        }

    }

}