package org.proto4j.xtral.io; //@date 18.09.2022

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

/**
 * The base class for connection implementations that use a class that
 * extends the {@link Socket} class.
 *
 * @param <S> the socket type
 */
public abstract class SocketConnectionBase<S extends Socket> extends ConnectionBase<S> {

    /**
     * {@inheritDoc}
     *
     * @param address {@inheritDoc}
     * @param port {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public void doConnect(InetAddress address, int port) throws IOException {
        Objects.requireNonNull(getConfiguration());
        Objects.requireNonNull(getChannelFactory());
        if (!initialized || !closed) {
            throw new IOException("Connection not initialized!");
        }

        if (getSocket() == null) {
            SocketFactory socketFactory = getConfiguration().getSocketFactory();
            if (socketFactory == null) {
                throw new IllegalArgumentException("DTFactory == null");
            }
            Socket socket = socketFactory.createSocket(address, port);
            // This will raise an exception if the provided socket object is not
            // a subclass of the type parameter's type.
            //noinspection unchecked
            setSocket((S) socket);
        }
        setChannel();
    }
}
