package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * Osszefogja es kezeli az uthalozat osszes csomopontjat (Field-eket).
 * Grafkent kezeli a palyat, es biztosit utvonal-kereso funkcionalitast.
 * A havazast is o koordinalja a snow() metoduson keresztul.
 */
public class Map {

    /** A terkep osszes mezoje (Lane, Building leszarmazottjai). */
    private List<Field> fields = new ArrayList<>();

    /**
     * Letrehoz egy ures terkepobjektumot.
     */
    public Map() {
    }

    /**
     * Visszaadja a legrovidebb jarhato utvonalat ket mezo kozott.
     *
     * @param from A kiindulasi mezo.
     * @param to A celmezo.
     * @return A legrovidebb ut mezoinek listaja.
     */
    public List<Field> findShortestPath(Field from, Field to) {
        Skeleton.enter("car", "map", "findShortestPath(l1, target)");
        Skeleton.exit("[l2, target]");
        return new ArrayList<>();
    }

    /**
     * Minden Lane tipusu mezon noveli a horeteg vastagsagat.
     * SD-02 alapjan: vegigiteral a fields listan es minden Lane-en
     * meghivja az addSnow(amount) metodust. Building tipusokon nem hiv addSnow()-t.
     */
    public void snow() {
        Skeleton.enter("gameLogic", "map", "snow()");

        // SD-02: Lane tipusu mezokre addSnow
        for (Field f : fields) {
            if (f instanceof Lane) {
                Lane lane = (Lane) f;
                Skeleton.enter("map", "lane", "addSnow(amount)");
                lane.addSnow(1.0);
                Skeleton.exit("void");
            }
        }

        Skeleton.exit("void");
    }

    /**
     * Hozzaad egy mezot a terkephez.
     *
     * @param f A hozzaadando mezo.
     */
    public void addField(Field f) {
        fields.add(f);
    }
}
