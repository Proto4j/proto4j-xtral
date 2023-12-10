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

package io.github.proto4j.xtral.bootstrap; //@date 17.09.2022

import java.io.IOException;

/**
 * The default implementation of this factory class creates object of the
 * {@link ServerSocketBootstrap} class.
 *
 * @see ServerSocketBootstrap
 * @see ServerBootstrapFactory
 */
class DefaultServerBootstrapFactory extends ServerBootstrapFactory {

    /**
     * Creates a new {@link ServerBootstrap} and initializes it.
     *
     * @return the newly created {@link ServerBootstrap}.
     * @throws IOException if an I/O Error occurs.
     */
    @Override
    public ServerBootstrap createBootstrap() throws IOException {
        return new ServerSocketBootstrap();
    }
}
