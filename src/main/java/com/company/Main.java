package com.company;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static final int CLASSIFICATION_LIMIT = 5; // fixed! do not change!  Refer to 'xtabSafe' as to why.
    public static final int MIN_CLASSIFICATION_SIZE = 2;
    public static final int ROW_COUNT = 1000 * 1000 * 1000; // 1e^9
    public static final int LONG_SIZE = 64;
    public static final int CLASSIFICATION_BYTE_LENGTH_AS_LONG = ROW_COUNT / LONG_SIZE;

    public static final int CUBE_SIZE = 100;

    public static final int NUM_TRIES = 3;

    public static void main(String[] args) {

        safeByteSetXTab();

    }

    private static void safeByteSetXTab() {
        long[][][] classifications = new long[CLASSIFICATION_LIMIT][MIN_CLASSIFICATION_SIZE][CLASSIFICATION_BYTE_LENGTH_AS_LONG];

        Random random = new Random();

        // Generate classifications
        for(int i = 0; i < CLASSIFICATION_LIMIT; i++) {
            for(int j = 0; j < MIN_CLASSIFICATION_SIZE; j++) {
                for(int k = 0; k < CLASSIFICATION_BYTE_LENGTH_AS_LONG; k++)
                classifications[i][j][k] = random.nextLong();
            }
        }

        int[][][] allSelectedValues = new int[CUBE_SIZE][CLASSIFICATION_LIMIT][1];
        for(int i = 0; i < CUBE_SIZE; i++) {
            for(int j = 0; j < CLASSIFICATION_LIMIT; j++) {
                int value = random.nextInt(classifications[j].length);
                allSelectedValues[i][j][0] = value;
            }
        }

        for(int i = 0; i < CUBE_SIZE; i++) {
            xtabSafe(classifications, allSelectedValues[i]);
        }

        long[] cube = new long[CUBE_SIZE];
        long time = 0L;
        for(int times = 0; times < NUM_TRIES; times++) {
            long currTime = System.currentTimeMillis();
            for(int i = 0; i < CUBE_SIZE; i++) {
                cube[i] = xtabSafe(classifications, allSelectedValues[i]);
            }
            time += System.currentTimeMillis() - currTime;
        }

        System.out.println(Arrays.toString(cube));

        System.out.println("Average time: " + time / NUM_TRIES + " Number of tries: " + NUM_TRIES);
        
    }

    private static long xtabSafe(long[][][] field, int[][] values) {
        int sum = 0;

        // loop through each long in that array.
        for(int j = 0; j < CLASSIFICATION_BYTE_LENGTH_AS_LONG; j++) {
            // well, this is faster than a loop... :(
            sum += Long.bitCount(
                        field[0][values[0][0]][j] &
                        field[1][values[1][0]][j] &
                        field[2][values[2][0]][j] &
                        field[3][values[3][0]][j] &
                        field[4][values[4][0]][j]
            );
        }

        return sum;
    }
}
