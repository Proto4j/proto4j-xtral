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

import io.github.proto4j.xtral.io.Connection;
import io.github.proto4j.xtral.io.channel.Channel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code ExceptionHandlers} are used by {@link Connection} objects to handle
 * uncaught exceptions while trying to connect, to send or to receive.
 * <p>
 * The method structure is defined as follows:
 * <pre>
 * &#064;ExceptionHandler
 * public void foo(Throwable, [Connection], [Channel]);
 * </pre>
 * The method must not have any return type. The {@link Connection} and {@link Channel}
 * are not required but can be added as well. This method can be used to
 * close the {@code Channel} or {@code Connection}.
 *
 * @see Connection
 * @see Channel
 * @since 1.0
 */
@IncludedHandler
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExceptionHandler {


}
