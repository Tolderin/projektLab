package model;

import skeleton.Skeleton;

/**
 * A buszvezető játékos által irányított jármű, amely két végállomás között
 * közlekedik. [cite: 1237]
 * Fordulókat teljesít; ütközés vagy csúszás esetén meghatározott ideig
 * mozgásképtelenné válik és elzárja a sávot. [cite: 1238]
 */
public class Bus extends Vehicle {

    /** hamis értéke esetén a busz ütközés miatt mozgásképtelen [cite: 1242] */
    private boolean isFunctioning = true;

    /** hány körig marad még mozgásképtelen [cite: 1243] */
    private int disabledTurnsLeft = 0;

    /**
     * mozgatja a buszt a tervezett következő mező felé (Vehicle-ből
     * felüldefiniálva). [cite: 1245]
     */
    @Override
    public void move() {
        Skeleton.enter("buszvezető", "bus", "move()");

        Skeleton.enter("bus", "l1", "remove(bus)");
        Skeleton.exit("void");

        Skeleton.enter("bus", "target", "accept(bus)");
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    /**
     * ütközést kezel egy másik járművel, beállítja a mozgásképtelen állapotot.
     * [cite: 1246]
     */
    public void collideWith(Vehicle v) {
        Skeleton.enter("Hívó", "bus", "collideWith(v)");
        this.isFunctioning = false;
        Skeleton.exit("void");
    }

    /**
     * jégpáncélon történő megcsúszást kezel. [cite: 1247]
     */
    public void slip() {
        Skeleton.enter("Hívó", "bus", "slip()");
        this.isFunctioning = false;
        Skeleton.exit("void");
    }

    /**
     * Visszaadja a busz nevét.
     * 
     * @return a busz azonosítója.
     */
    public String getName() {
        Skeleton.enter("Hívó", "bus", "getName()");
        Skeleton.exit("BusName");
        return "BusName";
    }
}