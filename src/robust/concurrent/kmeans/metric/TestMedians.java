package robust.concurrent.kmeans.metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestMedians {

    public static void main(String[] args) {

        int[] numValues = new int[]{30000000};


        for (int n : numValues) {
            List<Float> randomNumbers = generateRandomNumbers(n);
            for (int z : new int[]{1, 4}) {
                long time0 = System.nanoTime();
                float result = QuickMedian.fastMedian(randomNumbers, z);
                long time1 = System.nanoTime();
                double elapsed = (time1 - time0) * 1e-9;
                System.out.println("Method " + z + " with " + n + " values\nResult: " + result + "\n Time taken (s): " + elapsed);
            }
        }


    }

    private static List<Float> generateRandomNumbers(int n) {
        List<Float> mist = new ArrayList<>();
        Random generator = new Random();
        for (int k = 0; k < n; k++) {
            if (generator.nextBoolean() && generator.nextBoolean()) {
                mist.add((float) ((70 * generator.nextGaussian())));
            } else {
                mist.add(generator.nextFloat() * 120);
            }
        }
        return mist;
    }
}
