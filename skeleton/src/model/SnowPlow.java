package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * A takarito jatekos altal iranyitott munkagep.
 * A raszerelt fej segitsegevel takaritja a savokat, penzt termel a takaritonak.
 * Holancai miatt jegen sem csuszik. A telephelyen tud fejet cserelni
 * es utantolteni.
 *
 * SD-06: SnowPlow.move() — mozgatas es takaritas.
 */
public class SnowPlow extends Vehicle implements IPurchasable {

    /** A jelenleg birtokolt hokotro fejek listaja. */
    private List<CleanerHead> attachments = new ArrayList<>();

    /** A jelenleg felszerelt hokotro fej. */
    private CleanerHead activeHead;

    /** A hokotro altal termelt penz (a jatekos szamlajan). */
    private int money = 0;

    /**
     * Mozgatja a hokotrot a tervezett utvonal szerint.
     * SD-06 alapjan: remove(sp) -> accept(sp) -> cleanCurrentLane()
     */
    @Override
    public void move() {
        Skeleton.enter("gameLogic", "snowPlow", "move()");

        Skeleton.enter("snowPlow", "l1", "remove(sp)");
        Skeleton.exit("void");

        Skeleton.enter("snowPlow", "l2", "accept(sp)");
        Skeleton.exit("void");

        // Takaritas — tenylegesen meghivja cleanCurrentLane()-t
        cleanCurrentLane();

        Skeleton.exit("void");
    }

    /**
     * Kicsereli az aktiv fejet a megadottra (csak telephelyen).
     * SD-12 alapjan: activeHead = newHead.
     *
     * @param newHead Az uj fej.
     */
    public void changeHead(CleanerHead newHead) {
        Skeleton.enter("homeBase", "snowPlow", "changeHead(newHead)");

        Skeleton.enter("snowPlow", "snowPlow", "activeHead = newHead");
        this.activeHead = newHead;
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    /**
     * A jelenlegi savon vegrehajtja az aktiv fejre jellemzo takaritast.
     * SD-06 alapjan: meghivja activeHead.clean(currentLane) metodust.
     * Tenylegesen delegalja a hivast a felszerelt fejnek.
     */
    public void cleanCurrentLane() {
        Skeleton.enter("snowPlow", "snowPlow", "cleanCurrentLane()");

        if (activeHead != null) {
            // Tenylegesen meghivja a fej clean() metodusat
            activeHead.clean(currentLane != null ? currentLane : new Lane());
        }

        Skeleton.exit("void");
    }

    /**
     * Az aktiv fej uzemanyag-utantoltese.
     * SD-13 alapjan: HomeBase -> sp -> activeHead.refillFuel(amount)
     *
     * @param amount A betoltendo mennyiseg.
     */
    public void refuelActiveHead(double amount) {
        if (activeHead instanceof SaltHead) {
            ((SaltHead) activeHead).refillFuel(amount);
        } else if (activeHead instanceof DragonHead) {
            ((DragonHead) activeHead).refillFuel(amount);
        }
    }

    /**
     * Visszaadja a hokotro arat.
     * A legdragabb segedeszkoz.
     *
     * @return 10000
     */
    @Override
    public int getPrice() {
        return 10000;
    }

    /**
     * Visszaadja a termek nevet.
     *
     * @return "SnowPlow"
     */
    @Override
    public String getName() {
        Skeleton.enter("gameLogic", "snowPlow", "getName()");
        Skeleton.exit("SnowPlow");
        return "SnowPlow";
    }

    /**
     * Visszaadja a hokotro altal termelt penzt.
     * SD-16-ban hasznalt a jatek vegi eredmeny osszealitasahoz.
     *
     * @return A termelt penz osszege.
     */
    public int getMoney() {
        Skeleton.enter("gameLogic", "snowPlow", "getMoney()");
        Skeleton.exit(String.valueOf(money));
        return money;
    }
}
