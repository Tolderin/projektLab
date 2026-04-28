package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Az osszes epulet-tipus (GeneralBuilding, HomeBase, Terminal) kozos
 * absztrakt ososztalya. Implementalja a Field interfeszt -- igy az
 * epuletek a graf csomopontjai. Felelossege egyseges alapot adni
 * a szomszedossagi kapcsolatok es a parkolo jarmuvek kezelesehez.
 */
public abstract class Building implements Field {

    /** A szomszedos mezok listaja. */
    public final List<Field> neighbors = new ArrayList<>();

    /** Az epuletben aktualisan tartozkodo (parkolo) jarmuvek. */
    protected final List<Vehicle> parkedVehicles = new ArrayList<>();

    /**
     * Fogadja az erkezo jarmuvet. Building eseteben nincs csuszas-
     * mechanizmus, csak elhelyezzuk a parkedVehicles listaban.
     *
     * @param v Az erkezo jarmu.
     */
    @Override
    public void accept(Vehicle v) {
        parkedVehicles.add(v);
    }

    /**
     * Eltavolitja a tavozo jarmuvet az epuletbol.
     *
     * @param v A tavozo jarmu.
     */
    @Override
    public void remove(Vehicle v) {
        parkedVehicles.remove(v);
    }

    /**
     * Visszaadja a szomszedos mezok listajat.
     *
     * @return A szomszedok.
     */
    @Override
    public List<Field> getNeighbors() {
        return neighbors;
    }
}
