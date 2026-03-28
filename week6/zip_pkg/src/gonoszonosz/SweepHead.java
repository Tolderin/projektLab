package gonoszonosz;

/**
 * A hókotróra szerelhető söprő típusú fej.
 * A havat a szomszédos sávba vagy az út mellé tolja.
 * Jeget nem tud eltávolítani, üzemanyagot nem igényel.
 * Ez a hókotró alapfelszerelése.
 */
public class SweepHead extends CleanerHead {

    /**
     * Létrehoz egy új söprő fejet az alapértelmezett árral.
     */
    public SweepHead() {
        super(0, false); // alapfelszerelés, ingyenes
    }

    /**
     * A sáv havát áttol a szomszédos sávba vagy az út szélére; jégre nincs hatása.
     * Ha van szomszédos Lane szomszéd, oda kerül a hó.
     * @param lane a takarítandó sáv
     */
    @Override
    public void clean(Lane lane) {
        Skeleton.call("SnowPlow", "SweepHead", "clean(lane)");
        double amount = lane.getSnowDepth();
        lane.removeSnow(amount);
        // A havat áttoljuk a szomszédos sávba
        for (Field neighbor : lane.getNeighbors()) {
            if (neighbor instanceof Lane) {
                ((Lane) neighbor).addSnow(amount);
                break;
            }
        }
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a fej nevét (IPurchasable interfészből).
     * @return "SweepHead"
     */
    @Override
    public String getName() {
        return "SweepHead";
    }
}
