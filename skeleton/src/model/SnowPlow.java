package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * A takarító játékos által irányított munkagép. [cite: 1476]
 * A rászerelt fej segítségével takarítja a sávokat, pénzt termel a takarítónak.
 * [cite: 1476]
 * Hóláncai miatt jégen sem csúszik. A telephelyen tud fejet cserélni és
 * utántölteni. [cite: 1477]
 */
public class SnowPlow extends Vehicle implements IPurchasable {

    /** a jelenleg birtokolt hókotró fejek listája [cite: 1486] */
    private List<CleanerHead> attachments = new ArrayList<>();

    /** a jelenleg felszerelt hókotró fej [cite: 1486] */
    private CleanerHead activeHead;

    /**
     * mozgatja a hókotrót a tervezett útvonal szerint (Vehicle-ből
     * felüldefiniálva). [cite: 1488]
     */
    @Override
    public void move() {
        Skeleton.enter("takarító", "snowPlow", "move()");

        Skeleton.enter("snowPlow", "l1", "remove(sp)");
        Skeleton.exit("void");

        Skeleton.enter("snowPlow", "l2", "accept(sp)");
        Skeleton.exit("void");

        cleanCurrentLane();

        Skeleton.exit("void");
    }

    /**
     * kicseréli az aktív fejet a megadottra (csak telephelyen). [cite: 1488, 1489]
     */
    public void changeHead(CleanerHead newHead) {
        Skeleton.enter("homeBase", "snowPlow", "changeHead(newHead)");

        Skeleton.enter("snowPlow", "snowPlow", "activeHead = newHead");
        this.activeHead = newHead;
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    /**
     * a jelenlegi sávon végrehajtja az aktív fejre jellemző takarítást. [cite:
     * 1489]
     */
    public void cleanCurrentLane() {
        Skeleton.enter("snowPlow", "snowPlow", "cleanCurrentLane()");

        if (activeHead != null) {
            Skeleton.enter("snowPlow", "activeHead", "clean(l2)");
            Skeleton.exit("void");
        } else {
            // A tesztelhetőség kedvéért szimuláljuk az aktív fejet, ha a dokumentáció
            // szerint haladunk
            Skeleton.enter("snowPlow", "head:CleanerHead", "clean(l2)");
            Skeleton.exit("void");
        }

        Skeleton.exit("void");
    }

    /**
     * visszaadja a hókotró árát (IPurchasable-ből). [cite: 1489]
     */
    @Override
    public int getPrice() {
        Skeleton.enter("Hívó", "snowPlow", "getPrice()");
        Skeleton.exit("5000"); // Példa érték
        return 5000;
    }

    /**
     * visszaadja a termék nevét (IPurchasable-ből). [cite: 1490]
     */
    @Override
    public String getName() {
        Skeleton.enter("Hívó", "snowPlow", "getName()");
        Skeleton.exit("Hókotró");
        return "Hókotró";
    }
}