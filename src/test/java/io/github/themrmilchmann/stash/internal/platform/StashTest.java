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
package io.github.themrmilchmann.stash.internal.platform;

import io.github.themrmilchmann.stash.Secret;
import io.github.themrmilchmann.stash.SecretSpec;
import io.github.themrmilchmann.stash.Serializer;
import io.github.themrmilchmann.stash.Stash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class StashTest {

    private static final Serializer<String> stringSerializer = new Serializer<>() {

        @Override
        public String deserialize(byte[] bytes) {
            return new String(bytes);
        }

        @Override
        public byte[] serialize(String data) {
            return data.getBytes();
        }

    };

    private static final SecretSpec<String> stringSecretSpec = SecretSpec.builder(stringSerializer)
        .build();

    @Test
    public void testLockAcquireAfterClear() {
        Stash stash = Stash.builder().build();
        Secret<String> secret = stash.put(stringSecretSpec, "foo");

        stash.clear();

        assertThrows(IllegalStateException.class, secret::acquire);
    }

    @Test
    public void testLockUseAfterClear() {
        Stash stash = Stash.builder().build();
        Secret<String> secret = stash.put(stringSecretSpec, "foo");

        Secret<String>.Lock lock = secret.acquire();
        stash.clear();

        assertThrows(IllegalStateException.class, () -> lock.set("bar"));
        assertFalse(lock.setIfAcquired(() -> "bar"));
    }

}