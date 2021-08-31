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
 * Exception thrown when insufficient memory is available to
 * perform an operation.  Designed to be throw before doing
 * something that would cause a <code>java.lang.OutOfMemoryError</code>.
 */
class InsufficientMemoryException extends Exception {

    private static final long serialVersionUID = 72138634L;

    /**
     * Constructor.
     *
     * @param message an explanatory message.
     */
    public InsufficientMemoryException(String message) {
        super(message);
    }

}

