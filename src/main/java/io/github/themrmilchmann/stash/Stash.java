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

import io.github.themrmilchmann.stash.internal.platform.universal.UniversalStorageFactory;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A {@code Stash} is a container that manages {@link Secret secrets}.
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public final class Stash {

    /**
     * {@return a {@link Builder}}
     *
     * @since   0.1.0
     */
    public static Builder builder() {
        return new Builder();
    }

    private final List<Secret<?>> secrets = new ArrayList<>();

    private final StorageFactory<?> storageFactory;

    private Stash(Builder builder, StorageFactory<?> storageFactory) {
        this.storageFactory = storageFactory;
    }

    /**
     * Clears this stash by {@link Secret.Lock#dispose() disposing} all secrets.
     *
     * @since   0.1.0
     */
    public void clear() {
        for (Iterator<Secret<?>> itr = this.secrets.iterator(); itr.hasNext(); ) {
            Secret<?> secret = itr.next();
            secret.dispose(itr::remove);
        }
    }

    /**
     * Creates a new {@link Secret} and puts it into this stash. The secret is
     * initialized with the given {@code value} and behaves as specified by the
     * given {@code spec}.
     *
     * @param <T>   the type of the secret
     * @param spec  the specification for the secret
     * @param value the initial value for the secret
     *
     * @return  the new secret
     *
     * @since   0.1.0
     */
    public <T> Secret<T> put(SecretSpec<T> spec, T value) {
        Objects.requireNonNull(spec);
        Objects.requireNonNull(value);

        Storage storage = this.storageFactory.create();
        int index = this.secrets.size();
        Secret<T> secret = new Secret<>(spec, storage, () -> this.secrets.remove(index), value);
        this.secrets.add(secret);

        return secret;
    }

    /**
     * A builder for {@link Stash} instances.
     *
     * @since   0.1.0
     */
    public static final class Builder {

        @Nullable
        private StorageFactory<?> storageFactory;

        private Builder() {}

        /**
         * {@return a new {@link Stash} instance}
         *
         * <p>If no {@link StorageFactory} has been set explicitly, a
         * {@link StorageFactory#isSupported() supported} one will be picked
         * automatically from the available implementations. If no supported
         * implementation is available, a <em>universal</em> fallback is used
         * instead.</p>
         *
         * <p>The universal storage is platform-independent but less secure
         * because it can not offload all sensitive information into protected
         * storage and instead relies on indirection and obscuration.</p>
         *
         * @since   0.1.0
         */
        @SuppressWarnings("rawtypes")
        public Stash build() {
            StorageFactory storageFactory = this.storageFactory;

            if (storageFactory == null) {
                ServiceLoader<StorageFactory> serviceLoader = ServiceLoader.load(StorageFactory.class);
                Optional<StorageFactory> optStorageFactory = serviceLoader.stream()
                    .map(ServiceLoader.Provider::get)
                    .filter(StorageFactory::isSupported)
                    .findFirst();

                if (optStorageFactory.isPresent()) {
                    storageFactory = optStorageFactory.get();
                } else {
                    UniversalStorageFactory universalStorageFactory = new UniversalStorageFactory();
                    if (!universalStorageFactory.isSupported()) throw new IllegalStateException();

                    storageFactory = universalStorageFactory;
                }
            }

            return new Stash(this, storageFactory);
        }

        /**
         * Sets the {@link StorageFactory} for the stash.
         *
         * @param value the {@code StorageFactory} for the stash
         *
         * @return  this builder instance
         *
         * @since   0.1.0
         */
        public Builder withStorageFactory(@Nullable StorageFactory<?> value) {
            this.storageFactory = value;
            return this;
        }

    }

}