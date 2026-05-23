package main;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import view.MapLayout;

/**
 * Tetszoleges rows×cols racsos pálya konstruálasanak segedosztalya.
 *
 * Alapelvek (a 13. heti spec szerint):
 *  - Egy ROAD KIZAROLAG KET KERESZTEZODES KOZOTT spannol -- a hosszu,
 *    "tobb-kereszttel" road-ok darabolva keletkeznek. Igy pl. egy 2x3
 *    racshoz nem 2 horizontalis hosszu road kell hanem 4 (rH00 + rH01
 *    + rH10 + rH11), es 3 vertikalis (rV00 + rV10 + rV20).
 *  - Ez kikuszoboli azt a vizualis bug-ot, amikor a MapLayout.shrink-
 *    LanesAtIntersections csak EGY szakaszra zsugorit egy hosszu sav-ot
 *    (a tobbi szakasz csak road-aszfalt marad, nem lehet rákattintani).
 *
 * Naming konvenciok:
 *  - Horizontalis road segment (r, c → c+1): "rH" + r + c
 *  - Vertikalis road segment (r → r+1, c): "rV" + c + r
 *  - Savok: roadId-bol "r" → "l" csere, plus "_f" / "_b" suffix
 *    (rH00 → lH00_f, lH00_b; rV00 → lV00_f, lV00_b)
 *  - Megj.: a 1-jegyu r/c miatt max 10x10 racsig konfliktusmentes.
 *
 * Hasznalata: a Demo osztaly peldanyositja, buildRoadsAndCrosses()-t hiv,
 * sajat parancsait hozzaadja a getCommands()-hez (buildingek, jatekosok,
 * jarmuvek), a parser-en at lefuttatja oket, majd applyLayout(layout)
 * elhelyezi a road-okat, es a Demo manualisan setBounds-olja a buildings-
 * et a builder.outsideAt(...) altal szamolt poziciokra.
 */
public class MapBuilder {

    /** Racs sor-szam (intersection-sorok). */
    private final int rows;

    /** Racs oszlop-szam (intersection-oszlopok). */
    private final int cols;

    /** Cella-tav (pixelben, ket szomszedos intersection bal-felso sarka kozott). */
    private final int cell;

    /** A bal-felso (TL) intersection bal-felso sarkanak x-koordinataja. */
    private final int ox;

    /** A bal-felso (TL) intersection bal-felso sarkanak y-koordinataja. */
    private final int oy;

    /** Road kereszt-iranyu szelessege (= 2 * LANE_BREADTH a 2-savos roadhoz). */
    private final int roadBr = 2 * MapLayout.LANE_BREADTH;

    /** Az osszegyujtott CLI parancsok. */
    private final List<String> commands = new ArrayList<>();

    /**
     * Letrehoz egy MapBuilder-t a megadott parameterekkel.
     *
     * @param rows Intersection-sorok szama (>=2).
     * @param cols Intersection-oszlopok szama (>=2).
     * @param cell Cella-tav pixelben.
     * @param ox   Origo X.
     * @param oy   Origo Y.
     */
    public MapBuilder(int rows, int cols, int cell, int ox, int oy) {
        this.rows = rows;
        this.cols = cols;
        this.cell = cell;
        this.ox = ox;
        this.oy = oy;
    }

    /** @return Az eddig osszegyujtott parancslista (modositathato!). */
    public List<String> getCommands() {
        return commands;
    }

    /** @return A racs sor-szam. */
    public int getRows() { return rows; }

    /** @return A racs oszlop-szam. */
    public int getCols() { return cols; }

    /** @return A horizontalis road segment ID-ja (r row, c → c+1 oszlopok kozott). */
    public String horizRoadId(int r, int c) { return "rH" + r + c; }

    /** @return A horizontalis road sav-prefixe (l + ugyanaz). */
    public String horizLaneStem(int r, int c) { return "lH" + r + c; }

    /** @return A vertikalis road segment ID-ja (c col, r → r+1 sorok kozott). */
    public String vertRoadId(int c, int r) { return "rV" + c + r; }

    /** @return A vertikalis road sav-prefixe. */
    public String vertLaneStem(int c, int r) { return "lV" + c + r; }

