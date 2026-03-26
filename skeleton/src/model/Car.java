package model;

import skeleton.Skeleton;

/**
 * A városlakók automatizált járműve, amely önállóan navigál lakása és
 * munkahelye között a legrövidebb járható útvonalon. [cite: 1254]
 * Ha vékony hórétegen halad, letapossa a havat; jégen csúszhat és balesetet
 * okozhat; mély hóban elakad, de szomszédos szabad sávba átsorolhat. [cite:
 * 1255]
 */
public class Car extends Vehicle {

    /** a lakás épülete, ahonnan indul [cite: 1259] */
    private Building homeBuilding;

    /** a munkahely épülete, ahova tart [cite: 1259] */
    private Building workBuilding;

    /** a térkép referenciája az útvonal-számításhoz [cite: 1260] */
    private Map gameMap;

    /**
     * a kocsi a kiszámolt útvonal alapján lép a következő mezőre (Vehicle-ből
     * felüldefiniálva). [cite: 1262]
     */
    @Override
    public void move() {
        Skeleton.enter("gameLogic", "car", "move()");
        calculatePath();

        // Autó áthelyezése
        Skeleton.enter("car", "l1", "remove(car)");
        Skeleton.exit("void");

        Skeleton.enter("car", "l2", "accept(car)");
        Skeleton.exit("void");

        // Letaposás és fagyás ellenőrzése
        Skeleton.enter("car", "l2", "applyCompaction()");
        Skeleton.exit("void");

        Skeleton.enter("car", "l2", "checkFreeze()");
        Skeleton.exit("void");

        // Csúszás ellenőrzése (SD-04 alapján)
        boolean isFrozen = Skeleton.askQuestion("Jeges a sáv (isFrozen == true)?");
        if (isFrozen) {
            boolean slips = Skeleton.askQuestion("Megcsúszik az autó?");
            if (slips) {
                slip();
            }
        }

        Skeleton.exit("void");
    }

    /**
     * meghívja a gameMap.findShortestPath()-t és beállítja a nextField-et. [cite:
     * 1263, 1264]
     */
    public void calculatePath() {
        Skeleton.enter("car", "car", "calculatePath()");
        Skeleton.enter("car", "map", "findShortestPath(l1, target)");
        Skeleton.exit("[l2, target]");
        Skeleton.exit("void");
    }

    /**
     * ütközés kezelése, sávlezárás meghatározott körre. [cite: 1265]
     */
    public void collideWith(Vehicle v) {
        Skeleton.enter("Hívó", "car", "collideWith(car)");
        Skeleton.exit("void");
    }

    /**
     * jégpáncélon megcsúszás kezelése. [cite: 1266]
     */
    public void slip() {
        Skeleton.enter("car", "car", "slip()");
        boolean hasOtherVehicle = Skeleton.askQuestion("Van másik jármű a sávban, amivel ütközhet?");

        if (hasOtherVehicle) {
            Skeleton.enter("car", "other", "collideWith(car)");
            Skeleton.exit("void");
        } else {
            Skeleton.enter("car", "l2", "setBlocked(true)");
            Skeleton.exit("void");
        }

        Skeleton.exit("void");
    }

    /**
     * átsorolás a szomszédos szabad sávba elakadás esetén. [cite: 1267]
     */
    public void changeLane() {
        Skeleton.enter("car", "car", "changeLane()");
        Skeleton.exit("void");
    }
}