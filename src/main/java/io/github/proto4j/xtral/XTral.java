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

package io.github.proto4j.xtral; //@date 17.09.2022

import io.github.proto4j.xtral.annotation.AllowConfig;
import io.github.proto4j.xtral.annotation.Client;
import io.github.proto4j.xtral.annotation.Server;
import io.github.proto4j.xtral.config.XTralConfiguration;
import io.github.proto4j.xtral.config.XTralConfigurationFactory;
import io.github.proto4j.xtral.config.XTralServerConfiguration;

import java.io.IOException;
import java.util.Objects;

/**
 * This class consists exclusively of static methods that operate on or return
 * {@link XTralServer} or XTralClient objects.
 * <p>
 * The methods of this class all throw a NullPointerException if the
 * arguments or class objects provided to them are null.
 *
 * @see XTralServer
 * @see XTralClient
 */
public final class XTral {

    /**
     * Creates a new {@code XTralServer} from the given annotated server
     * class and starts it with the given arguments.
     *
     * @param cls the server class
     * @param args the start-method arguments
     * @return a new {@code XTralServer} from the given annotated server class.
     * @throws Exception if an error occurs
     */
    public static XTralServer runServer(Class<?> cls, Object... args)
            throws Exception {
        XTralServer s = server(cls);
        s.start(args);
        return s;
    }

    /**
     * Creates a new {@code XTralServer} from the given annotated server
     * class.
     * <p>
     * The given class object must not be {@code null} and has to be annotated
     * with the {@link Server} annotation. The provided server class must be
     * located in the root package where all {@code Agent} classes can be
     * found in the same or in sub-packages.
     *
     * @param cls the server class
     * @return a new {@code XTralServer} from the given annotated server class.
     */
    public static XTralServer server(Class<?> cls) throws IOException {
        Objects.requireNonNull(cls);

        // Check whether the given class is annotated with the @Server
        // annotation.
        if (!cls.isAnnotationPresent(Server.class)) {
            throw new UnsupportedOperationException("Class must be annotated with @Client");
        }

        // Create the referenced server instance
        Object ref;
        try {
            ref = cls.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(e);
        }

        // The next step tries to load the XTralConfiguration
        XTralConfigurationFactory<?> factory = getConfigurationFactory(cls, ref);
        Objects.requireNonNull(factory);
        // Create the config instance and check if the factor returns a null
        // value which may lead to an exception later on.
        XTralConfiguration<?> config = factory.createConfiguration();
        if (!(config instanceof XTralServerConfiguration)) {
            throw new NullPointerException("Invalid XTralConfiguration");
        }

        return new XTralServer((XTralServerConfiguration<?>) config);
    }

    /**
     * Creates a new {@code XTralClient} from the given annotated client
     * class.
     * <p>
     * The given class object must not be {@code null} and has to be annotated
     * with the {@link Client} annotation. The provided client class must be
     * located in the root package where all {@code Agent} classes can be
     * found in the same or in sub-packages.
     *
     * @param clientCls the client class
     * @return a new {@code XTralClient} from the given annotated client class.
     */
    public static XTralClient client(Class<?> clientCls)
            throws UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(clientCls);

        // Check whether the given class is annotated with the @Client
        // annotation.
        if (!clientCls.isAnnotationPresent(Client.class)) {
            throw new UnsupportedOperationException("Class must be annotated with @Client");
        }

        // Create the referenced client instance
        Object ref;
        try {
            ref = clientCls.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(e);
        }

        // The next step tries to load the XTralConfiguration
        XTralConfigurationFactory<?> factory = getConfigurationFactory(clientCls, ref);
        Objects.requireNonNull(factory, "factory");
        // Create the config instance and check if the factor returns a null
        // value which may lead to an exception later on.
        XTralConfiguration<?> config = factory.createConfiguration();
        if (config == null) {
            throw new NullPointerException("Could not create XTralConfiguration");
        }

        return new XTralClient(config);
    }

    private static XTralConfigurationFactory<?> getConfigurationFactory(Class<?> cls, Object ref) {
        XTralConfigurationFactory<?> factory = null;
        if (cls.isAnnotationPresent(AllowConfig.class)) {
            AllowConfig acfg = cls.getAnnotation(AllowConfig.class);
            // There are three possible outcomes:
            if (acfg.path().isEmpty()) {
                // 1. there is not .properties file and not factory class
                // given. This program will try to use the base class as the
                // factory class.
                if (acfg.value().isInterface()) {
                    if (!XTralConfigurationFactory.class.isAssignableFrom(cls)) {
                        throw new UnsupportedOperationException("no factory specified");
                    }
                    factory = (XTralConfigurationFactory<?>) ref;
                }
                // 2. The factory class is given
                else try {
                    factory = acfg.value().getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new UnsupportedOperationException(e);
                }
            }
            // 3. Read the properties file and create a configuration factory
            // from the provided values.
            else {
                throw new UnsupportedOperationException("not implemented!");
            }
        }
        return factory;
    }
}
