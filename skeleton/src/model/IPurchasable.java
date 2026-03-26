package model;

/**
 * A boltban megvásárolható termékeket összefogó interfész.
 * Minden vásárolható terméknek (hókotró fej, üzemanyag, hókotró) meg kell
 * valósítania.
 */
public interface IPurchasable {

    /**
     * Visszaadja a termék árát.
     * 
     * @return A termék ára.
     */
    int getPrice();

    /**
     * Visszaadja a termék nevét.
     * 
     * @return A termék neve mint String.
     */
    String getName();
}