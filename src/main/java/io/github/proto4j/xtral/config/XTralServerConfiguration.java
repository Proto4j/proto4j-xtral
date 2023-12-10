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

package io.github.proto4j.xtral.config;//@date 17.09.2022

import io.github.proto4j.xtral.XTralServer;
import io.github.proto4j.xtral.bootstrap.ServerBootstrapFactory;
import io.github.proto4j.xtral.multicast.XTralMulticastConfiguration;

import javax.net.ServerSocketFactory;

/**
 * A template configuration with all attributes the {@link XTralServer} will
 * be using.
 *
 * @param <T> the initial client type (not {@code XTralClient} or
 *         {@link XTralServer})
 * @see XTralServer
 * @see XTralServerConfiguration
 * @see XTralMulticastConfiguration
 */
public interface XTralServerConfiguration<T> extends XTralConfiguration<T> {

    /**
     * Returns server socket factory.
     *
     * @return the server socket factory
     */
    public ServerSocketFactory getServerSocketFactory();

    /**
     * Returns the bootstrap factory.
     *
     * @return the bootstrap factory
     */
    public ServerBootstrapFactory getBootstrapFactory();
}
