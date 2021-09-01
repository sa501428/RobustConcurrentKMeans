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

/**
 * Defines object which register with implementation of <code>KMeans</code>
 * to be notified of significant events during clustering.
 */
public interface KMeansListener {

    /**
     * A message has been received.
     */
    void kmeansMessage(String message);

    /**
     * KMeans is complete.
     *
     * @param clusters the output of clustering.
     */
    void kmeansComplete(Cluster[] clusters);

    /**
     * An error occurred during KMeans clustering.
     */
    void kmeansError(Throwable t);

}

