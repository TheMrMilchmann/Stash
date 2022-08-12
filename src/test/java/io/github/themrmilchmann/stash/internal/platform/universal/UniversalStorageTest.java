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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public final class UniversalStorageTest {

    private static final UniversalStorageFactory factory = new UniversalStorageFactory();

    private static Field fieldKey, fieldIV, fieldData;

    @BeforeAll
    public static void init() throws NoSuchFieldException {
        Class<?> cls = UniversalStorage.class;

        fieldKey = cls.getDeclaredField("key");
        fieldKey.setAccessible(true);

        fieldIV = cls.getDeclaredField("iv");
        fieldIV.setAccessible(true);

        fieldData = cls.getDeclaredField("data");
        fieldData.setAccessible(true);
    }

    @Test
    public void test() throws IllegalAccessException {
        UniversalStorage storage = factory.create();

        assertNull(fieldKey.get(storage));
        assertNull(fieldIV.get(storage));
        assertNull(fieldData.get(storage));

        Random random = new Random();

        byte[] bytes = new byte[100];
        random.nextBytes(bytes);

        storage.write(bytes);

        byte[] iv, data;

        assertNotNull(fieldKey.get(storage));
        assertNotNull(iv = (byte[]) fieldIV.get(storage));
        assertNotNull(data = (byte[]) fieldData.get(storage));

        byte[] read = storage.read();

        assertArrayEquals(bytes, read);
        assertNull(fieldKey.get(storage));
        assertNull(fieldIV.get(storage));
        assertNull(fieldData.get(storage));

        for (byte b : iv) assertEquals((byte) 0, b);
        for (byte b : data) assertEquals((byte) 0, b);
    }

}