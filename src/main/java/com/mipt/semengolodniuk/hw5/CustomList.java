package com.mipt.semengolodniuk.hw5;

import java.util.Iterator;

public interface CustomList<E> extends Iterable<E> {
    int size();
    boolean isEmpty();
    void add(E e);
    E get(int index);
    E set(int index, E e);
    E remove(int index);
    boolean remove(Object o);
    boolean contains(Object o);
    void clear();
    Iterator<E> iterator();
}
