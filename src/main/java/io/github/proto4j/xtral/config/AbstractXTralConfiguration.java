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

package io.github.proto4j.xtral.config; //@date 17.09.2022

import io.github.proto4j.xtral.io.ConnectionFactory;
import io.github.proto4j.xtral.bootstrap.ServerBootstrapFactory;
import io.github.proto4j.xtral.io.channel.ChannelFactory;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractXTralConfiguration<T> implements XTralServerConfiguration<T> {

    private final AtomicReference<T> reference = new AtomicReference<>(null);
    private final Class<T>           type;

    private ExecutorService   service;
    private ChannelFactory<?>    channelFactory;
    private ConnectionFactory<?> connectionFactory;

    private SocketFactory          socketFactory       = SocketFactory.getDefault();
    private ServerSocketFactory    serverSocketFactory = ServerSocketFactory.getDefault();
    private ServerBootstrapFactory bootstrapFactory    = ServerBootstrapFactory.getDefault();

    public AbstractXTralConfiguration(Class<T> type, T instance) {
        this.type = type;
        this.reference.set(instance);
    }

    public AbstractXTralConfiguration(Class<T> type, T instance,
                                      ExecutorService service,
                                      SocketFactory socketFactory,
                                      ServerSocketFactory serverSocketFactory,
                                      ChannelFactory<?> channelFactory,
                                      ConnectionFactory<?> connectionFactory,
                                      ServerBootstrapFactory bootstrapFactory) {
        this.type                = type;
        this.service             = service;
        this.socketFactory       = socketFactory;
        this.serverSocketFactory = serverSocketFactory;
        this.channelFactory      = channelFactory;
        this.connectionFactory   = connectionFactory;
        this.bootstrapFactory    = bootstrapFactory;
        this.reference.set(instance);
    }

    @Override
    public abstract Properties getProperties();

    @Override
    public Class<T> getReferenceType() {
        return type;
    }

    @Override
    public T getReference() {
        return reference.get();
    }

    @Override
    public SocketFactory getSocketFactory() {
        return socketFactory;
    }

    protected void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    @Override
    public ServerSocketFactory getServerSocketFactory() {
        return serverSocketFactory;
    }

    protected void setServerSocketFactory(ServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }

    @Override
    public ChannelFactory<?> getChannelFactory() {
        return channelFactory;
    }

    protected void setChannelFactory(ChannelFactory<?> channelFactory) {
        this.channelFactory = channelFactory;
    }

    @Override
    public ExecutorService getExecutorService() {
        return service;
    }

    @Override
    public ConnectionFactory<?> getConnectionFactory() {
        return connectionFactory;
    }

    protected void setConnectionFactory(ConnectionFactory<?> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    protected void setService(ExecutorService service) {
        this.service = service;
    }

    @Override
    public ServerBootstrapFactory getBootstrapFactory() {
        return bootstrapFactory;
    }

    public void setBootstrapFactory(ServerBootstrapFactory bootstrapFactory) {
        this.bootstrapFactory = bootstrapFactory;
    }
}
