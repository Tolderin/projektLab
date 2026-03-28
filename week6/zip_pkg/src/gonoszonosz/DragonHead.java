package gonoszonosz;

/**
 * A legdrágább és leghatékonyabb hókotró fej.
 * Gázturbinával azonnal elolvasztja a sávon lévő teljes havat és jégpáncélt.
 * Működéséhez biokerozin szükséges; ha a készlet kiürül, a clean() nem hat.
 * Nem akadályozza meg az újabb hó lerakódását.
 */
public class DragonHead extends CleanerHead {

    /** A jelenleg tárolt biokerozin mennyisége. */
    private double fuelAmount;

    /** A biokerozin egységára. */
    private double fuelPrice;

    /**
     * Létrehoz egy új sárkány fejet.
     * @param price      a fej ára
     * @param fuelAmount kezdeti biokerozin mennyiség
     * @param fuelPrice  a biokerozin egységára
     */
    public DragonHead(int price, double fuelAmount, double fuelPrice) {
        super(price, true);
        this.fuelAmount = fuelAmount;
        this.fuelPrice = fuelPrice;
    }

    /**
     * Azonnal eltávolítja az összes havat és jeget a sávról biokerozin elégetésével.
     * Ha nincs elegendő biokerozin, nem csinál semmit.
     * A Skeleton megkérdezi a felhasználót, hogy van-e elegendő üzemanyag.
     * @param lane a kezelendő sáv
     */
    @Override
    public void clean(Lane lane) {
        Skeleton.call("SnowPlow", "DragonHead", "clean(lane)");
        boolean hasFuel = Skeleton.ask("Van elegendő biokerozin a tartályban (fuelAmount > 0)?");
        if (hasFuel) {
            fuelAmount -= 1.0;
            lane.removeSnow(lane.getSnowDepth());
            lane.removeIce();
        }
        Skeleton.ret("void");
    }

    /**
     * Biokerozint tölt a tartályba a telephelyen.
     * @param amount a feltöltendő biokerozin mennyisége
     */
    public void refillFuel(double amount) {
        Skeleton.call("HomeBase", "DragonHead", "refillFuel(" + amount + ")");
        fuelAmount += amount;
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a fej nevét.
     * @return "DragonHead"
     */
    @Override
    public String getName() {
        return "DragonHead";
    }

    /**
     * Visszaadja a tárolt biokerozin mennyiségét.
     * @return biokerozin mennyiség
     */
    public double getFuelAmount() {
        return fuelAmount;
    }
}
