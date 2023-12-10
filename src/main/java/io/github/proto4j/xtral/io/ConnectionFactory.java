/*
 * MIT License
 *
 * Copyright (c) 2023 Proto4j-Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.proto4j.xtral.io; //@date 16.09.2022

import io.github.proto4j.xtral.annotation.ExceptionHandler;

import java.io.IOException;

/**
 * Configuration factories are used to provide a new {@link Connection}
 * object.
 * <p>
 * This factory is designed to throw an exception on failure which will
 * not be covered by any registered {@link ExceptionHandler}.
 *
 * @param <T> the socket type
 * @since 1.0
 */
@FunctionalInterface
public interface ConnectionFactory<T> {

    /**
     * Creates a new customized {@link Connection} object.
     *
     * @return a new customized {@link Connection} object.
     * @throws IOException if an I/O Error occurs.
     */
    public abstract Connection<T> createConnection() throws IOException;
}
