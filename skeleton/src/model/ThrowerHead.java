package model;

import skeleton.Skeleton;

/**
 * A hómaró fej (ThrowerHead) nagyobb mennyiségű hó hatékony eltávolítására
 * szolgál.
 * Működéséhez üzemanyag szükséges. Ha kifogy az üzemanyag (Empty állapot),
 * nem végzi el a takarítást.
 */
public class ThrowerHead extends CleanerHead {

    public ThrowerHead() {
        this.fuelAmount = 100.0; // Kezdeti üzemanyag
    }

    /**
     * Eltávolítja a havat a sávközepekről, ha van elegendő üzemanyag.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "throwerHead", "clean(l)");

        boolean isFueled = Skeleton.askQuestion("Van elegendő üzemanyag a hómaróban (fuelAmount > 0)?");

        if (isFueled) {
            Skeleton.enter("throwerHead", "l", "removeSnow(amount)");
            l.removeSnow(3.0); // Nagyobb mennyiségű havat távolít el, mint a sima seprű
            Skeleton.exit("void");
            this.fuelAmount -= 15.0; // Fogyasztás
        } else {
            Skeleton.enter("throwerHead", "Skeleton", "A fej Empty állapotban van, nem csinál semmit.");
            Skeleton.exit("");
        }

        Skeleton.exit("void");
    }

    @Override
    public int getPrice() {
        Skeleton.enter("Hívó", "throwerHead", "getPrice()");
        Skeleton.exit("3000");
        return 3000;
    }

    @Override
    public String getName() {
        Skeleton.enter("Hívó", "throwerHead", "getName()");
        Skeleton.exit("Hómaró");
        return "Hómaró";
    }
}