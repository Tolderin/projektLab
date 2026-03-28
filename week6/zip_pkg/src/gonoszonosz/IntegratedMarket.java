package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * A telephelyen lévő bolt, amelyből a takarítók eszközöket és üzemanyagot vásárolhatnak.
 * CD-12 alapján: buyItem(sp, newHead) → Skeleton kérdés →
 * I: sp.money -= getPrice(), attachments.add(newHead), success visszatérés.
 * N: false visszatérés.
 * CD-13 alapján: buyItem(sp, fuelItem) → Skeleton kérdés →
 * I: sp.money -= getPrice(), success visszatérés. N: false.
 * CD-14 alapján: buyItem(existingSp, newSp) → Skeleton kérdés →
 * I: new SnowPlow(), existingSp.money -= getPrice(), acceptSnowPlow(sp). N: false.
 */
public class IntegratedMarket {

    /** A boltban kapható termékek listája. */
    private List<IPurchasable> availableItems;

    /**
     * Létrehoz egy új áruházat.
     */
    public IntegratedMarket() {
        this.availableItems = new ArrayList<>();
    }

    /**
     * CD-12/13: Megvásárol egy terméket.
     * Skeleton kérdés: "Van elegendő pénze a játékosnak a vásárláshoz?"
     * I → sp.money -= getPrice(), ha fej: attachments.add(newHead), success.
     * N → false.
     * @param buyer a vásárló takarító
     * @param item  a megvásárolni kívánt termék
     * @return true ha sikeres, false ha nincs elég pénz
     */
    public boolean buyItem(Cleaner buyer, IPurchasable item) {
        Skeleton.call("Cleaner", "IntegratedMarket", "buyItem(sp, " + item.getName() + ")");
        boolean hasMoney = Skeleton.ask("Van elegendő pénze a játékosnak a vásárláshoz?");
        if (hasMoney) {
            // CD-12: 1.1 sp.money -= getPrice()
            buyer.deductMoney(item.getPrice());
            // CD-12: 1.2 attachments.add(newHead) – ha a termék CleanerHead
            if (item instanceof CleanerHead) {
                // A fej hozzáadása a SnowPlow attachments listájához a swapHead során történik
            }
            Skeleton.ret("success");
            return true;
        } else {
            Skeleton.ret("false");
            return false;
        }
    }

    /**
     * Visszaadja a megvásárolható termékek listáját.
     * @return elérhető termékek listája
     */
    public List<IPurchasable> getAvailableItems() {
        Skeleton.call("Cleaner", "IntegratedMarket", "getAvailableItems()");
        Skeleton.ret("List<IPurchasable>(size=" + availableItems.size() + ")");
        return availableItems;
    }

    /**
     * Hozzáad egy terméket az áruház kínálatához.
     * @param item a hozzáadandó termék
     */
    public void addItem(IPurchasable item) {
        availableItems.add(item);
    }
}
