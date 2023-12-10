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

package io.github.proto4j.xtral.io.channel;//@date 16.09.2022

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A {@code Channel} is used to delegate the sending and receiving of custom
 * messages.
 * <p>
 * Implementations of this class may use the {@link #setOption(ChannelOption, Object)}
 * or {@link #getOption(ChannelOption)} methods to set global or local channel
 * configuration variables.
 *
 * @param <S> the socket type
 */
public interface Channel<S> extends Closeable {

    /**
     * Writes the given Object by using the provided {@link Socket}.
     *
     * @param socket The socket object which is used to delegate the writing
     *         process.
     * @param obj the object to be written
     * @throws IOException if an I/O error occurs, or if channel is closed.
     */
    public void write(S socket, Object obj)
            throws IOException;

    /**
     * Reads an object from the given {@link Socket}. This method will block
     * until the object is created, an I/O Error occurs or the end of the
     * stream is reached.
     *
     * @param socket The socket object which is used to read the binary contents
     *         from the stream.
     * @return the data read or an object created from the received data
     * @throws IOException if an I/O error occurs, or if channel is closed.
     */
    public Object read(S socket) throws IOException;

    /**
     * Sets the value of a channel option.
     *
     * @param option The channel option
     * @param value The value of the channel option. A value of {@code null}
     *         may be valid for some options.
     * @throws UnsupportedOperationException if the channel does not support
     *                                       the option.
     * @throws IllegalArgumentException      if the value is not valid for
     *                                       the option.
     * @throws IOException                   if an I/O error occurs, or if the
     *                                       channel is closed.
     * @throws NullPointerException          if option is {@code null}
     */
    public abstract <T> void setOption(ChannelOption<T> option, T value)
            throws IOException;

    /**
     * Sets the value of a channel option.
     *
     * @param <T> The type of the channel option value
     * @param option The channel option
     * @return The value of the channel option.
     * @throws UnsupportedOperationException if the channel does not support
     *                                       the option.
     * @throws IOException                   if an I/O error occurs, or if the
     *                                       channel is closed.
     * @throws NullPointerException          if option is {@code null}
     */
    public abstract <T> T getOption(ChannelOption<T> option)
            throws IOException;

    /**
     * Returns a set of the channel options supported by this channel.
     * <p>
     * This method will continue to return the set of options even after
     * the channel has been closed.
     *
     * @return A set of the channel options supported by this {@link Channel}.
     *         This set may be empty.
     */
    public Set<ChannelOption<?>> getOptions();

    /**
     * Returns a {@link Stream} of the socket options supported by this
     * channel.
     * <p>
     * This method will continue to return the set of options even after
     * the channel has been closed.
     *
     * @return A stream of the channel options supported by this {@link Channel}.
     *         This set may be empty.
     */
    public Stream<ChannelOption<?>> options();



}
