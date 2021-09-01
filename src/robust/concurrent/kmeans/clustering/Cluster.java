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

/**
 * Class to represent a cluster of coordinates.
 */
public class Cluster {

    // Indices of the member coordinates.
    private final int[] memberIndexes;
    // The cluster center.
    private final float[] center;

    /**
     * Constructor.
     *
     * @param memberIndexes indices of the member coordinates.
     * @param center        the cluster center.
     */
    public Cluster(int[] memberIndexes, float[] center) {
        this.memberIndexes = memberIndexes;
        this.center = center;
    }

    /**
     * Get the member indices.
     *
     * @return an array containing the indices of the member coordinates.
     */
    public int[] getMemberIndexes() {
        return memberIndexes;
    }

    /**
     * Get the cluster center.
     *
     * @return a reference to the cluster center array.
     */
    public float[] getCenter() {
        return center;
    }

    public Cluster getClone() {
        if (memberIndexes != null && center != null) {
            int[] membersCopy = Arrays.copyOf(memberIndexes, memberIndexes.length);
            float[] centerCopy = Arrays.copyOf(center, center.length);
            return new Cluster(membersCopy, centerCopy);
        }
        return null;
    }
}

