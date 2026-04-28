package model;

import java.util.List;

/**
 * A graf alapkove: minden palyaelem (Lane, Building leszarmazottjai)
 * ezt az interfeszt valositja meg. Biztositja, hogy a jarmuvek
 * egysegesen tudjanak mozogni a grafban, es hogy minden csomopont
 * ismerje szomszedait.
 */
public interface Field {

    /**
     * Fogadja az erkezo jarmuvet. Lane eseteben ez aktivalhatja a
     * csuszas-mechanizmust ha a sav jeges; Building eseteben csak
     * elhelyezi a jarmuvet a parkedVehicles listaban.
     *
     * @param v Az erkezo jarmu.
     */
    void accept(Vehicle v);

    /**
     * Eltavolitja a tavozo jarmuvet errol a mezorol.
     *
     * @param v A tavozo jarmu.
     */
    void remove(Vehicle v);

    /**
     * Visszaadja a szomszedos mezok listajat. A graf navigaciohoz
     * (BFS/Dijkstra a Map-ben), illetve a savvaltas (Car.changeLane)
     * dontesehez hasznalt.
     *
     * @return A szomszedos Field objektumok listaja.
     */
    List<Field> getNeighbors();
}
