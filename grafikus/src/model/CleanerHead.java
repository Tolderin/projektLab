package model;

/**
 * A hokotrora (SnowPlow) szerelheto kulonbozo fejek absztrakt
 * ososztalya. Implementalja az IPurchasable interfeszt, igy
 * megvasarolhato az aruhazban. Eloirja a clean(Lane) metodust,
 * amelyet minden konkret fejnek implementalnia kell.
 */
public abstract class CleanerHead implements IPurchasable {

    /** A fej ara a boltban. */
    protected int price;

    /** Hasznal-e uzemanyagot (so vagy biokerozin vagy zuzalek). */
    protected boolean usesFuel;

    /** A fej neve (a getName() altal visszaadott). */
    protected String name;

    /**
     * A sav megtisztitasa. A leszarmazottaknak kell megvalositaniuk
     * a sajat takaritasi szabaly szerint.
     *
     * @param lane A takaritando sav.
     */
    public abstract void clean(Lane lane);

    /**
     * Visszaadja a fej arat.
     *
     * @return Az ar.
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a fej nevet.
     *
     * @return A nev.
     */
    @Override
    public String getName() {
        return name;
    }
}
