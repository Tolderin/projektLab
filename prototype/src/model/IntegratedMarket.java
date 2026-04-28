package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A telephelyen levo bolt, amelybol a takaritok eszkozoket es
 * uzemanyagot vasarolhatnak. Felelossege a vasarlasi tranzakciok
 * lebonyolitasa, az ar levonasa a Player penzkesletebol.
 */
public class IntegratedMarket {

    /** A boltban kaphato termekek listaja. */
    private final List<IPurchasable> availableItems = new ArrayList<>();

    /**
     * Megvasarol egy termeket. Ha a vasarlonak eleg penze van,
     * a tranzakcio lebonyolodik (penz csokken), kulonben false-szal
     * tert vissza.
     *
     * @param buyer A vasarlo jatekos (Cleaner).
     * @param item  A megvasarolando termek.
     * @return true ha sikeres, false ha nincs eleg penz.
     */
    public boolean buyItem(Player buyer, IPurchasable item) {
        if (buyer.money >= item.getPrice()) {
            buyer.money -= item.getPrice();
            return true;
        }
        return false;
    }

    /**
     * Visszaadja a megvasarolhato termekek listajat.
     *
     * @return A kaphato termekek.
     */
    public List<IPurchasable> getAvailableItems() {
        return availableItems;
    }
}
