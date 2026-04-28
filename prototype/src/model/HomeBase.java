package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A hokotrok bazisa, amely maga is Field a grafban. Felelossege a
 * hokotrok fogadasa, a fejcserek es az uzemanyag-utantoltesek
 * koordinalasa. A beepitett aruhaznak (IntegratedMarket) is helyet ad.
 */
public class HomeBase extends Building {

    /** A telephelyen levo bolt. */
    public IntegratedMarket market;

    /** A telephely maximalis befogadokepessege. */
    public int capacity = 10;

    /** Privat lista a telephelyen aktualisan tartozkodo hokotrok
     *  nyilvantartasara. */
    private final List<SnowPlow> dockedPlows = new ArrayList<>();

    /**
     * Letrehoz egy telephelyet a beepitett aruhazzal.
     */
    public HomeBase() {
        this.market = new IntegratedMarket();
    }

    /**
     * Fogadja a visszatero hokotrot, kapacitaskezelessel.
     *
     * @param sp A visszatero hokotro.
     */
    public void acceptSnowPlow(SnowPlow sp) {
        if (dockedPlows.size() < capacity) {
            dockedPlows.add(sp);
            super.accept(sp);
        }
    }

    /**
     * Utantolti a megadott hokotro aktiv fej-tartalyat (csak ha SaltHead,
     * DragonHead vagy GravelHead).
     *
     * @param sp     A hokotro, amelynek a fejet feltoltjuk.
     * @param amount A betoltendo mennyiseg.
     */
    public void refuelSnowPlow(SnowPlow sp, double amount) {
        if (sp.activeHead instanceof SaltHead) {
            ((SaltHead) sp.activeHead).refillFuel(amount);
        } else if (sp.activeHead instanceof DragonHead) {
            ((DragonHead) sp.activeHead).refillFuel(amount);
        } else if (sp.activeHead instanceof GravelHead) {
            ((GravelHead) sp.activeHead).refillFuel(amount);
        }
    }

    /**
     * Fejcseret hajt vegre a megadott hokotron. Csak akkor hat,
     * ha az sp jelenleg a telephelyen tartozkodik (dockedPlows-ban van).
     *
     * @param sp      A hokotro, amelyen a fejcsere tortenik.
     * @param newHead Az uj fej.
     * @return true ha a fejcsere sikerult, false ha sp nincs a telephelyen.
     */
    public boolean swapHead(SnowPlow sp, CleanerHead newHead) {
        if (dockedPlows.contains(sp)) {
            sp.changeHead(newHead);
            return true;
        }
        return false;
    }

    /**
     * Override: amikor egy jarmu erkezik a HomeBase mezore (pl.
     * mozgas soran), automatikusan dockoljuk SnowPlow eseten.
     *
     * @param v Az erkezo jarmu.
     */
    @Override
    public void accept(Vehicle v) {
        super.accept(v);
        if (v instanceof SnowPlow) {
            SnowPlow sp = (SnowPlow) v;
            if (!dockedPlows.contains(sp)) {
                dockedPlows.add(sp);
            }
        }
    }

    /**
     * Override: tavozaskor a SnowPlow-t kivessuk a dockedPlows-bol.
     *
     * @param v A tavozo jarmu.
     */
    @Override
    public void remove(Vehicle v) {
        super.remove(v);
        if (v instanceof SnowPlow) {
            dockedPlows.remove(v);
        }
    }

    /**
     * Igaz, ha az adott hokotro jelenleg a telephelyen tartozkodik.
     * Az 'equip' parancs ellenorzi a HomeBase-ben tartozkodast.
     *
     * @param sp A vizsgalt hokotro.
     * @return true ha a sp dockolva van.
     */
    public boolean isDocked(SnowPlow sp) {
        return dockedPlows.contains(sp);
    }
}
