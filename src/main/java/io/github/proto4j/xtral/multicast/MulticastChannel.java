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

package io.github.proto4j.xtral.multicast;//@date 17.09.2022

import io.github.proto4j.xtral.io.channel.Channel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

/**
 * A Multicast channel does not differ much from the base {@link Channel} -
 * it just defines some extra methods that supports the initialization of
 * the used socket.
 *
 * @see Channel
 */
public interface MulticastChannel extends Channel<MulticastSocket> {

    /**
     * Joins a multicast group to begin receiving all datagrams sent to the
     * group.
     * <p>
     * A multicast channel may join several multicast groups, including
     * the same group on more than one interface. An implementation may impose
     * a limit on the number of groups that may be joined at the same time.
     *
     * @param socket The socket which should join the multicast group
     * @param address The multicast address to join
     * @throws IOException If an I/O error occurs
     */
    public default void joinGroup(MulticastSocket socket, InetAddress address)
            throws IOException {
        socket.joinGroup(address);
    }

    /**
     * Joins a multicast group to begin receiving all datagrams sent to the
     * group.
     * <p>
     * A multicast channel may join several multicast groups, including
     * the same group on more than one interface. An implementation may impose
     * a limit on the number of groups that may be joined at the same time.
     *
     * @param socket The socket which should join the multicast group
     * @param group The multicast address to join
     * @param netiface The network interface on which to join the group
     * @throws IOException If an I/O error occurs
     */
    public default void joinGroup(MulticastSocket socket, SocketAddress group, NetworkInterface netiface)
            throws IOException {
        socket.joinGroup(group, netiface);
    }

    /**
     * Leaves a multicast group on a specified local interface.
     *
     * @param socket The socket which should leave the multicast group
     * @param group The multicast address to leave
     * @param iface The network interface on which to leave the group
     * @throws IOException If an I/O error occurs
     */
    public default void leaveGroup(MulticastSocket socket, SocketAddress group, NetworkInterface iface)
            throws IOException {
        socket.leaveGroup(group, iface);
    }

    /**
     * Leaves a multicast group on a specified local interface.
     *
     * @param socket The socket which should leave the multicast group
     * @param address The multicast address to leave
     * @throws IOException If an I/O error occurs
     */
    public default void leaveGroup(MulticastSocket socket, InetAddress address)
            throws IOException {
        socket.leaveGroup(address);
    }
}
