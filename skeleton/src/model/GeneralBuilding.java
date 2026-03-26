package model;

import skeleton.Skeleton;

/**
 * Az autók által felkeresett általános épület (lakás vagy munkahely)[cite:
 * 1333, 1334].
 * A Building absztrakt osztályból származik, tehát maga is Field a
 * gráfban[cite: 1335].
 * Felelőssége parkolóhelyet biztosítani az autóknak és navigációs célpontként
 * szolgálni[cite: 1336].
 */
public class GeneralBuilding extends Building {

    @Override
    public void accept(Vehicle v) {
        Skeleton.enter("Hívó", "generalBuilding", "accept(v)");
        // Ide parkol be az autó (Car)
        Skeleton.exit("void");
    }

    @Override
    public void remove(Vehicle v) {
        Skeleton.enter("Hívó", "generalBuilding", "remove(v)");
        // Innen indul el az autó
        Skeleton.exit("void");
    }

    @Override
    public java.util.List<Field> getNeighbors() {
        Skeleton.enter("Hívó", "generalBuilding", "getNeighbors()");
        Skeleton.exit("List<Field>");
        return neighbors;
    }
}