package gonoszonosz;

/**
 * A hókotróra szerelhető cserélhető eszközök absztrakt ősosztálya.
 * Meghatározza, hogy a hókotró hogyan hat a sávon lévő hóra és jégre.
 * Minden konkrét fejnek implementálnia kell a clean(Lane) metódust.
 */
public abstract class CleanerHead implements IPurchasable {

    /** A fej ára a boltban. */
    protected int price;

    /** Meghatározza, hogy a fej igényel-e üzemanyagot. */
    protected boolean usesFuel;

    /**
     * Létrehozza a hókotrófejet a megadott árral.
     * @param price    a fej vételára
     * @param usesFuel igaz, ha a fej üzemanyagot igényel
     */
    public CleanerHead(int price, boolean usesFuel) {
        this.price = price;
        this.usesFuel = usesFuel;
    }

    /**
     * A konkrét fejre jellemző takarítási műveletet elvégzi az adott sávon.
     * Minden alosztálynak saját implementációja van.
     * @param lane a takarítandó sáv
     */
    public abstract void clean(Lane lane);

    /**
     * Visszaadja a fej árát (IPurchasable interfészből).
     * @return a fej ára
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Meghatározza, hogy a fej használ-e üzemanyagot.
     * @return true, ha üzemanyagot igényel
     */
    public boolean isUsesFuel() {
        return usesFuel;
    }
}
