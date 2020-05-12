package lesson_5;

public class MainClass {
    public static final int CARS_COUNT = 4;

    public static void main(String[] args) {
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));

        for (int i = 0; i < CARS_COUNT; i++) {
            new Thread(new Car(race, 20 + (int) (Math.random() * 10))).start();
        }
    }
}
