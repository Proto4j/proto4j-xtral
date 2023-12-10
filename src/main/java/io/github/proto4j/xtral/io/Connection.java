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

import io.github.proto4j.xtral.XTralServer;
import io.github.proto4j.xtral.annotation.ExceptionHandler;
import io.github.proto4j.xtral.annotation.InboundHandler;
import io.github.proto4j.xtral.annotation.IncludedHandler;
import io.github.proto4j.xtral.annotation.OutboundHandler;
import io.github.proto4j.xtral.config.XTralConfiguration;
import io.github.proto4j.xtral.io.channel.Channel;
import io.github.proto4j.xtral.io.channel.ChannelFactory;

import javax.net.SocketFactory;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * {@code Connection} objects represent a two-way connection to an endpoint.
 * <p>
 * They can be used to read data from a linked {@link Channel} or write data
 * to it. Different handlers can be registered by providing their annotation
 * type. <i>Operations done in this class are synchronized</i>.
 * <p>
 * By default, the registered {@link CallableHandler} objects are stored in
 * a {@link ConnectionBase.HandlerList} that delegates different collection
 * methods. They can be retrieved by calling {@link #getHandlers(Class)}.
 * The list system is not implemented in this abstract class to enable more
 * flexibility on creating new classes that inherit functionalities of a
 * connection.
 * <p>
 * The base class notifies stored handlers on the following events:
 * <ul>
 *     <li>{@link #sendObject(Object)}: if an object is ready to be send</li>
 *     <li>{@link #readObject()}: if an object was read</li>
 *     <li>Error: if any error occurs in the methods mentioned above, the
 *     registered exception handlers are notified.</li>
 * </ul>
 * It is possible to register new handlers by adding the {@link IncludedHandler}
 * annotation on the custom defined one. {@code CallableHandler} objects can
 * be retrieved by calling {@link CallableHandler#getInstance(Object, String, Class[])}.
 * <p>
 * Before the {@link #doConnect(InetAddress, int)} method can be called, this
 * connection has to be {@code initialized} by executing the {@code init()}
 * method. The {@code ConnectSpec} is a tagging interface that contains no
 * default attributes. It is used to provide more flexibility to initialize
 * this connection object. After applying the {@code ConnectSpec} object
 * to this connection, the {@link #initialized} must be set to {@code true}
 * in order to continue the connection process.
 * <p>
 * A connection is established by calling {@link #doConnect(InetAddress, int)}
 * with a qualified address and port. A new socket object will be retrieved
 * via a {@link SocketFactory} if none was set before (see {@link XTralServer#next()}).
 * If the socket was created successfully, a new {@link Channel} will be build
 * with a {@code ChannelFactory} stored inside the configuration object.
 * <p>
 * To summarize the basic control flow of this class, the following code can
 * be used as an example:
 * <pre>{@code
 * ConnectionFactory<Socket> factory = ...
 * Connection<Socket> connection = factory.createConnection();
 * connection.init((ConnectSpec)null);
 * connection.doConnect(host, port);
 * }</pre>
 * <p>
 * When calling either {@link #sendObject(Object)} or {@link #readObject()},
 * there will be a redundancy check to ensure the reading and writing process
 * execute flawless.
 *
 * @param <S> the socket type
 * @see CallableHandler
 * @see ConnectionFactory
 */
public abstract class Connection<S> implements Closeable {

    /**
     * Indicates whether this connection has been initialized.
     *
     * @since 1.0
     */
    protected volatile boolean initialized;

    /**
     * Indicates whether this connection has been closed.
     *
     * @since 1.0
     */
    protected volatile boolean closed;

    /**
     * A global shared configuration instance that stores the used factories.
     *
     * @since 1.0
     */
    private XTralConfiguration<?> configuration;

    /**
     * The linked channel object for delegating the read and write process
     */
    private volatile Channel<S> channel;

    /**
     * The socket object.
     */
    private volatile S socket;

    /**
     * Creates a new {@link Connection} without being initialized.
     */
    protected Connection() {
    }

    /**
     * Initializes this {@code Connection} object and sets the {@link #initialized}
     * variable to {@code true}.
     *
     * @param spec the implemented {@code ConnectSpec} or {@code null} if no
     *         parameters are used.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void init(ConnectSpec spec) throws IOException;

    /**
     * Tries to resolve all {@code CallableHandler}s mapped to the given
     * annotation type.
     * <p>
     * This method will throw an exception if no handlers are mapped to the
     * given type.
     *
     * @param cls the annotation type the handlers are mapped to
     * @param <A> the annotation type
     * @return all {@code CallableHandler}s mapped to the given annotation
     *         type as an array.
     * @throws NullPointerException   if the provided class object was
     *                                {@code null}.
     * @throws NoSuchElementException if no mapping exists for the given
     *                                annotation type.
     */
    public abstract <A extends Annotation> CallableHandler[] getHandlers(Class<A> cls)
            throws NullPointerException, NoSuchElementException;

    /**
     * Inserts the specified element at the first position in the mapped handler
     * list (index {@code 0}).
     * <p>
     * If no mapping exists for the given annotation class, a new one will be
     * created.
     *
     * @param cls the mapping key
     * @param handler the {@code CallableHandler} to insert.
     * @param <A> the annotation type
     * @throws NullPointerException if the given handler is {@code null}
     */
    public abstract <A extends Annotation> void addFirst(Class<A> cls, CallableHandler handler)
            throws NullPointerException;

    /**
     * Inserts the specified element at the end of the mapped handler list.
     * <p>
     * If no mapping exists for the given annotation class, a new one will be
     * created.
     *
     * @param cls the mapping key
     * @param handler the {@code CallableHandler} to insert.
     * @param <A> the annotation type
     * @throws NullPointerException if the given handler is {@code null}
     */
    public abstract <A extends Annotation> void addLast(Class<A> cls, CallableHandler handler)
            throws NullPointerException;

    /**
     * Notifies all handlers mapped to the given annotation class.
     * <p>
     * For parameter inspection refer to the base implementation in
     * {@link ConnectionBase#notifyChange(Class, Object...)}.
     *
     * @param cls the mapping key
     * @param args context arguments (1. context-specific, 2. this, 3. channel)
     * @param <A> the annotation type
     * @return the modified first argument value (optional)
     */
    protected abstract <A extends Annotation> Object notifyChange(Class<A> cls, Object... args);

    /**
     * Starts the connection process and establishes a new connection.
     * <p>
     * A new socket object will be retrieved via a {@code SocketFactory} if
     * none was set before (see {@link XTralServer#next()}). If the socket
     * was created successfully, a new {@link Channel} will be build with a
     * {@code ChannelFactory} stored inside the configuration object.
     *
     * @param host the bind address
     * @param port the port to bind to
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if any of the retrieved factories are
     *                                  {@code null}
     */
    public synchronized void doConnect(String host, int port) throws IOException {
        doConnect(InetAddress.getByName(host), port);
    }

    /**
     * Starts the connection process and establishes a new connection.
     * <p>
     * A new socket object will be retrieved via a {@code SocketFactory} if
     * none was set before (see {@link XTralServer#next()}). If the socket
     * was created successfully, a new {@link Channel} will be build with a
     * {@code ChannelFactory} stored inside the configuration object.
     *
     * @param address the bind address
     * @param port the port to bind to
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if any of the retrieved factories are
     *                                  {@code null}
     */
    public abstract void doConnect(InetAddress address, int port) throws IOException;

    /**
     * Returns the linked {@link Channel} object.
     *
     * @return The linked {@link Channel} object.
     */
    public synchronized Channel<S> getChannel() {
        return channel;
    }

    /**
     * Returns the used {@code Socket} instance of type {@code S}.
     *
     * @return The used {@code Socket} instance.
     */
    public synchronized S getSocket() {
        return socket;
    }

    /**
     * Tries to set a new socket.
     * <p>
     * This method will fail up on these two conditions: Either
     * <ol>
     *    <li>the given argument is {@code null},</li>
     *    <li>or the stored socket is already set.</li>
     * </ol>
     *
     * @param socket the socket to be used.
     * @throws NullPointerException  if the given argument is {@code null}
     * @throws IllegalStateException if the stored socket is already set
     */
    public synchronized final void setSocket(S socket)
            throws NullPointerException, IllegalStateException, IOException {
        Objects.requireNonNull(socket);
        this.socket = socket;
    }

    /**
     * Tries to create a new {@link Channel} objects based on the factory
     * provided by the {@link XTralConfiguration}.
     * <p>
     * This method will have no effect if the channel has already been set.
     *
     * @throws IOException           if an I/O error occurs
     * @throws NullPointerException  if the factory returns a {@code null}
     *                               value
     * @throws IllegalStateException if the {@code ChannelFactory} is not
     *                               defined.
     */
    public synchronized final void setChannel() throws IOException {
        if (channel != null) {
            return;
        }
        ChannelFactory<?> factory = getChannelFactory();
        if (factory == null) {
            throw new IllegalStateException("ChannelFactory == null");
        }

        Channel<?> ch = Objects.requireNonNull(factory.createChannel());
        //noinspection unchecked
        this.channel = (Channel<S>) ch;
    }

    /**
     * Reads an object from the underlying {@link Channel}. This method will
     * block until the object is created, an I/O Error occurs or the end of
     * the stream is reached.
     * <p>
     * Note that the returned object may have been modified by the registered
     * handlers through {@link #notifyChange(Class, Object...)}.
     *
     * @return the object that was received or {@code null} if an error occurs
     * @throws UnsupportedOperationException if this connection was not
     *                                       initialized or has been closed.
     */
    public synchronized Object readObject() {
        if (!initialized) {
            throw new UnsupportedOperationException("Connection not initialized");
        }

        if (closed) {
            throw new UnsupportedOperationException("Connection closed!");
        }
        try {
            cyclicCheck("readObject");
            Object obj = getChannel().read(getSocket());

            return notifyChange(InboundHandler.class, obj, this, getChannel());
        } catch (Exception e) {
            notifyChange(ExceptionHandler.class, e, this, getChannel());
            return null;
        }
    }

    /**
     * Writes the given Object by using the linked {@link Channel}.
     *
     * @param o the object to be sent
     * @throws UnsupportedOperationException if this connection was not
     *                                       initialized or has been closed.
     * @throws NullPointerException          if the message object is
     *                                       {@code null}
     */
    public synchronized void sendObject(Object o) {
        if (!initialized) {
            throw new UnsupportedOperationException("Connection not initialized");
        }
        if (closed) {
            throw new UnsupportedOperationException("Connection closed!");
        }
        try {
            cyclicCheck("sendObject");
            o = notifyChange(OutboundHandler.class, o, this);
            if (o == null) {
                throw new NullPointerException("message is null");
            }

            getChannel().write(getSocket(), o);
        } catch (Exception e) {
            notifyChange(ExceptionHandler.class, e, this, getChannel());
        }
    }

    /**
     * Returns whether this connection has been closed.
     *
     * @return {@code true} if this connection object has been closed;
     *         {@code false} otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Closes this connection and releases any system resources associated
     * with it. If the connection is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public synchronized void close() throws IOException {
        if (isClosed()) {
            return;
        }
        getChannel().close();
        closed = true;
    }

    /**
     * Returns the stored {@link XTralConfiguration} object.
     *
     * @return The stored {@link XTralConfiguration} object.
     */
    public XTralConfiguration<?> getConfiguration() {
        return configuration;
    }

    /**
     * Sets a new {@link XTralConfiguration}.
     *
     * @param configuration the configuration object
     */
    public synchronized void setConfiguration(XTralConfiguration<?> configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }

    /**
     * Returns the stored {@link ChannelFactory}.
     *
     * @return The stored {@link ChannelFactory}.
     */
    public ChannelFactory<?> getChannelFactory() {
        return getConfiguration().getChannelFactory();
    }

    // Redundancy check with an Exception
    private void cyclicCheck(String name) {
        Exception exception = new Exception();
        StackTraceElement[] stackTrace = exception.getStackTrace();

        int count = 0;
        for (StackTraceElement e : stackTrace) {
            if (e.getMethodName().equals(name)) {
                count++;
            }
        }

        if (count > 1) {
            throw new RuntimeException("Cannot call "+name+"() twice");
        }
    }

}
