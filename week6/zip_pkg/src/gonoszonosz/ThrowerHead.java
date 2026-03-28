package gonoszonosz;

/**
 * A söprőhöz hasonló hókotró fej, de a havat jóval messzebbre (pl. a járdára) szórja,
 * nem csupán a szomszédos sávba. Jeget nem tud eltávolítani, üzemanyagot nem igényel.
 */
public class ThrowerHead extends CleanerHead {

    /** Az a távolság, ameddig a hányó fej a havat elhajítja. */
    private double throwDistance;

    /**
     * Létrehoz egy új hányó fejet a megadott árral és dobótávolsággal.
     * @param price         a fej ára
     * @param throwDistance a hó elhajítási távolsága
     */
    public ThrowerHead(int price, double throwDistance) {
        super(price, false);
        this.throwDistance = throwDistance;
    }

    /**
     * A sáv havát messzire szórja el az útról; jégre nincs hatása.
     * Szomszédos Lane-en nem történik addSnow() hívás (a hó a járdára kerül).
     * @param lane a takarítandó sáv
     */
    @Override
    public void clean(Lane lane) {
        Skeleton.call("SnowPlow", "ThrowerHead", "clean(lane)");
        double amount = lane.getSnowDepth();
        lane.removeSnow(amount);
        // A hó messzire kerül (járdára), szomszéd sávon nincs addSnow()
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a fej nevét.
     * @return "ThrowerHead"
     */
    @Override
    public String getName() {
        return "ThrowerHead";
    }

    /**
     * Visszaadja az elhajítási távolságot.
     * @return throwDistance értéke
     */
    public double getThrowDistance() {
        return throwDistance;
    }
}
