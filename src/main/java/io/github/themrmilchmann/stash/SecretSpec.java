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

/**
 * A {@code SecretSpec} may be used to specify the behavior of a type of
 * secrets.
 *
 * @param <T>   the type of the secret
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public final class SecretSpec<T> {

    /**
     * {@return a {@link Builder} initialized with the given parameters}
     *
     * @param <T>           the type of the secret
     * @param serializer    the serializer to be used for secrets
     *
     * @since   0.1.0
     */
    public static <T> Builder<T> builder(Serializer<T> serializer) {
        return new Builder<>(serializer);
    }

    private final Serializer<T> serializer;

    private SecretSpec(Builder<T> builder) {
        this.serializer = builder.serializer;
    }

    /**
     * {@return the serializer to be used for secrets}
     *
     * @since   0.1.0
     */
    public Serializer<T> getSerializer() {
        return this.serializer;
    }

    /**
     * A builder for {@link SecretSpec} instances.
     *
     * @since   0.1.0
     */
    public static final class Builder<T> {

        private final Serializer<T> serializer;

        private Builder(Serializer<T> serializer) {
            this.serializer = serializer;
        }

        /**
         * {@return a new {@link SecretSpec} instance}
         *
         * @since   0.1.0
         */
        public SecretSpec<T> build() {
            return new SecretSpec<>(this);
        }

    }

}