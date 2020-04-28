package lesson_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static void main(String[] args) {
        replaceAndDisplayResult(3, 7, new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        System.out.println();
        replaceAndDisplayResult(0, 9, new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"});
        System.out.println();

        convertAndDisplay(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        System.out.println();
        convertAndDisplay(new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"});
    }

    public static <T> void replaceAndDisplayResult(int firstIndex, int secondIndex, T[] array) {
        System.out.println(Arrays.toString(array));
        replaceArrayItems(firstIndex, secondIndex, array);
        System.out.println(Arrays.toString(array));
    }

    public static <T> void convertAndDisplay(T[] array) {
        System.out.println(Arrays.toString(array));
        System.out.println(convertArrayToList1(array));
        System.out.println(convertArrayToList2(array));
    }

    @SafeVarargs
    public static <T> void replaceArrayItems(int firstIndex, int secondIndex, T... array) {
        if (firstIndex < 0 || secondIndex < 0 || firstIndex > array.length - 1 || secondIndex > array.length - 1) {
            throw new RuntimeException("Индекс за пределами массива");
        }

        T buffer = array[secondIndex];
        array[secondIndex] = array[firstIndex];
        array[firstIndex] = buffer;
    }

    @SafeVarargs
    public static <T> List<T> convertArrayToList1(T... array) {
        return Arrays.asList(array);
    }

    @SafeVarargs
    public static <T> List<T> convertArrayToList2(T... array) {
        List<T> result = new ArrayList<>();

        for (T item : array) {
            result.add(item);
        }

        return result;
    }
}
