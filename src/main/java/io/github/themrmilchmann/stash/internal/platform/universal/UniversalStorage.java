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
package io.github.themrmilchmann.stash.internal.platform.universal;

import io.github.themrmilchmann.stash.Storage;

import javax.annotation.Nullable;
import javax.crypto.*;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.security.auth.DestroyFailedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import static io.github.themrmilchmann.stash.internal.platform.universal.UniversalStorageFactory.ALGORITHM;

/**
 * A platform-independent {@link Storage} implementation that obscures the data
 * by encrypting it with a random key using the ChaCha20 algorithm. However, the
 * key is stored in memory.
 *
 * @author  Leon Linhart
 */
public final class UniversalStorage implements Storage {

    private static Cipher getCipher() {
        try {
            return Cipher.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyGenerator getKeyGenerator() {
        try {
            return KeyGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable private SecretKey key;
    @Nullable private byte[] iv;
    @Nullable private byte[] data;

    @Override
    public byte[] read() {
        assert (this.key != null);
        assert (this.iv != null);
        assert (this.data != null);

        Cipher cipher = getCipher();
        ChaCha20ParameterSpec spec = new ChaCha20ParameterSpec(this.iv, 0);

        try {
            cipher.init(Cipher.DECRYPT_MODE, this.key, spec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to initialize cipher for decryption", e);
        }

        byte[] bytes = new byte[cipher.getOutputSize(this.data.length)];
        int read;

        try {
            read = cipher.doFinal(this.data, 0, this.data.length, bytes);
        } catch (IllegalBlockSizeException | ShortBufferException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        try {
            byte[] res = new byte[read];
            System.arraycopy(bytes, 0, res, 0, read);

            return res;
        } finally {
            Arrays.fill(bytes, (byte) 0);
            Arrays.fill(this.data, (byte) 0);
            Arrays.fill(this.iv, (byte) 0);
            this.data = null;
            this.iv = null;

            try {
                this.key.destroy();
            } catch (DestroyFailedException e) {
                /*
                 * It's unfortunate that we may not be able to destroy the key
                 * here, but it ultimately doesn't matter since we generate a
                 * new key anyway. (Additionally, the IV and the data is
                 * zeroed.)
                 */
            } finally {
                this.key = null;
            }
        }
    }

    @Override
    public void write(byte[] bytes) {
        KeyGenerator keyGenerator = getKeyGenerator();
        this.key = keyGenerator.generateKey();

        Cipher cipher = getCipher();

        this.iv = new byte[12];

        try {
            SecureRandom.getInstanceStrong().nextBytes(this.iv);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not generate a strong nonce", e);
        }

        ChaCha20ParameterSpec spec = new ChaCha20ParameterSpec(this.iv, 0);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to initialize cipher for encryption", e);
        }

        this.data = new byte[cipher.getOutputSize(bytes.length)];

        try {
            cipher.doFinal(bytes, 0, bytes.length, this.data);
        } catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

}