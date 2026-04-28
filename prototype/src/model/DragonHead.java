package model;

/**
 * A legdragabb es leghatekonyabb hokotro fej. Gazturbinaval azonnal
 * elolvasztja a savon levo teljes havat es jegpancelt. Mukodesehez
 * biokerozin szukseges. Ha a keszlet kiurul, a clean() nem hat.
 *
 * A 7. heti valtozas: a zuzalekra nincs hatasa.
 */
public class DragonHead extends CleanerHead {

    /** A jelenleg tarolt biokerozin mennyisege. */
    public double fuelAmount = 0.0;

    /** A tarolt biokerozin ara. */
    public double fuelPrice = 20.0;

    /** Konstans: az egy sav takaritasahoz szukseges biokerozin. */
    private static final double FUEL_CONSUMPTION_RATE = 1.0;

    /**
     * Letrehoz egy sarkany fejet.
     */
    public DragonHead() {
        this.price = 100;
        this.usesFuel = true;
        this.name = "dragonhead";
    }

    /**
     * Azonnal eltavolitja az osszes havat es jeget a savrol.
     *
     * @param lane A takaritando sav.
     */
    @Override
    public void clean(Lane lane) {
        if (fuelAmount > 0) {
            lane.removeSnow(lane.snowDepth);
            lane.removeIce();
            fuelAmount -= FUEL_CONSUMPTION_RATE;
            if (fuelAmount < 0) {
                fuelAmount = 0;
            }
        }
    }

    /**
     * Biokerozint tolt a tartalyba.
     *
     * @param amount A betoltendo mennyiseg.
     */
    public void refillFuel(double amount) {
        fuelAmount += amount;
    }
}
