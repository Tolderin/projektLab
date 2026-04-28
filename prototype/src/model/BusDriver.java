package model;

/**
 * A buszt vezeto jatekos, akinek celja, hogy a busz a vegallomasok
 * (Terminal) kozott ingazzon. Minden teljesitett fordulora pontot kap.
 */
public class BusDriver extends Player {

    /** Privat szamlalo: a jatekos altal eddig teljesitett sikeres
     *  fordulok szama. */
    private int completedRounds = 0;

    /**
     * Elkezdi a buszvezeto koret. Prototipus modban ures.
     */
    @Override
    public void startTurn() {
        // Prototipus mod: lasd Player
    }

    /**
     * Befejezi a buszvezeto koret. Prototipus modban ures.
     */
    @Override
    public void endTurn() {
        // Prototipus mod
    }

    /**
     * Noveli a teljesitett fordulok szamat. A Terminal.registerArrival
     * hivja amikor egy busz erkezik a vegallomasra.
     */
    public void incrementRounds() {
        this.completedRounds++;
    }

    /**
     * Visszaadja a teljesitett fordulok szamat.
     *
     * @return A teljesitett fordulok szama.
     */
    public int getCompletedRounds() {
        return completedRounds;
    }
}
