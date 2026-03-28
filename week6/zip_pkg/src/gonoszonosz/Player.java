package gonoszonosz;

/**
 * A játékos absztrakt osztálya, a Cleaner és a BusDriver közös ősosztálya.
 * Tárolja az alapvető játékos-tulajdonságokat és előírja
 * a körkezdeményező/-záró metódusokat.
 */
public abstract class Player {

    /** A játékos neve. */
    protected String name;

    /** A játékos aktuális pontszáma. */
    protected int score;

    /**
     * Létrehoz egy új játékost a megadott névvel.
     * @param name a játékos neve
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    /**
     * Elkezdi az adott játékos körét. Minden konkrét játékos típus saját logikával valósítja meg.
     */
    public abstract void startTurn();

    /**
     * Befejezi az adott játékos körét. Minden konkrét játékos típus saját logikával valósítja meg.
     */
    public abstract void endTurn();

    /**
     * Visszaadja a játékos nevét.
     * @return a játékos neve
     */
    public String getName() {
        return name;
    }

    /**
     * Visszaadja a játékos aktuális pontszámát.
     * @return a pontszám
     */
    public int getScore() {
        return score;
    }
}
