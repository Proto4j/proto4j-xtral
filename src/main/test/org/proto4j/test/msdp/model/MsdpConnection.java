package org.proto4j.test.msdp.model; //@date 18.09.2022

import org.proto4j.xtral.io.ConnectSpec;
import org.proto4j.xtral.multicast.MulticastConnectionBase;

import java.io.IOException;
import java.net.InetAddress;

public class MsdpConnection extends MulticastConnectionBase {

    @Override
    public void init(ConnectSpec spec) throws IOException {
        // add options here
        initialized = true;
    }

    @Override
    public synchronized void doConnect(InetAddress address, int port) throws IOException {
        super.doConnect(address, port);
        getChannel().joinGroup(getSocket(), getSocket().getInetAddress());
    }
}
