package model;

/**
 * Sot szoro hokotro fej. A kiszort so idovel felolvasztja a havat
 * es a jegpancelt, es megakadalyozza az uj ho megmaradasat.
 * Mukodesehez so szukseges; kiuruleskor hatastalanna valik.
 *
 * Allapotok:
 * - Fueled (uzemanyaggal): aktivan szor sot, beallitja a saltEffect-et.
 * - Empty (ures): nincs hatasa.
 *
 * A 7. heti valtozas: a zuzalekra nincs hatasa.
 */
public class SaltHead extends CleanerHead {

    /** A jelenleg tarolt so mennyisege. */
    public double fuelAmount = 0.0;

    /** A tarolt uzemanyag (so) ara. */
    public double fuelPrice = 10.0;

    /** Konstans: a sohatas idotartama korokben. */
    private static final int SALT_DURATION_TURNS = 3;

    /** Konstans: az egy savra jutos so mennyisege. */
    private static final double SALT_CONSUMPTION_RATE = 1.0;

    /**
     * Letrehoz egy soszoro fejet.
     */
    public SaltHead() {
        this.price = 50;
        this.usesFuel = true;
        this.name = "salthead";
    }

    /**
     * Sot szor a savra, beallitja a sohatas idotartamat.
     *
     * @param lane A takaritando sav.
     */
    @Override
    public void clean(Lane lane) {
        if (fuelAmount > 0) {
            lane.applySaltEffect(SALT_DURATION_TURNS);
            fuelAmount -= SALT_CONSUMPTION_RATE;
            if (fuelAmount < 0) {
                fuelAmount = 0;
            }
        }
    }

    /**
     * Sot tolt a tartalyba (a HomeBase.refuelSnowPlow hivja).
     *
     * @param amount A betoltendo so mennyisege.
     */
    public void refillFuel(double amount) {
        fuelAmount += amount;
    }
}
