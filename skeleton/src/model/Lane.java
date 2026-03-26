package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * Az út egyetlen forgalmi sávja és egyben Field a gráfban[cite: 1394].
 * Felelőssége nyilvántartani a hóréteg vastagságát, a jégpáncél állapotát
 * és a rajta tartózkodó járműveket[cite: 1395].
 * Kezeli a letaposás miatti jégképződési mechanizmust[cite: 1395].
 */
public class Lane implements Field {
    private double snowDepth = 0.0;
    private boolean isFrozen = false;
    private double iceThickness = 0.0;
    private int compactionCount = 0;
    private List<Vehicle> vehicles = new ArrayList<>();
    private double saltEffect = 0.0;
    private List<Field> neighbors = new ArrayList<>();

    @Override
    public void accept(Vehicle v) {
        Skeleton.enter("Hívó", "lane", "accept(car/bus/sp)");
        vehicles.add(v);
        Skeleton.exit("void");
    }

    @Override
    public void remove(Vehicle v) {
        Skeleton.enter("Hívó", "lane", "remove(car/bus/sp)");
        vehicles.remove(v);
        Skeleton.exit("void");
    }

    @Override
    public List<Field> getNeighbors() {
        Skeleton.enter("Hívó", "lane", "getNeighbors()");
        Skeleton.exit("List<Field>");
        return neighbors;
    }

    /** Növeli a hóréteg vastagságát[cite: 1406]. */
    public void addSnow(double amount) {
        Skeleton.enter("Hívó", "lane", "addSnow(" + amount + ")");
        snowDepth += amount;
        Skeleton.exit("void");
    }

    /** Csökkenti a hóréteg vastagságát[cite: 1406]. */
    public void removeSnow(double amount) {
        Skeleton.enter("Hívó", "lane", "removeSnow(" + amount + ")");
        snowDepth = Math.max(0, snowDepth - amount);
        Skeleton.exit("void");
    }

    /** A jégpáncélt feltöri és havává alakítja[cite: 1406]. */
    public void breakIce() {
        Skeleton.enter("Hívó", "lane", "breakIce()");
        this.isFrozen = false;
        Skeleton.exit("void");
    }

    /** Eltávolítja a jégpáncélt[cite: 1407]. */
    public void removeIce() {
        Skeleton.enter("Hívó", "lane", "removeIce()");
        this.isFrozen = false;
        this.iceThickness = 0;
        Skeleton.exit("void");
    }

    /** Ellenőrzi, hogy kell-e jégpáncél képződjön[cite: 1408]. */
    public void checkFreeze() {
        Skeleton.enter("lane", "lane", "checkFreeze()");
        boolean makeFrozen = Skeleton.askQuestion("Elérte a letaposások száma a küszöböt, és van hó a sávon?");
        if (makeFrozen) {
            this.isFrozen = true;
            Skeleton.enter("lane", "lane", "isFrozen = true");
            Skeleton.exit("void");
        }
        Skeleton.exit("void");
    }

    /** Növeli a compactionCount-ot járműáthaladáskor[cite: 1409]. */
    public void applyCompaction() {
        Skeleton.enter("Hívó", "lane", "applyCompaction()");
        compactionCount++;
        Skeleton.exit("void");
    }

    /** Beállítja a sóhatást a sávon[cite: 1409]. */
    public void applySaltEffect(double duration) {
        Skeleton.enter("Hívó", "lane", "applySaltEffect(" + duration + ")");
        this.saltEffect = duration;
        Skeleton.exit("void");
    }

    /** Igaz, ha a sáv járhatatlan[cite: 1410]. */
    public boolean isBlocked() {
        Skeleton.enter("Hívó", "lane", "isBlocked()");
        boolean blocked = Skeleton.askQuestion("Le van zárva ez a sáv?");
        Skeleton.exit(String.valueOf(blocked));
        return blocked;
    }
}