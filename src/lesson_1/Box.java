package lesson_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Box<T extends Fruit> {
    private final List<T> content;
    private final String name;

    public Box(String name) {
        content = new ArrayList<>();
        this.name = name;
    }

    public float getContentWeight() {
        float weight = 0.0f;
        for (Fruit fruit : content) {
            weight += fruit.getWeight();
        }
        return weight;
    }

    public boolean compareWeightTo(lesson_1.Box<? extends Fruit> anotherBox) {
        return Math.abs(getContentWeight() - anotherBox.getContentWeight()) < 0.001;
    }

    public void putAllContentTo(lesson_1.Box<T> anotherBox) {
        if (anotherBox == null) {
            throw new RuntimeException("another box is full");
        } else if (anotherBox == this) {
            throw new RuntimeException("source and target boxes are the same box");
        } else if (content.size() == 0) {
            throw new RuntimeException("ox is empty");
        }

        anotherBox.getContent().addAll(content);
        content.clear();
    }

    public void addFruits(T... fruits) {
        content.addAll(Arrays.asList(fruits));
    }

    public List<T> getContent() {
        return content;
    }

    public String getName() {
        return name;
    }
}
