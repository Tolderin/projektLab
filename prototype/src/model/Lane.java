package model;

import java.util.ArrayList;
import java.util.List;

import cli.Context;
import io.OutputFormatter;

/**
 * Az uthalozat grafjanak alapcsomopontja: egy forgalmi sav, amelyen
 * a jarmuvek kozlekednek. Felelossege nyilvantartani a horeteg
 * vastagsagat, a jegpancel allapotat es a tartozkodo jarmuveket.
 *
 * Allapotok es atmenetek:
 * - Tiszta (Clear): nincs ho/jeg.
 * - Havas (Snowy): horeteg van rajta. Eleg letaposasra Jeges allapotba megy.
 * - Jeges (Icy): jegpancel van rajta. Jegtoro hatasara visszamegy Havas allapotba.
 * - Lezart (Blocked): baleset miatt; meghatarozott korok utan visszanyilik.
 *
 * A 7. heti valtozas: bevezetve a gravelDepth attributum es az
 * addGravel/removeGravel metodusok. A zuzalek megszunteti a jeg
 * csuszossagat de a sopro/hanyo fej eltakaritja.
 */
public class Lane implements Field {

    /** A savon levo horeteg vastagsaga. */
    public double snowDepth = 0.0;

    /** Igaz, ha a savon jegpancel van. */
    public boolean isFrozen = false;

    /** Hanyszor hajtottak at jarmuvek -- kuszob felett jegpancel kepzodik. */
    public int compactionCount = 0;

    /** A savon tartozkodo jarmuvek listaja. */
    public final List<Vehicle> vehicles = new ArrayList<>();

    /** A sohatas fennmaradasanak ideje (korokben). */
    public double saltEffect = 0.0;

    /** A szomszedos mezok listaja. */
    public final List<Field> neighbors = new ArrayList<>();

    /** A savon levo zuzalek mennyisege (7. heti uj attributum). */
    public double gravelDepth = 0.0;

    /** Konstans: a letaposasi kuszobertek a fagyashoz. */
    private static final int COMPACTION_THRESHOLD = 3;

    /** Konstans: a jegtores utan visszamarado ho mennyisege. */
    private static final double ICE_BREAK_SNOW_AMOUNT = 1.0;

    /** A savlezarasbol hatralevo korok szama. */
    int blockedTurns = 0;

    /**
     * Fogadja az erkezo jarmuvet. Ha a sav jeges, a Determinism alapjan
     * eldonti, hogy a jarmu megcsusszon-e (Lane.accept-ben aktivalodo
     * csuszas-mechanizmus).
     *
     * @param v Az erkezo jarmu.
     */
    @Override
    public void accept(Vehicle v) {
        vehicles.add(v);
        if (isFrozen) {
            String vehicleId = Context.objectManager.getId(v);
            if (Context.determinism.shouldSlip(vehicleId)) {
                v.slip(this);
            }
        }
    }

    /**
     * Eltavolitja a tavozo jarmuvet a savrol.
     *
     * @param v A tavozo jarmu.
     */
    @Override
    public void remove(Vehicle v) {
        vehicles.remove(v);
    }

    /**
     * Visszaadja a szomszedos mezok listajat.
     *
     * @return A szomszedok listaja.
     */
    @Override
    public List<Field> getNeighbors() {
        return neighbors;
    }

    /**
     * Noveli a horeteg vastagsagat. Ha a saltEffect aktiv, a sopro
     * hatas megakadalyozza az uj ho megmaradasat.
     *
     * @param amount A hozzaadando ho mennyisege.
     */
    public void addSnow(double amount) {
        if (saltEffect > 0) {
            return;
        }
        snowDepth += amount;
    }

    /**
     * Csokkenti a horeteg vastagsagat. Mivel a havat eltakaritjak,
     * a letaposas-szamlalo is nullara all.
     *
     * @param amount Az eltavolitando ho mennyisege.
     */
    public void removeSnow(double amount) {
        snowDepth -= amount;
        if (snowDepth <= 0) {
            snowDepth = 0;
        }
        compactionCount = 0;
    }

    /**
     * A jegpancelt feltori es havava alakitja. Ha nincs jeg, nem hat.
     */
    public void breakIce() {
        if (isFrozen) {
            isFrozen = false;
            // A jegtoro fej egy meghatarozott mennyisegu havat hagy hatra
            this.snowDepth += ICE_BREAK_SNOW_AMOUNT;
        }
    }

    /**
     * Eltavolitja a jegpancelt teljesen.
     */
    public void removeIce() {
        isFrozen = false;
    }

    /**
     * Ellenorzi, hogy kell-e jegpancel kepzodjon. Ha a letaposas-
     * szamlalo eler egy kuszobot es van ho a savon, a sav jeges
     * lesz, a horeteg eltunik.
     */
    public void checkFreeze() {
        if (compactionCount >= COMPACTION_THRESHOLD && snowDepth > 0) {
            isFrozen = true;
            snowDepth = 0;
            compactionCount = 0;
        }
    }

    /**
     * Noveli a letaposas-szamlalot, ha van ho a savon, es ellenorzi
     * a fagyas felteteleit.
     */
    public void applyCompaction() {
        if (snowDepth > 0) {
            compactionCount++;
            checkFreeze();
        }
    }

    /**
     * Beallitja a sohatast a savon. A so atvettsegi ido alatt
     * megakadalyozza az uj ho megmaradasat (lasd addSnow).
     *
     * @param duration A sohatas idotartama korokben.
     */
    public void applySaltEffect(double duration) {
        this.saltEffect = duration;
    }

    /**
     * Igaz, ha a sav jarhatatlan (lezart).
     *
     * @return true ha a sav jelenleg lezart.
     */
    public boolean isBlocked() {
        return blockedTurns > 0;
    }

    /**
     * Privat segedmetodus a sav lezarasara meghatarozott korre.
     * A Vehicle.slip() es Vehicle.collideWith() hivja.
     *
     * @param turns A lezaras hossza korokben.
     */
    void setBlocked(int turns) {
        this.blockedTurns = turns;
    }

    /**
     * Privat segedmetodus a koronkenti lezarasok es sohatas
     * frissitesere. A NextTurnCommand minden korben hivja.
     */
    public void updateTurnEffects() {
        if (saltEffect > 0) {
            saltEffect--;
        }
        if (blockedTurns > 0) {
            blockedTurns--;
        }
    }

    /**
     * Noveli a savon levo zuzalek mennyiseget (7. heti uj metodus).
     *
     * @param amount A hozzaadando zuzalek.
     */
    public void addGravel(double amount) {
        gravelDepth += amount;
    }

    /**
     * Csokkenti a savon levo zuzalek mennyiseget (7. heti uj metodus).
     *
     * @param amount Az eltavolitando zuzalek.
     */
    public void removeGravel(double amount) {
        gravelDepth -= amount;
        if (gravelDepth <= 0) {
            gravelDepth = 0;
        }
    }

    /**
     * Diagnosztikai segedmetodus: visszaadja a savon tartozkodo
     * jarmuvek azonositoinak vesszovel elvalasztott listajat
     * a stat parancs kimenetehez.
     *
     * @return Pl. "[]" vagy "[car_1, bus_1]"
     */
    public String getVehiclesAsString() {
        if (vehicles.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vehicles.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            String id = Context.objectManager.getId(vehicles.get(i));
            sb.append(id != null ? id : "?");
        }
        sb.append("]");
        return sb.toString();
    }
}
