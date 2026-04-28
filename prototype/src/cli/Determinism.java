package cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A jatek determinisztikussagat vezerlo osztaly. Ket modot tamogat:
 * 'random on' (alapertelmezett) -- esely-alapu csuszas-szimulacio,
 * 'random off' -- a force_slip parancs altal explicit beallitott
 * vehicleId -> shouldSlip parok hatarozzak meg a csuszast.
 *
 * A 7. heti specifikacio szerint determinisztikus modban "alapesetben
 * mindig megcsuszna" egy jarmu. Vagyis ha 'random off' van bekapcsolva
 * de a forceSlipMap-ben nincs explicit override, akkor TRUE
 * (a csuszas bekovetkezik). A force_slip parancs ezt felulirja.
 */
public class Determinism {

    /** Kapcsolja a vletlen elemeket. Alapertelmezetten true. */
    private boolean randomEnabled = true;

    /** vehicleId -> shouldSlip override map. */
    private final Map<String, Boolean> forceSlipMap = new HashMap<>();

    /** Vletlen-generator a 'random on' modhoz. Fix seed-del a
     *  reprodukalhatosag erdekeben. */
    private final Random rng = new Random(42L);

    /**
     * Visszaadja, hogy a random mod be van-e kapcsolva.
     *
     * @return true ha a vletlenszerseg aktiv.
     */
    public boolean isRandomEnabled() {
        return randomEnabled;
    }

    /**
     * Be- vagy kikapcsolja a random modot. A 'random on|off' parancs hivja.
     *
     * @param v Az uj allapot (true = vletlen mod, false = determinisztikus).
     */
    public void setRandomEnabled(boolean v) {
        this.randomEnabled = v;
    }

    /**
     * Beallitja egy konkret jarmure a force_slip override-ot.
     * A 'force_slip <id> [true|false]' parancs hivja.
     *
     * @param vehicleId  A jarmu szoveges azonositoja.
     * @param shouldSlip true = csusszon meg, false = ne csusszon meg.
     */
    public void setForceSlip(String vehicleId, boolean shouldSlip) {
        forceSlipMap.put(vehicleId, shouldSlip);
    }

    /**
     * Eldonti, hogy egy jarmu megcsusszon-e jeges savra erkezeskor.
     * Hasznalat: a Lane.accept(Vehicle) hivja amikor isFrozen=true.
     *
     * Logika:
     * 1) Ha van explicit force_slip override -> azt hasznaljuk.
     * 2) Ha 'random off' es nincs override -> TRUE (default csuszas
     *    determinisztikus modban a 7. heti spec szerint).
     * 3) Ha 'random on' -> 50% esely.
     *
     * @param vehicleId A jarmu azonositoja.
     * @return true ha a jarmunek meg kell csusznia.
     */
    public boolean shouldSlip(String vehicleId) {
        if (forceSlipMap.containsKey(vehicleId)) {
            return forceSlipMap.get(vehicleId);
        }
        if (!randomEnabled) {
            return true;
        }
        return rng.nextDouble() < 0.5;
    }

    /**
     * Eldonti, hogy egy jarmu force-slipped (kenyszer-csuszas) allapotban
     * van-e -- vagyis az aktualis kor elejen, fuggetlenul a Lane.accept-tol,
     * a NextTurnCommand altal aktivalando csuszas-mechanizmus szukseges-e.
     * Csak akkor true, ha az ID-re EXPLICITEN beallitottak true erteket.
     *
     * @param vehicleId A jarmu azonositoja.
     * @return true csak ha a forceSlipMap-ben true van regisztralva.
     */
    public boolean isForceSlipped(String vehicleId) {
        return forceSlipMap.getOrDefault(vehicleId, false);
    }
}
