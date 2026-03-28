package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * Az összes épülettípus (GeneralBuilding, HomeBase, Terminal) közös absztrakt ősosztálya.
 * Implementálja a Field interfészt. Felelőssége, hogy egységes alapot biztosítson az
 * épületeknek a szomszédossági kapcsolatok kezeléséhez.
 */
public abstract class Building implements Field {

    /** A szomszédos mezők listája (Field interfészből). */
    protected List<Field> neighbors;

    /**
     * Létrehoz egy új épületet üres szomszédsági listával.
     */
    public Building() {
        this.neighbors = new ArrayList<>();
    }

    /**
     * Fogadja az érkező járművet az épületbe (pl. parkoló).
     * @param v az érkező jármű
     */
    @Override
    public abstract void accept(Vehicle v);

    /**
     * Eltávolítja a távozó járművet az épületből.
     * @param v a távozó jármű
     */
    @Override
    public abstract void remove(Vehicle v);

    /**
     * Visszaadja a szomszédos mezők listáját.
     * @return szomszédos Field objektumok listája
     */
    @Override
    public List<Field> getNeighbors() {
        return neighbors;
    }

    /**
     * Hozzáad egy szomszédos mezőt az épület szomszédsági listájához.
     * @param f a hozzáadandó szomszéd mező
     */
    public void addNeighbor(Field f) {
        neighbors.add(f);
    }
}
