package view;

import java.awt.Color;

import model.BusDriver;
import model.Cleaner;
import model.Player;

/**
 * Statikus segedosztaly a jatekosok szin-kodolasahoz. A Cleaner
 * piros (a SnowPlowView meglevo torzs-szinevel egyezo), a BusDriver
 * sarga (a BusView torzs-szinevel egyezo). A turn-indikator es a
 * HUDPanel ezen keresztul keri le a jelenlegi jatekos szinet.
 *
 * Tobbjatekos esetekre (>1 Cleaner vagy >1 BusDriver) a default szin
 * marad ervenyes -- a kesobbi tobb-szin rotacio itt egy helyen
 * boviheto. A modell oldalon nincs "color" mezo: a szin csak a
 * view-reteg tulajdonsaga.
 */
public final class PlayerColors {

    /** Cleaner-szin (egyezik a SnowPlowView body-vel). */
    public static final Color CLEANER = new Color(195, 50, 50);

    /** BusDriver-szin (egyezik a BusView body-vel). */
    public static final Color BUS_DRIVER = new Color(220, 180, 50);

    /** Default szin -- ismeretlen vagy hianyzo jatekos eseten. */
    public static final Color NEUTRAL = new Color(80, 80, 90);

    /** Privat konstruktor: utility class. */
    private PlayerColors() {
    }

    /**
     * Visszaadja egy adott Player-hez tartozo szint.
     *
     * @param p A jatekos (lehet null).
     * @return A torzs-szin; null vagy ismeretlen Player eseten NEUTRAL.
     */
    public static Color forPlayer(Player p) {
        if (p instanceof Cleaner) {
            return CLEANER;
        }
        if (p instanceof BusDriver) {
            return BUS_DRIVER;
        }
        return NEUTRAL;
    }
}
