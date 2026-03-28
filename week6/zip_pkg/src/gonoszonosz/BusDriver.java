package gonoszonosz;

/**
 * A buszvezető játékos osztálya.
 * Feladata, hogy a rábízott busszal a lehető legtöbb fordulót tegye meg
 * a kijelölt végállomások között az adott műszak alatt.
 */
public class BusDriver extends Player {

    /** A buszvezető által irányított busz. */
    private Bus bus;

    /** A sikeresen teljesített fordulók száma. */
    private int completedRounds;

    /**
     * Létrehoz egy új buszvezetőt a megadott névvel.
     * @param name a buszvezető neve
     */
    public BusDriver(String name) {
        super(name);
        this.completedRounds = 0;
    }

    /**
     * Jelzi a buszvezető körének kezdetét.
     * Naplózza a hívást a Skeleton segítségével.
     */
    @Override
    public void startTurn() {
        Skeleton.call("GameLogic", "BusDriver", "startTurn()");
        Skeleton.ret("void");
    }

    /**
     * Jelzi a buszvezető körének végét.
     * Naplózza a hívást a Skeleton segítségével.
     */
    @Override
    public void endTurn() {
        Skeleton.call("BusDriver", "GameLogic", "endTurn()");
        Skeleton.ret("void");
    }

    /**
     * Növeli a teljesített fordulók számát eggyel.
     * A Terminal.registerArrival() hívja meg, amikor a busz megérkezik.
     */
    public void incrementRounds() {
        completedRounds++;
        score = completedRounds;
        System.out.println("  [BusDriver] " + name + " teljesített fordulók száma: " + completedRounds);
    }

    /**
     * Beállítja a buszvezető által irányított buszt.
     * @param bus a vezérelt busz
     */
    public void setBus(Bus bus) {
        this.bus = bus;
    }

    /**
     * Visszaadja a buszvezető által irányított buszt.
     * @return a Bus objektum
     */
    public Bus getBus() {
        return bus;
    }

    /**
     * Visszaadja a teljesített fordulók számát.
     * @return fordulók száma
     */
    public int getCompletedRounds() {
        return completedRounds;
    }
}
