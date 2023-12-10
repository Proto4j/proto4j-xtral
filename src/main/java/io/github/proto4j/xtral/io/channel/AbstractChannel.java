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

package io.github.proto4j.xtral.io.channel; //@date 17.09.2022

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * The skeletal implementation of a {@link Channel}. This class provides a
 * synchronized {@link java.io.Closeable} implementation with a boolean
 * variable.
 * <p>
 * The read() and write() methods should be implemented by inheritors of
 * this class.
 *
 * @see Channel
 * @since 1.0
 */
public abstract class AbstractChannel<S> implements Channel<S> {

    /**
     * Indicates whether this channel is closed
     */
    private volatile boolean closed;

    private final ConcurrentMap<ChannelOption<?>, Object> options = new ConcurrentHashMap<>();

    /**
     * Returns whether this channel is closed.
     *
     * @return {@code true} if this channel has been closed; {@code false}
     *         otherwise
     */
    protected synchronized boolean isClosed() {
        return closed;
    }

    /**
     * Closes this channel and releases any system resources associated
     * with it. If the channel is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (isClosed()) {
            return;
        }
        closed = true;
    }

    /**
     * Returns a set of the channel options supported by this channel.
     * <p>
     * This method will continue to return the set of options even after
     * the channel has been closed.
     *
     * @return A set of the channel options supported by this {@link Channel}.
     *         This set may be empty.
     */
    @Override
    public Set<ChannelOption<?>> getOptions() {
        return options.keySet();
    }

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
    @Override
    public <T> void setOption(ChannelOption<T> option, T value) throws IOException {
        Objects.requireNonNull(option);

        if (!options.containsKey(option)) {
            throw new UnsupportedOperationException("Unsupported option: " + option.name());
        }
        options.put(option, value);
    }

    /**
     * Sets the value of a channel option.
     *
     * @param option The channel option
     * @return The value of the channel option.
     * @throws UnsupportedOperationException if the channel does not support
     *                                       the option.
     * @throws IOException                   if an I/O error occurs, or if the
     *                                       channel is closed.
     * @throws NullPointerException          if option is {@code null}
     */
    @Override
    public <T> T getOption(ChannelOption<T> option) throws IOException {
        Objects.requireNonNull(option);

        if (!options.containsKey(option)) {
            throw new UnsupportedOperationException("Unsupported option: " + option.name());
        }

        Object obj = options.get(option);
        return option.type().cast(obj);
    }

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
    @Override
    public Stream<ChannelOption<?>> options() {
        return getOptions().stream();
    }
}
