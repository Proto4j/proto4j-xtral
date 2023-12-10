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

/**
 * <h2>XTral-Annotations</h2>
 * This package contains definitions of all annotations that can be used to
 * generate {@code XTralServer} and {@code XTralClient} objects.
 * <p>
 * Handler annotations are special up on the defined annotations, because
 * they can are annotated additionally with {@code @IncludedHeader}. The basic
 * handlers that can be registered to a {@code Connection} object are the
 * following:
 * <ul>
 *     <li>{@link io.github.proto4j.xtral.annotation.InboundHandler}: For handling
 *     incoming packets/ messaged</li>
 *     <li>{@link io.github.proto4j.xtral.annotation.OutboundHandler}: For handling
 *     outgoing messages (edit them for example).</li>
 *     <li>{@link io.github.proto4j.xtral.annotation.ExceptionHandler}: For handling
 *     exceptions that are thrown while reading or writing data</li>
 * </ul>
 * {@link io.github.proto4j.xtral.io.CallableHandler} objects are designed to be
 * created at runtime, therefore it is possible to define custom Handler
 * annotations and add custom {@code CallableHandler} objects to the
 * connection.
 *
 * @see io.github.proto4j.xtral.annotation.IncludedHandler
 * @see io.github.proto4j.xtral.annotation.Agent
 * @see io.github.proto4j.xtral.annotation.Client
 * @see io.github.proto4j.xtral.annotation.Server
 *
 * @since 1.0
 */
package io.github.proto4j.xtral.annotation;