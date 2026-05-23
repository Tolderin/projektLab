package view;

import java.awt.Rectangle;

/**
 * Pusztan kozmetikai vizualis-elem (fa, szikla, hopelyhe, hegy, hid)
 * a pálya hatterehez. Nem befolyasolja a modellt -- semmilyen
 * Field, Vehicle, vagy connect_fields-graphic kapcsolatban nincs.
 *
 * A Decorations-okat a Demo osztaly applyLayout-ja adja hozza a
 * MapLayout-hoz; a GamePanel.paintComponent ket fázisban rajzolja:
 *  - background dekoraciok (fak, sziklak, hopelyhek) a road-ok ELOTT;
 *  - overlay dekoraciok (hegyek, hid-ivek) a road-ok UTAN, opcionalisan
 *    a roadra "ratevodve" (alagut / hid illuziora).
 */
public class Decoration {

    /** A dekoracio fajtaja. */
    public enum Type {
        /** Fenyofa: zold haromszog + barna torzs. */
        TREE,
        /** Szikla: szurke ovalis. */
        ROCK,
        /** Hopelyhe: feher kis "*" szimbolum. */
        SNOWFLAKE,
        /** Hegy: nagy haromszog (alagut-illuziohoz a road-ra ratevodve). */
        MOUNTAIN,
        /** Hid-iv: ko-szinu iv (hid-illuziohoz). */
        BRIDGE
    }

    /** A dekoracio tipusa. */
    public final Type type;

    /** A befoglalo teglalap (pixelben). */
    public final Rectangle bounds;

    /**
     * Letrehoz egy dekoraciot.
     *
     * @param type   A fajtaja.
     * @param bounds A befoglalo teglalap.
     */
    public Decoration(Type type, Rectangle bounds) {
        this.type = type;
        this.bounds = bounds;
    }

    /**
     * Tomeg-shortcut: az adott tipus a road-OVERLAY kategoriaba esik
     * (= a road-ok rajzolasa UTAN kell kirajzolni), egyebkent
     * background-dekor.
     *
     * @return true ha overlay (MOUNTAIN, BRIDGE), false ha background.
     */
    public boolean isOverlay() {
        return type == Type.MOUNTAIN || type == Type.BRIDGE;
    }
}
