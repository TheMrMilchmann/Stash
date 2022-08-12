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
 * A {@code Serializer} is responsible for converting between arbitrary data of
 * type {@code T} and a serialized {@code byte[]} representation.
 *
 * @param <T>   the type of data processed by this serializer
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public interface Serializer<T> {

    /*
     * TODO
     *  The idea of doing this via byte[] is potentially unsafe as the GC could
     *  hit a safe-point and move objects during de-/serialization. In this
     *  case, the old data may not be properly cleared. To prevent this, it
     *  might make sense to use an off-heap buffer instead. MemorySegments could
     *  be useful here. (ByteBuffers are impractical due to their lifetime.)
     */

    /**
     * Deserializes data of type {@code T} from the given {@code bytes}.
     *
     * <p>The given array should not be stored or referenced outside of this
     * method. Modifications are unsupported and may lead to undefined behavior.
     * </p>
     *
     * @param bytes the bytes to deserialize
     *
     * @return  the deserialized data
     *
     * @apiNote To ensure that sensitive data is kept in memory for only as long
     *          as possible, callers should take care of overwriting the given
     *          {@code bytes} after calling this method.
     *
     * @since   0.1.0
     */
    T deserialize(byte[] bytes);

    /**
     * Serializes the given {@code data} into an array of bytes.
     *
     * <p>The returned array should not be stored or referenced outside of this
     * method.</p>
     *
     * @param data  the data to serialize
     *
     * @return  the serialized bytes
     *
     * @since   0.1.0
     */
    byte[] serialize(T data);

}
