package org.proto4j.test.msdp.model; //@date 18.09.2022

import org.proto4j.xtral.config.AbstractXTralConfiguration;
import org.proto4j.xtral.io.ConnectionFactory;
import org.proto4j.xtral.io.channel.ChannelFactory;
import org.proto4j.xtral.multicast.DatagramSocketFactory;
import org.proto4j.xtral.multicast.XTralMulticastConfiguration;

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
