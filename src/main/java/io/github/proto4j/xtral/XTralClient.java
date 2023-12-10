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

package io.github.proto4j.xtral; //@date 16.09.2022

import io.github.proto4j.xtral.annotation.Agent;
import io.github.proto4j.xtral.annotation.Client;
import io.github.proto4j.xtral.annotation.IncludedHandler;
import io.github.proto4j.xtral.config.XTralConfiguration;
import io.github.proto4j.xtral.io.CallableHandler;
import io.github.proto4j.xtral.io.ConnectionFactory;
import io.github.proto4j.xtral.io.Connection;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The base class for all {@code XTralClient} implementations.
 * <p>
 * This client can store several connections simultaneously. They will be
 * sorted out automatically if they have been closed. Classes annotated with
 * {@link Agent} will be instantiated at runtime if they contain handler
 * methods. Handlers are added if the handler annotation is annotated with
 * {@link IncludedHandler}.
 * <p>
 * {@link Connection}s that are opened with this client will be casted
 * automatically at runtime to the preferred one, for example:
 * <pre>
 *     // Connection-class: MyConnection
 *     XTralClient client = ...
 *     MyConnection c = client.openConnection();
 * </pre>
 * <b>Warning:</b> the {@link #openConnection()} method is not type-safe and
 * will result in an exception if the wrong connection type is provided.
 * <p>
 * Handlers are designed to intercept traffic before a message is going to be
 * sent and after a message has been received. They can be used to modify the
 * incoming or outgoing message. How they are going to be added to the
 * connection opened by this client is set every handler annotation with a
 * {@code addFirst} or {@code addLast} attribute. For a detailed view on hpw
 * handlers are added to created connections, see
 * {@link #linkHandler(Annotation, Connection, CallableHandler)}.
 * <p>
 * This implementation of a client is thread-safe to almost all
 * attributes it provides. Additionally, the {@link Connection}
 * implements a redundancy check to prevent cyclic calls.
 *
 * @see Connection
 * @since 1.0
 */
public class XTralClient implements Closeable {

    /**
     * Classes that are annotated with the {@link Agent} will be instantiated
     * and mapped with their class name in this concurrent map.
     *
     * @see Agent
     */
    private final ConcurrentMap<String, Object> beanCache = new ConcurrentHashMap<>();

    /**
     * A list of all connections that were created by theis client and are
     * active. Closed connections are removed automatically when calling
     * {@link #getConnections()}.
     */
    private final List<Connection<?>> cache = new CopyOnWriteArrayList<>();

    /**
     * All classes that are loaded while generating are stored in this list.
     */
    private final List<Class<?>> components = new CopyOnWriteArrayList<>();

    /**
     * A global shared configuration instance that stores the used factories.
     *
     * @since 1.0
     */
    private final XTralConfiguration<?> configuration;

    /**
     * Indicates whether this client has been closed.
     *
     * @since 1.0
     */
    private volatile boolean closed;

    /**
     * Creates a new client based on the given configuration.
     *
     * @param config the shared configuration instance
     */
    public XTralClient(XTralConfiguration<?> config) {
        Objects.requireNonNull(config);
        configuration = config;
        beanCache.put(config.getReferenceType().getName(), config.getReference());

        String base = config.getReferenceType().getPackageName();
        resolveClasses(ClassLoader.getSystemClassLoader(), base);
    }

    /**
     * Closes this client and releases any system resources associated
     * with it. If the client is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }

        for (Connection<?> connection : getConnections()) {
            if (!connection.isClosed()) {
                connection.close();
            }
        }
        cache.clear();
        closed = true;
    }

    /**
     * Creates a new {@link Connection} object and adds all methods annotated
     * with defined handler annotations to the connection.
     *
     * @param <T> the connection type
     * @return the newly created connection
     * @throws IOException if an I/O error occurs
     */
    public synchronized <T extends Connection<?>> T openConnection() throws IOException {
        if (isClosed()) {
            throw new IOException("Client is closed");
        }
        ConnectionFactory<?> factory = getConfiguration().getConnectionFactory();
        if (factory == null) {
            throw new IOException("ConnectionFactory == null");
        }

        Connection<?> connection = factory.createConnection();
        connection.setConfiguration(getConfiguration());
        // filter all agents and optionally create instances of the given
        // agent classes.
        Object[] agents = components.stream()
                                    .filter(Agent.class::isAssignableFrom)
                                    .map(this::createHandler)
                                    .toArray();

        for (Object agent : agents) {
            // iterate over all possible methods
            for (Method target : agent.getClass().getMethods()) {
                if (target.isSynthetic() || Modifier.isStatic(target.getModifiers())
                        || Modifier.isAbstract(target.getModifiers())) {
                    continue;
                }

                for (Annotation a0 : target.getAnnotations()) {
                    // Handler annotations must be annotated with IncludedHandler
                    // to get added by this routine.
                    Class<?> type = a0.annotationType();
                    if (type.isAnnotationPresent(IncludedHandler.class)) {
                        CallableHandler handler =
                                createCallableHandler(agent, target, a0);

                        linkHandler(a0, connection, handler);
                    }
                }
            }
        }

        cache.add(connection);
        //noinspection unchecked
        return (T) connection;
    }

    /**
     * Tries to start the client by executing a method annotated with the
     * {@link Client.Entry} annotation.
     *
     * @param args the method arguments
     * @throws Exception if an error occurs
     */
    public synchronized void start(Object... args) throws Exception {
        Objects.requireNonNull(getConfiguration());

        Object ref = getConfiguration().getReference();
        for (Method method : ref.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Client.Entry.class)) {
                continue;
            }
            method.setAccessible(true);
            try {
                Object[] nargs = new Object[args.length + 1];
                System.arraycopy(args, 0, nargs, 1, args.length);
                nargs[0] = this;

                method.invoke(ref, nargs);
            } catch (InvocationTargetException e) {
                throw (Exception) e.getTargetException();
            }
            break;
        }
    }

    /**
     * Closes the connection only if it is contained in the cached
     * connections.
     *
     * @param connection the connection to close
     * @throws IOException if an I/O error occurs
     */
    public synchronized void close(Connection<?> connection) throws IOException {
        if (cache.contains(connection)) {
            connection.close();
            cache.remove(connection);
        }
    }

    /**
     * Returns the stored {@link XTralConfiguration} object.
     *
     * @return The stored {@link XTralConfiguration} object.
     */
    public XTralConfiguration<?> getConfiguration() {
        return configuration;
    }

    /**
     * Returns whether this client has been closed.
     *
     * @return {@code true} if this client object has been closed;
     *         {@code false} otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Removes all closed connections and returns the list afterwards.
     *
     * @return a list of cached connections
     */
    public synchronized List<Connection<?>> getConnections() {
        cache.removeIf(Connection::isClosed);
        return cache;
    }

    // PackageScanner
    private synchronized void resolveClasses(ClassLoader cl, String pkg) {
        if (pkg == null || pkg.isEmpty()) return;

        String      base   = pkg.replaceAll("\\.", "/");
        InputStream stream = cl.getResourceAsStream(base);

        if (stream == null) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            while (reader.ready()) {
                String   clsName = reader.readLine();
                Class<?> cls;
                if (clsName == null || !isClass(clsName)) {
                    if (isPackage(clsName)) {
                        String nextPkg = String.join(".", pkg, clsName);
                        resolveClasses(cl, nextPkg);
                    }
                    continue;
                }

                try {
                    clsName = clsName.split("\\.")[0];
                    cls     = Class.forName(String.join(".", pkg, clsName));
                } catch (ClassNotFoundException s) {
                    continue;
                }

                if (cls.getDeclaredClasses().length != 0) {
                    components.addAll(Arrays.asList(cls.getDeclaredClasses()));
                }
                components.add(cls);
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private boolean isClass(String clsName) {
        return clsName.endsWith("class") && !clsName.contains("package-info");
    }

    private boolean isPackage(String name) {
        return name != null && !name.contains("package-info");
    }

    private Object createHandler(Class<?> cls) {
        try {
            String name = cls.getName();
            if (beanCache.containsKey(name)) {
                return beanCache.get(name);
            }
            Object o = cls.getDeclaredConstructor().newInstance();
            beanCache.put(name, o);
            return o;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private CallableHandler createCallableHandler(Object agent, Method m, Annotation a0) {
        return new CallableHandler(a0, agent, m, getConfiguration().getExecutorService());
    }

    private void linkHandler(Annotation a0, Connection<?> connection, CallableHandler handler) {
        Class<? extends Annotation> type    = a0.annotationType();
        boolean                     asFirst = false;
        try {
            for (Method method : a0.annotationType().getDeclaredMethods()) {
                if (method.getName().equals("addFirst")) {
                    asFirst = (boolean) method.invoke(a0);
                } else if (method.getName().equals("addLast")) {
                    if ((boolean) method.invoke(a0)) {
                        asFirst = false;
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            // ignore
        }
        if (asFirst) {
            connection.addFirst(type, handler);
        } else {
            connection.addLast(type, handler);
        }
    }
}
