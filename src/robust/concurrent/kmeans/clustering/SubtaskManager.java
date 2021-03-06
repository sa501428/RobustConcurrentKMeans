/*
 *
 * Original copyright:
 * 2011 The ARIES Consortium and integratedmodelling.org
 *
 * Original file is part of Thinklab under the terms of GPLv3+.
 * It is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * You should have received a copy of the License.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Code modified by Muhammad Saad Shamim in 2021
 * See README for details
 *
 */

package robust.concurrent.kmeans.clustering;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * The class which manages the SMT-adapted subtasks.
 */
public class SubtaskManager {


    // A Barrier to wait on multiple Workers to finish up the current task.
    // In single-processor mode, there is no need for a barrier, so it
    // is not set.
    public static CyclicBarrier mBarrier;
    // The executor that runs the Workers.
    // When in multiple processor mode, this is a ThreadPoolExecutor
    // with a fixed number of threads. In single-processor mode, it's
    // a simple implementation that calls the single worker's run
    // method directly.
    private final Executor mExecutor;
    // The worker objects which implement Runnable.
    private final Worker[] mWorkers;
    // True if the at least one of the Workers is doing something.
    private boolean mWorking;

    /**
     * Constructor
     *
     * @param numThreads the number of worker threads to be used for
     *                   the subtasks.
     */
    SubtaskManager(int numThreads, boolean useKMedians) {

        if (numThreads <= 0) {
            throw new IllegalArgumentException("number of threads <= 0: "
                    + numThreads);
        }

        int coordCount = RobustConcurrentKMeans.mCoordinates.length;

        // There would be no point in having more workers than
        // coordinates, since some of the workers would have nothing
        // to do.
        if (numThreads > coordCount) {
            numThreads = Math.max(coordCount, 1);
        }

        // Create the workers.
        mWorkers = new Worker[numThreads];

        // To hold the number of coordinates for each worker.
        int[] coordsPerWorker = new int[numThreads];

        // Initialize with the base amounts.
        Arrays.fill(coordsPerWorker, coordCount / numThreads);

        // There may be some leftovers, since coordCount may not be
        // evenly divisible by numWorkers. Add a coordinate to each
        // until all are covered.
        int leftOvers = coordCount - numThreads * coordsPerWorker[0];
        for (int i = 0; i < leftOvers; i++) {
            coordsPerWorker[i]++;
        }

        int startCoord = 0;
        // Instantiate the workers.
        for (int i = 0; i < numThreads; i++) {
            // Each worker needs to know its starting coordinate and the number of
            // coordinates it handles.
            mWorkers[i] = new Worker(startCoord, coordsPerWorker[i], useKMedians);
            startCoord += coordsPerWorker[i];
        }

        if (numThreads == 1) { // Single-processor mode.

            // Create a simple executor that directly calls the single
            // worker's run method.  Do not set the barrier.
            mExecutor = runnable -> {
                if (!Thread.interrupted()) {
                    runnable.run();
                } else {
                    throw new RejectedExecutionException();
                }
            };

        } else { // Multiple-processor mode.

            // Need the barrier to notify the controlling thread when the
            // Workers are done.
            // Method called after all workers have called await() on the
            // barrier.  The call to workersDone()
            // unblocks the controlling thread.
            mBarrier = new CyclicBarrier(numThreads, this::workersDone);

            // Set the executor to a fixed thread pool with
            // threads that do not time out.
            mExecutor = Executors.newFixedThreadPool(numThreads);
        }
    }

    /**
     * Make the cluster assignments.
     */
    void makeAssignments() {
        Worker.mDoing = Worker.MAKING_ASSIGNMENTS;
        work();
    }

    /**
     * Compute the distances between the coordinates and those centers with
     * update flags.
     */
    void computeDistances() {
        Worker.mDoing = Worker.COMPUTING_DISTANCES;
        work();
    }

    /**
     * Perform the current subtask, waiting until all the workers
     * finish their part of the current task before returning.
     *
     * @return true if the subtask succeeded.
     */
    private boolean work() {
        boolean ok = false;
        // Set the working flag to true.
        mWorking = true;
        try {
            if (mBarrier != null) {
                // Resets the barrier so it can be reused if
                // this is not the first call to this method.
                mBarrier.reset();
            }
            // Now execute the run methods on the Workers.
            for (Worker mWorker : mWorkers) {
                mExecutor.execute(mWorker);
            }
            if (mBarrier != null) {
                // Block until the workers are done.  The barrier
                // triggers the unblocking.
                waitOnWorkers();
                // If the isBroken() method of the barrier returns false,
                // no problems.
                ok = !mBarrier.isBroken();
            } else {
                // No barrier, so the run() method of a single worker
                // was called directly and everything must have worked
                // if we made it here.
                ok = true;
            }
        } catch (RejectedExecutionException ree) {
            // Possibly thrown by the executor.
        } finally {
            mWorking = false;
        }
        return ok;
    }

    /**
     * Called from work() to put the controlling thread into
     * wait mode until the barrier calls workersDone().
     */
    private synchronized void waitOnWorkers() {
        // It is possible for the workers to have finished so quickly that
        // workersDone() has already been called.  Since workersDone() sets
        // mWorking to false, check this flag before going into wait mode.
        // Not doing so could result in hanging the SubtaskManager.
        while (mWorking) {
            try {
                // Blocks until workersDone() is called.
                wait();
            } catch (InterruptedException ie) {
                // mBarrier.isBroken() will return true.
                break;
            }
        }
    }

    /**
     * Notifies the controlling thread that it can come out of
     * wait mode.
     */
    private synchronized void workersDone() {
        // If this gets called before waitOnWorkers(), setting this
        // to false prevents waitOnWorkers() from entering a
        // permanent wait.
        mWorking = false;
        notifyAll();
    }

    /**
     * Shutdown the thread pool when k-means is finished.
     */
    void shutdown() {
        if (mExecutor instanceof ThreadPoolExecutor) {
            shutdownAndAwaitTermination((ThreadPoolExecutor) mExecutor);
        }
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(10, TimeUnit.MINUTES)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(10, TimeUnit.MINUTES))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            System.err.println("Thread Interruption");
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the number of cluster assignment changes made in the
     * previous call to makeAssignments().
     */
    int numberOfMoves() {
        // Sum the contributions from the workers.
        int moves = 0;
        for (Worker mWorker : mWorkers) {
            moves += mWorker.numberOfMoves();
        }
        return moves;
    }
}