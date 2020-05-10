package lesson_5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

public class Car implements Runnable {
    private static int CAR_INDEX;
    private static ReentrantLock racePreparing = new ReentrantLock();
    private static CyclicBarrier startAllowing = new CyclicBarrier(MainClass.CARS_COUNT);
    private static ReentrantLock raceStarted = new ReentrantLock();
    private static ReentrantLock raceWon = new ReentrantLock();
    private static CountDownLatch finishCounter = new CountDownLatch(MainClass.CARS_COUNT);
    private static ReentrantLock raceFinished = new ReentrantLock();

    private Race race;
    private int speed;
    private String name;

    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CAR_INDEX++;
        this.name = "Участник #" + CAR_INDEX;
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public void run() {
        if (racePreparing.tryLock()) {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        }

        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            startAllowing.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (raceStarted.tryLock()) {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        }

        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }

        if (raceWon.tryLock()) {
            System.out.println(this.name + " WIN");
        }

        finishCounter.countDown();

        try {
            finishCounter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (raceFinished.tryLock()) {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        }
    }
}
