package gonoszonosz;

/**
 * A városlakók automatizált járműve, amely önállóan navigál lakása és munkahelye között
 * a legrövidebb járható útvonalon. Ha vékony hórétegen halad, letapossa a havat;
 * jégen csúszhat és balesetet okozhat; mély hóban elakad, de szomszédos szabad sávba átsorolhat.
 */
public class Car extends Vehicle {

    /** A lakás épülete, ahonnan az autó indul. */
    private Building homeBuilding;

    /** A munkahely épülete, ahova az autó tart. */
    private Building workBuilding;

    /** A térkép referenciája az útvonal-számításhoz. */
    private Map gameMap;

    /**
     * Létrehoz egy új autót a megadott lakással, munkahellyel és térképpel.
     * @param homeBuilding a lakás épülete
     * @param workBuilding a munkahely épülete
     * @param gameMap      a játék térképe
     */
    public Car(Building homeBuilding, Building workBuilding, Map gameMap) {
        this.homeBuilding = homeBuilding;
        this.workBuilding = workBuilding;
        this.gameMap = gameMap;
    }

    /**
     * CD-03 / SD-03 és CD-04 / SD-04 alapján:
     * Az autó a kiszámolt útvonal alapján lép a következő mezőre.
     * Hívási sorrend: calculatePath() → remove(car) l1-en → accept(car) l2-n →
     * applyCompaction() → checkFreeze() → [Jeges?] → [Megcsúszik?] → slip().
     * Ha a célsáv le van zárva (SD-15): changeLane() hívódik.
     */
    @Override
    public void move() {
        Skeleton.call("GameLogic", "Car", "move()");

        // 1.1: calculatePath()
        calculatePath();

        if (nextField == null) {
            Skeleton.ret("void");
            return;
        }

        // CD-15: isBlocked() hívás az l2 sávon – ha le van zárva, changeLane()
        if (nextField instanceof Lane) {
            Lane targetLane = (Lane) nextField;
            if (targetLane.isBlocked()) {
                // 1.3: changeLane()
                changeLane();
                Skeleton.ret("void");
                return;
            }
        }

        // 1.2: remove(car) az előző sávon
        if (currentLane != null) {
            currentLane.remove(this);
        }

        // 1.3: accept(car) a célmezőn
        nextField.accept(this);

        if (nextField instanceof Lane) {
            Lane lane = (Lane) nextField;
            currentLane = lane;

            // 1.4: applyCompaction()
            lane.applyCompaction();

            // 1.5: checkFreeze() – Skeleton kérdez
            lane.checkFreeze();

            // SD-04: [Jeges a sáv?] → [Megcsúszik az autó?] → slip()
            boolean icy = Skeleton.ask("Jeges a sav?");
            if (icy) {
                boolean slips = Skeleton.ask("Megcsúszik az auto?");
                if (slips) {
                    slip();
                }
            }
        }

        Skeleton.ret("void");
    }

    /**
     * CD-03: car.calculatePath() → map.findShortestPath(l1, target) → [L2, TARGET] visszatér.
     * Beállítja a nextField-et a következő Lane-re.
     */
    public void calculatePath() {
        Skeleton.call("Car", "Car", "calculatePath()");
        Field target = workBuilding;
        java.util.List<Field> path = gameMap.findShortestPath(currentLane, target);
        if (path != null && path.size() > 1) {
            nextField = path.get(1);
        }
        Skeleton.ret("void");
    }

    /**
     * CD-04: collideWith(CAR) az other:Vehicle-re hívódik.
     * A sávon setBlocked(true) hívódik.
     * @param v a másik ütköző jármű
     */
    public void collideWith(Vehicle v) {
        Skeleton.call("Car", "Car", "collideWith(" + v.getClass().getSimpleName() + ")");
        // CD-04: setBlocked(true) az l2 sávon
        if (currentLane != null) {
            currentLane.setBlocked(3);
        }
        Skeleton.ret("void");
    }

    /**
     * CD-04: slip() – Skeleton kérdez: "Van másik jármű a sávban, amivel ütközhet?"
     * I → collideWith(CAR) → setBlocked(true).
     */
    public void slip() {
        Skeleton.call("Car", "Car", "slip()");
        boolean hasVehicle = Skeleton.ask("Van másik jármű a sávban, amivel ütközhet?");
        if (hasVehicle) {
            // collideWith egy other:Vehicle-re megy
            collideWith(this);
        }
        Skeleton.ret("void");
    }

    /**
     * CD-15: changeLane() – getNeighbors() → isBlocked() szomszédon →
     * "Szabad a szomszédos sáv?" → I → remove(car) + accept(car) +
     * applyCompaction() + checkFreeze() a szomszéd sávon.
     */
    public void changeLane() {
        Skeleton.call("Car", "Car", "changeLane()");
        if (currentLane == null) {
            Skeleton.ret("void");
            return;
        }
        // 1.3.1: getNeighbors()
        for (Field neighbor : currentLane.getNeighbors()) {
            if (neighbor instanceof Lane) {
                Lane neighborLane = (Lane) neighbor;
                // 1.3.2: isBlocked() a szomszéd sávon
                boolean free = Skeleton.ask("Szabad a szomszédos sav?");
                if (free) {
                    // 1.3.3: remove(car) l1-en
                    currentLane.remove(this);
                    // 1.3.4: accept(car) a szomszéd sávon
                    neighborLane.accept(this);
                    currentLane = neighborLane;
                    // CD-15: 1.3.5 applyCompaction(), 1.3.6 checkFreeze() a szomszéd sávon
                    neighborLane.applyCompaction();
                    neighborLane.checkFreeze();
                    break;
                }
            }
        }
        Skeleton.ret("void");
    }
}
