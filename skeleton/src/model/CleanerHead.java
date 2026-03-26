package model;

/**
 * A hokotrora (SnowPlow) szerelheto kulonbozo fejek absztrakt ososztalya.
 * Implementalja az IPurchasable interfeszt, igy megvasarolhato az aruhazban.
 * Felelossege a savok takaritasi logikajanak (clean) eloirasa.
 * Minden konkret fejnek implementalnia kell a clean(Lane) metodust.
 */
public abstract class CleanerHead implements IPurchasable {

    /** A fej ara a boltban. */
    protected int price;

    /** Hasznal-e uzemanyagot (so vagy biokerozin). */
    protected boolean usesFuel;

    /**
     * A sav megtisztitasa. A leszarmazottaknak kell megvalositaniuk.
     *
     * @param l A takaritando sav (Lane).
     */
    public abstract void clean(Lane l);
}
