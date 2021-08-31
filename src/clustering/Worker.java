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

package clustering;

import metric.DistanceMetric;
import metric.RobustEuclideanDistance;
import metric.RobustManhattanDistance;

import java.util.concurrent.BrokenBarrierException;

/**
 * The class which does the hard work of the subtasks.
 */
public class Worker implements Runnable {

    static final int DOING_NOTHING = 0;
    static final int COMPUTING_DISTANCES = 1;
    static final int MAKING_ASSIGNMENTS = 2;
    // Codes used to identify what step is being done.
    // What the object is currently doing
    public static int mDoing = Worker.DOING_NOTHING;
    // Defines range of coordinates to cover.
    private final int mStartCoord;
    private final int mNumCoords;
    // Number of moves made by this worker in the last call
    // to workerMakeAssignments().  The SubtaskManager totals up
    // this value from all the workers in numberOfMoves().
    private int mMoves;
    private final DistanceMetric euclidean = RobustEuclideanDistance.SINGLETON;
    private final DistanceMetric manhattan = RobustManhattanDistance.SINGLETON;
    private final boolean useKMedians;

    /**
     * Constructor
     *
     * @param startCoord index of the first coordinate covered by
     *                   this Worker.
     * @param numCoords  the number of coordinates covered.
     */
    Worker(int startCoord, int numCoords, boolean useKMedians) {
        mStartCoord = startCoord;
        mNumCoords = numCoords;
        this.useKMedians = useKMedians;
    }

    /**
     * Returns the number of moves this worker made in the last
     * execution of workerMakeAssignments()
     */
    int numberOfMoves() {
        return mMoves;
    }

    /**
     * The run method.  It accesses the SubtaskManager field mDoing
     * to determine what subtask to perform.
     */
    public void run() {
        try {
            switch (mDoing) {
                case COMPUTING_DISTANCES:
                    workerComputeDistances(RobustConcurrentKMeans.mProtoClusters);
                    break;
                case MAKING_ASSIGNMENTS:
                    workerMakeAssignments();
                    break;
            }
        } finally {
            // If there's a barrier, call its await() method.  To ensure it
            // gets done, it's placed in the finally clause.
            if (SubtaskManager.mBarrier != null) {
                try {
                    SubtaskManager.mBarrier.await();
                    // barrier.isBroken() will return true if either of these
                    // exceptions happens, so the SubtaskManager will detect
                    // the problem.
                } catch (InterruptedException | BrokenBarrierException ignored) {
                }
            }
        }

    }

    /**
     * Compute the distances for the covered coordinates
     * to the updated centers.
     */
    private void workerComputeDistances(ProtoCluster[] mProtoClusters) {
        int lim = mStartCoord + mNumCoords;
        for (int i = mStartCoord; i < lim; i++) {
            int numClusters = mProtoClusters.length;
            for (int c = 0; c < numClusters; c++) {
                ProtoCluster cluster = mProtoClusters[c];
                if (cluster.getConsiderForAssignment() && cluster.needsUpdate()) {
                    RobustConcurrentKMeans.mDistanceCache[i][c] = distanceL2Norm(RobustConcurrentKMeans.mCoordinates[i],
                            cluster.getCenter());
                }
            }
        }
    }

    /**
     * Assign each covered coordinate to the nearest cluster.
     */
    private void workerMakeAssignments() {
        mMoves = 0;
        int lim = mStartCoord + mNumCoords;
        for (int i = mStartCoord; i < lim; i++) {
            int c = nearestCluster(i);
            RobustConcurrentKMeans.mProtoClusters[c].add(i);
            if (RobustConcurrentKMeans.mClusterAssignments[i] != c) {
                RobustConcurrentKMeans.mClusterAssignments[i] = c;
                mMoves++;
            }
        }
    }

    /**
     * Compute the euclidean distance between the two arguments.
     */
    private float distanceL2Norm(float[] coord, float[] center) {
        if (useKMedians) {
            return manhattan.distance(coord, center);
        } else {
            return euclidean.distance(coord, center);
        }
    }

    /**
     * Find the nearest cluster to the coordinate identified by
     * the specified index.
     */
    private int nearestCluster(int ndx) {
        int nearest = -1;
        double min = Double.MAX_VALUE;
        int numClusters = RobustConcurrentKMeans.mProtoClusters.length;
        for (int c = 0; c < numClusters; c++) {
            if (RobustConcurrentKMeans.mProtoClusters[c].getConsiderForAssignment()) {
                double d = RobustConcurrentKMeans.mDistanceCache[ndx][c];
                if (d < min) {
                    min = d;
                    nearest = c;
                }
            }
        }
        return nearest;
    }

}