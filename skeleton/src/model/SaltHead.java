package model;

import skeleton.Skeleton;

/**
 * Sot szoro hokotro fej. A kiszort so idovel felolvasztja a havat es
 * jegpancelt, es megakadalyozza az ujabb ho megmaradasat.
 * Mukodesehez so szukseges; kiuruleskor hatastalanna valik.
 *
 * SD-09: SaltHead.clean(lane) ellenorzi, hogy fuelAmount > 0.
 * Ha igen: lane.applySaltEffect(activeTime) es csokkenti a fuelAmount-ot.
 * Ha nem: nem csinal semmit.
 *
 * Allapotgep: Fueled (fuelAmount > 0) es Empty (fuelAmount = 0).
 */
public class SaltHead extends CleanerHead {

    /** A jelenleg tarolt so mennyisege. */
    private double fuelAmount;

    /** A tarolt uzemanyag ara. */
    private double fuelPrice;

    /**
     * Letrehozza a soszoro fejet.
     * Ar: 4000.
     * Hasznal uzemanyagot (so).
     * Kezdeti toltet: 100.0.
     */
    public SaltHead() {
        this.price = 4000;
        this.usesFuel = true;
        this.fuelAmount = 100.0;
        this.fuelPrice = 500.0;
    }

    /**
     * Sozza a savot, ha van elegendo so a tartalyban.
     * SD-09 alapjan: I/N kerdes a so megleterol.
     * Ha van: applySaltEffect(activeTime) es fuelAmount csokken.
     * Ha nincs: nem csinal semmit.
     *
     * @param l A takaritando sav.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "saltHead", "clean(l)");

        boolean isFueled = Skeleton.askQuestion(
                "Van elegendo so a tartalyban (fuelAmount > 0)?");

        if (isFueled) {
            Skeleton.enter("saltHead", "l", "applySaltEffect(activeTime)");
            l.applySaltEffect(5.0);
            Skeleton.exit("void");

            this.fuelAmount -= 10.0;
        }

        Skeleton.exit("void");
    }

    /**
     * Sot tolt a tartalyba a telephelyen.
     *
     * @param amount A betoltendo so mennyisege.
     */
    public void refillFuel(double amount) {
        Skeleton.enter("homeBase", "saltHead", "refillFuel(" + amount + ")");
        this.fuelAmount += amount;
        Skeleton.exit("void");
    }

    /**
     * Visszaadja a soszoro fej arat.
     *
     * @return 4000
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a fej nevet.
     *
     * @return "SaltHead"
     */
    @Override
    public String getName() {
        return "SaltHead";
    }
}
