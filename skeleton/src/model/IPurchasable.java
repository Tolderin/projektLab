package model;

/**
 * A boltban megvasarolhato termekeket osszefogo interfesz.
 * Minden vasarolhato termeknek (hokotro fej, hokotro)
 * meg kell valositania.
 */
public interface IPurchasable {

    /**
     * Visszaadja a termek arat.
     *
     * @return A termek ara egesz szamkent.
     */
    int getPrice();

    /**
     * Visszaadja a termek nevet.
     *
     * @return A termek neve mint String.
     */
    String getName();
}
