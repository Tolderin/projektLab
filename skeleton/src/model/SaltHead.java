package model;

import skeleton.Skeleton;

/**
 * A sószóró fej sót juttat az útra, ami megakadályozza a hó letaposás általi
 * jéggé fagyását.
 * Működéséhez só (üzemanyag) szükséges. Két állapota van: Fueled (van só) és
 * Empty (nincs só).
 * Ha kifogy, a clean() metódus nem csinál semmit.
 */
public class SaltHead extends CleanerHead {

    public SaltHead() {
        this.fuelAmount = 100.0; // Kezdeti töltet
    }

    /**
     * Sózza a sávot, ha van elegendő só a tartályban.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "saltHead", "clean(l)");

        // Állapotgép ellenőrzése a felhasználón keresztül
        boolean isFueled = Skeleton.askQuestion("Van elegendő só a tartályban (fuelAmount > 0)?");

        if (isFueled) {
            Skeleton.enter("saltHead", "l", "applySaltEffect(duration)");
            l.applySaltEffect(5.0); // 5 körig tartó sóhatás
            Skeleton.exit("void");
            this.fuelAmount -= 10.0; // Fogyasztás
        } else {
            Skeleton.enter("saltHead", "Skeleton", "A fej Empty állapotban van, nem csinál semmit.");
            Skeleton.exit("");
        }

        Skeleton.exit("void");
    }

    @Override
    public int getPrice() {
        Skeleton.enter("Hívó", "saltHead", "getPrice()");
        Skeleton.exit("2500");
        return 2500;
    }

    @Override
    public String getName() {
        Skeleton.enter("Hívó", "saltHead", "getName()");
        Skeleton.exit("Sószóró");
        return "Sószóró";
    }
}