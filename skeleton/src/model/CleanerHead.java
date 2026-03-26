package model;

import skeleton.Skeleton;

/**
 * A hókotróra (SnowPlow) szerelhető különböző fejek absztrakt ősosztálya.
 * Implementálja az IPurchasable interfészt, így megvásárolható az áruházban.
 * Felelőssége a sávok takarítási logikájának (clean) előírása, valamint
 * az üzemanyag/töltet utántöltésének alapértelmezett kezelése.
 */
public abstract class CleanerHead implements IPurchasable {

    /** A fejben lévő üzemanyag/töltet mennyisége. */
    protected double fuelAmount = 0.0;

    /**
     * A sáv megtisztítása. A leszármazottaknak kell megvalósítaniuk.
     * 
     * @param l A takarítandó sáv (Lane).
     */
    public abstract void clean(Lane l);

    /**
     * Utántölti a fej tartályát a megadott mennyiséggel.
     * Alapértelmezés szerint csak logol, a leszármazottak felüldefiniálhatják.
     * 
     * @param amount A betöltendő üzemanyag mennyisége.
     */
    public void refillFuel(double amount) {
        Skeleton.enter("homeBase", "cleanerHead", "refillFuel(" + amount + ")");
        this.fuelAmount += amount;
        Skeleton.exit("void");
    }
}