package model;

import java.util.ArrayList;
import java.util.List;

import cli.Context;
import io.OutputFormatter;

/**
 * A varoslakok automatizalt jarmuve, amely onalloan navigal lakasa
 * es munkahelye kozott a legrovidebb jarhato utvonalon. Ha vekony
 * horetegen halad, letapossa a havat; jegen csuszhat es balesetet
 * okozhat; mely hoban elakad, de szomszedos szabad savba atsorolhat.
 *
 * Allapotok es atmenetek:
 * - Moving (Mozgasban): normalisan halad az utvonalan.
 * - Disabled (Mozgaskeptelen): utkozes vagy csuszas utan kerul ide;
 *   waitTurns csokkentesevel ter vissza Moving-ba.
 */
public class Car extends Vehicle {

    /** A lakas epulete, ahonnan az auto indul. */
    public Building homeBuilding;

    /** A munkahely epulete, ahova az auto tart. */
    public Building workBuilding;

    /** A terkep referenciaja az utvonal-szamitashoz. */
    public Map gameMap;

    /** Privat: az auto eppen a munkahelyere tart-e (true)
     *  vagy hazafele (false). Cel-elerese eseten invertalodik. */
    private boolean isGoingToWork = true;

    /** Konstans: hany kort kell kihagyni egy baleset utan. */
    private static final int PENALTY_TURNS = 3;

    /** Privat: hany korbol marad meg ki az auto baleset miatt. */
    private int waitTurns = 0;

    /**
     * A kocsi a kiszamolt utvonal alapjan lep a kovetkezo mezore.
     * Ha jelenleg balesetet "vart le", ez a hivas csak csokkenti
     * a varakozas-szamlalot. A NextTurnCommand minden korben hivja.
     */
    @Override
    public void move() {
        if (waitTurns > 0) {
            waitTurns--;
            return;
        }
        calculatePath();
        if (nextField == null) {
            return;
        }
        Field oldField = currentField;
        currentField.remove(this);
        nextField.accept(this);
        // A slip() a Lane.accept-ben aktivalodhatott, ami waitTurns-t
        // beallithatott. Ettol fuggetlenul az alabbi state-update fut.
        if (nextField instanceof Lane) {
            ((Lane) nextField).applyCompaction();
        }
        // [EVENT] csak ha a jarmu tenylegesen mozdult
        String vid = Context.objectManager.getId(this);
        String dst = Context.objectManager.getId(nextField);
        OutputFormatter.printEvent(
                (vid != null ? vid : "?")
                        + " moved to "
                        + (dst != null ? dst : "?"));
        currentField = nextField;
        nextField = null;
        // Cel-eleres ellenorzese
        Building target = isGoingToWork ? workBuilding : homeBuilding;
        if (currentField == target) {
            isGoingToWork = !isGoingToWork;
        }
    }

    /**
     * Meghivja a terkep utvonalkeresojet es beallitja a kovetkezo
     * mezot. Ha az utvonal-lista ures, savvaltast probal.
     */
    public void calculatePath() {
        Building target = isGoingToWork ? workBuilding : homeBuilding;
        if (gameMap == null || target == null) {
            return;
        }
        List<Field> path = gameMap.findShortestPath(currentField, target);
        if (!path.isEmpty()) {
            nextField = path.get(0);
        } else {
            changeLane();
        }
    }

    /**
     * Utkozes kezelese: bunteto-korok beallitasa, az aktualis sav
     * lezarasa.
     *
     * @param v A masik jarmu, amivel az utkozes tortent.
     */
    @Override
    public void collideWith(Vehicle v) {
        waitTurns = PENALTY_TURNS;
        if (currentField instanceof Lane) {
            ((Lane) currentField).setBlocked(PENALTY_TURNS);
        }
    }

    /**
     * Jegpancelon megcsuszas kezelese: bunteto-korok beallitasa,
     * tobbi jarmu utkoztetese, sav lezarasa, esemeny naplozasa.
     *
     * @param onLane A sav, amelyiken a csuszas megtortent.
     */
    @Override
    public void slip(Lane onLane) {
        waitTurns = PENALTY_TURNS;
        List<Vehicle> snapshot = new ArrayList<>(onLane.vehicles);
        for (Vehicle other : snapshot) {
            if (other != this) {
                other.collideWith(this);
            }
        }
        onLane.setBlocked(PENALTY_TURNS);

        String vid = Context.objectManager.getId(this);
        String fid = Context.objectManager.getId(onLane);
        OutputFormatter.printEvent(
                (vid != null ? vid : "?")
                        + " slipped on "
                        + (fid != null ? fid : "?")
                        + ". Field is now blocked.");
    }

    /**
     * Atsorolas a szomszedos szabad savba elakadas eseten.
     * Iteral a szomszedokon, az elso szabad Lane-t valasztja.
     */
    public void changeLane() {
        if (currentField == null) {
            return;
        }
        for (Field neighbor : currentField.getNeighbors()) {
            if (neighbor instanceof Lane && !((Lane) neighbor).isBlocked()) {
                nextField = neighbor;
                return;
            }
        }
    }

    /**
     * Diagnosztikai: visszaadja, hogy az auto eppen a munkahelyere
     * tart-e. A stat parancs kimeneteben jelenik meg.
     *
     * @return true ha a munkahely fele tart.
     */
    public boolean isGoingToWork() {
        return isGoingToWork;
    }

    /**
     * Diagnosztikai: visszaadja a hatralevo varakozasi koroket.
     *
     * @return waitTurns aktualis erteke.
     */
    public int getWaitTurns() {
        return waitTurns;
    }
}
