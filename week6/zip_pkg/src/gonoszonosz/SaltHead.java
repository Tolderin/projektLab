package gonoszonosz;

/**
 * Sót szóró hókotró fej.
 * A kiszórt só idővel felolvasztja a havat és jégpáncélt,
 * és megakadályozza az újabb hó megmaradását.
 * Működéséhez só szükséges; kiürüléskor hatástalan.
 */
public class SaltHead extends CleanerHead {

    /** A jelenleg tárolt só mennyisége. */
    private double fuelAmount;

    /** A só egységára vásárláskor. */
    private double fuelPrice;

    /**
     * Létrehoz egy új sószóró fejet.
     * @param price      a fej ára
     * @param fuelAmount kezdeti só mennyiség
     * @param fuelPrice  a só egységára
     */
    public SaltHead(int price, double fuelAmount, double fuelPrice) {
        super(price, true);
        this.fuelAmount = fuelAmount;
        this.fuelPrice = fuelPrice;
    }

    /**
     * Sót szór a sávra, beállítja a sóhatás időtartamát.
     * Ha nincs elegendő só (fuelAmount == 0), nem csinál semmit.
     * A Skeleton megkérdezi a felhasználót, hogy van-e elegendő só.
     * @param lane a kezelendő sáv
     */
    @Override
    public void clean(Lane lane) {
        Skeleton.call("SnowPlow", "SaltHead", "clean(lane)");
        boolean hasFuel = Skeleton.ask("Van elegendő só a tartályban (fuelAmount > 0)?");
        if (hasFuel) {
            fuelAmount -= 1.0;
            lane.applySaltEffect(5.0); // 5 körig hat
        }
        Skeleton.ret("void");
    }

    /**
     * Sót tölt a tartályba a telephelyen.
     * @param amount a feltöltendő só mennyisége
     */
    public void refillFuel(double amount) {
        Skeleton.call("HomeBase", "SaltHead", "refillFuel(" + amount + ")");
        fuelAmount += amount;
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a fej nevét.
     * @return "SaltHead"
     */
    @Override
    public String getName() {
        return "SaltHead";
    }

    /**
     * Visszaadja a tárolt só mennyiségét.
     * @return só mennyiség
     */
    public double getFuelAmount() {
        return fuelAmount;
    }
}
