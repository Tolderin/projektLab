package gonoszonosz;

/**
 * A játékban szereplő összes jármű absztrakt ősosztálya.
 * Tárolja a jármű aktuális sávját és a tervezett következő mezőt,
 * és előírja a move() metódust minden konkrét jármű számára.
 */
public abstract class Vehicle {

    /** A jármű aktuális sávja. */
    protected Lane currentLane;

    /** A tervezett következő mező, ahová a jármű lépni szeretne. */
    protected Field nextField;

    /**
     * A jármű mozgatása a jelenlegi helyzetről a következő tervezett mezőre.
     * Minden konkrét jármű saját logikájával valósítja meg.
     */
    public abstract void move();

    /**
     * Beállítja a jármű aktuális sávját.
     * @param lane az új aktuális sáv
     */
    public void setCurrentLane(Lane lane) {
        this.currentLane = lane;
    }

    /**
     * Visszaadja a jármű aktuális sávját.
     * @return az aktuális Lane objektum
     */
    public Lane getCurrentLane() {
        return currentLane;
    }

    /**
     * Beállítja a jármű következő célmezőjét.
     * @param field a következő célmező
     */
    public void setNextField(Field field) {
        this.nextField = field;
    }

    /**
     * Visszaadja a jármű tervezett következő mezőjét.
     * @return a következő Field objektum
     */
    public Field getNextField() {
        return nextField;
    }
}
