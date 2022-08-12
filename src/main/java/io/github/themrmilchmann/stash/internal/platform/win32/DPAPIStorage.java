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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinDef;
import io.github.themrmilchmann.stash.Storage;
import io.github.themrmilchmann.stash.internal.jna.win32.Crypt32Ext;

import javax.annotation.Nullable;

/**
 * A (relatively) secure {@link Storage} implementation based on Windows' DPAPI.
 * The cold storage is unswappable memory protected by the OS itself.
 *
 * @author  Leon Linhart
 */
public final class DPAPIStorage implements Storage {

    private static int fit(int length, int blockSize) {
        return ((length + blockSize + 1) / blockSize) * blockSize;
    }

    @Nullable private Memory memory;
    @Nullable private WinDef.LPVOID hMemory;

    DPAPIStorage() {}

    @Override
    public byte[] read() {
        assert (this.memory != null);
        assert (this.hMemory != null);

        if (!Crypt32Ext.INSTANCE.CryptUnprotectMemory(this.hMemory, (int) this.memory.size(), Crypt32Ext.CRYPTPROTECTMEMORY_SAME_PROCESS)) {
            int code = Native.getLastError();
            String message = Kernel32Util.getLastErrorMessage();

            throw new RuntimeException("CryptUnprotectMemory produced unexpected error[" + code + "]: " + message);
        }

        int size = this.memory.getInt(0);
        byte[] bytes = new byte[size];
        this.memory.read(Integer.BYTES, bytes, 0, bytes.length);
        this.memory.clear();

        return bytes;
    }

    @Override
    public void write(byte[] bytes) {
        int size = fit(Integer.BYTES + bytes.length, Crypt32Ext.CRYPTPROTECTMEMORY_BLOCK_SIZE);

        this.memory = new Memory(size);
        this.hMemory = new WinDef.LPVOID(this.memory);

        this.memory.setInt(0, bytes.length);
        this.memory.write(Integer.BYTES, bytes, 0, bytes.length);

        if (!Crypt32Ext.INSTANCE.CryptProtectMemory(hMemory, size, Crypt32Ext.CRYPTPROTECTMEMORY_SAME_PROCESS)) {
            int code = Native.getLastError();
            String message = Kernel32Util.getLastErrorMessage();

            throw new RuntimeException("CryptProtectMemory produced unexpected error[" + code + "]: " + message);
        }
    }

}