package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A telephelyen levo bolt, amelybol a takaritok eszkozoket es
 * uzemanyagot vasarolhatnak. Felelossege a vasarlasi tranzakciok
 * lebonyolitasa, az ar levonasa a Player penzkesletebol.
 */
public class IntegratedMarket extends Observable {

    /** A boltban kaphato termekek listaja. */
    private final List<IPurchasable> availableItems = new ArrayList<>();

    /**
     * Megvasarol egy termeket. Ha a vasarlonak eleg penze van,
     * a tranzakcio lebonyolodik (penz csokken a Player.addMoney
     * push-jelzessel egyutt), kulonben false-szal tert vissza.
     *
     * @param buyer A vasarlo jatekos (Cleaner).
     * @param item  A megvasarolando termek.
     * @return true ha sikeres, false ha nincs eleg penz.
     */
    public boolean buyItem(Player buyer, IPurchasable item) {
        if (buyer.money >= item.getPrice()) {
            // A Player.addMoney negativ osszegre is mukodik, es
            // automatikusan notifyObservers("moneyChanged")-et hiv,
            // amit a HUDPanel a UI-bol azonnal eszrevesz.
            buyer.addMoney(-(double) item.getPrice());
            notifyObservers("itemBought");
            return true;
        }
        notifyObservers("itemUnaffordable");
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
