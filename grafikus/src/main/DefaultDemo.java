package main;

import java.util.ArrayList;
import java.util.List;

import cli.CommandParser;
import view.MapLayout;

/**
 * A default demo-pálya konstrukcioja. Egy 3x3 racs-szerkezetu varos:
 *  - 3 vizszintes road (rH1, rH2, rH3, felulrol lefele)
 *  - 3 fuggoleges road (rV1, rV2, rV3, balrol jobbra)
 *  - 9 keresztezodes (a vizszintes/fuggoleges metszetein)
 *  - 6 epulet a 4 sarokba + 2 oldalsava
 *  - 1 hokotro a HomeBase-en, 1 busz, 2 NPC auto
 *
 * Az apply() ket dolgot vegez:
 *  1. CLI parancsokat futtat a CommandParser-en (create / connect_fields
 *     / spawn / set_money), ugyanazokat amiket egy *_in.txt fajl
 *     hasznalna. Ezzel a modell-allapot teljes felepitett.
 *  2. A MapLayout-ot kozvetlenul beallitja az adott pixel-pozíciókkal
 *     (placeRoadHorizontal / placeRoadVertical) -- megkerulve az
 *     autoLayout-ot, mert annak 9 keresztezodessel a center-center
 *     elrendezesi heurisztikaja nem mukodik.
 */
public final class DefaultDemo {

    /** A racs vizszintes road-jainak szama. */
    public static final int ROWS = 3;

    /** A racs fuggoleges road-jainak szama. */
    public static final int COLS = 3;

    /** Keresztezodes-kozeppontok kozti tavolsag pixelben. */
    public static final int CELL = 150;

    /** A road tulnyulasa a keresztezodesen pixelben. */
    public static final int EXT = 35;

    /** A racs bal-felso elso keresztezodesenek x-koordinataja. */
    public static final int OX = 120;

    /** A racs bal-felso elso keresztezodesenek y-koordinataja. */
    public static final int OY = 160;

    /** Privat konstruktor: ez egy static utility osztaly. */
    private DefaultDemo() {
    }

    /**
     * Felepiti a default demo-pályát: futtatja a CLI parancsokat
     * majd manualisan beallitja a layout-ot.
     *
     * @param parser A felkonfiguralt CommandParser.
     * @param layout Az aktiv MapLayout (a road-pozíciók beallitasahoz).
     */
    public static void apply(CommandParser parser, MapLayout layout) {
        for (String line : buildConfig()) {
            parser.parseLine(line);
        }
        applyLayout(layout);
    }

    /**
     * Felepiti a CLI parancsok listajat a 3x3 racs-pályához.
     *
     * @return Az osszes parancs, soronkent.
     */
    private static List<String> buildConfig() {
        List<String> c = new ArrayList<>();
        c.add("random off");
        // 3 vizszintes + 3 fuggoleges road, mindegyik 1 forward + 1 backward sáv
        for (int i = 1; i <= ROWS; i++) {
            c.add("create road rH" + i);
            c.add("set_road_length rH" + i + " 200.0");
            c.add("create lane lH" + i + "_f");
            c.add("create lane lH" + i + "_b");
            c.add("add_to_road rH" + i + " lane lH" + i + "_f forward");
            c.add("add_to_road rH" + i + " lane lH" + i + "_b backward");
        }
        for (int j = 1; j <= COLS; j++) {
            c.add("create road rV" + j);
            c.add("set_road_length rV" + j + " 200.0");
            c.add("create lane lV" + j + "_f");
            c.add("create lane lV" + j + "_b");
            c.add("add_to_road rV" + j + " lane lV" + j + "_f forward");
            c.add("add_to_road rV" + j + " lane lV" + j + "_b backward");
        }
        // Keresztezodesek: ROWS × COLS = 9 keresztezodes, mindegyik 4 connect_fields
        for (int i = 1; i <= ROWS; i++) {
            for (int j = 1; j <= COLS; j++) {
                c.add("connect_fields lH" + i + "_f lV" + j + "_f");
                c.add("connect_fields lH" + i + "_f lV" + j + "_b");
                c.add("connect_fields lH" + i + "_b lV" + j + "_f");
                c.add("connect_fields lH" + i + "_b lV" + j + "_b");
            }
        }
        // 6 epulet: 4 sarokba + 2 oldalt kozepen
        c.add("create homebase hb1");
        c.add("create terminal t1");
        c.add("create building b1");
        c.add("create building b2");
        c.add("create building b3");
        c.add("create building b4");
        // hb1 (bal-felso) es t1 (jobb-felso): a felso road forward savjara.
        // A placeBuildings algoritmus 0.10 es 0.90 frakcio-pozícióra teszi a kettot.
        c.add("connect_fields hb1 lH1_f");
        c.add("connect_fields t1 lH1_f");
        // b1 (bal-also) es b2 (jobb-also): az also road backward savjara
        c.add("connect_fields b1 lH3_b");
        c.add("connect_fields b2 lH3_b");
        // b3 a bal kozep oldalra, b4 a jobb kozep oldalra
        c.add("connect_fields b3 lV1_f");
        c.add("connect_fields b4 lV3_b");
        // Jatekosok
        c.add("create cleaner c1");
        c.add("create busdriver bd1");
        // Hokotro a HomeBase-en
        c.add("spawn snowplow sp1 hb1 c1");
        // Busz a felso road forward savjan
        c.add("spawn bus bus1 lH1_f");
        // 2 NPC auto: home/work valami epulet-par kozott
        c.add("spawn car car1 lV1_f b3 t1");
        c.add("spawn car car2 lV3_b b4 hb1");
        c.add("set_money c1 500");
        return c;
    }

