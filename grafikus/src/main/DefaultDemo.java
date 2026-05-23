package main;

import java.util.ArrayList;
import java.util.List;

import cli.CommandParser;
import view.MapLayout;

/**
 * A default demo-pálya konstrukcioja. Egyetlen negyzet alaku
 * varos: minden oldala egy road, a 4 sarok = 4 keresztezodes.
 *  - 4 road (rTop, rBot, rLeft, rRight) -- mindegyik 1 forward
 *    + 1 backward sav (osszesen 8 lane)
 *  - 4 keresztezodes a sarkoknal; minden sarok 4 sav-kombinacio
 *    cross-connectelve (osszesen 16 connect_fields)
 *  - 4 epulet a 4 sarokba:
 *      - t1 terminal a bal-felso sarokba
 *      - t2 terminal a jobb-felso sarokba
 *      - hb1 HomeBase a bal-also sarokba
 *      - b1 epulet a jobb-also sarokba (az "utolso" sarok)
 *  - 1 hokotro (sp1) a HomeBase-en, tulajdonosa Cleaner (c1)
 *  - 1 NPC auto (car1): home=b1, work=t1 (atelloben, a negyzet
 *    ket szelen at)
 *
 * Az apply() ket dolgot vegez:
 *  1. CLI parancsokat futtat a CommandParser-en (create / add_to_road
 *     / connect_fields / spawn / set_money), ugyanazokat amiket egy
 *     *_in.txt fajl hasznalna. Ezzel a modell-allapot teljes felepitett.
 *  2. A MapLayout-ot kozvetlenul beallitja az adott pixel-pozíciókkal
 *     (placeRoadHorizontal / placeRoadVertical) -- megkerulve az
 *     autoLayout-ot, mert annak heurisztikaja a sarki keresztezodes-
 *     osszerendezest nem mindig adja vissza atlathato modon.
 */
public final class DefaultDemo {

    /** A racs vizszintes road-jainak szama (top + bottom). */
    public static final int ROWS = 2;

    /** A racs fuggoleges road-jainak szama (left + right). */
    public static final int COLS = 2;

    /** Keresztezodes-kozeppontok kozti tavolsag pixelben. */
    public static final int CELL = 380;

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
     * Felepiti a CLI parancsok listajat a negyzet alaku pályához.
     * 4 road (mindegyik 1 forward + 1 backward sav = 8 lane osszesen),
     * 4 sarki keresztezodes mindegyikenel a 4 sav-kombinaciot
     * cross-connectelve (16 connect_fields).
     *
     * @return Az osszes parancs, soronkent.
     */
    private static List<String> buildConfig() {
        List<String> c = new ArrayList<>();
        c.add("random off");

        // ----- 4 road (negyzet 4 oldala), mindegyik 1 forward + 1 backward sav -----
        c.add("create road rTop");
        c.add("set_road_length rTop " + CELL + ".0");
        c.add("create lane lTop_f");
        c.add("create lane lTop_b");
        c.add("add_to_road rTop lane lTop_f forward");
        c.add("add_to_road rTop lane lTop_b backward");

        c.add("create road rBot");
        c.add("set_road_length rBot " + CELL + ".0");
        c.add("create lane lBot_f");
        c.add("create lane lBot_b");
        c.add("add_to_road rBot lane lBot_f forward");
        c.add("add_to_road rBot lane lBot_b backward");

        c.add("create road rLeft");
        c.add("set_road_length rLeft " + CELL + ".0");
        c.add("create lane lLeft_f");
        c.add("create lane lLeft_b");
        c.add("add_to_road rLeft lane lLeft_f forward");
        c.add("add_to_road rLeft lane lLeft_b backward");

        c.add("create road rRight");
        c.add("set_road_length rRight " + CELL + ".0");
        c.add("create lane lRight_f");
        c.add("create lane lRight_b");
        c.add("add_to_road rRight lane lRight_f forward");
        c.add("add_to_road rRight lane lRight_b backward");

        // ----- Sarki keresztezodesek: 4 corner × 4 sav-kombinacio = 16 connect_fields -----
        // TL: rTop ∩ rLeft
        c.add("connect_fields lTop_f lLeft_f");
        c.add("connect_fields lTop_f lLeft_b");
        c.add("connect_fields lTop_b lLeft_f");
        c.add("connect_fields lTop_b lLeft_b");
        // TR: rTop ∩ rRight
        c.add("connect_fields lTop_f lRight_f");
        c.add("connect_fields lTop_f lRight_b");
        c.add("connect_fields lTop_b lRight_f");
        c.add("connect_fields lTop_b lRight_b");
        // BL: rBot ∩ rLeft
        c.add("connect_fields lBot_f lLeft_f");
        c.add("connect_fields lBot_f lLeft_b");
        c.add("connect_fields lBot_b lLeft_f");
        c.add("connect_fields lBot_b lLeft_b");
        // BR: rBot ∩ rRight
        c.add("connect_fields lBot_f lRight_f");
        c.add("connect_fields lBot_f lRight_b");
        c.add("connect_fields lBot_b lRight_f");
        c.add("connect_fields lBot_b lRight_b");

        // ----- 4 epulet a 4 sarokba -----
        c.add("create terminal t1");     // bal-felso (TL)
        c.add("create terminal t2");     // jobb-felso (TR)
        c.add("create homebase hb1");    // bal-also (BL)
        c.add("create building b1");     // jobb-also (BR) -- az "utolso" sarok

        // Az epuletek lane-hez kapcsolasa (a placeBuildings es a
        // jarmu-navigaciot vezerlo adjacency-graf szamara)
        c.add("connect_fields t1 lTop_f");
        c.add("connect_fields t2 lTop_f");
        c.add("connect_fields hb1 lBot_b");
        c.add("connect_fields b1 lBot_b");

        // ----- Jatekosok -----
        c.add("create cleaner c1");          // Cleaner: a hokotrot iranyitja
        c.add("create busdriver bd1");       // BusDriver: a buszt iranyitja

        // ----- Hokotro a HomeBase-en, c1 tulajdonban -----
        c.add("spawn snowplow sp1 hb1 c1");

        // ----- Busz a t1 terminal melletti felso savon, bd1 tulajdonban -----
        // A bus a top road forward savjan all, kozvetlenul t1 mellett
        // (t1 a connect_fields-szel a lTop_f-hez kotodik). A bd1
        // BusDriver-t adjuk meg owner-kent, igy a turn-order rendszer
        // (GameLogic.getCurrentTurnVehicle) be tudja sorolni.
        c.add("spawn bus bus1 lTop_f bd1");

        // ----- NPC auto: home=b1 (BR), work=t1 (TL) -- atelloben -----
        // Kezdo lane = lBot_b (mert a b1-bol erre lep a graf szerint;
        // a route a negyzet bal-also majd bal-felso szelen vezet).
        c.add("spawn car car1 lBot_b b1 t1");

        // Boseges kezdo penz a market/equip tesztelesehez
        c.add("set_money c1 1000000");
        return c;
    }

