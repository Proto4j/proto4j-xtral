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

package io.github.proto4j.xtral; //@date 17.09.2022

import io.github.proto4j.xtral.annotation.Server;
import io.github.proto4j.xtral.bootstrap.ServerBootstrap;
import io.github.proto4j.xtral.bootstrap.ServerBootstrapFactory;
import io.github.proto4j.xtral.config.XTralConfiguration;
import io.github.proto4j.xtral.config.XTralServerConfiguration;
import io.github.proto4j.xtral.io.Connection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

/**
 * The {@code XTralServer} is based on a {@link XTralClient}, but provides
 * an iterator-style implementation on creating new connections.
 * <p>
 * This implementation of a server is written to react passively, so it
 * listens for incoming connections. Although, it is designed to listen
 * for new requests only, new connections can be created by calling the
 * {@link #openConnection()} method.
 * <p>
 * Additionally, this server won't act multithreaded if it wasn't defined
 * explicitly. The {@link #start(Object...)} method can be called via a
 * runnable wrapper on a new thread with {@link #execute(Runnable)}.
 * <p>
 * The basic iteration process of new connections would be the following one:
 * <pre>{@code
 * XTralServer server = ...
 * for (Connection<?> connection : server) {
 *     connection.init((ConnectSpec)null);
 *     server.execute(() -> {
 *         while (!connection.isClosed()) {
 *             connection.readObject();
 *         }
 *     });
 * }
 * }</pre>
 *
 * @since 1.0
 * @see XTralClient
 * @see Connection
 */
public class XTralServer extends XTralClient implements
        Iterator<Connection<?>>, Iterable<Connection<?>> {

    /**
     * An object implementing the mechanism for accepting new connections.
     *
     * @since 1.0
     */
    private final ServerBootstrap bootstrap;

    /**
     * Creates a new server based on the given configuration.
     *
     * @param configuration the shared configuration instance
     */
    public XTralServer(XTralServerConfiguration<?> configuration) throws IOException {
        super(configuration);
        ServerBootstrapFactory factory =
                getConfiguration().getBootstrapFactory();
        if (factory == null) {
            throw new NullPointerException("ServerBootstrapFactory == null");
        }
        bootstrap = factory.createBootstrap();
    }

    /**
     * {@inheritDoc}
     *
     * @param args {@inheritDoc}
     * @throws Exception {@inheritDoc}
     */
    @Override
    public synchronized void start(Object... args) throws Exception {
        Objects.requireNonNull(getConfiguration());

        Object ref = getConfiguration().getReference();
        for (Method method : ref.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Server.Entry.class)) {
                continue;
            }
            method.setAccessible(true);
            try {
                Object[] nargs = new Object[args.length + 1];
                System.arraycopy(args, 0, nargs, 1, args.length);
                nargs[0] = this;

                method.invoke(ref, nargs);
            } catch (InvocationTargetException e) {
                throw (Exception) e.getTargetException();
            }
            break;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return getBootstrap().hasNext();
    }

    /**
     * Returns the executor service instance.
     *
     * @return The executor service instance
     */
    public ExecutorService getExecutorService() {
        XTralConfiguration<?> config = getConfiguration();
        if (config.getExecutorService() == null) {
            throw new IllegalArgumentException("ExecutorService == null");
        }
        return config.getExecutorService();
    }

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's {@code get} method will return the task's result upon
     * successful completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if the task is null
     */
    public <T> Future<T> submit(Callable<T> task) {
        return getExecutorService().submit(task);
    }

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return {@code null} upon <em>successful</em> completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if the task is null
     */
    public Future<?> submit(Runnable task) {
        return getExecutorService().submit(task);
    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     *                                    accepted for execution
     * @throws NullPointerException       if command is null
     */
    public void execute(Runnable command) {
        getExecutorService().execute(command);
    }

    /**
     * {@inheritDoc}
     *
     * @return the next accepted connection
     */
    @Override
    public Connection<?> next() {
        if (isClosed()) {
            throw new NoSuchElementException("Server was closed");
        }
        try {
            Connection<Socket> connection = openConnection();
            connection.setSocket(getBootstrap().next());
            connection.setChannel();
            return connection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Iterator<Connection<?>> iterator() {
        return this;
    }

    /**
     * Returns the server bootstrap.
     *
     * @return The server bootstrap.
     */
    public ServerBootstrap getBootstrap() {
        return bootstrap;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public XTralServerConfiguration<?> getConfiguration() {
        return (XTralServerConfiguration<?>) super.getConfiguration();
    }
}

