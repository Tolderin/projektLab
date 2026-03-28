package gonoszonosz;

/**
 * A buszvezető játékos által irányított jármű, amely két végállomás között közlekedik.
 * Fordulókat teljesít; ütközés vagy csúszás esetén meghatározott ideig
 * mozgásképtelenné válik és elzárja a sávot.
 */
public class Bus extends Vehicle {

    /** Hamis értéke esetén a busz ütközés miatt mozgásképtelen. */
    private boolean isFunctioning;

    /** Hány körig marad még mozgásképtelen a busz. */
    private int disabledTurnsLeft;

    /** A buszt irányító buszvezető játékos. */
    private BusDriver driver;

    /** Hány körig zárja le a sávot baleset esetén. */
    private static final int DISABLED_TURNS = 3;

    /**
     * Létrehoz egy új, működőképes buszt.
     * @param driver a buszt irányító buszvezető
     */
    public Bus(BusDriver driver) {
        this.driver = driver;
        this.isFunctioning = true;
        this.disabledTurnsLeft = 0;
    }

    /**
     * Mozgatja a buszt a tervezett következő mező felé.
     * Ha mozgásképtelen, csökkenti a hátramaradó körök számát.
     * Ha eléri a végállomást, regisztrálja a fordulót.
     */
    @Override
    public void move() {
        Skeleton.call("GameLogic", "Bus", "move()");
        if (!isFunctioning) {
            disabledTurnsLeft--;
            if (disabledTurnsLeft <= 0) {
                isFunctioning = true;
            }
            Skeleton.ret("void");
            return;
        }
        if (nextField != null) {
            if (currentLane != null) {
                currentLane.remove(this);
            }
            nextField.accept(this);
            if (nextField instanceof Lane) {
                currentLane = (Lane) nextField;
            }
        }
        Skeleton.ret("void");
    }

    /**
     * Ütközést kezel egy másik járművel, beállítja a mozgásképtelen állapotot.
     * A sávot is lezárja a megadott körszámra.
     * @param v a másik érintett jármű
     */
    public void collideWith(Vehicle v) {
        Skeleton.call("Bus", "Bus", "collideWith(" + v.getClass().getSimpleName() + ")");
        isFunctioning = false;
        disabledTurnsLeft = DISABLED_TURNS;
        if (currentLane != null) {
            currentLane.setBlocked(DISABLED_TURNS);
        }
        Skeleton.ret("void");
    }

    /**
     * Jégpáncélon történő megcsúszást kezel.
     * A Skeleton megkérdezi, hogy van-e másik jármű, amellyel ütközhet.
     */
    public void slip() {
        Skeleton.call("Lane", "Bus", "slip()");
        boolean hasVehicle = Skeleton.ask("Van másik jármű a sávban, amivel ütközhet?");
        if (hasVehicle) {
            collideWith(this); // szimbolikus: van egy másik jármű
        }
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a buszt irányító buszvezetőt.
     * @return a BusDriver objektum
     */
    public BusDriver getDriver() {
        return driver;
    }

    /**
     * Visszaadja, hogy a busz jelenleg működőképes-e.
     * @return true, ha a busz közlekedni tud
     */
    public boolean isFunctioning() {
        return isFunctioning;
    }
}
