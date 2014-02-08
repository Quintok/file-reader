package com.company;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeByteSet {
    private static final Unsafe UNSAFE;
    private static final int LONG_SIZE_BYTES = 64/8;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe)field.get(null);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final int pageSize;
    private final long address;
    private final long size;

    public UnsafeByteSet(long setSize) {
        // useful? dunno.
        pageSize = UNSAFE.pageSize();

        // allocate memory for the byte set set.
        address = UNSAFE.allocateMemory(setSize * LONG_SIZE_BYTES);
        size = setSize;
    }

    public UnsafeByteSet(long[] value) {
        this(value.length);

        for(int i = 0; i < value.length; i++) {
            setByte(value[i], i);
        }
    }

    public void setByte(long value, int index) {
        long offset = address + (index * LONG_SIZE_BYTES);
        UNSAFE.putLong(offset, value);
    }

    public long getByteUnsafe(int index) {
        return UNSAFE.getLong(address + (index * LONG_SIZE_BYTES));
    }

    // Ands against itself and in the process destroys itself
    public void and(UnsafeByteSet other) {
        if(other.size != this.size) {
            throw new IllegalArgumentException("both sets must have the same size");
        }

        for(int i = 0; i < size; i++) {
            setByte(getByteUnsafe(i) & other.getByteUnsafe(i), i);
        }
    }

    public long cardinality() {
        long result = 0L;
        for(int i = 0; i < size; i++) {
            Long.bitCount(getByteUnsafe(i));
        }
        return result;
    }

    public void destroy() {
        UNSAFE.freeMemory(address);
    }
}
