package model;

import java.util.List;
import skeleton.Skeleton;

/**
 * A hókotrók bázisa, amely maga is Field a gráfban (Building leszármazott).
 * Felelőssége a hókotrók fogadása, a fejcserék és az üzemanyag-utántöltések
 * koordinálása.
 * A beépített áruháznak is helyet ad.
 */
public class HomeBase extends Building {

    /** A telephelyen lévő bolt[cite: 1346]. */
    private IntegratedMarket market;

    /** A telephely maximális befogadóképessége[cite: 1347]. */
    private int capacity;

    public HomeBase() {
        this.market = new IntegratedMarket();
    }

    /**
     * Fogadja a visszatérő hókotrót, belső kapacitáskezeléssel[cite: 1349].
     */
    public void acceptSnowPlow(SnowPlow sp) {
        Skeleton.enter("Hívó", "homeBase", "acceptSnowPlow(sp)");
        // Belső logika helyett csak kilépünk a szkeleton miatt
        Skeleton.exit("void");
    }

    /**
     * Utántölti a megadott hókotró üzemanyag-tartályát[cite: 1350].
     */
    public void refuelSnowPlow(SnowPlow sp, double amount) {
        Skeleton.enter("takarító", "homeBase", "refuelSnowPlow(sp, amount)");

        Skeleton.enter("homeBase", "sp", "getActiveHead()");
        Skeleton.exit("HEAD");

        Skeleton.enter("homeBase", "HEAD", "refillFuel(amount)");
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    /**
     * Fejcserét hajt végre a megadott hókotróra[cite: 1351].
     */
    public void swapHead(SnowPlow sp, CleanerHead newHead) {
        Skeleton.enter("takarító", "homeBase", "swapHead(sp, newHead)");

        Skeleton.enter("homeBase", "sp", "changeHead(newHead)");
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    @Override
    public void accept(Vehicle v) {
        Skeleton.enter("Hívó", "homeBase", "accept(v)");
        Skeleton.exit("void");
    }

    @Override
    public void remove(Vehicle v) {
        Skeleton.enter("Hívó", "homeBase", "remove(v)");
        Skeleton.exit("void");
    }

    @Override
    public List<Field> getNeighbors() {
        Skeleton.enter("Hívó", "homeBase", "getNeighbors()");
        Skeleton.exit("List<Field>");
        return neighbors;
    }
}