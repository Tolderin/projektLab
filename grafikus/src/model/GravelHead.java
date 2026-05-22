package model;

/**
 * Zuzalek (zuzott ko) szoro hokotro fej -- 7. heti uj fejtipus.
 * A kiszort zuzalek megszunteti a jeg csuszossagat, de a sopro
 * es hanyo fej a zuzalekot eltakaritja, mintha ho lenne.
 * A langszoro (DragonHead), jegtoro (IcebreakerHead) es so
 * (SaltHead) fej a zuzalekra nem hat. A zuzalek nem tomorodik.
 * Ha ho esik ra, a ho egy ido utan befedi.
 *
 * Mukodesehez zuzalek (mint uzemanyag) szukseges. Ha a keszlet
 * kiurul, a clean() nem hat.
 */
public class GravelHead extends CleanerHead {

    /** A jelenleg tarolt zuzalek mennyisege. */
    public double fuelAmount = 0.0;

    /** A tarolt uzemanyag (zuzalek) ara. */
    public double fuelPrice = 10.0;

    /** Konstans: az egy savra jutos kiszort zuzalek mennyisege. */
    private static final double GRAVEL_CONSUMPTION_RATE = 1.0;

    /**
     * Letrehoz egy zuzalekszoro fejet.
     */
    public GravelHead() {
        this.price = 50;
        this.usesFuel = true;
        this.name = "gravelhead";
    }

    /**
     * Zuzalekot juttat a savra, megakadalyozva a csuszasveszelyt.
     *
     * @param lane A kezelendo sav.
     */
    @Override
    public void clean(Lane lane) {
        if (fuelAmount > 0) {
            lane.addGravel(GRAVEL_CONSUMPTION_RATE);
            fuelAmount -= GRAVEL_CONSUMPTION_RATE;
            if (fuelAmount < 0) {
                fuelAmount = 0;
            }
        }
    }

    /**
     * Zuzalekot tolt a tartalyba.
     *
     * @param amount A betoltendo mennyiseg.
     */
    public void refillFuel(double amount) {
        fuelAmount += amount;
    }
}
