package robust.concurrent.kmeans.clustering;

public class RobustConcurrentKMedians extends RobustConcurrentKMeans {
    /**
     * Constructor that uses the return from
     * Runtime.getRuntime().availableProcessors() as the number
     * of threads for time-consuming steps.
     *
     * @param coordinates   two-dimensional array containing the coordinates to be clustered.
     * @param k             the number of desired clusters.
     * @param maxIterations the maximum number of clustering iterations.
     * @param randomSeed    seed used with the random number generator.
     */
    public RobustConcurrentKMedians(float[][] coordinates, int k, int maxIterations,
                                    long randomSeed, int medianSkip) {
        super(coordinates, k, maxIterations, randomSeed);
        useKMedians = true;
        if (medianSkip > 1) {
            super.medianSkip = medianSkip;
        }
    }
}
