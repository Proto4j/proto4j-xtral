package org.proto4j.xtral.config; //@date 17.09.2022

import org.proto4j.xtral.bootstrap.ServerBootstrapFactory;
import org.proto4j.xtral.io.ConnectionFactory;
import org.proto4j.xtral.io.channel.ChannelFactory;

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
