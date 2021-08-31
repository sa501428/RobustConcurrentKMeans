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

/**
 * Simple K-Means clustering interface.
 */
interface KMeans extends Runnable {

    /**
     * Adds a KMeansListener to be notified of significant happenings.
     *
     * @param l the listener to be added.
     */
    void addKMeansListener(KMeansListener l);

    /**
     * Removes a KMeansListener from the listener list.
     *
     * @param l the listener to be removed.
     */
    void removeKMeansListener(KMeansListener l);

    /**
     * Get the clusters computed by the algorithm.  This method should
     * not be called until clustering has completed successfully.
     *
     * @return an array of Cluster objects.
     */
    Cluster[] getClusters();

}

