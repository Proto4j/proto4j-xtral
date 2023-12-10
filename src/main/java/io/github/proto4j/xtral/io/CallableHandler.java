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

package io.github.proto4j.xtral.io;//@date 17.09.2022

import io.github.proto4j.xtral.annotation.IncludedHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public final class CallableHandler {

    private final Object lock = new Object();

    private final Object agent;
    private final Method target;

    private final ExecutorService service;

    private boolean sync;
    private boolean parallel;

    public static CallableHandler getInstance(Object src,  String name, Class<?>... argTypes)
        throws NullPointerException, ReflectiveOperationException {
        return getInstance(src, null, name, argTypes);
    }

    public static CallableHandler getInstance(Object src, ExecutorService service, String name, Class<?>... argTypes)
        throws NullPointerException, ReflectiveOperationException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(name);

        Method method = src.getClass().getMethod(name, argTypes);
        Annotation a0 = null;
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(IncludedHandler.class)) {
                a0 = annotation;
                break;
            }
        }

        Objects.requireNonNull(a0);
        return new CallableHandler(a0, src, method, service);
    }

    public CallableHandler(Annotation annotation, Object agent, Method target, ExecutorService service) {
        this.agent   = agent;
        this.target  = target;
        this.service = service;

        try {
            Method m = annotation.annotationType().getDeclaredMethod("sync");
            if (m.getReturnType() == boolean.class) {
                sync = (boolean) m.invoke(annotation);
            }

            m = annotation.annotationType().getDeclaredMethod("parallel");
            if (m.getReturnType() == boolean.class) {
                parallel = (boolean) m.invoke(annotation);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Object invoke(Object... args) throws Exception {
        if (isSync()) {
            synchronized (lock) {
                target.setAccessible(true);
            }
        } else {
            target.setAccessible(true);
        }

        Class<?>[] types = target.getParameterTypes();
        Object[] values = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            for (Object arg : args) {
                if (type.isAssignableFrom(arg.getClass())) {
                    values[i] = arg;
                    break;
                }
            }
        }

        if (target.getReturnType() == void.class) {
            if (isParallel()) {
                service.submit(() -> target.invoke(agent, values)).get();
            } else {
                target.invoke(agent, values);
            }
            return args[0];
        } else {
            if (isParallel()) {
                return service.submit(() -> target.invoke(agent, values)).get();
            }
            return target.invoke(agent, values);
        }
    }

    public boolean isSync() {
        return sync;
    }

    public boolean isParallel() {
        return parallel;
    }
}
