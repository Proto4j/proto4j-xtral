package org.proto4j.test.msdp.model; //@date 18.09.2022

import org.proto4j.xtral.io.channel.AbstractChannel;
import org.proto4j.xtral.io.channel.ChannelOption;
import org.proto4j.xtral.multicast.MulticastChannel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MsdpChannel extends AbstractChannel<MulticastSocket> implements MulticastChannel {

    public static final ChannelOption<InetAddress> ADDRESS_CHANNEL_OPTION =
            new ChannelOption<>(InetAddress.class, "channel.address");

    public static final ChannelOption<Integer> PORT_CHANNEL_OPTION =
            new ChannelOption<>(Integer.class, "channel.port");

    public static final ChannelOption<Integer> BUFFER_CHANNEL_OPTION =
            new ChannelOption<>(Integer.class, "channel.buffer");

    public MsdpChannel() throws IOException {
        super();
        setOption(ADDRESS_CHANNEL_OPTION, null);
        setOption(PORT_CHANNEL_OPTION, -1);
        setOption(BUFFER_CHANNEL_OPTION, -1);
    }

    @Override
    public void write(MulticastSocket socket, Object obj) throws IOException {
        if (!(obj instanceof byte[])) {
            throw new IOException("Invalid type: " + obj.getClass().getName());
        }
        byte[] buffer = (byte[]) obj;
        int    port   = getPort(socket);

        InetAddress address = getAddress(socket);
        DatagramPacket packet = new DatagramPacket(
                buffer, 0, buffer.length,
                address,
                port
        );
        socket.send(packet);
    }

    @Override
    public Object read(MulticastSocket socket) throws IOException{
        int size = getBufferSize(socket);
        DatagramPacket packet = new DatagramPacket(new byte[size], size);

        socket.receive(packet);
        return packet;
    }

    public int getPort(MulticastSocket socket) throws IOException {
        int port = getOption(PORT_CHANNEL_OPTION);
        if (port == -1) {
            port = socket.getPort();
            setOption(PORT_CHANNEL_OPTION, port);
        }
        return port;
    }

    public InetAddress getAddress(MulticastSocket socket) throws IOException {
        InetAddress address = getOption(ADDRESS_CHANNEL_OPTION);
        if (address == null) {
            address = socket.getInetAddress();
            setOption(ADDRESS_CHANNEL_OPTION, address);
        }
        return address;
    }

    public Integer getBufferSize(MulticastSocket socket) throws IOException {
        int size = getOption(BUFFER_CHANNEL_OPTION);
        if (size == -1) {
            size = socket.getReceiveBufferSize();
            setOption(BUFFER_CHANNEL_OPTION, size);
        }
        return size;
    }
}
