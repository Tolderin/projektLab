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
        // A bus a top road forward savjan all, kozvetlenul t1 mellett.
        // A 13. heti spec: a busznak (járattól függően) ket vegallomas
        // (t1 es t2) kozott kell minel tobbszor megfordulnia. Pontot
        // csak az alternalo terminal-erkezesekre kap.
        c.add("spawn bus bus1 lTop_f bd1 t1 t2");

        // ----- NPC auto: home=b1 (BR), work=t1 (TL) -- atelloben -----
        // Kezdo lane = lBot_b (mert a b1-bol erre lep a graf szerint;
        // a route a negyzet bal-also majd bal-felso szelen vezet).
        c.add("spawn car car1 lBot_b b1 t1");

        // Kezdo penz: a fej-arak (50/75/100/150/200/300) + esetleges
        // hokotro-vasarlas (1000) figyelembevetelevel meretezve.
        c.add("set_money c1 5000");
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

        // 13. heti: kozmetikai dekoraciok. Tel-tema (fak, sziklak,
        // hopelyhek). Egy hegy az also road (rBot) felett -- alagut-illuzio.
        addCosmetics(layout);
    }

    /**
     * Hozzaad nehany kozmetikai dekoraciot a palya hatterehez (a
     * MapLayout decorations listajan keresztul a GamePanel rajzolja).
     *
     * @param layout Az aktiv MapLayout.
     */
    private static void addCosmetics(view.MapLayout layout) {
        // 13. heti utolso revisio: minden dekoraciot ~40 px-szel jobbra
        // toltunk a vizualis centralasert.
        int xShift = 40;
        // Fak a kozepre eso ures teruleten
        int[][] treePositions = {
                { 270, 310 }, { 340, 280 }, { 410, 340 },
                { 470, 290 }, { 350, 420 }, { 290, 460 },
                { 420, 470 }, { 480, 430 }
        };
        for (int[] p : treePositions) {
            layout.addDecoration(new view.Decoration(view.Decoration.Type.TREE,
                    new java.awt.Rectangle(p[0] + xShift, p[1], 18, 24)));
        }
        // Sziklak
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(310 + xShift, 380, 14, 8)));
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(450 + xShift, 380, 12, 7)));
        // Hopelyhek (szorvanyosan a palya korul)
        int[][] snowPositions = {
                { 50, 380 }, { 690, 380 }, { 380, 30 }, { 380, 730 },
                { 720, 50 }, { 30, 750 }
        };
        for (int[] p : snowPositions) {
            layout.addDecoration(new view.Decoration(view.Decoration.Type.SNOWFLAKE,
                    new java.awt.Rectangle(p[0] + xShift, p[1], 12, 12)));
        }
        // Egy hegy az also road (rBot) felett -- alagut-szeru illuzio.
        // PONTOSAN a rBot szegmens kozeppontjara; bottom-edge a road
        // also szelevel egyezo (igy az "alagut-ivu" pontosan a road-on van).
        addMountainCenteredOn(layout, "rBot", 140, 140);
    }

    /**
     * Egy hegyet (MOUNTAIN tipus) elhelyez a megadott road szegmens
     * vizszintes kozepere, a hegy alsó szegelye a road alsó szegelyevel
     * egyenlo. Igy a tunnel-arch (a hegy aljan rajzolt sotetebb iv)
     * pontosan a road-on lesz.
     *
     * @param layout MapLayout (must have road bounds already set).
     * @param roadId Road ID.
     * @param mw     Mountain width.
     * @param mh     Mountain height.
     */
    static void addMountainCenteredOn(view.MapLayout layout, String roadId,
                                      int mw, int mh) {
        java.awt.Rectangle rb = layout.getRoadBounds(roadId);
        if (rb == null) {
            return;
        }
        int mx = rb.x + (rb.width - mw) / 2;
        int my = rb.y + rb.height - mh;
        layout.addDecoration(new view.Decoration(view.Decoration.Type.MOUNTAIN,
                new java.awt.Rectangle(mx, my, mw, mh)));
    }

    /**
     * Egy hidat (BRIDGE tipus) elhelyez a megadott road szegmens
     * vizszintes kozeppontjara, a hid magassaga = road magassaga.
     * Igy a pillarok pontosan a road ket vegen helyezkednek el.
     *
     * @param layout MapLayout (road bounds already set).
     * @param roadId Road ID.
     * @param bw     Bridge width (jellemzoen 220).
     */
    static void addBridgeCenteredOn(view.MapLayout layout, String roadId, int bw) {
        java.awt.Rectangle rb = layout.getRoadBounds(roadId);
        if (rb == null) {
            return;
        }
        int bx = rb.x + (rb.width - bw) / 2;
        int by = rb.y;
        layout.addDecoration(new view.Decoration(view.Decoration.Type.BRIDGE,
                new java.awt.Rectangle(bx, by, bw, rb.height)));
    }
}
