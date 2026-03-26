package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * A telephelyen levo bolt, amelybol a takaritok eszkozoket
 * es uzemanyagot vasarolhatnak.
 * Felelossege az eladhato termekek nyilvantartasa es a vasarlasi
 * tranzakciok lebonyolitasa.
 */
public class IntegratedMarket {

    /** A boltban kaphato termekek listaja. */
    private List<IPurchasable> availableItems = new ArrayList<>();

    /**
     * Megvasarol egy termeket ha van eleg penz.
     * SD-12/13/14 alapjan: I/N kerdes a jatekos penzerol.
     * Igazzal ter vissza ha sikeres, hamissal ha nem.
     *
     * @param buyer A vasarlo hokotro (a jatekos neven).
     * @param item A megvasarolando termek.
     * @return true ha sikeres a vasarlas, false ha nem.
     */
    public boolean buyItem(SnowPlow buyer, IPurchasable item) {
        Skeleton.enter("takarito", "market", "buyItem(sp, item)");

        boolean hasEnoughMoney = Skeleton.askQuestion(
                "Van elegendo penze a jatekosnak a vasarlashoz?");

        if (hasEnoughMoney) {
            Skeleton.exit("true");
            return true;
        } else {
            Skeleton.exit("false");
            return false;
        }
    }

    /**
     * Visszaadja a megvasarolhato termekek listajat.
     *
     * @return A kaphato termekek listaja.
     */
    public List<IPurchasable> getAvailableItems() {
        return availableItems;
    }
}
