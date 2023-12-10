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

package io.github.proto4j.xtral.config;//@date 14.09.2022

import io.github.proto4j.xtral.XTral;
import io.github.proto4j.xtral.XTralServer;
import io.github.proto4j.xtral.io.ConnectionFactory;
import io.github.proto4j.xtral.io.channel.Channel;
import io.github.proto4j.xtral.io.channel.ChannelFactory;
import io.github.proto4j.xtral.multicast.XTralMulticastConfiguration;
import io.github.proto4j.xtral.XTralClient;
import io.github.proto4j.xtral.io.Connection;

import javax.net.SocketFactory;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * The base class for all configuration types.
 * <p>
 * This class is used within different contexts and should provide at least
 * the properties given here. There are some mutations of this configuration
 * class like {@link XTralMulticastConfiguration} or {@link XTralServerConfiguration}.
 * <p>
 * This configuration template can be used to create {@link XTralClient}
 * objects.
 *
 * @param <T> the initial client type (not {@code XTralClient} or
 *         {@link XTralServer})
 * @see XTralClient
 * @see XTralServerConfiguration
 * @see XTralMulticastConfiguration
 * @since 1.0
 */
public interface XTralConfiguration<T> {

    /**
     * Returns the initial client/server class type. This class object refers
     * to the class provided in {@link XTral#client(Class)} or
     * {@link XTral#server(Class)}.
     *
     * @return The initial client/server class type.
     */
    Class<T> getReferenceType();

    /**
     * Returns the initial client/server object. This object is an instance
     * of the class type provided in {@link XTral#client(Class)} or
     * {@link XTral#server(Class)}.
     *
     * @return The initial client/server object
     */
    T getReference();

    /**
     * Returns the factory for creating {@link java.net.Socket} objects.
     *
     * @return the factory for creating {@link java.net.Socket} objects.
     * @see SocketFactory
     */
    SocketFactory getSocketFactory();

    /**
     * Returns the factory for creating {@link Channel} objects.
     *
     * @return the factory for creating {@link Channel} objects.
     * @see ChannelFactory
     */
    ChannelFactory<?> getChannelFactory();

    /**
     * Returns the {@link ExecutorService} instance used to schedule tasks.
     *
     * @return The {@link ExecutorService} instance used to schedule tasks.
     */
    ExecutorService getExecutorService();

    /**
     * Returns the factory for creating {@link Connection} objects.
     *
     * @return the factory for creating {@link Connection} objects.
     * @see ConnectionFactory
     */
    ConnectionFactory<?> getConnectionFactory();

    /**
     * Returns additional attributes defined in the environment or in a
     * properties file.
     *
     * @return environment specific attributes
     */
    Properties getProperties();

}
