package org.proto4j.test.msdp; //@date 18.09.2022

import org.proto4j.test.msdp.client.MsdpClient;
import org.proto4j.xtral.XTral;
import org.proto4j.xtral.XTralClient;

public class MainApplication {

    public static void main(String[] args) throws Exception {
        XTralClient client = XTral.client(MsdpClient.class);
        client.start();
    }
}
