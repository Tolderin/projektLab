package model;

/**
 * A hanyo fej a havat messzire szorja az uttestrol (jellemzoen a
 * jardara), nem csak a szomszedos savba. A 7. heti valtozas szerint
 * a zuzalekot is messzire szorja. Jeget nem tud eltavolitani.
 * Uzemanyagot nem igenyel.
 */
public class ThrowerHead extends CleanerHead {

    /** Az a tavolsag, ameddig a hanyo fej a havat elhajitja. */
    public double throwDistance = 10.0;

    /**
     * Letrehoz egy hanyo fejet.
     */
    public ThrowerHead() {
        this.price = 50;
        this.usesFuel = false;
        this.name = "throwerhead";
    }

    /**
     * A sav letakaritasa: a havat es a zuzalekot eltavolitja az
     * uttestrol veglegesen (a szomszed savokat NEM modositja, mert
     * a ho/zuzalek a jardara kerul).
     *
     * @param lane A takaritando sav.
     */
    @Override
    public void clean(Lane lane) {
        double sAmount = lane.snowDepth;
        double gAmount = lane.gravelDepth;
        lane.removeSnow(sAmount);
        lane.removeGravel(gAmount);
        // Szomszedos savokat nem modositunk
    }
}
