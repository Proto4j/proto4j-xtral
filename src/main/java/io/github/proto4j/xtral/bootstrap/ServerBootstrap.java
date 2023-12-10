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

import io.github.proto4j.xtral.XTralServer;
import io.github.proto4j.xtral.io.Connection;

import javax.net.ServerSocketFactory;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * The base class for all {@code ServerBootstrap} classes. They are used to
 * create new {@link Socket} objects based on the linked {@link ServerSocket}
 * object.
 * <p>
 * This class implements the functionality of an iterator, therefore an
 * instance of this class can be used as follows:
 * <pre>{@code
 * ServerBootstrap bootstrap = ...
 * while (bootstrap.hasNext()) {
 *     Socket socket = bootstrap.next();
 * }
 * }</pre>
 * The {@link XTralServer} wraps that functionality by creating new
 * {@link Connection} objects based on the given {@link Socket}s provided by
 * this class.
 *
 * @see Iterator
 * @see Closeable
 * @see ServerBootstrapFactory
 *
 * @since 1.0
 */
public abstract class ServerBootstrap implements Closeable, Iterator<Socket> {

    /**
     * Used to indicate whether this bootstrap object can start to create
     * {@link Socket} objects.
     *
     * @serial
     */
    protected volatile boolean initialized;

    /**
     * The referenced {@code ServerSocket} object that creates each {@link Socket}
     * object.
     *
     * @since 1.0
     */
    private volatile ServerSocket serverSocket;

    /**
     * A simple variable storing the closed-state. See the {@link #close()}
     * method for details of the closing process.
     *
     * @see #close()
     */
    private volatile boolean closed;

    /**
     * Creates a new {@code ServerBootstrap} object and initializes internal
     * fields.
     * <p>
     * The implementation of this method can differ based on the context of
     * this class. By default, the {@link #initialized} variable is set to
     * {@code true} in order to enable further processing.
     *
     * @throws IOException if an I/O Error occurs
     */
    public ServerBootstrap() throws IOException {
        initialized = true;
    }

    /**
     * Returns the used {@link ServerSocket} object.
     *
     * @return The used {@link ServerSocket} object.
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Creates a new {@link ServerSocket} object by calling
     * {@link ServerSocketFactory#createServerSocket()} on the provided
     * factory object.
     * <p>
     * Note that this method will produce a {@link NullPointerException} if
     * the given factory is {@code null}.
     *
     * @param factory the {@link ServerSocketFactory} used for creating the
     *         {@link ServerSocket} object.
     * @throws IOException          if an I/O Error occurs
     * @throws NullPointerException if the given factory is {@code null}.
     */
    public synchronized void setServerSocket(ServerSocketFactory factory)
            throws IOException {
        Objects.requireNonNull(factory);
        serverSocket = factory.createServerSocket();
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return isInitialized() && !isClosed() && !serverSocket.isClosed();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public synchronized Socket next() {
        if (isClosed()) {
            throw new NoSuchElementException("ServerSocket is closed");
        }
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes this {@code ServerBootstrap} and releases all used information.
     * <p>
     * After calling this method, other methods of this class will be unusable.
     *
     * @throws IOException {@inheritDoc}
     */
    @Override
    public synchronized void close() throws IOException {
        serverSocket.close();
        closed = true;
    }

    /**
     * Returns whether this {@code ServerBootstrap} object has been closed.
     *
     * @return {@code true} if this bootstrap object has been closed;
     *         {@code false} otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Returns whether this {@code ServerBootstrap} object has been
     * initialized.
     *
     * @return {@code true} if this bootstrap object has been initialized;
     *         {@code false} otherwise.
     */
    public boolean isInitialized() {
        return initialized;
    }
}
