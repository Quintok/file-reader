package com.company;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class Main {

    public static final int CLASSIFICATION_LIMIT = 5;
    public static final int MIN_CLASSIFICATION_SIZE = 2;
    public static final int ROW_COUNT = 1000 * 1000 * 1000; // 1e^9
    public static final int LONG_SIZE = 64;

    public static void main(String[] args) {

        if(args.length == 1)
            unsafeByteSetXTab();
        else
            safeByteSetXTab();

    }

    private static void safeByteSetXTab() {
        List<BitSet[]> classifications = new ArrayList<BitSet[]>();

        // Generate classifications
        for(int i = 0; i < CLASSIFICATION_LIMIT; i++) {
            BitSet[] classificationValues = new BitSet[MIN_CLASSIFICATION_SIZE];
            for(int j = 0; j < MIN_CLASSIFICATION_SIZE; j++) {
                long[] value = createValue();
                classificationValues[j] = BitSet.valueOf(value);

            }
            classifications.add(classificationValues);
        }

        Random random = new Random();
        List<List<BitSet>> allSelectedValues = new ArrayList<List<BitSet>>();
        for(int i = 0; i < 100; i++) {
            List<BitSet> selectedValues = new ArrayList<BitSet>();
            for(int j = 0; j < CLASSIFICATION_LIMIT; j++) {
                int value = random.nextInt(classifications.get(j).length);
                BitSet selectedValue = classifications.get(j)[value];
                selectedValues.add(selectedValue);
            }
            allSelectedValues.add(selectedValues);
        }

        System.out.println("Warmup Safe");
        for(int i = 0; i < 100; i++) {
            if(i % 10 == 0) {
                System.out.println("So far: " + i);
            }
            xtabSafe(allSelectedValues.get(i));
        }

        System.out.println("XTab Safe");
        long currTime = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
            if(i % 10 == 0) {
                System.out.println("So far: " + i);
                continue;
            }
            xtabSafe(allSelectedValues.get(i));
        }
        long time = System.currentTimeMillis() - currTime;
        System.out.println("Time in ms: " + time);
    }

    private static void unsafeByteSetXTab() {
        List<UnsafeByteSet[]> classifications = new ArrayList<UnsafeByteSet[]>();

        // Generate classifications
        for(int i = 0; i < CLASSIFICATION_LIMIT; i++) {
            UnsafeByteSet[] classificationValues = new UnsafeByteSet[MIN_CLASSIFICATION_SIZE];
            for(int j = 0; j < MIN_CLASSIFICATION_SIZE; j++) {
                long[] value = createValue();
                classificationValues[j] = new UnsafeByteSet(value);

            }
            classifications.add(classificationValues);
        }

        Random random = new Random();
        List<List<UnsafeByteSet>> allSelectedValues = new ArrayList<List<UnsafeByteSet>>();
        for(int i = 0; i < 100; i++) {
            List<UnsafeByteSet> selectedValues = new ArrayList<UnsafeByteSet>();
            for(int j = 0; j < CLASSIFICATION_LIMIT; j++) {
                int value = random.nextInt(classifications.get(j).length);
                UnsafeByteSet selectedValue = classifications.get(j)[value];
                selectedValues.add(selectedValue);
            }
            allSelectedValues.add(selectedValues);
        }

        System.out.println("warmup");
        for(int i = 0; i < 100; i++) {
            if(i % 10 == 0) {
                System.out.println("So far: " + i);
            }
            xtab(allSelectedValues.get(i));
        }

        System.out.println("XTab Unsafe");
        long currTime = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
            if(i % 10 == 0) {
                System.out.println("So far: " + i);
                continue;
            }
            xtab(allSelectedValues.get(i));
        }
        long time = System.currentTimeMillis() - currTime;
        System.out.println("Time in ms: " + time);
    }

    private static long xtab(List<UnsafeByteSet> selectedValues) {

        UnsafeByteSet result = selectedValues.get(0);
        for(int i = 1; i < selectedValues.size(); i++) {
            result.and(selectedValues.get(i));
        }

        return result.cardinality();
    }

    private static long xtabSafe(List<BitSet> selectedValues) {
        BitSet result = selectedValues.get(0);
        for(int i = 1; i < selectedValues.size(); i++) {
            result.and(selectedValues.get(i));
        }

        return result.cardinality();
    }

    // Make a random long[] full of noise
    private static long[] createValue() {
        long[] result = new long[ROW_COUNT / LONG_SIZE];
        Random random = new Random();
        for(int i = 0; i < ROW_COUNT / LONG_SIZE; i++)
            result[i] = random.nextLong();
        return result;
    }
}
