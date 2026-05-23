package main;

import cli.CommandParser;
import view.MapLayout;

/**
 * Kozepes-meretu pálya (MapType.MEDIUM): 2x3 racs intersection.
 *
 * A 13. heti modositas alapjan a road-ok szegmentaltak: minden
 * road kizarolag KET szomszedos intersection kozott spannol. Ez
 * a MapBuilder-en at automatikusan keletkezik.
 *
 * Roadok (7 db):
 *  - 4 horizontalis: rH00, rH01 (top row), rH10, rH11 (bot row)
 *  - 3 vertikalis:   rV00 (left), rV10 (mid), rV20 (right)
 *
 * Intersection-ek (6 db) ID-zes: (r, c) ahol r ∈ {0,1}, c ∈ {0,1,2}.
 *
 * Jatekosok:
 *  - Cleaner c1: 2 hokotrovel (sp1, sp2 mindketto hb1-en parkolva)
 *  - BusDriver bd1: 1 busz (bus1)
 *  - BusDriver bd2: 1 busz (bus2)
 *
 * Epuletek (6 sarok kulso pontjain):
 *  - hb1 (TL  – (0,0) outside): c1 hokotroi
 *  - t1  (T   – (0,1) folott)
 *  - t2  (TR  – (0,2) outside)
 *  - b1  (BL  – (1,0) outside)
 *  - t3  (B   – (1,1) alatt)
 *  - b2  (BR  – (1,2) outside)
 *
 * NPC autok: car1 (b1 → t2), car2 (b2 → t1).
 */
public final class MediumDemo {

    /** Cella-tav pixelben. */
    public static final int CELL = 380;

    /** Origo X. */
    public static final int OX = 120;

    /** Origo Y. */
    public static final int OY = 160;

    /** Privat: utility class. */
    private MediumDemo() {
    }

    /**
     * Alkalmazza a kozepes-meretu palya konfiguracioját.
     *
     * @param parser A felkonfiguralt CommandParser.
     * @param layout Az aktiv MapLayout.
     */
    public static void apply(CommandParser parser, MapLayout layout) {
        MapBuilder builder = new MapBuilder(2, 3, CELL, OX, OY);
        // 1. Roadok + sarki cross-connect
        builder.buildRoadsAndCrosses();

        // 2. Epuletek (6 sarok korul)
        builder.getCommands().add("create homebase hb1");
        builder.getCommands().add("create terminal t1");
        builder.getCommands().add("create terminal t2");
        builder.getCommands().add("create building b1");
        builder.getCommands().add("create terminal t3");
        builder.getCommands().add("create building b2");

        // 3. Epuletek -> lane (a placeBuildings es a jarmu-navigaicio graf-szomszedsaga miatt)
        // Top row: hb1 / t1 a bal-szegmens forwardjan (lH00_f), t2 a jobb-szegmens forwardjan (lH01_f)
        builder.getCommands().add("connect_fields hb1 lH00_f");
        builder.getCommands().add("connect_fields t1 lH00_f");
        builder.getCommands().add("connect_fields t2 lH01_f");
        // Bot row: b1 a bal-szegmens backwardjan, t3 / b2 a jobb-szegmens backwardjan
        builder.getCommands().add("connect_fields b1 lH10_b");
        builder.getCommands().add("connect_fields t3 lH11_b");
        builder.getCommands().add("connect_fields b2 lH11_b");

        // 4. Jatekosok
        builder.getCommands().add("create cleaner c1");
        builder.getCommands().add("create busdriver bd1");
        builder.getCommands().add("create busdriver bd2");

        // 5. Hokotrok (mindketto hb1-en, c1 tulajdonban)
        builder.getCommands().add("spawn snowplow sp1 hb1 c1");
        builder.getCommands().add("spawn snowplow sp2 hb1 c1");

        // 6. Buszok (13. heti: jaratuk ket vegallomas kozott)
        // bus1 (bd1): t1 <-> t2 (felso szegmensek terminaljai)
        builder.getCommands().add("spawn bus bus1 lH00_f bd1 t1 t2");
        // bus2 (bd2): t3 <-> t2 (also-kozep es jobb-felso terminalok)
        builder.getCommands().add("spawn bus bus2 lH11_f bd2 t3 t2");

        // 7. NPC autok
        builder.getCommands().add("spawn car car1 lH10_b b1 t2");
        builder.getCommands().add("spawn car car2 lH11_b b2 t1");

        // 8. Kezdo penz (5000 -- a fej-arakat figyelembe veve)
        builder.getCommands().add("set_money c1 5000");

        // Parancsok lefuttatasa
        for (String line : builder.getCommands()) {
            parser.parseLine(line);
        }

        // Layout (road-szegmensek + buildings)
        builder.applyLayout(layout);
        int bs = MapLayout.BUILDING_SIZE;
        int gap = 18;
        layout.setBounds("hb1", builder.outsideAt(0, 0, "TL", bs, gap));
        layout.setBounds("t1",  builder.outsideAt(0, 1, "T",  bs, gap));
        layout.setBounds("t2",  builder.outsideAt(0, 2, "TR", bs, gap));
        layout.setBounds("b1",  builder.outsideAt(1, 0, "BL", bs, gap));
        layout.setBounds("t3",  builder.outsideAt(1, 1, "B",  bs, gap));
        layout.setBounds("b2",  builder.outsideAt(1, 2, "BR", bs, gap));

        // 13. heti kozmetikai dekoraciok (a 2x3 racs ures kozepso
        // foltjai + alagut/hid illuzio nehany roadon).
        addCosmetics(layout);
    }

    /**
     * Dekoraciok hozzaadasa a kozepes palya hatterehez.
     *
     * @param layout MapLayout.
     */
    private static void addCosmetics(view.MapLayout layout) {
        // 13. heti utolso revisio: shift +40 a jobbb-vizualis centralasert
        int xShift = 40;
        // Fak a 2 racs-cella belsejeben
        int[][] treePositions = {
                { 270, 320 }, { 340, 290 }, { 410, 350 },
                { 470, 300 }, { 660, 320 }, { 720, 290 },
                { 790, 360 }, { 850, 320 },
                { 290, 430 }, { 380, 470 }, { 700, 430 }, { 790, 470 }
        };
        for (int[] p : treePositions) {
            layout.addDecoration(new view.Decoration(view.Decoration.Type.TREE,
                    new java.awt.Rectangle(p[0] + xShift, p[1], 18, 24)));
        }
        // Sziklak
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(310 + xShift, 390, 14, 8)));
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(650 + xShift, 400, 16, 9)));
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(840 + xShift, 470, 14, 8)));
        // Hopelyhek a palya szelein
        int[][] snowPositions = {
                { 50, 380 }, { 1080, 380 }, { 470, 30 }, { 470, 740 },
                { 1080, 50 }, { 30, 720 }, { 920, 30 }, { 920, 740 }
        };
        for (int[] p : snowPositions) {
            layout.addDecoration(new view.Decoration(view.Decoration.Type.SNOWFLAKE,
                    new java.awt.Rectangle(p[0] + xShift, p[1], 12, 12)));
        }
        // Egy hegy a bot-row jobb szegmens (rH11) pontos kozeppontjan -- alagut
        DefaultDemo.addMountainCenteredOn(layout, "rH11", 140, 140);
        // Egy hid (BRIDGE) a felso road TR szegmens (rH01) pontos kozeppontjan
        DefaultDemo.addBridgeCenteredOn(layout, "rH01", 220);
    }
}
