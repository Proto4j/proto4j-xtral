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

package io.github.proto4j.xtral.annotation;//@date 17.09.2022

import io.github.proto4j.xtral.config.XTralConfiguration;
import io.github.proto4j.xtral.io.Connection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutorService;

/**
 * Indicates that the annotated method will handle outgoing object messages.
 * <p>
 * There is a pre-defined structure of the declared methods:
 * <pre>{@code
 * public [RETURN | void] onNext(Object, [Connection]);
 * }</pre>
 * The object message always has to be declared as a parameter. The used
 * {@link Connection} which can read and write objects is optional. The
 * {@code Connection} object implements a mechanism that there will be an
 * exception if the {@link Connection#sendObject(Object)} method is called
 * in a cyclic context.
 * <p>
 * The executed methods can affect the outgoing messages by returning
 * a new value. Usually, they return nothing which does not affect the
 * message to be sent.
 *
 * @see InboundHandler
 * @since 1.0
 */
@IncludedHandler
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OutboundHandler {

    /**
     * Returns whether the annotated Method should run in parallel.
     * <p>
     * The default value for this property will be {@code false}. Note that
     * the {@link ExecutorService} provided by the {@link XTralConfiguration}
     * will be used to execute the method on a new thread.
     *
     * @return {@code true} if a new thread should be created when executing
     *         the annotated method
     */
    boolean parallel() default false;

    /**
     * Indicates the annotated method will be executed in a synchronized
     * context.
     *
     * @return {@code true} if the annotated method should be executed in a
     *         synchronized context
     */
    boolean sync() default false;

    /**
     * Indicates that the annotated handler will be the first one that
     * processes outgoing packets.
     *
     * @return {@code true} if this handler should handle outgoing messages
     *         first.
     */
    boolean addFirst() default false;

    /**
     * Indicates that the annotated handler will be added to the end of the
     * handler chain.
     *
     * @return {@code true} if this handler should handle outgoing messages
     *         at last.
     */
    boolean addLast() default false;
}
