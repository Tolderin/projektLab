package model;

/**
 * A játékban szereplő összes jármű absztrakt ősosztálya. [cite: 1526]
 * Tárolja a jármű aktuális sávját és a tervezett következő mezőt, és előírja a
 * move() metódust. [cite: 1526]
 */
public abstract class Vehicle {

    /** A jármű aktuális sávja [cite: 1531] */
    protected Lane currentLane;

    /** A tervezett következő mező [cite: 1532] */
    protected Field nextField;

    /**
     * A jármű mozgatása a jelenlegi helyzetről a következő tervezett mezőre
     * (absztrakt). [cite: 1534]
     */
    public abstract void move();
}