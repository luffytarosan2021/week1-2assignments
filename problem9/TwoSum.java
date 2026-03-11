import java.util.*;

public class TwoSum {

    public static void twoSum(int[] nums, int target) {

        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {

            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                System.out.println("Pair found at indices: "
                        + map.get(complement) + " and " + i);
                return;
            }

            map.put(nums[i], i);
        }

        System.out.println("No pair found");
    }

    public static void main(String[] args) {

        int[] nums = {500, 300, 200};
        int target = 500;

        twoSum(nums, target);
    }
}