import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestedClassTest2 {
    private static TestedClass testedClass;
    private boolean result;
    private int[] inputArray;
    private int[] allowedValues;

    public TestedClassTest2(boolean result, int[] inputArray, int[] allowedValues) {
        this.result = result;
        this.inputArray = inputArray;
        this.allowedValues = allowedValues;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true, new int[]{1, 4, 4, 1, 4, 1}, new int[]{1, 4}},
                {false, new int[]{5, 4, 4, 6, 4, 7}, new int[]{1, 4}},
                {false, new int[]{1, 5, 6, 1, 7, 1}, new int[]{1, 4}},
                {false, new int[]{1, 4, 4, 5, 4, 1}, new int[]{1, 4}},
                {false, new int[]{2, 3, 5, 6, 7,}, new int[]{1, 4}}
        });
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        testedClass = new TestedClass();
    }

    @Test
    public void arrayGettingPartTest() {
        Assert.assertSame(result, testedClass.checkArrayItems(inputArray, allowedValues));
    }
}
