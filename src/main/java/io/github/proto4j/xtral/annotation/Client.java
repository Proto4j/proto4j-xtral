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

package io.github.proto4j.xtral.annotation;//@date 11.09.2022

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated class will be the base class for creating the
 * {@code XTralClient}.
 * <p>
 * This annotation is used for internal checks that have to be done and won't
 * be used in other contexts.
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Client {

    /**
     * This annotation is used to indicate that the annotated method will be
     * the client's starting point. The method can be called after the
     * {@code XTralClient} was created.
     * <p>
     * The first parameter of this method has to be the {@code XTralClient}
     * object - others are provided with the {@code start()} call on the client.
     * The structure can be defined as follows:
     * <pre>
     *      &#064;Client.Entry
     *      public void startMyClient(XTralClient client, [args])
     *          [throws Exception] {
     *           ...
     *      }
     * </pre>
     * If the defined structure is not provided the {@code start()} call will
     * produce an {@link NoSuchMethodException}.
     *
     * @since 1.0
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface Entry {
    }

}
