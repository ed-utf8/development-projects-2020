package gg.hound.arena.util;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {

    private final NavigableMap<Double, E> navigableMap = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection(Random random) throws IllegalArgumentException {
        if (random == null) {
            throw new IllegalArgumentException("The given random instance must not be null.");
        }

        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        navigableMap.put(total, result);
        return this;
    }

    public E next() {
        return navigableMap.ceilingEntry(random.nextDouble() * total).getValue();
    }

}