    /**
     * Manualisan beallitja a road-pozíciókat és az épület-pozíciókat
     * is a MapLayout-on. A 3x3 racs:
     *  - rHi (i=1..3): y = OY + (i-1) * CELL, x = OX - EXT, hossza =
     *    (COLS-1) * CELL + 2 * EXT + roadBr
     *  - rVj (j=1..3): x = OX + (j-1) * CELL, y = OY - EXT, hossza =
     *    (ROWS-1) * CELL + 2 * EXT + roadBr
     *
     * Az épületek a 4 sarokra (rács kulso oldalain) + 2 oldali kozep-
     * ponton helyezkednek el, a road-tol egy konstans gap-pel.
     *
     * @param layout Az aktiv MapLayout.
     */
    private static void applyLayout(MapLayout layout) {
        int laneBr = MapLayout.LANE_BREADTH;
        int roadBr = 2 * laneBr; // 1 forward + 1 backward
        int hLength = (COLS - 1) * CELL + 2 * EXT + roadBr;
        int vLength = (ROWS - 1) * CELL + 2 * EXT + roadBr;
        for (int i = 1; i <= ROWS; i++) {
            int x = OX - EXT;
            int y = OY + (i - 1) * CELL;
            layout.placeRoadHorizontal("rH" + i, x, y, hLength);
        }
        for (int j = 1; j <= COLS; j++) {
            int x = OX + (j - 1) * CELL;
            int y = OY - EXT;
            layout.placeRoadVertical("rV" + j, x, y, vLength);
        }

        // Epuletek manualis elhelyezese:
        int bs = MapLayout.BUILDING_SIZE;
        int gap = 18;
        int rH_topY = OY;
        int rH_bottomY = OY + (ROWS - 1) * CELL + roadBr;
        int rV_leftX = OX;
        int rV_rightX = OX + (COLS - 1) * CELL + roadBr;
        int midRowsY = OY + ((ROWS - 1) * CELL + roadBr) / 2 - bs / 2;
        // hb1 a bal-felso kvadrans:  bal road bal oldalan, felso road folott
        layout.setBounds("hb1", new java.awt.Rectangle(
                rV_leftX - bs - gap, rH_topY - bs - gap, bs, bs));
        // t1 a jobb-felso kvadrans
        layout.setBounds("t1", new java.awt.Rectangle(
                rV_rightX + gap, rH_topY - bs - gap, bs, bs));
        // b1 a bal-also kvadrans
        layout.setBounds("b1", new java.awt.Rectangle(
                rV_leftX - bs - gap, rH_bottomY + gap, bs, bs));
        // b2 a jobb-also kvadrans
        layout.setBounds("b2", new java.awt.Rectangle(
                rV_rightX + gap, rH_bottomY + gap, bs, bs));
        // b3 a bal oldali kozep
        layout.setBounds("b3", new java.awt.Rectangle(
                rV_leftX - bs - gap, midRowsY, bs, bs));
        // b4 a jobb oldali kozep
        layout.setBounds("b4", new java.awt.Rectangle(
                rV_rightX + gap, midRowsY, bs, bs));
    }
}
