package model;

import skeleton.Skeleton;

/**
 * Specialis hokotro fej, amely a jegpancel feltoresere specializalodott.
 * A feltort jeget ho formajaban az uton hagyja, a csuszaszveszely megszunik,
 * de a sav havassa valik. Havat nem takarit el, uzemanyagot nem igenyel.
 *
 * SD-11: IcebreakerHead.clean(lane) ellenorzi, hogy a sav jeges-e.
 * Ha igen: lane.breakIce() es lane.addSnow(iceThickness * faktor).
 * Ha nem: nem csinal semmit.
 */
public class IcebreakerHead extends CleanerHead {

    /**
     * Letrehozza a jegtoro fejet.
     * Ar: 3000.
     * Nem hasznal uzemanyagot.
     */
    public IcebreakerHead() {
        this.price = 3000;
        this.usesFuel = false;
    }

    /**
     * A savon levo jegpancelt feltori es havava alakitja.
     * SD-11 alapjan: I/N kerdes a jeges allapotrol.
     * Ha jeges: breakIce() + addSnow(iceThickness * faktor).
     * Ha nem jeges: nem csinal semmit.
     *
     * @param l A takaritando sav.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "icebreakerHead", "clean(l)");

        boolean isFrozen = Skeleton.askQuestion("Jeges a sav (isFrozen == true)?");

        if (isFrozen) {
            Skeleton.enter("icebreakerHead", "l", "breakIce()");
            l.breakIce();
            Skeleton.exit("void");

            Skeleton.enter("icebreakerHead", "l", "addSnow(iceThickness * faktor)");
            l.addSnow(1.0);
            Skeleton.exit("void");
        }

        Skeleton.exit("void");
    }

    /**
     * Visszaadja a jegtoro fej arat.
     *
     * @return 3000
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a fej nevet.
     *
     * @return "IcebreakerHead"
     */
    @Override
    public String getName() {
        return "IcebreakerHead";
    }
}
