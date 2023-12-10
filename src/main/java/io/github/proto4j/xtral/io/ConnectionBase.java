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

package io.github.proto4j.xtral.io; //@date 18.09.2022

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The base class for connections that use the {@link HandlerList} for
 * storing the {@link CallableHandler} objects.
 *
 * @param <S> the socket type
 * @see HandlerList
 */
public abstract class ConnectionBase<S> extends Connection<S> {

    /**
     * All handlers are stored in a {@link HandlerList} that is mapped to a
     * specific annotation type.
     */
    private final ConcurrentMap<Class<?>, HandlerList> handlerCache =
            new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     *
     * @param cls {@inheritDoc}
     * @param <A> {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException   {@inheritDoc}
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public <A extends Annotation> CallableHandler[] getHandlers(Class<A> cls)
            throws NullPointerException, NoSuchElementException {
        Objects.requireNonNull(cls);

        if (!handlerCache.containsKey(cls)) {
            throw new NoSuchElementException(cls.getName() + " not found");
        }

        HandlerList list = handlerCache.get(cls);
        if (list.isEmpty()) {
            return new CallableHandler[0];
        }
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     *
     * @param cls {@inheritDoc}
     * @param handler {@inheritDoc}
     * @param <A> {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public <A extends Annotation> void addFirst(Class<A> cls, CallableHandler handler)
            throws NullPointerException {
        Objects.requireNonNull(cls);
        Objects.requireNonNull(handler);

        HandlerList list = getList(cls);
        list.add(0, handler);
    }

    /**
     * {@inheritDoc}
     *
     * @param cls {@inheritDoc}
     * @param handler {@inheritDoc}
     * @param <A> {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public <A extends Annotation> void addLast(Class<A> cls, CallableHandler handler) throws NullPointerException {
        Objects.requireNonNull(cls);
        Objects.requireNonNull(handler);

        HandlerList list = getList(cls);
        list.add(handler);
    }

    /**
     * {@inheritDoc}
     *
     * @param cls {@inheritDoc}
     * @param args {@inheritDoc}
     * @param <A> {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected synchronized <A extends Annotation> Object notifyChange(Class<A> cls, Object... args) {
        Objects.requireNonNull(cls);

        HandlerList list = getList(cls);
        if (list.isEmpty()) {
            // The provided arguments should contain at least one value and
            // if so, the first argument will be the object that can be
            // modified.
            return args.length >= 1 ? args[0] : null;
        }

        try {
            for (CallableHandler handler : list) {
                Object next = handler.invoke(args);
                args[0] = next;
            }
        } catch (Exception e) {
            // The try-catch block is defined outside the for-loop, because
            // we don't want to notify handlers even after one of them throws
            // an exception (could be a malformed Object).
            throw new IllegalCallerException(e);
        }
        // The same as above: the first argument will be the object that can
        // be modified.
        return args.length >= 1 ? args[0] : null;
    }

    protected synchronized final HandlerList getList(Class<?> cls) {
        assert cls != null;
        HandlerList list = handlerCache.get(cls);
        // The HandlerList object may be null if no mapping exists for the
        // given annotation class. Therefore, a new list object is created
        // and added to the cache.
        if (list == null) {
            list = new HandlerList();
            handlerCache.put(cls, list);
        }
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException {@inheritDoc}
     */
    @Override
    public synchronized void close() throws IOException {
        if (isClosed()) {
            return;
        }
        super.close();
        // Release all stored handlers, so they can be removed by the
        // internal garbage collector.
        for (HandlerList list : handlerCache.values()) {
            list.clear();
        }
        handlerCache.clear();
    }

    /**
     * An ordered collection of {@code CallableHandler} objects.
     * <p>
     * This class implements the {@link Iterable} interface, therefore it can
     * be used in the following situation:
     * <pre>{@code
     * HandlerList list = new HandlerList();
     * for (CallableHandler handler : list) {
     *     //...
     * }
     * }</pre>
     *
     * @since 1.0
     */
    public static final class HandlerList implements Iterable<CallableHandler> {
        private final List<CallableHandler> list =
                Collections.synchronizedList(new CopyOnWriteArrayList<>());

        /**
         * Indicates whether some other object is "equal to" this one.
         *
         * @param obj the reference object with which to compare.
         * @return {@code true} if this object is the same as the obj
         *         argument; {@code false} otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HandlerList) {
                return obj == this || ((HandlerList) obj).list.equals(this.list);
            }
            return false;
        }

        /**
         * Returns the number of elements in this list.  If this list contains
         * more than {@code Integer.MAX_VALUE} elements, returns
         * {@code Integer.MAX_VALUE}.
         *
         * @return the number of elements in this list
         */
        public int size() {
            return list.size();
        }

        /**
         * Returns {@code true} if this list contains no elements.
         *
         * @return {@code true} if this list contains no elements
         */
        public boolean isEmpty() {
            return list.isEmpty();
        }

        /**
         * Returns an array containing all the elements in this list in
         * proper sequence (from first to last element); the runtime type of
         * the returned array is that of the specified array.  If the list fits
         * in the specified array, it is returned therein.  Otherwise, a new
         * array is allocated with the runtime type of the specified array and
         * the size of this list.
         *
         * <p>If the list fits in the specified array with room to spare (i.e.,
         * the array has more elements than the list), the element in the array
         * immediately following the end of the list is set to {@code null}.
         * (This is useful in determining the length of the list <i>only</i> if
         * the caller knows that the list does not contain any null elements.)
         *
         * <p>Like the {@link #toArray()} method, this method acts as bridge between
         * array-based and collection-based APIs.  Further, this method allows
         * precise control over the runtime type of the output array, and may,
         * under certain circumstances, be used to save allocation costs.
         *
         * <p>Suppose {@code x} is a list known to contain only strings.
         * The following code can be used to dump the list into a newly
         * allocated array of {@code String}:
         *
         * <pre>{@code
         *     String[] y = x.toArray(new String[0]);
         * }</pre>
         * <p>
         * Note that {@code toArray(new Object[0])} is identical in function to
         * {@code toArray()}.
         *
         * @return an array containing the elements of this list
         * @throws ArrayStoreException  if the runtime type of the specified array
         *                              is not a supertype of the runtime type of every element in
         *                              this list
         * @throws NullPointerException if the specified array is null
         */
        public CallableHandler[] toArray() {
            return list.toArray(CallableHandler[]::new);
        }

        /**
         * Appends the specified element to the end of this list (optional
         * operation).
         *
         * <p>Lists that support this operation may place limitations on what
         * elements may be added to this list.  In particular, some
         * lists will refuse to add null elements, and others will impose
         * restrictions on the type of elements that may be added.  List
         * classes should clearly specify in their documentation any restrictions
         * on what elements may be added.
         *
         * @param callableHandler element to be appended to this list
         * @throws UnsupportedOperationException if the {@code add} operation
         *                                       is not supported by this list
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws NullPointerException          if the specified element is null and this
         *                                       list does not permit null elements
         * @throws IllegalArgumentException      if some property of this element
         *                                       prevents it from being added to this list
         */
        public void add(CallableHandler callableHandler) {
            list.add(callableHandler);
        }

        /**
         * Removes the first occurrence of the specified element from this list,
         * if it is present (optional operation).  If this list does not contain
         * the element, it is unchanged.  More formally, removes the element with
         * the lowest index {@code i} such that
         * {@code Objects.equals(o, get(i))}
         * (if such an element exists).  Returns {@code true} if this list
         * contained the specified element (or equivalently, if this list changed
         * as a result of the call).
         *
         * @param o element to be removed from this list, if present
         * @return {@code true} if this list contained the specified element
         * @throws ClassCastException            if the type of the specified element
         *                                       is incompatible with this list
         *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
         * @throws NullPointerException          if the specified element is null and this
         *                                       list does not permit null elements
         *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
         * @throws UnsupportedOperationException if the {@code remove} operation
         *                                       is not supported by this list
         */
        public boolean remove(CallableHandler o) {
            return list.remove(o);
        }

        /**
         * Returns the element at the specified position in this list.
         *
         * @param index index of the element to return
         * @return the element at the specified position in this list
         * @throws IndexOutOfBoundsException if the index is out of range
         *                                   ({@code index < 0 || index >= size()})
         */
        public CallableHandler get(int index) {
            return list.get(index);
        }

        /**
         * Replaces the element at the specified position in this list with the
         * specified element (optional operation).
         *
         * @param index index of the element to replace
         * @param element element to be stored at the specified position
         * @return the element previously at the specified position
         * @throws UnsupportedOperationException if the {@code set} operation
         *                                       is not supported by this list
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws NullPointerException          if the specified element is null and
         *                                       this list does not permit null elements
         * @throws IllegalArgumentException      if some property of the specified
         *                                       element prevents it from being added to this list
         * @throws IndexOutOfBoundsException     if the index is out of range
         *                                       ({@code index < 0 || index >= size()})
         */
        public CallableHandler set(int index, CallableHandler element) {
            return list.set(index, element);
        }

        /**
         * Inserts the specified element at the specified position in this list
         * (optional operation).  Shifts the element currently at that position
         * (if any) and any subsequent elements to the right (adds one to their
         * indices).
         *
         * @param index index at which the specified element is to be inserted
         * @param element element to be inserted
         * @throws UnsupportedOperationException if the {@code add} operation
         *                                       is not supported by this list
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws NullPointerException          if the specified element is null and
         *                                       this list does not permit null elements
         * @throws IllegalArgumentException      if some property of the specified
         *                                       element prevents it from being added to this list
         * @throws IndexOutOfBoundsException     if the index is out of range
         *                                       ({@code index < 0 || index > size()})
         */
        public void add(int index, CallableHandler element) {
            list.add(index, element);
        }

        /**
         * Returns an iterator over elements of type {@code T}.
         *
         * @return an Iterator.
         */
        @Override
        public Iterator<CallableHandler> iterator() {
            return list.iterator();
        }

        /**
         * Removes all the elements from this list (optional operation).
         * The list will be empty after this call returns.
         *
         * @throws UnsupportedOperationException if the {@code clear} operation
         *                                       is not supported by this list
         */
        public void clear() {
            list.clear();
        }
    }

}

