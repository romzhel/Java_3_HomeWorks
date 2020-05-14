import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestedClassTest1 {
    private static TestedClass testedClass;
    private int lastItemValue;
    private int[] inputArray;
    private int[] result;
    private int[] exceptionTestArray;

    public TestedClassTest1(int lastItemValue, int[] inputArray, int[] resultArray, int[] exceptionTestArray) {
        this.lastItemValue = lastItemValue;
        this.inputArray = inputArray;
        this.result = resultArray;
        this.exceptionTestArray = exceptionTestArray;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {4, new int[]{1, 2, 3, 4, 5, 6}, new int[]{5, 6}, new int[]{1, 2, 3, 5}},
                {4, new int[]{4, 1, 2, 4, 3, 5}, new int[]{3, 5}, new int[]{6, 7, 8, 9}},
                {4, new int[]{4, 1, 2, 4, 4, 4}, new int[]{}, new int[]{1}},
                {5, new int[]{1, 2, 3, 4, 5, 6}, new int[]{6}, new int[]{1, 2, 3, 4}},
                {5, new int[]{4, 1, 2, 5, 3, 4}, new int[]{3, 4}, new int[]{6, 7, 8, 9}},
                {5, new int[]{4, 1, 2, 4, 4, 5}, new int[]{}, new int[]{4}}
        });
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        testedClass = new TestedClass();
    }

    @Test
    public void arrayGettingPartTest() {
        Assert.assertArrayEquals(result, testedClass.getArrayPartAfterValue(lastItemValue, inputArray));
    }

    @Test(expected = RuntimeException.class)
    public void arrayGettingPartTestExceptions2() {
        testedClass.getArrayPartAfterValue(lastItemValue, exceptionTestArray);
    }
}
