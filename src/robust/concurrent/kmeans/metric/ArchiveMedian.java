package robust.concurrent.kmeans.metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArchiveMedian {

    public static float fastMedianType2(List<Float> coll) {
        float result;
        int n = coll.size() / 2;

        if (coll.size() % 2 == 0)  // even number of items; find the middle two and average them
            result = (nth(coll, n - 1) + nth(coll, n)) / 2f;
        else                      // odd number of items; return the one in the middle
            result = nth(coll, n);
        return result;
    } // median(coll)


    /*****************
     * adapted from https://stackoverflow.com/questions/11955728/how-to-calculate-the-median-of-an-array
     *******************/

    public static float nth(List<Float> coll, int n) {
        if (coll.size() < 20) {
            Collections.sort(coll);
            return coll.get(n);
        }

        float result, pivot;
        List<Float> underPivot = new ArrayList<>();
        List<Float> overPivot = new ArrayList<>();
        int numEqual = 0;

        pivot = getPivot(coll);

        for (float obj : coll) {
            if (obj < pivot) {
                underPivot.add(obj);
            } else if (obj > pivot) {
                overPivot.add(obj);
            } else {
                numEqual++;
            }
        }

        if (n < underPivot.size()) {
            overPivot = null;
            result = nth(underPivot, n);
        } else if (n < underPivot.size() + numEqual) {
            result = pivot;
        } else {
            int diffN = underPivot.size() + numEqual;
            underPivot = null;
            result = nth(overPivot, n - diffN);
        }

        return result;
    }

    private static float getPivot(List<Float> coll) {
        if (coll.size() < 100) {
            return coll.get(coll.size() / 2);
        }

        List<Float> subSampledNumbers = new ArrayList<>();
        int n = coll.size();
        for (int z = 0; z < n; z += (n / 9)) {
            subSampledNumbers.add(coll.get(z));
        }
        Collections.sort(subSampledNumbers);
        return subSampledNumbers.get(subSampledNumbers.size() / 2);
    }


    public static float fastMedianType3(float[] coll) {
        float result;
        int n = coll.length / 2;

        if (coll.length % 2 == 0)  // even number of items; find the middle two and average them
            result = (findKthLargestV3(coll, n - 1) + findKthLargestV3(coll, n)) / 2f;
        else                      // odd number of items; return the one in the middle
            result = findKthLargestV3(coll, n);
        return result;
    } // median(coll)

    /**
     * https://leetcode.com/problems/kth-largest-element-in-an-array/discuss/60300/java-quick-select
     */

    public static float findKthLargestV3(float[] nums, int k) {
        int start = 0, end = nums.length - 1, index = nums.length - k;
        while (start < end) {
            int pivot = partition3(nums, start, end);
            if (pivot < index) start = pivot + 1;
            else if (pivot > index) end = pivot - 1;
            else return nums[pivot];
        }
        return nums[start];
    }

    private static int partition3(float[] nums, int start, int end) {
        int pivot = start;
        float temp;
        while (start <= end) {
            while (start <= end && nums[start] <= nums[pivot]) start++;
            while (start <= end && nums[end] > nums[pivot]) end--;
            if (start > end) break;
            temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
        }
        temp = nums[end];
        nums[end] = nums[pivot];
        nums[pivot] = temp;
        return end;
    }
}
