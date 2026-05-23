package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A buszt vezeto jatekos, akinek celja, hogy a busz a vegallomasok
 * (Terminal) kozott ingazzon. Minden teljesitett fordulora pontot kap.
 */
public class BusDriver extends Player {

    /** Privat szamlalo: a jatekos altal eddig teljesitett sikeres
     *  fordulok szama. */
    private int completedRounds = 0;

    /** A jatekos iranyitasa alatt allo buszok listaja. A turn-order
     *  rendszer (GameLogic.getCurrentTurnVehicle) ezen iteral. */
    private final List<Bus> controlledBuses = new ArrayList<>();

    /**
     * Hozzaad egy buszt a jatekos flottajahoz, es kolcsonosen rogziti
     * a tulajdonosi kapcsolatot. A SpawnCommand hivja a bus opcionalis
     * owner argumentumakent.
     *
     * @param bus Az ujonnan tulajdonba vett busz.
     */
    public void addBus(Bus bus) {
        controlledBuses.add(bus);
        bus.owner = this;
    }

    /**
     * Visszaadja a jatekos altal kontrollalt buszok listajat.
     *
     * @return A buszok listaja.
     */
    public List<Bus> getControlledBuses() {
        return controlledBuses;
    }

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
