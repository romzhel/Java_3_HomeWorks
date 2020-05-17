package lesson_7;

public class TestedClass {

    @BeforeSuite
    public void init() {
        System.out.println("tested class init method");
    }

    @Test(order = 6)
    public void method1() {
        System.out.println("tested class method 1");
    }

    @Test(order = 5)
    public void method2() {
        System.out.println("tested class method 2");
    }

    @Test(order = 4)
    private void method3() {
        System.out.println("tested class method 3");
    }

    @Test(order = 3)
    public void method4() {
        System.out.println("tested class method 4");
    }

    @Test(order = 2)
    public void method5() {
        System.out.println("tested class method 5");
    }

    @Test(order = 1)
    public void method6() {
        System.out.println("tested class method 6");
    }

    @AfterSuite
    public void close() {
        System.out.println("tested class close method");
    }
}
