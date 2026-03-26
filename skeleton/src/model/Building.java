package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Az osszes epulettipus (GeneralBuilding, HomeBase, Terminal) kozos absztrakt
 * ososztalya. Implementalja a Field interfeszt.
 * Felelossege, hogy egyseges alapot biztositson az epuleteknek
 * a szomszedossagi kapcsolatok kezelesehez.
 */
public abstract class Building implements Field {

    /** A szomszedos mezok listaja. */
    protected List<Field> neighbors = new ArrayList<>();

    /**
     * Fogadja az erkezo jarmuvet.
     * A Lane-ektol elteroen az epuletek nem logolnak enter/exit-et,
     * a hivo felel a naplozasert.
     *
     * @param v Az erkezo jarmu.
     */
    @Override
    public void accept(Vehicle v) {
        // Skeleton: a hivo oldal logol
    }

    /**
     * Eltavolitja a tavozo jarmuvet.
     *
     * @param v A tavozo jarmu.
     */
    @Override
    public void remove(Vehicle v) {
        // Skeleton: a hivo oldal logol
    }

    /**
     * Visszaadja a szomszedos mezok listajat.
     *
     * @return A szomszedos Field objektumok listaja.
     */
    @Override
    public List<Field> getNeighbors() {
        return neighbors;
    }
}
