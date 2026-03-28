package gonoszonosz;

/**
 * A telephelyen lévő boltban megvásárolható termékeket összefogó interfész.
 * Minden vásárolható terméknek (hókotró fej, üzemanyag, hókotró) meg kell valósítania.
 */
public interface IPurchasable {

    /**
     * Visszaadja a termék árát.
     * @return a termék ára egész számban
     */
    int getPrice();

    /**
     * Visszaadja a termék nevét.
     * @return a termék neve
     */
    String getName();
}
