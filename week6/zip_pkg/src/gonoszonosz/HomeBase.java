package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * A hókotrók bázisa, amely maga is Field a gráfban (Building leszármazott).
 * Felelőssége a hókotrók fogadása, fejcserék és üzemanyag-utántöltések koordinálása.
 * CD-13 alapján: refuelSnowPlow(sp, amount) → sp.getActiveHead() → HEAD visszatér →
 * head.refillFuel(amount) → fuelAmount += amount.
 */
public class HomeBase extends Building {

    /** A telephelyen lévő beépített áruház. */
    private IntegratedMarket market;

    /** A telephely maximális befogadóképessége hókotróban mérve. */
    private int capacity;

    /** A jelenleg a telephelyen tartózkodó hókotrók listája. */
    private List<SnowPlow> parkedPlows;

    /**
     * Létrehoz egy új telephelyet.
     * @param capacity a telephely maximális befogadóképessége
     */
    public HomeBase(int capacity) {
        super();
        this.capacity = capacity;
        this.market = new IntegratedMarket();
        this.parkedPlows = new ArrayList<>();
    }

    /**
     * Fogadja az érkező járművet.
     * @param v az érkező jármű
     */
    @Override
    public void accept(Vehicle v) {
        Skeleton.call("SnowPlow", "HomeBase", "accept(" + v.getClass().getSimpleName() + ")");
        if (v instanceof SnowPlow) {
            acceptSnowPlow((SnowPlow) v);
        }
        Skeleton.ret("void");
    }

    /**
     * Eltávolítja a távozó járművet.
     * @param v a távozó jármű
     */
    @Override
    public void remove(Vehicle v) {
        Skeleton.call("SnowPlow", "HomeBase", "remove(" + v.getClass().getSimpleName() + ")");
        if (v instanceof SnowPlow) {
            parkedPlows.remove(v);
        }
        Skeleton.ret("void");
    }

    /**
     * CD-01: acceptSnowPlow(sp) – fogadja a visszatérő hókotrót.
     * @param sp a fogadandó hókotró
     */
    public void acceptSnowPlow(SnowPlow sp) {
        Skeleton.call("HomeBase", "HomeBase", "acceptSnowPlow(sp)");
        if (parkedPlows.size() < capacity) {
            parkedPlows.add(sp);
        }
        Skeleton.ret("void");
    }

    /**
     * CD-13: refuelSnowPlow(sp, amount) → sp.getActiveHead() → HEAD →
     * head.refillFuel(amount) → fuelAmount += amount.
     * @param sp     a feltöltendő hókotró
     * @param amount a feltöltendő mennyiség
     */
    public void refuelSnowPlow(SnowPlow sp, double amount) {
        Skeleton.call("Cleaner", "HomeBase", "refuelSnowPlow(sp, " + amount + ")");
        // CD-13: getActiveHead() hívás
        Skeleton.call("HomeBase", "SnowPlow", "getActiveHead()");
        CleanerHead head = sp.getActiveHead();
        Skeleton.ret("HEAD");
        // CD-13: refillFuel(amount)
        if (head instanceof SaltHead) {
            ((SaltHead) head).refillFuel(amount);
        } else if (head instanceof DragonHead) {
            ((DragonHead) head).refillFuel(amount);
        }
        Skeleton.ret("void");
    }

    /**
     * CD-12: swapHead(sp, newHead) → sp.changeHead(newHead) → activeHead = newHead.
     * @param sp      a hókotró
     * @param newHead az új fej
     */
    public void swapHead(SnowPlow sp, CleanerHead newHead) {
        Skeleton.call("Cleaner", "HomeBase", "swapHead(sp, " + newHead.getName() + ")");
        // CD-12: 2.1 changeHead(newHead) → 2.1.1 activeHead = newHead
        sp.changeHead(newHead);
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a beépített áruházat.
     * @return az IntegratedMarket objektum
     */
    public IntegratedMarket getMarket() {
        return market;
    }
}
