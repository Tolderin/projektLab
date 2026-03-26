package model;

import java.util.List;

/**
 * A graf alapkove: minden palyaelem (Lane, Building leszarmazottjai)
 * ezt az interfeszt valositja meg.
 * Biztositja, hogy a jarmuvek egysegesen tudjanak mozogni a grafban,
 * es hogy minden csomopont ismerje szomszedjait.
 */
public interface Field {

    /**
     * Fogadja az erkezo jarmuvet erre a mezore.
     *
     * @param v Az erkezo jarmu (Vehicle).
     */
    void accept(Vehicle v);

    /**
     * Eltavolitja a tavozo jarmuvet errol a mezorol.
     *
     * @param v A tavozo jarmu (Vehicle).
     */
    void remove(Vehicle v);

    /**
     * Visszaadja a szomszedos mezok listajat.
     *
     * @return A szomszedos Field objektumok listaja.
     */
    List<Field> getNeighbors();
}
