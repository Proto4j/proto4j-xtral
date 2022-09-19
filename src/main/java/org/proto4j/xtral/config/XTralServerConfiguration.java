package org.proto4j.xtral.config;//@date 17.09.2022

import org.proto4j.xtral.XTralClient;
import org.proto4j.xtral.XTralServer;
import org.proto4j.xtral.bootstrap.ServerBootstrapFactory;
import org.proto4j.xtral.multicast.XTralMulticastConfiguration;

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
