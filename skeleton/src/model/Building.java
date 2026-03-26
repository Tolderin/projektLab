package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * Az összes épülettípus (GeneralBuilding, HomeBase, Terminal) közös absztrakt
 * ősosztálya[cite: 1225, 1227].
 * Implementálja a Field interfészt[cite: 1227].
 * Felelőssége, hogy egységes alapot biztosítson az épületeknek a szomszédossági
 * kapcsolatok kezeléséhez[cite: 1228].
 */
public abstract class Building implements Field {

    /** A szomszédos mezők listája (Field interfészből)[cite: 1232]. */
    protected List<Field> neighbors = new ArrayList<>();

    /**
     * Fogadja az érkező járművet (Field-ből felüldefiniálva)[cite: 1234].
     */
    @Override
    public void accept(Vehicle v) {
        Skeleton.enter("Hívó", "building", "accept(v)");
        Skeleton.exit("void");
    }

    /**
     * Eltávolítja a távozó járművet (Field-ből felüldefiniálva)[cite: 1234].
     */
    @Override
    public void remove(Vehicle v) {
        Skeleton.enter("Hívó", "building", "remove(v)");
        Skeleton.exit("void");
    }

    /**
     * Visszaadja a szomszédos mezőket (Field-ből)[cite: 1234].
     */
    @Override
    public List<Field> getNeighbors() {
        Skeleton.enter("Hívó", "building", "getNeighbors()");
        Skeleton.exit("List<Field>");
        return neighbors;
    }
}