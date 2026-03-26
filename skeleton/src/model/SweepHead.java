package model;

import java.util.List;
import skeleton.Skeleton;

/**
 * A hokotro alapertelmezett feje, amely a havat sepri le az utrol.
 * A havat a szomszedos savba vagy az ut melle tolja.
 * Jeget nem tud eltavolitani. Uzemanyagot nem igenyel.
 *
 * SD-07: SweepHead.clean(lane) meghivja a lane.removeSnow(amount)-t,
 * majd a lane.getNeighbors()-bol megkapja a szomszed Lane-t,
 * vegul a neighborLane.addSnow(amount)-t hivja – attolva a havat.
 */
public class SweepHead extends CleanerHead {

    /**
     * Letrehozza a sopro fejet.
     * Ar: 1000 (a legolcsobb fej).
     * Nem hasznal uzemanyagot.
     */
    public SweepHead() {
        this.price = 1000;
        this.usesFuel = false;
    }

    /**
     * A sav letakaritasa: a havat eltavolitja es attol a szomszedos savba.
     * SD-07 alapjan: removeSnow -> getNeighbors -> addSnow(neighbor).
     *
     * @param l A takaritando sav.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "sweepHead", "clean(l)");

        // Ho eltavolitasa a savrol
        Skeleton.enter("sweepHead", "l", "removeSnow(amount)");
        l.removeSnow(1.0);
        Skeleton.exit("void");

        // Szomszedos savok lekerdezese
        Skeleton.enter("sweepHead", "l", "getNeighbors()");
        List<Field> neighbors = l.getNeighbors();
        Skeleton.exit("[neighbor, ...]");

        // Ho attolasa a szomszedos savba
        if (!neighbors.isEmpty() && neighbors.get(0) instanceof Lane) {
            Lane neighbor = (Lane) neighbors.get(0);
            Skeleton.enter("sweepHead", "neighbor", "addSnow(amount)");
            neighbor.addSnow(1.0);
            Skeleton.exit("void");
        }

        Skeleton.exit("void");
    }

    /**
     * Visszaadja a sopro fej arat.
     *
     * @return 1000
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a fej nevet.
     *
     * @return "SweepHead"
     */
    @Override
    public String getName() {
        return "SweepHead";
    }
}
