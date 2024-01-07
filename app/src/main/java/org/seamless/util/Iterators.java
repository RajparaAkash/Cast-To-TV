package org.seamless.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;


public class Iterators {

    
    public static class Empty<E> implements Iterator<E> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    
    public static class Singular<E> implements Iterator<E> {
        protected int current;
        protected final E element;

        public Singular(E e) {
            this.element = e;
        }

        @Override
        public boolean hasNext() {
            return this.current == 0;
        }

        @Override
        public E next() {
            this.current++;
            return this.element;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    
    public static abstract class Synchronized<E> implements Iterator<E> {
        int nextIndex = 0;
        boolean removedCurrent = false;
        final Iterator<E> wrapped;

        protected abstract void synchronizedRemove(int i);

        public Synchronized(Collection<E> collection) {
            this.wrapped = new CopyOnWriteArrayList(collection).iterator();
        }

        @Override
        public boolean hasNext() {
            return this.wrapped.hasNext();
        }

        @Override
        public E next() {
            this.removedCurrent = false;
            this.nextIndex++;
            return this.wrapped.next();
        }

        @Override
        public void remove() {
            int i = this.nextIndex;
            if (i == 0) {
                throw new IllegalStateException("Call next() first");
            }
            if (this.removedCurrent) {
                throw new IllegalStateException("Already removed current, call next()");
            }
            synchronizedRemove(i - 1);
            this.removedCurrent = true;
        }
    }
}
