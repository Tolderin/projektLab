package model;

import java.util.ArrayList;
import java.util.List;

import cli.Context;

// (Cleaner / BusDriver / SnowPlow / Bus is mar a model csomagban van)

/**
 * A jatek menetet vezerlo osztaly. Felelossege a korok szervezese,
 * az autok navigalasanak inditasa, a jatek vegenek detektalasa
 * es az eredmeny osszesitese.
 *
 * A 8. heti spec szerint az advanceTurn() lepesei:
 * 1. Game-over ellenorzes
 * 2. turnCount++
 * 3. ha eleri a maxTurns-t -> endGame
 * 4. Map.snow()
 * 5. minden vehicle.move()
 * 6. manageTurn()
 *
 * A NextTurnCommand a jelen prototipusban szandekosan az
 * advanceTurn() ELOTT vegrehajt egy "force-slip" lepest is
 * (a force_slip true beallitassal jeges savon levo jarmuvek
 * azonnali csuszas-mechanizmusa).
 */
public class GameLogic extends Observable {

    /** A jatek terkepe. */
    public Map gameMap;

    /** A jatekban resztvevo jatekosok listaja. */
    public final List<Player> players = new ArrayList<>();

    /** Az aktualis kor sorszama. */
    public int turnCount = 0;

    /** A jatek hossza korokben. */
    public int maxTurns = 100;

    /** Az osszes jarmu listaja (mozgashoz a NextTurnCommand iteralja). */
    public final List<Vehicle> vehicles = new ArrayList<>();

    /** Privat: igaz ha a jatek mar veget ert. */
    private boolean isGameOver = false;

    /**
     * Round-cadence kapcsolo: ha true, akkor egy korben minden
     * SnowPlow es minden Bus csak egyszer mozgathato (a MovePlowCommand /
     * MoveBusCommand visszautasitja a tovabbi probalkozasokat), a
     * CommandBridge GUI-utvonal pedig automatikusan kiad egy next_turn-t,
     * amint MINDEN SnowPlow es Bus elhasznalta a lepeset. A
     * advanceTurn() a kor elejen visszaallitja a hasMovedThisTurn
     * flageket false-ra.
     *
     * Alapertelmezetten false (a CLI testek nem ismerik ezt a
     * mechanikat). A MainApp.main() turn-on-ja kapcsolja be a GUI
     * inditasakor.
     */
    public boolean roundCadenceEnabled = false;

    /**
     * Inicializalja a jatekot. A prototipus reteg az inicializalast
     * tipikusan a CLI konfiguracios parancsokon (create/spawn) keresztul
     * vegzi, igy ez itt csak alapertekek beallitasa.
     */
    public void startGame() {
        if (gameMap == null) {
            gameMap = new Map();
        }
        turnCount = 0;
        isGameOver = false;
    }

    /**
     * Eggyel noveli a korszamlalot, vegrehajtja a vegrehajtasi fazist.
     */
    public void advanceTurn() {
        if (isGameOver) {
            return;
        }
        // Round-cadence: a kor ELEJEN visszaallitjuk a hasMovedThisTurn
        // flageket, hogy az uj korben mindenki ujra mozgathato legyen.
        for (Vehicle v : vehicles) {
            if (v instanceof SnowPlow) {
                ((SnowPlow) v).hasMovedThisTurn = false;
            } else if (v instanceof Bus) {
                ((Bus) v).hasMovedThisTurn = false;
            }
        }
        turnCount++;
        if (turnCount >= maxTurns) {
            endGame();
            return;
        }
        if (gameMap != null) {
            gameMap.snow();
        }
        // Snapshot lista hasznalata: a move() Lane-en valo modositasai
        // (slip, lock) ne dobjanak ConcurrentModificationException-t.
        List<Vehicle> snapshot = new ArrayList<>(vehicles);
        for (Vehicle v : snapshot) {
            v.move();
        }
        manageTurn();
        // Lane state-frissites koronkent (saltEffect, blockedTurns)
        if (gameMap != null) {
            for (Field f : gameMap.fields) {
                if (f instanceof Lane) {
                    ((Lane) f).updateTurnEffects();
                }
            }
        }
        notifyObservers("turnAdvanced");
    }

    /**
     * Kezeli az aktiv jatekosok dontesi fazisat. A prototipus reteg
     * automatizalt parancs-feldolgozast hasznal, ezert ez itt ures.
     */
    public void manageTurn() {
        // Prototipus: a CLI parancsok lepnek a jatekosok helyett
    }

    /**
     * Befejezi a jatekot.
     */
    public void endGame() {
        isGameOver = true;
        notifyObservers("gameEnded");
    }

    /**
     * Igaz, ha a jatek mar veget ert.
     *
     * @return Game-over allapot.
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Hozzaad egy jarmuvet a globalis vehicles listahoz (a SpawnCommand
     * hivja).
     *
     * @param v Az uj jarmu.
     */
    public void addVehicle(Vehicle v) {
        vehicles.add(v);
    }

    /**
     * Hozzaad egy jatekost a players listahoz (a CreateCommand hivja).
     *
     * @param p Az uj jatekos.
     */
    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * A 13. heti turn-order modell. Visszaadja a kovetkezo, ebben a
     * korben meg nem mozgatott jatekos-jarmuvet. Sorrend:
     *  1. ObjectManager-bol az osszes Cleaner regisztracios sorrendben,
     *     mindegyik Cleaner controlledPlows-aban a hokotrok sorrendben;
     *  2. ObjectManager-bol az osszes BusDriver, mindegyik
     *     controlledBuses-aben a buszok sorrendben.
     *
     * Ha mindegyik mar lepett, null-t ad vissza -- ez jelzi, hogy a
     * kornyezet (env) fazisa kovetkezik.
     *
     * @return A kovetkezo aktualis jarmu vagy null.
     */
    public Vehicle getCurrentTurnVehicle() {
        for (Object o : Context.objectManager.getAll().values()) {
            if (o instanceof Cleaner) {
                for (SnowPlow sp : ((Cleaner) o).getControlledPlows()) {
                    if (!sp.hasMovedThisTurn) {
                        return sp;
                    }
                }
            }
        }
        for (Object o : Context.objectManager.getAll().values()) {
            if (o instanceof BusDriver) {
                for (Bus b : ((BusDriver) o).getControlledBuses()) {
                    if (!b.hasMovedThisTurn) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    /**
     * A kovetkezo aktualis jarmu tulajdonos jatekosat adja vissza
     * (a HUD es a TurnIndicatorPanel hasznalja).
     *
     * @return A jatekos vagy null.
     */
    public Player getCurrentPlayer() {
        Vehicle v = getCurrentTurnVehicle();
        if (v instanceof SnowPlow) {
            return ((SnowPlow) v).getOwner();
        }
        if (v instanceof Bus) {
            return ((Bus) v).owner;
        }
        return null;
    }
}
