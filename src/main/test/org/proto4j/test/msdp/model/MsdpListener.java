package org.proto4j.test.msdp.model;//@date 18.09.2022

import org.proto4j.test.msdp.client.MsdpClient;

public interface MsdpListener {

    public abstract void onPacketReceived(MsdpClient client, MsdpPacket packet);
}