    /**
     * Manualisan beallitja a road-pozíciókat és az épület-pozíciókat
     * is a MapLayout-on.
     *  - rTop / rBot: y = OY illetve OY + CELL, x = OX - EXT,
     *    hossz = CELL + 2*EXT + roadBr
     *  - rLeft / rRight: x = OX illetve OX + CELL, y = OY - EXT,
     *    hossz = CELL + 2*EXT + roadBr
     *
     * A 4 sarki epulet a road-kvadransok kulso oldalan helyezkedik
     * el (bs + gap tavolsagra a road-kvadrans sarkatol).
     *
     * @param layout Az aktiv MapLayout.
     */
    private static void applyLayout(MapLayout layout) {
        int laneBr = MapLayout.LANE_BREADTH;
        int roadBr = 2 * laneBr; // 1 forward + 1 backward
        int hLength = (COLS - 1) * CELL + 2 * EXT + roadBr;
        int vLength = (ROWS - 1) * CELL + 2 * EXT + roadBr;

        // 2 horizontal road (top, bottom)
        int hX = OX - EXT;
        layout.placeRoadHorizontal("rTop", hX, OY, hLength);
        layout.placeRoadHorizontal("rBot", hX, OY + (ROWS - 1) * CELL, hLength);

        // 2 vertical road (left, right)
        int vY = OY - EXT;
        layout.placeRoadVertical("rLeft",  OX,                          vY, vLength);
        layout.placeRoadVertical("rRight", OX + (COLS - 1) * CELL,      vY, vLength);

        // Epuletek manualis elhelyezese a 4 sarokba
        int bs = MapLayout.BUILDING_SIZE;
        int gap = 18;
        int rH_topY = OY;
        int rH_bottomY = OY + (ROWS - 1) * CELL + roadBr;
        int rV_leftX = OX;
        int rV_rightX = OX + (COLS - 1) * CELL + roadBr;

        // t1 a bal-felso sarokba (rLeft bal oldalan, rTop folott)
        layout.setBounds("t1", new java.awt.Rectangle(
                rV_leftX - bs - gap, rH_topY - bs - gap, bs, bs));
        // t2 a jobb-felso sarokba (rRight jobb oldalan, rTop folott)
        layout.setBounds("t2", new java.awt.Rectangle(
                rV_rightX + gap, rH_topY - bs - gap, bs, bs));
        // hb1 a bal-also sarokba (rLeft bal oldalan, rBot alatt)
        layout.setBounds("hb1", new java.awt.Rectangle(
                rV_leftX - bs - gap, rH_bottomY + gap, bs, bs));
        // b1 a jobb-also sarokba (rRight jobb oldalan, rBot alatt) -- az "utolso" sarok
        layout.setBounds("b1", new java.awt.Rectangle(
                rV_rightX + gap, rH_bottomY + gap, bs, bs));
    }
}
