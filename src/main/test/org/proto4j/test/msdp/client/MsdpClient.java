package org.proto4j.test.msdp.client; //@date 18.09.2022

import org.proto4j.test.msdp.model.MsdpConfiguration;
import org.proto4j.test.msdp.model.MsdpConnection;
import org.proto4j.test.msdp.model.MsdpListener;
import org.proto4j.test.msdp.model.MsdpPacket;
import org.proto4j.xtral.XTralClient;
import org.proto4j.xtral.annotation.AllowConfig;
import org.proto4j.xtral.annotation.Client;
import org.proto4j.xtral.config.XTralConfiguration;
import org.proto4j.xtral.config.XTralConfigurationFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Client
@AllowConfig
public class MsdpClient implements XTralConfigurationFactory<MsdpClient> {

    private final List<MsdpListener> listenerList = new LinkedList<>();

    private volatile XTralClient client;

    @Client.Entry
    public void startClient(XTralClient client) throws Exception {
        this.client = client;

        MsdpConnection connection = client.openConnection();
        Properties     properties = client.getConfiguration().getProperties();

        connection.init(null);
        connection.doConnect((InetAddress) properties.get("msdp.address"), 3439);
        client.getConfiguration()
              .getExecutorService()
              .execute(() -> listen(connection));

    }

    private void listen(MsdpConnection connection) {
        while (!connection.isClosed()) {
            MsdpPacket packet = (MsdpPacket) connection.readObject();

            for (MsdpListener msdpListener : listenerList) {
                msdpListener.onPacketReceived(this, packet);
            }
        }
    }

    @Override
    public XTralConfiguration<MsdpClient> createConfiguration() {
        try {
            return new MsdpConfiguration<>(MsdpClient.class, this);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public XTralClient getClient() {
        return client;
    }

    public boolean add(MsdpListener msdpListener) {
        return listenerList.add(msdpListener);
    }

    public boolean remove(MsdpListener o) {
        return listenerList.remove(o);
    }
}
