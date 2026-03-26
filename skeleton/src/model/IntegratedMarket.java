package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * A telephelyen lévő bolt, amelyből a takarítók eszközöket és üzemanyagot
 * vásárolhatnak.
 * Felelőssége az eladható termékek nyilvántartása és a vásárlási tranzakciók
 * lebonyolítása.
 */
public class IntegratedMarket {

    /** A boltban kapható termékek listája[cite: 1375]. */
    private List<IPurchasable> availableItems = new ArrayList<>();

    /**
     * Megvásárol egy terméket ha van elég pénz; igazzal tér vissza ha sikeres[cite:
     * 1377].
     */
    public boolean buyItem(SnowPlow buyer, IPurchasable item) {
        Skeleton.enter("takarító", "market", "buyItem(sp, item)");

        // A dokumentáció alapján a vásárláskor meg kell kérdezni a felhasználót[cite:
        // 273, 274].
        boolean hasEnoughMoney = Skeleton.askQuestion("Van elegendő pénze a játékosnak a vásárláshoz?");

        if (hasEnoughMoney) {
            Skeleton.enter("market", "Skeleton", "success");
            Skeleton.exit("");
            Skeleton.exit("true");
            return true;
        } else {
            Skeleton.enter("market", "Skeleton", "false");
            Skeleton.exit("");
            Skeleton.exit("false");
            return false;
        }
    }

    /**
     * Visszaadja a megvásárolható termékek listáját[cite: 1378].
     */
    public List<IPurchasable> getAvailableItems() {
        Skeleton.enter("Hívó", "market", "getAvailableItems()");
        Skeleton.exit("List<IPurchasable>");
        return availableItems;
    }
}