package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * Az ut egyetlen forgalmi savja es egyben Field a grafban.
 * Felelossege nyilvantartani a horeteg vastagsagat, a jegpancel allapotat
 * es a rajta tartozkodo jarmuveket.
 * Kezeli a letaposas miatti jegkepzodesi mechanizmust.
 *
 * A Lane "level" metodusai (accept, remove, addSnow, removeSnow, breakIce,
 * removeIce, applyCompaction, applySaltEffect, getNeighbors) NEM logolnak
 * sajat enter/exit-et — a HIVO fel felel a naplozasert, mivel o tudja
 * a caller nevet. Kivetel: checkFreeze() es isBlocked(), amelyekben
 * I/N kerdes van.
 */
public class Lane implements Field {

    /** A savon levo horeteg vastagsaga. */
    private double snowDepth = 0.0;

    /** Igaz, ha a savon jegpancel van. */
    private boolean isFrozen = false;

    /** A jegpancel vastagsaga. */
    private double iceThickness = 0.0;

    /** Hanyszor hajtottak at jarmuvek — kuszob felett jegpancel kepzodik. */
    private int compactionCount = 0;

    /** A savon tartozkodo jarmuvek listaja. */
    private List<Vehicle> vehicles = new ArrayList<>();

    /** A sohatas fennmaradasanak ideje. */
    private double saltEffect = 0.0;

    /** A szomszedos mezok listaja. */
    private List<Field> neighbors = new ArrayList<>();

    /**
     * Fogadja az erkezo jarmuvet. Nem logol — a hivo fel naploz.
     *
     * @param v Az erkezo jarmu.
     */
    @Override
    public void accept(Vehicle v) {
        vehicles.add(v);
    }

    /**
     * Eltavolitja a tavozo jarmuvet. Nem logol — a hivo fel naploz.
     *
     * @param v A tavozo jarmu.
     */
    @Override
    public void remove(Vehicle v) {
        vehicles.remove(v);
    }

    /**
     * Visszaadja a szomszedos mezok listajat. Nem logol — a hivo fel naploz.
     *
     * @return A szomszedos Field objektumok listaja.
     */
    @Override
    public List<Field> getNeighbors() {
        return neighbors;
    }

    /**
     * Noveli a horeteg vastagsagat. Nem logol — a hivo fel naploz.
     *
     * @param amount A hozzaadando ho mennyisege.
     */
    public void addSnow(double amount) {
        snowDepth += amount;
    }

    /**
     * Csokkenti a horeteg vastagsagat. Nem logol — a hivo fel naploz.
     *
     * @param amount Az eltavolitando ho mennyisege.
     */
    public void removeSnow(double amount) {
        snowDepth = Math.max(0, snowDepth - amount);
    }

    /**
     * A jegpancelt feltori es havava alakitja. Nem logol — a hivo fel naploz.
     */
    public void breakIce() {
        this.isFrozen = false;
    }

    /**
     * Eltavolitja a jegpancelt teljesen. Nem logol — a hivo fel naploz.
     */
    public void removeIce() {
        this.isFrozen = false;
        this.iceThickness = 0;
    }

    /**
     * Ellenorzi, hogy kell-e jegpancel kepzodjon.
     * SAJAT enter/exit-et logol, mert I/N kerdest tartalmaz.
     * SD-03: "Elerte a letaposasok szama a kuszobot, es van ho a savon?"
     */
    public void checkFreeze() {
        Skeleton.enter("l2", "l2", "checkFreeze()");
        boolean thresholdReached = Skeleton.askQuestion(
                "Elerte a letaposasok szama a kuszobot, es van ho a savon?");
        if (thresholdReached) {
            this.isFrozen = true;
            Skeleton.enter("l2", "l2", "isFrozen = true");
            Skeleton.exit("void");
        }
        Skeleton.exit("void");
    }

    /**
     * Noveli a compactionCount-ot jarmuathaladaskor.
     * Nem logol — a hivo fel naploz.
     */
    public void applyCompaction() {
        compactionCount++;
    }

    /**
     * Beallitja a sohatast a savon. Nem logol — a hivo fel naploz.
     *
     * @param duration A sohatas idotartama korokban.
     */
    public void applySaltEffect(double duration) {
        this.saltEffect = duration;
    }

    /**
     * Igaz, ha a sav jarhatatlan (lezart).
     * SAJAT enter/exit-et logol, mert I/N kerdest tartalmaz.
     *
     * @return true ha a sav le van zarva.
     */
    public boolean isBlocked() {
        Skeleton.enter("car", "l2", "isBlocked()");
        boolean blocked = Skeleton.askQuestion("Le van zarva ez a sav?");
        Skeleton.exit(String.valueOf(blocked));
        return blocked;
    }
}
