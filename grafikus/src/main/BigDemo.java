package main;

import cli.CommandParser;
import view.MapLayout;

/**
 * Nagy meretu pálya (MapType.HARD): 3x3 racs intersection.
 *
 * A 13. heti modositas alapjan a road-ok szegmentaltak (MapBuilder):
 * minden road KET szomszedos intersection kozott spannol -- igy nem
 * keletkezik "ket egyenlo gap" / "elveszett kozepso szakasz" bug.
 *
 * Roadok (12 db):
 *  - 6 horizontalis: rH00, rH01 (top), rH10, rH11 (mid), rH20, rH21 (bot)
 *  - 6 vertikalis:   rV00, rV01 (left), rV10, rV11 (mid), rV20, rV21 (right)
 *
 * Intersection-ek (9 db) ID-zes: (r, c) ahol r, c ∈ {0,1,2}.
 *
 * Jatekosok:
 *  - Cleaner c1 (sp1 a hb1-nel), Cleaner c2 (sp2 a hb2-nel)
 *  - BusDriver bd1 (bus1), BusDriver bd2 (bus2)
 *
 * Epuletek (8 darab a 3x3 racs korul + 2 oldalvasarcsi):
 *  - hb1 (TL  – (0,0)): c1 hokotroje
 *  - t1  (T   – (0,1))
 *  - t2  (TR  – (0,2))
 *  - b1  (L   – (1,0))
 *  - b2  (R   – (1,2))
 *  - hb2 (BL  – (2,0)): c2 hokotroje
 *  - t3  (B   – (2,1))
 *  - t4  (BR  – (2,2))
 *
 * NPC autok: car1 (b1 → t2), car2 (b2 → hb2), car3 (b1 → t4).
 */
public final class BigDemo {

    /** Cella-tav (kisebb, hogy a 3x3 racs befejezzen a 1200x880 ablakba). */
    public static final int CELL = 250;

    /** Origo X. */
    public static final int OX = 110;

    /** Origo Y. */
    public static final int OY = 150;

    /** Privat: utility class. */
    private BigDemo() {
    }

    /**
     * Alkalmazza a nagy palya konfiguracioját.
     *
     * @param parser A felkonfiguralt CommandParser.
     * @param layout Az aktiv MapLayout.
     */
    public static void apply(CommandParser parser, MapLayout layout) {
        MapBuilder builder = new MapBuilder(3, 3, CELL, OX, OY);
        // 1. Roadok + sarki cross-connect
        builder.buildRoadsAndCrosses();

        // 2. Epuletek
        builder.getCommands().add("create homebase hb1");
        builder.getCommands().add("create terminal t1");
        builder.getCommands().add("create terminal t2");
        builder.getCommands().add("create building b1");
        builder.getCommands().add("create building b2");
        builder.getCommands().add("create homebase hb2");
        builder.getCommands().add("create terminal t3");
        builder.getCommands().add("create terminal t4");

        // 3. Epuletek -> lane
        // Top row: hb1 / t1 a lH00_f-en, t2 a lH01_f-en
        builder.getCommands().add("connect_fields hb1 lH00_f");
        builder.getCommands().add("connect_fields t1 lH00_f");
        builder.getCommands().add("connect_fields t2 lH01_f");
        // Mid row (oldalkozepi epuletek):
        //   b1 az MR (1,0) sarokrol --> lV00 vagy lV01; valasszuk lV00_f-t
        //   b2 az MR (1,2) sarokrol --> lV20 vagy lV21; valasszuk lV21_b-t
        builder.getCommands().add("connect_fields b1 lV00_f");
        builder.getCommands().add("connect_fields b2 lV21_b");
        // Bot row:
        builder.getCommands().add("connect_fields hb2 lH20_f");
        builder.getCommands().add("connect_fields t3 lH20_b");
        builder.getCommands().add("connect_fields t4 lH21_b");

        // 4. Jatekosok
        builder.getCommands().add("create cleaner c1");
        builder.getCommands().add("create cleaner c2");
        builder.getCommands().add("create busdriver bd1");
        builder.getCommands().add("create busdriver bd2");

        // 5. Hokotrok
        builder.getCommands().add("spawn snowplow sp1 hb1 c1");
        builder.getCommands().add("spawn snowplow sp2 hb2 c2");

        // 6. Buszok (13. heti: jaratuk ket vegallomas kozott)
        // bus1 (bd1): t1 <-> t2 (felso ket terminal)
        builder.getCommands().add("spawn bus bus1 lH00_f bd1 t1 t2");
        // bus2 (bd2): t3 <-> t4 (also ket terminal)
        builder.getCommands().add("spawn bus bus2 lH20_f bd2 t3 t4");

        // 7. NPC autok
        builder.getCommands().add("spawn car car1 lV00_f b1 t2");
        builder.getCommands().add("spawn car car2 lV21_b b2 hb2");
        builder.getCommands().add("spawn car car3 lV00_f b1 t4");

        // 8. Kezdo penz (5000 cleaner-enkent -- a fej-arak es a
        // hokotro-vasarlas (1000) figyelembevetelevel meretezve).
        builder.getCommands().add("set_money c1 5000");
        builder.getCommands().add("set_money c2 5000");

        // Parancsok lefuttatasa
        for (String line : builder.getCommands()) {
            parser.parseLine(line);
        }

        // Layout: road-szegmensek + buildings
        builder.applyLayout(layout);
        int bs = MapLayout.BUILDING_SIZE;
        int gap = 16;
        layout.setBounds("hb1", builder.outsideAt(0, 0, "TL", bs, gap));
        layout.setBounds("t1",  builder.outsideAt(0, 1, "T",  bs, gap));
        layout.setBounds("t2",  builder.outsideAt(0, 2, "TR", bs, gap));
        layout.setBounds("b1",  builder.outsideAt(1, 0, "L",  bs, gap));
        layout.setBounds("b2",  builder.outsideAt(1, 2, "R",  bs, gap));
        layout.setBounds("hb2", builder.outsideAt(2, 0, "BL", bs, gap));
        layout.setBounds("t3",  builder.outsideAt(2, 1, "B",  bs, gap));
        layout.setBounds("t4",  builder.outsideAt(2, 2, "BR", bs, gap));

        // 13. heti kozmetikai dekoraciok
        addCosmetics(layout);
    }

