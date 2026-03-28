package gonoszonosz;

import java.util.List;

/**
 * A játékpálya gráfjának alapköve.
 * Minden pályaelem (Lane, Building leszármazottjai) ezt az interfészt valósítja meg.
 * Biztosítja, hogy a járművek egységesen tudjanak mozogni a gráfban,
 * és hogy minden csomópont ismerje szomszédait.
 */
public interface Field {

    /**
     * Fogadja az érkező járművet erre a mezőre.
     * @param v az érkező jármű
     */
    void accept(Vehicle v);

    /**
     * Eltávolítja a távozó járművet erről a mezőről.
     * @param v a távozó jármű
     */
    void remove(Vehicle v);

    /**
     * Visszaadja a szomszédos mezők listáját.
     * @return szomszédos Field objektumok listája
     */
    List<Field> getNeighbors();
}
