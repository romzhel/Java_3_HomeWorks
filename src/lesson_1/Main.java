package lesson_1;

public class Main {
    public static void main(String[] args) {
        lesson_1.Box<lesson_1.Apple> appleBox1 = new lesson_1.Box<>("Яблоки 1");
        lesson_1.Box<lesson_1.Apple> appleBox2 = new lesson_1.Box<>("Яблоки 2");

        lesson_1.Box<lesson_1.Orange> orangeBox1 = new lesson_1.Box<>("Апельсины 1");
        lesson_1.Box<lesson_1.Orange> orangeBox2 = new lesson_1.Box<>("Апельсины 2");

        appleBox1.addFruits(new lesson_1.Apple(), new lesson_1.Apple(), new lesson_1.Apple());
        orangeBox1.addFruits(new lesson_1.Orange(), new lesson_1.Orange());

        displayBoxWeight(appleBox1);
        displayBoxWeight(orangeBox1);

        System.out.println();

        displayBoxWeightComparing(appleBox1, orangeBox1);

        System.out.println();

        displayBoxWeight(appleBox1);
        displayBoxWeight(appleBox2);
        System.out.println("пересыпаем");
        appleBox1.putAllContentTo(appleBox2);
        displayBoxWeight(appleBox1);
        displayBoxWeight(appleBox2);

        System.out.println();
        displayBoxWeightComparing(appleBox1, appleBox2);
    }

    public static void displayBoxWeight(lesson_1.Box<?> box) {
        System.out.printf("%s весит %.1f\n", box.getName(), box.getContentWeight());
    }

    public static void displayBoxWeightComparing(lesson_1.Box<?> box1, lesson_1.Box<?> box2) {
        System.out.printf("Вес %s %sравен %s\n", box1.getName(), box1.compareWeightTo(box2) ? "" : "не ", box2.getName());
    }
}