    /**
     * Kozmetikai dekoraciok a nagy palyahoz. 3x3 racs eseten a
     * 4 kozepso racs-cella belsejei (~250x250 px) befogadnak nehany
     * fa+szikla csoportot. Az MM (kozeppont) cellaba kerul a
     * "BRIDGE" felirat, a kozepso road egyik fele alagut.
     *
     * @param layout MapLayout.
     */
    private static void addCosmetics(view.MapLayout layout) {
        // Fak a 4 belso cella koruli ures teruleten
        int[][] treePositions = {
                { 200, 240 }, { 240, 300 }, { 280, 350 },
                { 470, 250 }, { 510, 300 }, { 540, 350 },
                { 200, 500 }, { 240, 540 }, { 270, 580 },
                { 470, 510 }, { 530, 560 }, { 580, 540 }
        };
        for (int[] p : treePositions) {
            layout.addDecoration(new view.Decoration(view.Decoration.Type.TREE,
                    new java.awt.Rectangle(p[0], p[1], 16, 22)));
        }
        // Sziklak a fak kozott
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(260, 320, 14, 8)));
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(500, 320, 12, 7)));
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(260, 570, 14, 8)));
        layout.addDecoration(new view.Decoration(view.Decoration.Type.ROCK,
                new java.awt.Rectangle(520, 570, 12, 7)));
        // Hopelyhek a palya szelein
        int[][] snowPositions = {
                { 30, 400 }, { 800, 400 }, { 360, 30 }, { 360, 850 },
                { 30, 100 }, { 800, 100 }, { 30, 800 }, { 800, 800 }
        };
        for (int[] p : snowPositions) {
            layout.addDecoration(new view.Decoration(view.Decoration.Type.SNOWFLAKE,
                    new java.awt.Rectangle(p[0], p[1], 11, 11)));
        }
        // Egy hegy a kozepso (rMidH) road felso szegmensere -- alagut
        layout.addDecoration(new view.Decoration(view.Decoration.Type.MOUNTAIN,
                new java.awt.Rectangle(170, 360, 130, 100)));
        // Egy hegy a (rBot) road masodik felere
        layout.addDecoration(new view.Decoration(view.Decoration.Type.MOUNTAIN,
                new java.awt.Rectangle(420, 620, 130, 100)));
        // Egy hid a felso road masodik szegmensere
        layout.addDecoration(new view.Decoration(view.Decoration.Type.BRIDGE,
                new java.awt.Rectangle(380, 155, 180, 70)));
    }
}
