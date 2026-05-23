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
        // 13. heti: a busznak (járattól függően) ket vegallomas
        // kozott kell megfordulnia. Egy "forduló" = a busz az A-rol
        // a B-re erkezik vagy forditva (azaz route-mentes). Ha
        // ugyanaz a terminal kovetkezik egymas utan, nem szamol.
        // Ha a busznak nincs definialt route (routeTerminalA/B null),
        // minden owner-rendelt terminal-erkezes pontot ad (egyszerusitett
        // fallback). Ha sem route sem owner nincs, minden BusDriver
        // kap pontot (legacy CLI test fallback).
        boolean scored = false;
        if (bus.owner != null
                && bus.routeTerminalA != null
                && bus.routeTerminalB != null) {
            // Csak ha ez a terminal a busz route-jaban van
            if (this == bus.routeTerminalA || this == bus.routeTerminalB) {
                Terminal other = (this == bus.routeTerminalA)
                        ? bus.routeTerminalB
                        : bus.routeTerminalA;
                if (bus.lastVisitedTerminal == other) {
                    bus.owner.incrementRounds();
                    bus.owner.addScore(50);
                    scored = true;
                }
                bus.lastVisitedTerminal = this;
            }
        } else if (bus.owner != null) {
            // Owner van, route nincs: minden terminal-erkezes pontot ad
            bus.owner.incrementRounds();
            bus.owner.addScore(50);
            scored = true;
        } else {
            // Sem owner sem route: regi (test-kompatibilis) viselkedes
            for (Object o : Context.objectManager.getAll().values()) {
                if (o instanceof BusDriver) {
                    BusDriver bd = (BusDriver) o;
                    bd.incrementRounds();
                    bd.addScore(50);
                }
            }
            scored = true;
        }
        // Esemeny jelzese
        String busId = Context.objectManager.getId(bus);
        String terminalId = Context.objectManager.getId(this);
        OutputFormatter.printEvent(
                (busId != null ? busId : "?")
                        + (scored ? " completed a round at " : " arrived at ")
                        + (terminalId != null ? terminalId : "?"));
    }
}
