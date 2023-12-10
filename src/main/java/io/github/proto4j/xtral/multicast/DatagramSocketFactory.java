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

package io.github.proto4j.xtral.multicast; //@date 18.09.2022

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A {@code DatagramSocketFactory} is used to create {@code DatagramSocket}
 * objects.
 * <p>
 * The default factory creates standard {@link DatagramSocket} objects.
 *
 * @see DatagramSocket
 */
public abstract class DatagramSocketFactory {

    /**
     * The default factory object (lazy creation).
     *
     * @see DefaultDatagramSocketFactory
     */
    private static DatagramSocketFactory defaultFactory = null;

    /**
     * Returns the default factory for {@link DatagramSocketFactory}
     * objects.
     *
     * @return the default factory for {@link DatagramSocketFactory}
     *         objects.
     *
     * @see DefaultDatagramSocketFactory
     */
    public static DatagramSocketFactory getDefault() {
        if (defaultFactory == null) {
            synchronized (DatagramSocketFactory.class) {
                defaultFactory = new DefaultDatagramSocketFactory();
            }
        }
        return defaultFactory;
    }

    /**
     * Creates a new {@link DatagramSocket} and bind it to the given address
     * and port.
     *
     * @return the newly created {@link DatagramSocket}.
     * @throws IOException if an I/O Error occurs.
     */
    public abstract DatagramSocket createSocket(InetAddress address, int port)
            throws IOException;
}
