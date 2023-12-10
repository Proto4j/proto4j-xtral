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

/**
 * <h2>XTral-Server-Bootstraps</h2>
 * This package contains a small utility class that defines an
 * {@link java.util.Iterator} style class that creates {@link java.net.Socket}
 * objects.
 * <p>
 * The basic usage of a {@link io.github.proto4j.xtral.bootstrap.ServerBootstrap} object
 * is the following:
 * <pre>{@code
 * ServerBootstrapFactory factory = ServerBootstrapFactory.getDefault();
 * ServerBootstrap bootstrap = factory.createBootstrap();
 * while (bootstrap.hasNext()) {
 *     Socket socket = bootstrap.next();
 * }
 * }</pre>
 * The {@code next()} method will block until the next connection is created.
 *
 * @see io.github.proto4j.xtral.bootstrap.ServerBootstrap
 * @see io.github.proto4j.xtral.bootstrap.ServerBootstrapFactory
 *
 * @since 1.0
 **/
package io.github.proto4j.xtral.bootstrap;