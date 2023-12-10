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

package io.github.proto4j.test.msdp.model; //@date 18.09.2022

import io.github.proto4j.xtral.config.AbstractXTralConfiguration;
import io.github.proto4j.xtral.io.ConnectionFactory;
import io.github.proto4j.xtral.io.channel.ChannelFactory;
import io.github.proto4j.xtral.multicast.DatagramSocketFactory;
import io.github.proto4j.xtral.multicast.XTralMulticastConfiguration;

import java.io.IOException;
import java.net.*;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;

public class MsdpConfiguration<T> extends AbstractXTralConfiguration<T>
        implements XTralMulticastConfiguration<T> {

    private final Properties properties = new Properties();

    public MsdpConfiguration(Class<T> type, T instance) throws UnknownHostException {
        super(type, instance);
        setChannelFactory((ChannelFactory<MulticastSocket>) MsdpChannel::new);
        setConnectionFactory((ConnectionFactory<MulticastSocket>) MsdpConnection::new);
        setService(new ForkJoinPool(5));

        properties.putIfAbsent("msdp.address", InetAddress.getByName("224.0.0.200"));
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public DatagramSocketFactory getDatagramFactory() {
        return new DatagramSocketFactory() {
            @Override
            public DatagramSocket createSocket(InetAddress address, int port) throws IOException {
                return new MulticastSocket(new InetSocketAddress(address.getHostName(), port));
            }
        };
    }
}
