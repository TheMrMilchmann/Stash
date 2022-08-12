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
package io.github.themrmilchmann.stash.internal.jna.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Crypt32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;
import io.github.themrmilchmann.stash.internal.platform.win32.DPAPIStorage;

/**
 * An extension to {@link Crypt32} that exposes additional functionality that is
 * required for {@link DPAPIStorage}.
 *
 * @author  Leon Linhart
 */
public interface Crypt32Ext extends Crypt32 {

    Crypt32Ext INSTANCE = Native.load("Crypt32", Crypt32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

    int CRYPTPROTECTMEMORY_SAME_PROCESS = 0;

    int CRYPTPROTECTMEMORY_BLOCK_SIZE = 16;

    boolean CryptProtectMemory(WinDef.LPVOID pDataIn, int cbDataIn, int dwFlags);

    boolean CryptUnprotectMemory(WinDef.LPVOID pDataIn, int cbDataIn, int dwFlags);

}