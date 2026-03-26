package model;

import skeleton.Skeleton;

/**
 * A buszjaratok vegpontja, egyben Field a grafban (Building leszarmazott).
 * Felelossege a fordulok regisztralasa: amikor egy busz megerkezik,
 * jelzi a buszvezetonek a fordulo teljesiteset.
 */
public class Terminal extends Building {

    /**
     * Letrehoz egy vegallomast.
     */
    public Terminal() {
    }

    /**
     * Regisztralja a busz erkezeset es jelzi a fordulot a buszvezetonek.
     * SD-05 alapjan: a fordulo rogzitesre kerul.
     *
     * @param bus Az erkezo busz.
     */
    public void registerArrival(Bus bus) {
        Skeleton.enter("bus", "terminal", "registerArrival(bus)");
        Skeleton.exit("fordulo rogzitve");
    }
}
