package model;

import java.util.List;
import skeleton.Skeleton;

/**
 * A hokotrok bazisa, amely maga is Field a grafban (Building leszarmazott).
 * Felelossege a hokotrok fogadasa, a fejcserek es az uzemanyag-utantoltesek
 * koordinalasa. A beepitett aruhaznak is helyet ad.
 */
public class HomeBase extends Building {

    /** A telephelyen levo bolt. */
    private IntegratedMarket market;

    /** A telephely maximalis befogadokepessege. */
    private int capacity;

    /**
     * Letrehozza a telephelyet a beepitett aruhazzal.
     */
    public HomeBase() {
        this.market = new IntegratedMarket();
    }

    /**
     * Fogadja a visszatero hokotrot, belso kapacitaskezelessel.
     *
     * @param sp A visszatero hokotro.
     */
    public void acceptSnowPlow(SnowPlow sp) {
        Skeleton.enter("market", "homeBase", "acceptSnowPlow(sp)");
        Skeleton.exit("void");
    }

    /**
     * Utantolti a megadott hokotro uzemanyag-tartalyat.
     * SD-13 alapjan: sp-n keresztul keri az aktiv fejet,
     * azon hivja refillFuel(amount)-ot.
     *
     * @param sp A hokotro amelynek a fejet feltoltjuk.
     * @param amount A betoltendo mennyiseg.
     */
    public void refuelSnowPlow(SnowPlow sp, double amount) {
        Skeleton.enter("takarito", "homeBase", "refuelSnowPlow(sp, amount)");

        // SD-13: HomeBase -> sp.activeHead -> refillFuel(amount)
        sp.refuelActiveHead(amount);

        Skeleton.exit("void");
    }

    /**
     * Fejcseret hajt vegre a megadott hokotron.
     * SD-12 alapjan: meghivja a SnowPlow.changeHead(newHead)-et.
     *
     * @param sp A hokotro amelyen a fejcsere tortenik.
     * @param newHead Az uj fej.
     */
    public void swapHead(SnowPlow sp, CleanerHead newHead) {
        Skeleton.enter("takarito", "homeBase", "swapHead(sp, newHead)");

        sp.changeHead(newHead);

        Skeleton.exit("void");
    }
}
