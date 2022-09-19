package org.proto4j.test.msdp.client.handler; //@date 18.09.2022

import org.proto4j.test.msdp.model.MsdpPacket;
import org.proto4j.xtral.annotation.Agent;
import org.proto4j.xtral.annotation.InboundHandler;
import org.proto4j.xtral.annotation.OutboundHandler;

import java.io.IOException;
import java.net.DatagramPacket;

@Agent
public class MsdpClientHandler {

    @InboundHandler(addFirst = true)
    public MsdpPacket onPacket(DatagramPacket packet) {
        return new MsdpPacket(packet);
    }

    @OutboundHandler(addLast = true)
    public byte[] onPacketSent(MsdpPacket packet) throws IOException {
        return packet.pack();
    }
}
