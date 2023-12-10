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
 * A {@code ServerBootstrapFactory} is used to create {@code ServerBootstrap}
 * objects.
 * <p>
 * The default factory creates standard {@link java.net.Socket} objects with
 * the {@code next()} method.
 *
 * @see ServerBootstrap
 */
public abstract class ServerBootstrapFactory {

    /**
     * The default factory object (lazy creation).
     *
     * @see DefaultServerBootstrapFactory
     */
    private static ServerBootstrapFactory defaultFactory;

    /**
     * Returns the default factory for {@link ServerBootstrapFactory}
     * objects.
     *
     * @return the default factory for {@link ServerBootstrapFactory}
     *         objects.
     *
     * @see DefaultServerBootstrapFactory
     */
    public static ServerBootstrapFactory getDefault() {
        synchronized (ServerBootstrapFactory.class) {
            // Lazily create the factory object. Synchronization is needed
            // to ensure the factory is created before other callers try to
            // access it.
            if (defaultFactory == null) {
                defaultFactory = new DefaultServerBootstrapFactory();
            }
            return defaultFactory;
        }
    }

    /**
     * Creates a new {@link ServerBootstrap} and initializes it.
     *
     * @return the newly created {@link ServerBootstrap}.
     * @throws IOException if an I/O Error occurs.
     */
    public abstract ServerBootstrap createBootstrap() throws IOException;
}
