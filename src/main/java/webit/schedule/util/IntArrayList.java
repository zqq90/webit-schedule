// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.util;

import java.util.Arrays;

public class IntArrayList {

    private int[] array;
    private int size;

    public IntArrayList() {
        this(10);
    }

    public IntArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Invalid capacity: " + initialCapacity);
        }
        array = new int[initialCapacity];
        size = 0;
    }

    public int[] toArray() {
        final int[] result;
        System.arraycopy(array, 0, result = new int[size], 0, size);
        return result;
    }

    public int[] toSortedArray() {
        final int[] result;
        Arrays.sort(result = toArray());
        return result;
    }

    public int get(int index) {
        if (index >= 0 && index < size) {
            return array[index];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public int size() {
        return size;
    }

    protected void add(int element) {
        ensureCapacity(size + 1);
        array[size++] = element;
    }

    public void addIfAbsent(int element) {
        if (this.contains(element) == false) {
            add(element);
        }
    }

    public void clear() {
        size = 0;
    }

    public boolean contains(int data) {
        for (int i = 0, len = size; i < len; i++) {
            if (array[i] == data) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(int data) {
        for (int i = 0; i < size; i++) {
            if (array[i] == data) {
                return i;
            }
        }
        return -1;
    }

    public void ensureCapacity(int mincap) {
        if (mincap > array.length) {
            int newcap = ((array.length * 3) >> 1) + 1;
            int[] olddata = array;
            array = new int[newcap < mincap ? mincap : newcap];
            System.arraycopy(olddata, 0, array, 0, size);
        }
    }
}
