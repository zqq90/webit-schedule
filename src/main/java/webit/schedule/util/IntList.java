// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.util;

import java.util.Arrays;

public final class IntList {

    private int[] array;
    private int size;

    public IntList() {
        this(10);
    }

    public IntList(int initialCapacity) {
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
        final int[] result = toArray();
        Arrays.sort(result);
        return result;
    }

    public int get(int index) {
        if (index >= 0 && index < size) {
            return array[index];
        }
        throw new IndexOutOfBoundsException();
    }

    public int size() {
        return size;
    }

    protected void add(int element) {
        final int index = this.size++;
        int[] arr = this.array;
        if (index == arr.length) {
            System.arraycopy(arr, 0, arr = this.array = new int[((index * 3) >> 1) + 1], 0, index);
        }
        arr[index] = element;
    }

    public void addIfAbsent(int element) {
        if (this.contains(element) == false) {
            add(element);
        }
    }

    public boolean contains(int data) {
        for (int i = 0, len = size; i < len; i++) {
            if (array[i] == data) {
                return true;
            }
        }
        return false;
    }
}