    /**
     * Felepit minden road-szegmenst + minden intersection-cross-connect-et.
     * Minden szegmens egy create road + 2 lane + 2 add_to_road blokkot
     * eredmenyez; minden intersection-en az ott talalkozo OSSZES lane
     * minden parosa connect_fields-szel kerul ossze.
     */
    public void buildRoadsAndCrosses() {
        // Horizontalis szegmensek (rows * (cols-1) darab)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols - 1; c++) {
                roadBlock(horizRoadId(r, c), horizLaneStem(r, c));
            }
        }
        // Vertikalis szegmensek (cols * (rows-1) darab)
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows - 1; r++) {
                roadBlock(vertRoadId(c, r), vertLaneStem(c, r));
            }
        }
        // Minden intersection cross-connect
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                crossAt(r, c);
            }
        }
    }

    /**
     * Letrehoz egy road + 2 sav blokkot (forward + backward).
     */
    private void roadBlock(String roadId, String laneStem) {
        int length = cell + roadBr;
        commands.add("create road " + roadId);
        commands.add("set_road_length " + roadId + " " + length + ".0");
        commands.add("create lane " + laneStem + "_f");
        commands.add("create lane " + laneStem + "_b");
        commands.add("add_to_road " + roadId + " lane " + laneStem + "_f forward");
        commands.add("add_to_road " + roadId + " lane " + laneStem + "_b backward");
    }

    /**
     * Egy adott intersection-en cross-connect-eli az ott talalkozo
     * MINDEN sav-parosat. Igy a jarmu szabad utat talal az adott
     * intersection-en at barmely irányba (egyenes, kanyar, U-fordulo).
     */
    private void crossAt(int r, int c) {
        List<String> lanes = lanesAtIntersection(r, c);
        for (int i = 0; i < lanes.size(); i++) {
            for (int j = i + 1; j < lanes.size(); j++) {
                commands.add("connect_fields " + lanes.get(i) + " " + lanes.get(j));
            }
        }
    }

    /**
     * Visszaadja az (r, c) intersection-en talalkozo lane-ek listajat.
     * Maximum 8 sav (4 irany × 2 lane mind), de szel- es saroknal kevesebb.
     */
    private List<String> lanesAtIntersection(int r, int c) {
        List<String> result = new ArrayList<>();
        // Bal felol jovo horizontalis (r, c-1 → c)
        if (c > 0) {
            String s = horizLaneStem(r, c - 1);
            result.add(s + "_f");
            result.add(s + "_b");
        }
        // Jobb fele meno horizontalis (r, c → c+1)
        if (c < cols - 1) {
            String s = horizLaneStem(r, c);
            result.add(s + "_f");
            result.add(s + "_b");
        }
        // Fent levo vertikalis (c, r-1 → r)
        if (r > 0) {
            String s = vertLaneStem(c, r - 1);
            result.add(s + "_f");
            result.add(s + "_b");
        }
        // Lent levo vertikalis (c, r → r+1)
        if (r < rows - 1) {
            String s = vertLaneStem(c, r);
            result.add(s + "_f");
            result.add(s + "_b");
        }
        return result;
    }

    /**
     * Elhelyezi az osszes road-szegmenst pixel-pontosan a MapLayout-on.
     * Minden szegmens hossza = cell + roadBr; az intersect-overlap a
     * szomszedos szegmensekkel folytonos road-szovet-megjelenitest ad.
     *
     * @param layout MapLayout amelyre helyezunk.
     */
    public void applyLayout(MapLayout layout) {
        int segLength = cell + roadBr;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols - 1; c++) {
                int x = ox + c * cell;
                int y = oy + r * cell;
                layout.placeRoadHorizontal(horizRoadId(r, c), x, y, segLength);
            }
        }
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows - 1; r++) {
                int x = ox + c * cell;
                int y = oy + r * cell;
                layout.placeRoadVertical(vertRoadId(c, r), x, y, segLength);
            }
        }
    }

    /**
     * Egy epulet befoglalo teglalapja egy intersection KULSO sarka mellett.
     * A direction parameter a relativ pozitioja az intersection-hez kepest:
     *  - "TL"/"TR"/"BL"/"BR": atloos sarok (kulso negyed).
     *  - "T"/"B": kozeppont folott / alatt (oldalkozep).
     *  - "L"/"R": kozeppont balra / jobbra (oldalkozep).
     *
     * @param r    Intersection row.
     * @param c    Intersection col.
     * @param dir  Relativ pozicio ("TL", "TR", "BL", "BR", "T", "B", "L", "R").
     * @param bs   Building size (negyzet oldalhossz).
     * @param gap  Tav az intersection es az epulet kozott.
     * @return A befoglalo Rectangle, vagy null ha ismeretlen dir.
     */
    public Rectangle outsideAt(int r, int c, String dir, int bs, int gap) {
        int interX = ox + c * cell;
        int interY = oy + r * cell;
        switch (dir) {
            case "TL":
                return new Rectangle(interX - bs - gap, interY - bs - gap, bs, bs);
            case "TR":
                return new Rectangle(interX + roadBr + gap, interY - bs - gap, bs, bs);
            case "BL":
                return new Rectangle(interX - bs - gap, interY + roadBr + gap, bs, bs);
            case "BR":
                return new Rectangle(interX + roadBr + gap, interY + roadBr + gap, bs, bs);
            case "T":
                return new Rectangle(interX + roadBr / 2 - bs / 2, interY - bs - gap, bs, bs);
            case "B":
                return new Rectangle(interX + roadBr / 2 - bs / 2, interY + roadBr + gap, bs, bs);
            case "L":
                return new Rectangle(interX - bs - gap, interY + roadBr / 2 - bs / 2, bs, bs);
            case "R":
                return new Rectangle(interX + roadBr + gap, interY + roadBr / 2 - bs / 2, bs, bs);
            default:
                return null;
        }
    }
}
