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

package io.github.proto4j.test.msdp.client; //@date 18.09.2022

import io.github.proto4j.test.msdp.model.MsdpConnection;
import io.github.proto4j.xtral.annotation.AllowConfig;
import io.github.proto4j.xtral.config.XTralConfigurationFactory;
import io.github.proto4j.test.msdp.model.MsdpConfiguration;
import io.github.proto4j.test.msdp.model.MsdpListener;
import io.github.proto4j.test.msdp.model.MsdpPacket;
import io.github.proto4j.xtral.XTralClient;
import io.github.proto4j.xtral.annotation.Client;
import io.github.proto4j.xtral.config.XTralConfiguration;

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
