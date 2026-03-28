package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * A takarító játékos által irányított munkagép.
 * A rászerelt fej segítségével takarítja a sávokat, pénzt termel a takarítónak.
 * Hóláncai miatt jégen sem csúszik. A telephelyen tud fejet cserélni és utántölteni.
 * Implementálja az IPurchasable interfészt, mivel a boltban megvásárolható.
 */
public class SnowPlow extends Vehicle implements IPurchasable {

    /** A jelenleg birtokolt hókotró fejek listája. */
    private List<CleanerHead> attachments;

    /** A jelenleg felszerelt hókotró fej. */
    private CleanerHead activeHead;

    /** A hókotró ára a boltban. */
    private int price;

    /** A hókotró neve. */
    private String name;

    /** A takarító, akihez ez a hókotró tartozik. */
    private Cleaner owner;

    /** Mennyit keresett eddig ez a hókotró. */
    private int money;

    /** Hány egységnyi pénzt termel minden megtakarított sávegység után. */
    private static final int EARN_PER_LANE = 10;

    /**
     * Létrehoz egy új hókotrót alapfelszereléssel (söprő fej).
     * @param name  a hókotró neve
     * @param price a hókotró ára
     * @param owner a tulajdonos takarító
     */
    public SnowPlow(String name, int price, Cleaner owner) {
        this.name = name;
        this.price = price;
        this.owner = owner;
        this.attachments = new ArrayList<>();
        this.activeHead = new SweepHead();
        this.attachments.add(activeHead);
        this.money = 0;
    }

    /**
     * Mozgatja a hókotrót a tervezett útvonal szerint.
     * Mozgás után megtisztítja az aktuális sávot.
     */
    @Override
    public void move() {
        Skeleton.call("GameLogic", "SnowPlow", "move()");
        if (nextField != null) {
            if (currentLane != null) {
                currentLane.remove(this);
            }
            nextField.accept(this);
            if (nextField instanceof Lane) {
                currentLane = (Lane) nextField;
            }
            cleanCurrentLane();
        }
        Skeleton.ret("void");
    }

    /**
     * A jelenlegi sávon végrehajtja az aktív fejre jellemző takarítást.
     * A megtakarítás után jutalom kerül a takarítóhoz.
     */
    public void cleanCurrentLane() {
        Skeleton.call("SnowPlow", "SnowPlow", "cleanCurrentLane()");
        if (currentLane != null && activeHead != null) {
            activeHead.clean(currentLane);
            // Jutalom jóváírása
            owner.addMoney(EARN_PER_LANE);
            money += EARN_PER_LANE;
        }
        Skeleton.ret("void");
    }

    /**
     * Kicseréli az aktív fejet a megadottra (csak telephelyen lehetséges).
     * @param newHead az új fej, amelyet fel kell szerelni
     */
    public void changeHead(CleanerHead newHead) {
        Skeleton.call("HomeBase", "SnowPlow", "changeHead(" + newHead.getName() + ")");
        if (!attachments.contains(newHead)) {
            attachments.add(newHead);
        }
        activeHead = newHead;
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a hókotró árát (IPurchasable interfészből).
     * @return a hókotró ára
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a hókotró nevét (IPurchasable interfészből).
     * @return a hókotró neve
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Visszaadja a jelenleg felszerelt aktív fejet.
     * @return az aktív CleanerHead objektum
     */
    public CleanerHead getActiveHead() {
        return activeHead;
    }

    /**
     * Visszaadja az eddig megszerzett pénz mennyiségét.
     * @return megszerzett pénz
     */
    public int getMoney() {
        return money;
    }
}
