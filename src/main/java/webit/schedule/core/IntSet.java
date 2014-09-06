// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.core;

import java.util.Arrays;

final class IntSet {

    private int[] array;
    private int size;

    IntSet() {
        this(10);
    }

    IntSet(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Invalid capacity: " + initialCapacity);
        }
        array = new int[initialCapacity];
        size = 0;
    }

    int[] toSortedArray() {
        final int[] result = new int[size];
        System.arraycopy(array, 0, result, 0, size);
        Arrays.sort(result);
        return result;
    }

    int get(int index) {
        if (index >= 0 && index < size) {
            return array[index];
        }
        throw new IndexOutOfBoundsException();
    }

    int size() {
        return size;
    }

    void add(int element) {
        if (this.contains(element)) {
            return;
        }
        final int index = this.size++;
        int[] arr = this.array;
        if (index == arr.length) {
            System.arraycopy(arr, 0, arr = this.array = new int[((index * 3) >> 1) + 1], 0, index);
        }
        arr[index] = element;
    }

    boolean contains(int data) {
        for (int i = 0, len = size; i < len; i++) {
            if (array[i] == data) {
                return true;
            }
        }
        return false;
    }
}
