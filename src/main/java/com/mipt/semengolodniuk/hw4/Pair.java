package com.mipt.semengolodniuk.hw4;

public class Pair<K, V> {
    private K left;
    private V right;

    public Pair(K left, V right) {
        this.left = left;
        this.right = right;
    }

    public K getLeft() { return left; }
    public V getRight() { return right; }
    public void setLeft(K left) { this.left = left; }
    public void setRight(V right) { this.right = right; }

    public Pair<V, K> swap() {
        return new Pair<>(right, left);
    }

    @Override
    public String toString() {
        return "Pair[" + left + "," + right + "]";
    }
}
