import java.util.Arrays;

public class TestedClass {
    public int[] getArrayPartAfterValue(int value, int[] inputArray) {
        for (int index = inputArray.length - 1; index >= 0; index--) {
            if (inputArray[index] == value) {
                return Arrays.copyOfRange(inputArray, index + 1, inputArray.length);
            }
        }
        throw new RuntimeException("Входящий массив не содержит цифры " + value);
    }

    public boolean checkArrayItems(int[] array, int... allowedValues) {
        boolean containsAllAllowedValues = true;
        boolean notContainsOtherValues = true;

        for (int neededValue : allowedValues) {
            containsAllAllowedValues &= Arrays.stream(array).anyMatch(value -> value == neededValue);
        }

        for (int value : array) {
            notContainsOtherValues &= Arrays.stream(allowedValues).anyMatch(allowedValue -> allowedValue == value);
        }

        return containsAllAllowedValues && notContainsOtherValues;
    }
}
