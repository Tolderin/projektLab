package model;

import cli.Context;
import io.OutputFormatter;

/**
 * A buszjaratok vegpontja, egyben Field a grafban (Building leszarmazott).
 * Felelossege a fordulok regisztralasa: amikor egy busz megerkezik,
 * jelzi a buszvezetonek a fordulo teljesiteset es generalja a
 * megfelelo [EVENT] uzenetet.
 */
public class Terminal extends Building {

    /**
     * Letrehoz egy vegallomast.
     */
    public Terminal() {
    }

    /**
     * Regisztralja a busz erkezeset es jelzi a fordulot a buszvezetonek.
     * A 8. heti spec alapjan keresi az osszes BusDriver-t es
     * mindegyiknek noveli a completedRounds-jat -- prototipus reteg
     * jelenleg nem koti egyertelmuen a buszt egy konkret driver-hez,
     * ezert a megoldas: minden BusDriver inkrementalodik. (Ez
     * teszt-szinten elegendo: tipikusan egy buszhoz egy driver tartozik.)
     *
     * @param bus Az erkezo busz.
     */
    public void registerArrival(Bus bus) {
        // BusDriver-ek megkeresese es completedRounds inkrementalas
        for (Object o : Context.objectManager.getAll().values()) {
            if (o instanceof BusDriver) {
                ((BusDriver) o).incrementRounds();
            }
        }
        // Esemeny jelzese
        String busId = Context.objectManager.getId(bus);
        String terminalId = Context.objectManager.getId(this);
        OutputFormatter.printEvent(
                (busId != null ? busId : "?")
                        + " completed a round at "
                        + (terminalId != null ? terminalId : "?"));
    }
}
