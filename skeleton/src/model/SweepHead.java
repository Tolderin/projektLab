package model;

import skeleton.Skeleton;

/**
 * A hókotró alapértelmezett feje, amely a havat sepri le az útról.
 * Nem igényel üzemanyagot (végtelen kapacitású).
 * Felelőssége, hogy a sávon (Lane) csökkentse a hóréteg vastagságát.
 */
public class SweepHead extends CleanerHead {

    /**
     * A sáv letakarítása. Meghívja a Lane removeSnow metódusát.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "sweepHead", "clean(l)");

        Skeleton.enter("sweepHead", "l", "removeSnow(amount)");
        l.removeSnow(1.0); // Példa értékkel csökkentjük a havat
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    @Override
    public int getPrice() {
        Skeleton.enter("Hívó", "sweepHead", "getPrice()");
        Skeleton.exit("1000");
        return 1000;
    }

    @Override
    public String getName() {
        Skeleton.enter("Hívó", "sweepHead", "getName()");
        Skeleton.exit("Söprű");
        return "Söprű";
    }
}