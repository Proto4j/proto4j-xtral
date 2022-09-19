package org.proto4j.test.msdp.client.handler; //@date 18.09.2022

import org.proto4j.xtral.annotation.Agent;
import org.proto4j.xtral.annotation.ExceptionHandler;
import org.proto4j.xtral.io.Connection;

import java.net.DatagramSocket;

@Agent
public class MsdpClientErrorHandler {

    @ExceptionHandler
    public void onError(Throwable throwable, Connection<DatagramSocket> connection) {
        System.out.println("Error: " + throwable.toString());
    }
}
