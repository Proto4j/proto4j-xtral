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

import io.github.proto4j.xtral.io.channel.AbstractChannel;
import io.github.proto4j.xtral.io.channel.ChannelOption;
import io.github.proto4j.xtral.multicast.MulticastChannel;

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
