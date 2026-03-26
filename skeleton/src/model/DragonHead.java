package model;

import skeleton.Skeleton;

/**
 * A sárkányfej gázlánggal (vagy mechanikus zúzóval) képes feltörni és
 * eltávolítani a jégpáncélt az útról.
 * Működéséhez üzemanyag (gáz) szükséges. Ha a fuelAmount 0 (Empty állapot), nem
 * csinál semmit.
 */
public class DragonHead extends CleanerHead {

    public DragonHead() {
        this.fuelAmount = 100.0; // Kezdeti töltet
    }

    /**
     * Feltöri a jeget a sávon, ha van elegendő üzemanyag a tartályban.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "dragonHead", "clean(l)");

        // Állapotgép ellenőrzése a felhasználón keresztül
        boolean isFueled = Skeleton.askQuestion("Van elegendő gáz a tartályban (fuelAmount > 0)?");

        if (isFueled) {
            Skeleton.enter("dragonHead", "l", "removeIce()");
            l.removeIce();
            Skeleton.exit("void");
            this.fuelAmount -= 20.0; // Fogyasztás (magasabb, mint a sónál)
        } else {
            Skeleton.enter("dragonHead", "Skeleton", "A fej Empty állapotban van, nem csinál semmit.");
            Skeleton.exit("");
        }

        Skeleton.exit("void");
    }

    @Override
    public int getPrice() {
        Skeleton.enter("Hívó", "dragonHead", "getPrice()");
        Skeleton.exit("4000");
        return 4000;
    }

    @Override
    public String getName() {
        Skeleton.enter("Hívó", "dragonHead", "getName()");
        Skeleton.exit("Sárkányfej");
        return "Sárkányfej";
    }
}