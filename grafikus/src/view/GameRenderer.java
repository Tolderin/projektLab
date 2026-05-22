package view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A teljes terkep kirajzolasanak orchestratora. Listakon tartja a
 * regisztralt RoadView, FieldView es VehicleView peldanyokat.
 *
 * A render() sorrend:
 *  1. RoadView-k: aszfalt-szinu hatter + sárga középvonal.
 *  2. FieldView-k (LaneView + BuildingView): sávok textúrái (hó,
 *     jég, zúzalék, só), épületek.
 *  3. drawIntersections: a kereszteződésekben (horizontális ∩
 *     vertikalis road-bounds) re-fillel az aszfalt-szín, hogy a
 *     sávok overlay-jei NE legyenek lathatok a kereszteződésnél
 *     -- igy az aszfalt-szürke kvadráns lesz a kereszteződés.
 *  4. VehicleView-k: a járművek a tetejére (legfelső szint).
 */
public class GameRenderer {

    /** A road-nezetek listaja (a savok hattere). */
    private final List<RoadView> roadViews = new ArrayList<>();

    /** A mezo-nezetek listaja, kirajzolasi sorrendben. */
    private final List<FieldView> fieldViews = new ArrayList<>();

    /** A jarmu-nezetek listaja, kirajzolasi sorrendben. */
    private final List<VehicleView> vehicleViews = new ArrayList<>();

    /** A palya elrendezesi adatai (mezo-ID -> bounds). */
    private final MapLayout layout;

    /**
     * Letrehoz egy GameRenderer-t a megadott layouttal.
     *
     * @param layout A palya kepernyo-elrendezese.
     */
    public GameRenderer(MapLayout layout) {
        this.layout = layout;
    }

    /**
     * Visszaadja a hasznalt layout objektumot.
     *
     * @return A layout.
     */
    public MapLayout getLayout() {
        return layout;
    }

    /**
     * Visszaadja a road-nezetek listajat.
     *
     * @return A road-view-k.
     */
    public List<RoadView> getRoadViews() {
        return roadViews;
    }

    /**
     * Visszaadja a kirajzolando mezo-nezetek listajat.
     *
     * @return A field-view-k.
     */
    public List<FieldView> getFieldViews() {
        return fieldViews;
    }

    /**
     * Visszaadja a kirajzolando jarmu-nezetek listajat.
     *
     * @return A vehicle-view-k.
     */
    public List<VehicleView> getVehicleViews() {
        return vehicleViews;
    }

    /**
     * Hozzaad egy road-nezetet a rendereles listajahoz.
     *
     * @param r A felvenni kivant road-view.
     */
    public void registerRoad(RoadView r) {
        roadViews.add(r);
    }

    /**
     * Hozzaad egy mezo-nezetet a rendereles listajahoz.
     *
     * @param v A felvenni kivant nezet.
     */
    public void registerField(FieldView v) {
        fieldViews.add(v);
    }

    /**
     * Hozzaad egy jarmu-nezetet a rendereles listajahoz.
     *
     * @param v A felvenni kivant nezet.
     */
    public void registerVehicle(VehicleView v) {
        vehicleViews.add(v);
    }

    /**
     * Eltavolitja az osszes regisztralt nezetet (uj load elott).
     */
    public void clear() {
        roadViews.clear();
        fieldViews.clear();
        vehicleViews.clear();
    }

    /**
     * A teljes palya kirajzolasa.
     *
     * @param g A celzott Graphics2D kontextus.
     */
    public void render(Graphics2D g) {
        // 1. Road hattere (aszfalt-szin az egesz road bounds-on,
        //    + sárga középvonal a kereszteződéseken kívul)
        for (RoadView rv : roadViews) {
            rv.draw(g);
        }
        // 2. Lane-ek overlay-jei (hó, jég, gravel, salt, blocked, irany).
        //    A Lane NEM rajzol külön hattert -- igy a road-aszfalt
        //    folytonosan latszik a keresztezodeseken is.
        for (FieldView fv : fieldViews) {
            if (fv instanceof LaneView) {
                fv.draw(g);
            }
        }
        // 3. Epuletek a sávok UTAN rajzolódnak (a road folott, ha
        //    atfednek)
        for (FieldView fv : fieldViews) {
            if (!(fv instanceof LaneView)) {
                fv.draw(g);
            }
        }
        // 4. Járművek a tetejen
        for (VehicleView vv : vehicleViews) {
            vv.draw(g);
        }
    }
}
