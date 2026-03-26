package model;

import java.util.List;

/**
 * A gráf alapköve: minden pályaelem (Lane, Cross, Building leszármazottjai)
 * ezt az interfészt valósítja meg[cite: 1302].
 * Biztosítja, hogy a járművek egységesen tudjanak mozogni a gráfban, és hogy
 * minden csomópont ismerje szomszédait[cite: 1303].
 */
public interface Field {

    /**
     * Fogadja az érkező járművet erre a mezőre[cite: 1307].
     * * @param v Az érkező jármű (Vehicle).
     */
    void accept(Vehicle v);

    /**
     * Eltávolítja a távozó járművet erről a mezőről[cite: 1307].
     * * @param v A távozó jármű (Vehicle).
     */
    void remove(Vehicle v);

    /**
     * Visszaadja a szomszédos mezők listáját[cite: 1307].
     * * @return A szomszédos Field objektumok listája.
     */
    List<Field> getNeighbors();
}