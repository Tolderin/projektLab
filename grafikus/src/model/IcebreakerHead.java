package model;

/**
 * Specialis hokotro fej, amely a jegpancel feltoresere
 * specializalodott. A feltort jeget ho formajaban az uton hagyja,
 * a csuszasveszely megszunik, de a sav havassa valik. Havat nem
 * takarit el, uzemanyagot nem igenyel.
 *
 * A 7. heti valtozas: a zuzalekra nincs hatasa (eltero a sopro/hanyo
 * fejtol). A clean() csak a jegre koncentral.
 */
public class IcebreakerHead extends CleanerHead {

    /**
     * Letrehoz egy jegtoro fejet.
     */
    public IcebreakerHead() {
        // 13. heti: jégtörő a 3. legolcsobb (söprő < hányó < jégtörő < sószóró < sárkány).
        this.price = 150;
        this.usesFuel = false;
        this.name = "icebreakerhead";
    }

    /**
     * A savon levo jegpancelt feltori es havava alakitja.
     *
     * @param lane A takaritando sav.
     */
    @Override
    public void clean(Lane lane) {
        if (lane.isFrozen) {
            lane.breakIce();
        }
    }
}
