package org.proto4j.xtral.multicast; //@date 18.09.2022

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

class DefaultDatagramSocketFactory extends DatagramSocketFactory {

    @Override
    public DatagramSocket createSocket(InetAddress address, int port)
            throws IOException {
        return new DatagramSocket(port, address);
    }
}
