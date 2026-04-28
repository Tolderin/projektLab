package model;

import java.util.ArrayList;
import java.util.List;

import cli.Context;
import io.OutputFormatter;

/**
 * A buszvezeto jatekos altal iranyitott jarmu, amely ket vegallomas
 * kozott kozlekedik. Fordulokat teljesit; utkozes vagy csuszas
 * eseten meghatarozott ideig mozgaskeptelenne valik es elzarja a savot.
 *
 * Allapotok es atmenetek:
 * - Mozgas (Moving): normalisan kozlekedik a buszvezeto iranyitasaval.
 * - Mozgaskeptelen (Disabled): utkozes vagy csuszas utan kerul ide;
 *   a disabledTurnsLeft csokkentesevel ter vissza Mozgasba.
 * - Vegallomason (AtTerminal): megerkezett a vegallomasra, a Terminal
 *   .registerArrival() hivodik, majd azonnal visszamegy Mozgasba.
 */
public class Bus extends Vehicle {

    /** Hamis erteke eseten a busz utkozes miatt mozgaskeptelen. */
    public boolean isFunctioning = true;

    /** Hany korig marad meg mozgaskeptelen. */
    public int disabledTurnsLeft = 0;

    /** A busz vizualis es logikai azonositasara szolgalo nev. */
    public String name;

    /** Konstans: hany kort kell kihagyni egy utkozes/megcsuszas utan. */
    private static final int PENALTY_TURNS = 3;

    /**
     * Mozgatja a buszt a tervezett kovetkezo mezo fele. Ha a busz
     * mozgaskeptelen, csak csokkenti a hatralevo bunteto-koroket.
     */
    @Override
    public void move() {
        if (!isFunctioning) {
            disabledTurnsLeft--;
            if (disabledTurnsLeft <= 0) {
                isFunctioning = true;
                disabledTurnsLeft = 0;
            }
            return;
        }
        if (nextField != null) {
            currentField.remove(this);
            nextField.accept(this);
            if (nextField instanceof Terminal) {
                ((Terminal) nextField).registerArrival(this);
            }
            currentField = nextField;
            nextField = null;
        }
    }

    /**
     * Utkozest kezel egy masik jarmuvel: bunteto-korok beallitasa.
     *
     * @param v A masik jarmu, amivel az utkozes tortent.
     */
    @Override
    public void collideWith(Vehicle v) {
        setPenalty(PENALTY_TURNS);
    }

    /**
     * Jegpancelon torteno megcsuszas kezelese: bunteto-korok
     * beallitasa, mas jarmuvekkel ütközés, sav lezarasa, esemeny
     * naplozasa.
     *
     * @param onLane A sav, amelyiken a csuszas megtortent.
     */
    @Override
    public void slip(Lane onLane) {
        setPenalty(PENALTY_TURNS);
        // Mas jarmuvek lecsapasa az ütközéssel
        // Iteralashoz masolat hasznalata, hogy a lista modositasa
        // ne dobjon ConcurrentModificationException-t.
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
     * Visszaadja a busz nevet.
     *
     * @return A busz neve.
     */
    public String getName() {
        return name;
    }

    /**
     * Privat segedmetodus a bunteto-korok beallitasara: a busz
     * mozgaskeptelenne valik egy adott korszamig.
     *
     * @param turns A buntetes hossza korokben.
     */
    private void setPenalty(int turns) {
        isFunctioning = false;
        disabledTurnsLeft = turns;
    }
}
