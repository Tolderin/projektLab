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
    // DragonHead.java javítása
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "dragonHead", "clean(l)");
        if (this.fuelAmount > 0) {
            l.removeSnow(1.0);
            l.removeIce();
            this.fuelAmount -= 20.0;
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