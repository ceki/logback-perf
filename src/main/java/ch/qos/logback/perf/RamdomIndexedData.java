package ch.qos.logback.perf;

// Inspired by http://bad-concurrency.blogspot.ch/2014/12/the-uncanny-valley-of-l3-cache.html#more
// by  Michael Barker. See also from https://github.com/mikeb01/qconsf2014 

import java.util.Random;

public class RamdomIndexedData {

    int[] values;
    int[] indexes;

    public void setup(int powerOf2) {
        int size = 1 << powerOf2;

        values = new int[size];
        indexes = new int[size];

        for (int i = 0; i < values.length; i++) {
            values[i] = i;
            indexes[i] = i;
        }

        shuffleArray(indexes);
    }

    private static void shuffleArray(int[] ar) {
        final Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            final int index = rnd.nextInt(i + 1);
            // Simple swap
            final int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

}
