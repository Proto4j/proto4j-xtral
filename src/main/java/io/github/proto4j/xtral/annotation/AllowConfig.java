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

import io.github.proto4j.xtral.config.XTralConfiguration;
import io.github.proto4j.xtral.config.XTralConfigurationFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to set individual options when creating the client
 * or server. There are two possibilities:
 * <ul>
 *     <li>Provide a class that extends the {@link XTralConfigurationFactory}</li>
 *     <li>Create a {@code .properties} file with the configuration that
 *     should be used.</li>
 * </ul>
 *
 * @see XTralConfigurationFactory
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AllowConfig {

    /**
     * Returns the class which provides the configuration values.
     * <p>
     * If no path is given and the {@link XTralConfigurationFactory} class is
     * provided here, the program assumes that the annotated class should be
     * used when creating the {@link XTralConfiguration} instance.
     *
     * @return The class which provides the configuration values.
     */
    Class<? extends XTralConfigurationFactory> value()
            default XTralConfigurationFactory.class;

    /**
     * Returns the resource path to the configuration file.
     *
     * @return The resource path to the configuration file.
     */
    String path() default "";
}
