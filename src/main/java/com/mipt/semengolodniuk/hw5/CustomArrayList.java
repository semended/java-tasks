package com.mipt.semengolodniuk.hw5;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomArrayList<E> implements CustomList<E> {
    private static final int DEFAULT_CAPACITY = 8;
    private static final double GROWTH = 1.5;

    private Object[] data;
    private int size;

    public CustomArrayList() {
        this.data = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void add(E e) {
        ensure(size + 1);
        data[size++] = e;
    }

    public E get(int index) {
        check(index);
        return element(index);
    }

    public E set(int index, E e) {
        check(index);
        E prev = element(index);
        data[index] = e;
        return prev;
    }

    public E remove(int index) {
        check(index);
        E prev = element(index);
        int move = size - index - 1;
        if (move > 0) System.arraycopy(data, index + 1, data, index, move);
        data[--size] = null;
        return prev;
    }

    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) {
            if (o == null ? data[i] == null : o.equals(data[i])) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            if (o == null ? data[i] == null : o.equals(data[i])) return true;
        }
        return false;
    }

    public void clear() {
        for (int i = 0; i < size; i++) data[i] = null;
        size = 0;
    }

    public Iterator<E> iterator() { return new It(); }

    private void ensure(int needed) {
        if (needed <= data.length) return;
        int cap = data.length;
        int next = Math.max((int)(cap * GROWTH), needed);
        Object[] nd = new Object[next];
        System.arraycopy(data, 0, nd, 0, size);
        data = nd;
    }

    private void check(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
    }

    @SuppressWarnings("unchecked")
    private E element(int index) { return (E) data[index]; }

    private class It implements Iterator<E> {
        int p;
        public boolean hasNext() { return p < size; }
        public E next() {
            if (p >= size) throw new NoSuchElementException();
            return element(p++);
        }
    }
}
