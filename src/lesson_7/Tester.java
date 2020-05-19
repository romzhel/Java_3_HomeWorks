package lesson_7;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

public class Tester {

    public static void main(String[] args) throws Exception {
        start(TestedClass.class);
    }

    public static void start(String className) throws Exception {
        start(Class.forName(className));
    }

    public static <T> void start(Class<T> testedClass) throws Exception {
        Method beforeSuiteMethod = null;
        Method afterSuiteMethod = null;
        Set<Method> testMethods = new TreeSet<>((o1, o2) ->
                o1.getAnnotation(Test.class).order() - o2.getAnnotation(Test.class).order());

        Method[] methods = testedClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (beforeSuiteMethod == null) {
                    beforeSuiteMethod = method;
                } else {
                    throw new RuntimeException("Более 1 метода с аннотацией BeforeSuite");
                }
            }

            if (method.isAnnotationPresent(AfterSuite.class)) {
                if (afterSuiteMethod == null) {
                    afterSuiteMethod = method;
                } else {
                    throw new RuntimeException("Более 1 метода с аннотацией AfterSuite");
                }
            }

            if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }

        T testedInstance = testedClass.getConstructor().newInstance();

        if (beforeSuiteMethod != null) {
            beforeSuiteMethod.setAccessible(true);
            beforeSuiteMethod.invoke(testedInstance);
        }

        for (Method method : testMethods) {
            method.setAccessible(true);
            method.invoke(testedInstance);
        }

        if (afterSuiteMethod != null) {
            afterSuiteMethod.setAccessible(true);
            afterSuiteMethod.invoke(testedInstance);
        }
    }
}
