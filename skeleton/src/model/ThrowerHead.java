package model;

import skeleton.Skeleton;

/**
 * A hanyo fej (ThrowerHead) a havat messzire szorja az uttestrol,
 * jellemzoen a jardara. A szomszedos savot nem modositja.
 * Jeget nem tud eltavolitani. Uzemanyagot nem igenyel.
 *
 * SD-08: ThrowerHead.clean(lane) meghivja a lane.removeSnow(amount)-t.
 * A throwDistance ertekenek megfeleloen a ho messzire kerul,
 * szomszedos Lane-en nem tortenik addSnow() hivas.
 */
public class ThrowerHead extends CleanerHead {

    /** Az a tavolsag, ameddig a hanyo fej a havat elhajitja. */
    private double throwDistance;

    /**
     * Letrehozza a hanyo fejet.
     * Ar: 2000 (masodik legolcsobb).
     * Nem hasznal uzemanyagot.
     */
    public ThrowerHead() {
        this.price = 2000;
        this.usesFuel = false;
        this.throwDistance = 10.0;
    }

    /**
     * A sav letakaritasa: a havat messzire szorja, szomszedos savot nem modosit.
     * SD-08 alapjan: csak removeSnow hivas, semmi mas.
     *
     * @param l A takaritando sav.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "throwerHead", "clean(l)");

        Skeleton.enter("throwerHead", "l", "removeSnow(amount)");
        l.removeSnow(3.0);
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    /**
     * Visszaadja a hanyo fej arat.
     *
     * @return 2000
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a fej nevet.
     *
     * @return "ThrowerHead"
     */
    @Override
    public String getName() {
        return "ThrowerHead";
    }
}
