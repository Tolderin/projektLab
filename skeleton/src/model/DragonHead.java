package model;

import skeleton.Skeleton;

/**
 * A legdragabb es leghatekonyabb hokotro fej. Gazturbinaval azonnal
 * elolvasztja a savon levo teljes havat es jegpancelt.
 * Mukodesehez biokerozin szukseges; ha a keszlet kiurul,
 * a clean() nem hat.
 *
 * SD-10: DragonHead.clean(lane) ellenorzi, hogy fuelAmount > 0.
 * Ha igen: lane.removeSnow(lane.snowDepth) es lane.removeIce().
 * Ha nem: nem csinal semmit.
 *
 * Allapotgep: Fueled (fuelAmount > 0) es Empty (fuelAmount = 0).
 */
public class DragonHead extends CleanerHead {

    /** A jelenleg tarolt biokerozin mennyisege. */
    private double fuelAmount;

    /** A tarolt uzemanyag ara. */
    private double fuelPrice;

    /**
     * Letrehozza a sarkany fejet.
     * Ar: 5000 (a legdragabb fej).
     * Hasznal uzemanyagot (biokerozin).
     * Kezdeti toltet: 100.0.
     */
    public DragonHead() {
        this.price = 5000;
        this.usesFuel = true;
        this.fuelAmount = 100.0;
        this.fuelPrice = 800.0;
    }

    /**
     * Azonnal eltavolitja az osszes havat es jeget a savrol.
     * SD-10 alapjan: I/N kerdes a biokerozin megleterol.
     * Ha van: removeSnow(snowDepth) es removeIce().
     * Ha nincs: nem csinal semmit.
     *
     * @param l A takaritando sav.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "dragonHead", "clean(l)");

        boolean isFueled = Skeleton.askQuestion(
                "Van elegendo biokerozin a tartalyban (fuelAmount > 0)?");

        if (isFueled) {
            Skeleton.enter("dragonHead", "l", "removeSnow(l.snowDepth)");
            l.removeSnow(1.0);
            Skeleton.exit("void");

            Skeleton.enter("dragonHead", "l", "removeIce()");
            l.removeIce();
            Skeleton.exit("void");

            this.fuelAmount -= 20.0;
        }

        Skeleton.exit("void");
    }

    /**
     * Biokerozint tolt a tartalyba a telephelyen.
     *
     * @param amount A betoltendo biokerozin mennyisege.
     */
    public void refillFuel(double amount) {
        Skeleton.enter("homeBase", "dragonHead", "refillFuel(" + amount + ")");
        this.fuelAmount += amount;
        Skeleton.exit("void");
    }

    /**
     * Visszaadja a sarkany fej arat.
     *
     * @return 5000
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a fej nevet.
     *
     * @return "DragonHead"
     */
    @Override
    public String getName() {
        return "DragonHead";
    }
}
