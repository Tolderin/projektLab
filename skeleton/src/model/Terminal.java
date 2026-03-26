package model;

import skeleton.Skeleton;

/**
 * A buszjáratok végpontja, egyben Field a gráfban (Building leszármazott)[cite:
 * 1502, 1503].
 * Felelőssége a fordulók regisztrálása: amikor egy busz megérkezik,
 * jelzi a buszvezetőnek a forduló teljesítését[cite: 1503].
 */
public class Terminal extends Building {

    /**
     * Regisztrálja a busz érkezését és jelzi a fordulót a buszvezetőnek[cite:
     * 1507].
     */
    public void registerArrival(Bus bus) {
        Skeleton.enter("bus/buszvezető", "terminal", "registerArrival(bus)");

        // Itt elvileg a buszvezető completedRounds változóját kéne növelni
        Skeleton.enter("terminal", "Skeleton", "A célmező végállomás (Terminal)? (I/N) -> I -> completedRounds++");
        Skeleton.exit("");

        Skeleton.exit("void");
    }

    @Override
    public void accept(Vehicle v) {
        Skeleton.enter("Hívó", "terminal", "accept(v)");
        Skeleton.exit("void");
    }

    @Override
    public void remove(Vehicle v) {
        Skeleton.enter("Hívó", "terminal", "remove(v)");
        Skeleton.exit("void");
    }
}