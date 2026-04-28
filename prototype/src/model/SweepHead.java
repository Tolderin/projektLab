package model;

import java.util.List;

/**
 * A hokotro alapertelmezett feje, amely a havat sopri le az utrol
 * a szomszedos savokba. A 7. heti valtozas szerint a zuzalekot is
 * eltakaritja es a szomszedos savba viszi (mintha ho lenne).
 * Jeget nem tud eltavolitani. Uzemanyagot nem igenyel.
 */
public class SweepHead extends CleanerHead {

    /**
     * Letrehoz egy sopro fejet.
     */
    public SweepHead() {
        this.price = 50;
        this.usesFuel = false;
        this.name = "sweephead";
    }

    /**
     * A sav letakaritasa: a havat es a zuzalekot eltavolitja, majd
     * a sav nyomvonala melle (azaz a kozvetlenul szomszedos savba)
     * tolja. Az implementacio az ELSO Lane-tipusu szomszedra tolja
     * a teljes mennyiseget (a 8. heti test 9 expected viselkedese
     * szerint), nem szetoszt egyenletesen.
     *
     * @param lane A takaritando sav.
     */
    @Override
    public void clean(Lane lane) {
        double sAmount = lane.snowDepth;
        double gAmount = lane.gravelDepth;
        lane.removeSnow(sAmount);
        lane.removeGravel(gAmount);

        // Az ELSO Lane-szomszedra tolja a hot/zuzalekot
        for (Field f : lane.getNeighbors()) {
            if (f instanceof Lane) {
                ((Lane) f).addSnow(sAmount);
                ((Lane) f).addGravel(gAmount);
                return;
            }
        }
    }
}
