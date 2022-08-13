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
package io.github.themrmilchmann.stash.internal.platform.win32;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@EnabledOnOs(OS.WINDOWS)
public final class DPAPIStorageTest {

    private static final DPAPIStorageFactory factory = new DPAPIStorageFactory();

    private static Field fieldMemory, fieldHMemory;

    @BeforeAll
    public static void init() throws NoSuchFieldException {
        Class<?> cls = DPAPIStorage.class;

        fieldMemory = cls.getDeclaredField("memory");
        fieldMemory.setAccessible(true);

        fieldHMemory = cls.getDeclaredField("hMemory");
        fieldHMemory.setAccessible(true);
    }

    @Test
    public void testWriteDispose() throws IllegalAccessException {
        DPAPIStorage storage = factory.create();

        assertNull(fieldMemory.get(storage));
        assertNull(fieldHMemory.get(storage));

        Random random = new Random();

        byte[] bytes = new byte[100];
        random.nextBytes(bytes);

        storage.write(bytes);
        assertNotNull(fieldMemory.get(storage));
        assertNotNull(fieldHMemory.get(storage));

        storage.dispose();
    }

    @Test
    public void testWriteRead() throws IllegalAccessException {
        DPAPIStorage storage = factory.create();

        assertNull(fieldMemory.get(storage));
        assertNull(fieldHMemory.get(storage));

        Random random = new Random();

        byte[] bytes = new byte[100];
        random.nextBytes(bytes);

        storage.write(bytes);
        assertNotNull(fieldMemory.get(storage));
        assertNotNull(fieldHMemory.get(storage));

        byte[] read = storage.read();

        assertArrayEquals(bytes, read);
        assertNull(fieldMemory.get(storage));
        assertNull(fieldHMemory.get(storage));
    }

}