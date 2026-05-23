package view;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cli.Context;
import model.Building;
import model.Field;
import model.HomeBase;
import model.Lane;
import model.Road;
import model.Terminal;

/**
 * A palya kepernyo-koordinatainak tarolasa.
 *
 * Ket uzemmod:
 *  - Explicit: a hivо (pl. DefaultDemo) kozvetlenul beallitja a
 *    road-pozíciókat (placeRoadHorizontal / placeRoadVertical
 *    publikus metodusokon at). Akkor a buildBuildings / autoLayoutMissing
 *    a Building-eket a savokhoz illeszti.
 *  - Implicit (autoLayout): a connect_fields gráf alapján
 *    automatikusan kalkulalja a road es lane pozíciókat. Ezt akkor
 *    hasznaljuk, amikor egy *_in.txt config-fajlt toltunk be load-dal.
 *
 * A spec 11.3.1/20. MapLayout (autoLayout(Map gameMap)) szerint a
 * sávok parhuzamosan futnak egy road-on belul (forwardLanes egymás
 * mellett, backwardLanes ugyanugy, atellenes oldalon).
 */
public class MapLayout {

    /** A mezok befoglalo teglalapjai pixelben. */
    private final Map<String, Rectangle> bounds = new LinkedHashMap<>();

    /** A roadok befoglalo teglalapjai (a road-szovet asszociatura). */
    private final Map<String, Rectangle> roadBounds = new LinkedHashMap<>();

    /** A road-ok iranya: true = horizontal, false = vertical. */
    private final Map<String, Boolean> roadHorizontal = new LinkedHashMap<>();

    /**
     * Pusztan kozmetikai vizualis-elemek (fak, hegyek, hidak, stb.) a
     * palya hatterere. A Demo osztalyok adjak hozza, a GamePanel
     * paintComponent-je ket fazisban rajzolja (background / overlay).
     */
    private final List<Decoration> decorations = new ArrayList<>();

    /** Egy sav szelessege/magassaga pixelben (a road kereszt-iranya). */
    public static final int LANE_BREADTH = 42;

    /** Egy sav hosszusaga pixelben (default, ha az autoLayout hivja). */
    public static final int LANE_LENGTH = 320;

    /** Egy epulet szelessege/magassaga pixelben. */
    public static final int BUILDING_SIZE = 60;

    /** Bal-felso margo pixelben. */
    private static final int MARGIN = 80;

    /** Komponensek kozotti fuggoleges hezag (multi-komponens layoutnal). */
    private static final int COMPONENT_GAP = 80;

    /**
     * Visszaadja az adott ID-hez tartozo befoglalo teglalapot.
     *
     * @param fieldId A keresett mezo ID-ja.
     * @return A teglalap, vagy null ha nincs ilyen ID.
     */
    public Rectangle getBounds(String fieldId) {
        return bounds.get(fieldId);
    }

    /**
     * Beallitja egy mezo befoglalo teglalapjat.
     *
     * @param fieldId A mezo ID-ja.
     * @param r       Az uj teglalap.
     */
    public void setBounds(String fieldId, Rectangle r) {
        bounds.put(fieldId, r);
    }

    /**
     * Visszaadja egy road befoglalo teglalapjat (RoadView-nak).
     *
     * @param roadId A road ID-ja.
     * @return A teglalap, vagy null.
     */
    public Rectangle getRoadBounds(String roadId) {
        return roadBounds.get(roadId);
    }

    /**
     * Megadja, hogy egy adott road horizontalisan helyezkedik-e el.
     *
     * @param roadId A road ID-ja.
     * @return true ha horizontalis, false ha vertikalis vagy ismeretlen.
     */
    public boolean isRoadHorizontal(String roadId) {
        Boolean b = roadHorizontal.get(roadId);
        return b != null && b;
    }

    /**
     * Visszaadja az osszes regisztralt road ID-bol befoglalo
     * teglalapba mutato szotart.
     *
     * @return A road bounds map.
     */
    public Map<String, Rectangle> getAllRoadBounds() {
        return roadBounds;
    }

    /**
     * Visszaadja az osszes letarolt ID->Rectangle parost a mezokre.
     *
     * @return A mezo-ID-bol befoglalo teglalapba mutato szotar.
     */
    public Map<String, Rectangle> getAll() {
        return bounds;
    }

    /**
     * Az osszes regisztralt befoglalo teglalapokat befoglalo
     * legkisebb teglalap.
     *
     * @return A teljes terkep meretet leiro teglalap.
     */
    public Rectangle getMapBounds() {
        if (bounds.isEmpty() && roadBounds.isEmpty()) {
            return new Rectangle(0, 0, 800, 600);
        }
        int maxX = 0;
        int maxY = 0;
        for (Rectangle r : bounds.values()) {
            maxX = Math.max(maxX, r.x + r.width);
            maxY = Math.max(maxY, r.y + r.height);
        }
        for (Rectangle r : roadBounds.values()) {
            maxX = Math.max(maxX, r.x + r.width);
            maxY = Math.max(maxY, r.y + r.height);
        }
        return new Rectangle(0, 0, maxX + MARGIN, maxY + MARGIN);
    }

    /**
     * Toroli az osszes nyilvantartott poziciot, beleertve a
     * dekoraciokat is (uj jatek inditasakor a Demo-k uj listat tudnak
     * felepiteni).
     */
    public void clear() {
        bounds.clear();
        roadBounds.clear();
        roadHorizontal.clear();
        decorations.clear();
    }

    /**
     * Hozzaad egy kozmetikai dekoraciot a palya hatterehez.
     *
     * @param d A dekoracio.
     */
    public void addDecoration(Decoration d) {
        if (d != null) {
            decorations.add(d);
        }
    }

    /**
     * Visszaadja a regisztralt dekoraciok listajat (sorrend = a Demo
     * felvetel sorrendje, ami a rajzolasi sorrend is).
     *
     * @return Az aktualis dekoracio-lista.
     */
    public List<Decoration> getDecorations() {
        return decorations;
    }

    /**
     * Egy kattintott pixel-koordinatahoz tartozo Field ID-jat adja
     * vissza. Az utoljara regisztralt Field-et adja vissza, igy az
     * epulet a sav fole rajzolva is kattinthato.
     *
     * @param p A kattintasi pont.
     * @return A talalt mezo ID-ja vagy null.
     */
    public String pickFieldId(Point p) {
        String last = null;
        for (Map.Entry<String, Rectangle> e : bounds.entrySet()) {
            if (e.getValue().contains(p)) {
                last = e.getKey();
            }
        }
        return last;
    }

    /**
     * Publikus metodus egy horizontalis road pozícionalasahoz.
     * A sávok PARHUZAMOSAN futnak: a forward sávok egymás mellett
     * felul (mindegyik a teljes road-hosszan), a backward sávok az
     * atellenes oldalon (forward-ok alatt).
     *
     * @param roadId A road ID-ja.
     * @param x      A road bal-felso x-pozíciója.
     * @param y      A road bal-felso y-pozíciója.
     * @param length A road pixel-hossza.
     */
    public void placeRoadHorizontal(String roadId, int x, int y, int length) {
        Object o = Context.objectManager.getObject(roadId);
        if (!(o instanceof Road)) return;
        Road r = (Road) o;
        int fwCount = r.forwardLanes.size();
        int bwCount = r.backwardLanes.size();
        int totalBreadth = Math.max(1, fwCount + bwCount) * LANE_BREADTH;
        for (int i = 0; i < fwCount; i++) {
            Lane lane = r.forwardLanes.get(i);
            String laneId = Context.objectManager.getId(lane);
            if (laneId == null) continue;
            bounds.put(laneId, new Rectangle(x, y + i * LANE_BREADTH,
                    length, LANE_BREADTH));
        }
        for (int i = 0; i < bwCount; i++) {
            Lane lane = r.backwardLanes.get(i);
            String laneId = Context.objectManager.getId(lane);
            if (laneId == null) continue;
            bounds.put(laneId, new Rectangle(x, y + (fwCount + i) * LANE_BREADTH,
                    length, LANE_BREADTH));
        }
        roadBounds.put(roadId, new Rectangle(x, y, length, totalBreadth));
        roadHorizontal.put(roadId, Boolean.TRUE);
    }

    /**
     * Publikus metodus egy vertikalis road pozícionalasahoz.
     * A sávok PARHUZAMOSAN futnak: a forward sávok egymás mellett
     * balrol, a backward sávok jobbrol.
     *
     * @param roadId A road ID-ja.
     * @param x      A road bal-felso x-pozíciója.
     * @param y      A road bal-felso y-pozíciója.
     * @param length A road pixel-hossza (vertikalis iranyban).
     */
    public void placeRoadVertical(String roadId, int x, int y, int length) {
        Object o = Context.objectManager.getObject(roadId);
        if (!(o instanceof Road)) return;
        Road r = (Road) o;
        int fwCount = r.forwardLanes.size();
        int bwCount = r.backwardLanes.size();
        int totalBreadth = Math.max(1, fwCount + bwCount) * LANE_BREADTH;
        for (int i = 0; i < fwCount; i++) {
            Lane lane = r.forwardLanes.get(i);
            String laneId = Context.objectManager.getId(lane);
            if (laneId == null) continue;
            bounds.put(laneId, new Rectangle(x + i * LANE_BREADTH, y,
                    LANE_BREADTH, length));
        }
        for (int i = 0; i < bwCount; i++) {
            Lane lane = r.backwardLanes.get(i);
            String laneId = Context.objectManager.getId(lane);
            if (laneId == null) continue;
            bounds.put(laneId, new Rectangle(x + (fwCount + i) * LANE_BREADTH, y,
                    LANE_BREADTH, length));
        }
        roadBounds.put(roadId, new Rectangle(x, y, totalBreadth, length));
        roadHorizontal.put(roadId, Boolean.FALSE);
    }

    /**
     * Akkor hivjuk, ha a hivо mar elozetesen beallitotta a road
     * pozíciókat (pl. DefaultDemo). Csak a Building-eket helyezi
     * el a kapcsolt sávok mellé, es a maradek Field-eket a layout
     * aljara. Elotte a savokat osszezsugoritjuk a sarki keresztezodes-
     * teglalapok kihagyasara, igy a click-detekcio es a lane-overlay
     * nem akad bele a keresztezodesbe.
     */
    public void placeBuildingsAndMissing() {
        shrinkLanesAtIntersections();
        placeBuildings();
        autoLayoutMissing();
    }

    /**
     * A spec 11.3.1/20. szerinti autoLayout. A pixelpoziciok
     * levezetese a connect_fields graf alapjan, harom retegben:
     *  1. Road-szintu BFS sav-szomszedsag alapjan (perpendikular
     *     orientacioval, kozeppont-igazitassal a keresztezodesnél).
     *  2. Sav-bounds osszezsugoritasa a sarki keresztezodesek korul
     *     (a sav csak a ket keresztezodes kozotti szakaszt fedi).
     *  3. Building elhelyezes a kapcsolt sav menten (zsugoritott
     *     bounds-on).
     *  4. Maradek Field-ekre egyszeru BFS-rács fallback.
     */
    public void autoLayout() {
        clear();
        placeRoadsByLaneAdjacency();
        shrinkLanesAtIntersections();
        placeBuildings();
        autoLayoutMissing();
    }

    /**
     * Sav-szomszedsag alapjan BFS-szel orientaciot rendel a road-okhoz
     * (egy connected component-en belül a kezdo road = horizontal,
     * minden szomszéd = perpendikularis), majd a road-okat csopor-
     * tositja orientáció szerint, ID-alapu sorrenddel; vegul egy
     * RACS-elrendezesben helyezi el oket. Igy multi-keresztezodeses
     * pályán (pl. 3x3, 2x4 stb. racs) az autoLayout szépen rendet
     * csinal.
     */
    private void placeRoadsByLaneAdjacency() {
        List<Road> roads = collectRoads();
        if (roads.isEmpty()) {
            return;
        }
        // 1. BFS-szel orientacio detektor (componentenkent)
        Map<Road, Boolean> horiz = new HashMap<>();
        Set<Road> visited = new HashSet<>();
        for (Road start : roads) {
            if (visited.contains(start)) continue;
            horiz.put(start, Boolean.TRUE);
            visited.add(start);
            Deque<Road> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                Road cur = queue.poll();
                boolean curHoriz = horiz.get(cur);
                for (Road n : findRoadNeighbors(cur)) {
                    if (visited.contains(n)) continue;
                    horiz.put(n, !curHoriz);
                    visited.add(n);
                    queue.add(n);
                }
            }
        }

        // 2. Csoportositas orientacio szerint, sorba rendezve ID-alapon
        List<Road> horRoads = new ArrayList<>();
        List<Road> verRoads = new ArrayList<>();
        for (Road r : roads) {
            if (Boolean.TRUE.equals(horiz.get(r))) {
                horRoads.add(r);
            } else {
                verRoads.add(r);
            }
        }
        horRoads.sort((a, b) -> safeId(a).compareTo(safeId(b)));
        verRoads.sort((a, b) -> safeId(a).compareTo(safeId(b)));

        // 3. Racs-elrendezes parameterei
        int cellGap = LANE_LENGTH;  // keresztezodes-kozeppontok kozti tav
        int ext = LANE_LENGTH / 5;  // a road tulnyulasa a keresztezodesen
        int gridOX = MARGIN + 60;   // bal-felso elso keresztezodes x
        int gridOY = MARGIN + 60;   // bal-felso elso keresztezodes y

        // 4. Pozíciók kiosztasa
        // Horizontalis road hossza = (verRoads.size() - 1) * cellGap + 2*ext + roadBr
        // de minimum a sajat roadBr
        for (int i = 0; i < horRoads.size(); i++) {
            Road r = horRoads.get(i);
            String rid = safeId(r);
            if (rid == null) continue;
            int len;
            if (verRoads.isEmpty()) {
                len = LANE_LENGTH;
            } else {
                len = (verRoads.size() - 1) * cellGap + 2 * ext + roadBreadthInPixels(r);
            }
            int x = gridOX - ext;
            int y = gridOY + i * cellGap;
            placeRoadHorizontal(rid, x, y, len);
        }
        for (int j = 0; j < verRoads.size(); j++) {
            Road r = verRoads.get(j);
            String rid = safeId(r);
            if (rid == null) continue;
            int len;
            if (horRoads.isEmpty()) {
                len = LANE_LENGTH;
            } else {
                len = (horRoads.size() - 1) * cellGap + 2 * ext + roadBreadthInPixels(r);
            }
            int x = gridOX + j * cellGap;
            int y = gridOY - ext;
            placeRoadVertical(rid, x, y, len);
        }
    }

    /**
     * Egy road ID-jat adja vissza biztonsagosan (null ha nincs).
     *
     * @param r A road.
     * @return Az ID, vagy null.
     */
    private String safeId(Road r) {
        return Context.objectManager.getId(r);
    }

    /**
     * Egy road kereszt-iranyu szelessege (a sávok szama × szelesseg).
     *
     * @param r A road.
     * @return Pixelben.
     */
    private int roadBreadthInPixels(Road r) {
        int fw = r.forwardLanes.size();
        int bw = r.backwardLanes.size();
        return Math.max(1, fw + bw) * LANE_BREADTH;
    }

    /**
     * Osszegyujti az osszes regisztralt Road-ot.
     *
     * @return A roadok listaja.
     */
    private List<Road> collectRoads() {
        List<Road> result = new ArrayList<>();
        for (Map.Entry<String, Object> e : Context.objectManager.getAll().entrySet()) {
            if (e.getValue() instanceof Road) {
                result.add((Road) e.getValue());
            }
        }
        return result;
    }

    /**
     * Az adott road sav-szomszedjait keresi a connect_fields gráfban.
     *
     * @param r A road.
     * @return A szomszedos road-ok.
     */
    private List<Road> findRoadNeighbors(Road r) {
        List<Road> result = new ArrayList<>();
        Set<Lane> myLanes = new LinkedHashSet<>();
        myLanes.addAll(r.forwardLanes);
        myLanes.addAll(r.backwardLanes);
        if (myLanes.isEmpty()) {
            return result;
        }
        for (Map.Entry<String, Object> e : Context.objectManager.getAll().entrySet()) {
            if (!(e.getValue() instanceof Road)) continue;
            Road other = (Road) e.getValue();
            if (other == r) continue;
            Set<Lane> otherLanes = new LinkedHashSet<>();
            otherLanes.addAll(other.forwardLanes);
            otherLanes.addAll(other.backwardLanes);
            if (otherLanes.isEmpty()) continue;
            boolean isNeighbor = false;
            outer:
            for (Lane ml : myLanes) {
                for (Field n : ml.getNeighbors()) {
                    if (n instanceof Lane && otherLanes.contains(n)) {
                        isNeighbor = true;
                        break outer;
                    }
                }
            }
            if (isNeighbor) {
                result.add(other);
            }
        }
        return result;
    }

    /**
     * Osszezsugoritja az osszes sav (Lane) befoglalo teglalapjat,
     * hogy ne fedjek a sarki keresztezodeseket. A road tarmac (a
     * RoadView altal rajzolt szurke hatter) tovabbra is folytono-
     * san atlatszik a keresztezodesen, de a sav-overlay (ho, jeg,
     * iranymutato nyil, kek move-target-highlight) es a click-
     * detekcio (pickFieldId) csak a keresztezodesen kivuli
     * szakaszra hat. Igy a "roads only span between two
     * intersections" osszbenyomas vizualisan is megjelenik.
     *
     * Algoritmus per sav: a getIntersectionRects() altal vissza-
     * adott teglalapokkal "kilyukasztva" a sav bounds-at, a
     * keletkezo szakaszok kozul a LEGNAGYOBB teruletut tartjuk meg.
     * 4-oldalu negyzet eseten ez a ket sarok kozti kozepso szakasz.
     */
    public void shrinkLanesAtIntersections() {
        List<Rectangle> intersections = getIntersectionRects();
        if (intersections.isEmpty()) {
            return;
        }
        Map<String, Rectangle> updated = new LinkedHashMap<>();
        for (Map.Entry<String, Rectangle> entry : bounds.entrySet()) {
            String id = entry.getKey();
            Object o = Context.objectManager.getObject(id);
            if (!(o instanceof Lane)) {
                continue;
            }
            Rectangle lane = entry.getValue();
            boolean horiz = lane.width > lane.height;
            List<Rectangle> overlaps = new ArrayList<>();
            for (Rectangle inter : intersections) {
                Rectangle ovr = lane.intersection(inter);
                if (!ovr.isEmpty()) {
                    overlaps.add(ovr);
                }
            }
            if (overlaps.isEmpty()) {
                continue;
            }
            if (horiz) {
                overlaps.sort((a, b) -> Integer.compare(a.x, b.x));
            } else {
                overlaps.sort((a, b) -> Integer.compare(a.y, b.y));
            }
            List<Rectangle> gaps = new ArrayList<>();
            if (horiz) {
                int currentX = lane.x;
                int laneEndX = lane.x + lane.width;
                for (Rectangle ovr : overlaps) {
                    int ovrEndX = ovr.x + ovr.width;
                    if (ovr.x > currentX) {
                        gaps.add(new Rectangle(currentX, lane.y,
                                ovr.x - currentX, lane.height));
                    }
                    if (ovrEndX > currentX) {
                        currentX = ovrEndX;
                    }
                }
                if (currentX < laneEndX) {
                    gaps.add(new Rectangle(currentX, lane.y,
                            laneEndX - currentX, lane.height));
                }
            } else {
                int currentY = lane.y;
                int laneEndY = lane.y + lane.height;
                for (Rectangle ovr : overlaps) {
                    int ovrEndY = ovr.y + ovr.height;
                    if (ovr.y > currentY) {
                        gaps.add(new Rectangle(lane.x, currentY,
                                lane.width, ovr.y - currentY));
                    }
                    if (ovrEndY > currentY) {
                        currentY = ovrEndY;
                    }
                }
                if (currentY < laneEndY) {
                    gaps.add(new Rectangle(lane.x, currentY,
                            lane.width, laneEndY - currentY));
                }
            }
            if (gaps.isEmpty()) {
                continue;
            }
            Rectangle largest = gaps.get(0);
            long largestArea = (long) largest.width * largest.height;
            for (Rectangle g : gaps) {
                long area = (long) g.width * g.height;
                if (area > largestArea) {
                    largest = g;
                    largestArea = area;
                }
            }
            updated.put(id, largest);
        }
        bounds.putAll(updated);
    }

    /**
     * Visszaadja az osszes road-keresztezodes pixel-teglalapjat
     * (horizontalis ∩ vertikalis road bounds). A RoadView ezt
     * hasznalja a kozepvonal megszakitasahoz.
     *
     * @return A keresztezodes-teglalapok listaja.
     */
    public List<Rectangle> getIntersectionRects() {
        List<Rectangle> hor = new ArrayList<>();
        List<Rectangle> ver = new ArrayList<>();
        for (Map.Entry<String, Rectangle> e : roadBounds.entrySet()) {
            if (isRoadHorizontal(e.getKey())) {
                hor.add(e.getValue());
            } else {
                ver.add(e.getValue());
            }
        }
        List<Rectangle> result = new ArrayList<>();
        for (Rectangle hb : hor) {
            for (Rectangle vb : ver) {
                int x1 = Math.max(hb.x, vb.x);
                int y1 = Math.max(hb.y, vb.y);
                int x2 = Math.min(hb.x + hb.width, vb.x + vb.width);
                int y2 = Math.min(hb.y + hb.height, vb.y + vb.height);
                if (x2 > x1 && y2 > y1) {
                    result.add(new Rectangle(x1, y1, x2 - x1, y2 - y1));
                }
            }
        }
        return result;
    }

    /**
     * Az osszes Building-et a kapcsolt sav menten helyezi el.
     * Ha az epulet pozíciója mar be van allitva (pl. a DefaultDemo
     * manualisan tette le), kihagyjuk -- igy a manualis pozíció
     * megmarad.
     */
    private void placeBuildings() {
        Map<String, List<String>> laneToBuildings = new LinkedHashMap<>();
        List<Map.Entry<String, Object>> standalone = new ArrayList<>();
        for (Map.Entry<String, Object> e : Context.objectManager.getAll().entrySet()) {
            if (!(e.getValue() instanceof Building)) continue;
            Building b = (Building) e.getValue();
            String bid = e.getKey();
            Lane attached = null;
            for (Field n : b.getNeighbors()) {
                if (n instanceof Lane) {
                    attached = (Lane) n;
                    break;
                }
            }
            if (attached == null) {
                standalone.add(e);
                continue;
            }
            String laneId = Context.objectManager.getId(attached);
            if (laneId == null) {
                standalone.add(e);
                continue;
            }
            // Ha mar van pozíciója, kihagyjuk (pl. DefaultDemo allitotta be)
            if (bounds.containsKey(bid)) continue;
            laneToBuildings.computeIfAbsent(laneId, k -> new ArrayList<>()).add(bid);
        }

        for (Map.Entry<String, List<String>> entry : laneToBuildings.entrySet()) {
            String laneId = entry.getKey();
            List<String> bids = entry.getValue();
            Rectangle laneB = bounds.get(laneId);
            if (laneB == null) continue;
            Object laneObj = Context.objectManager.getObject(laneId);
            if (!(laneObj instanceof Lane)) continue;
            Lane lane = (Lane) laneObj;
            Road owner = findRoadOf(lane);
            if (owner == null) {
                for (int i = 0; i < bids.size(); i++) {
                    int x = laneB.x + laneB.width + 12;
                    int y = laneB.y + i * (BUILDING_SIZE + 10);
                    bounds.put(bids.get(i), new Rectangle(x, y,
                            BUILDING_SIZE, BUILDING_SIZE));
                }
                continue;
            }
            String roadId = Context.objectManager.getId(owner);
            boolean horiz = isRoadHorizontal(roadId);
            boolean isForward = owner.forwardLanes.contains(lane);
            int n = bids.size();
            for (int i = 0; i < n; i++) {
                String bid = bids.get(i);
                double frac;
                if (n == 1) {
                    frac = 0.22;
                } else {
                    frac = 0.10 + (i * 0.80 / (n - 1));
                }
                int bx, by;
                if (horiz) {
                    int xCenter = laneB.x + (int) (frac * laneB.width);
                    bx = xCenter - BUILDING_SIZE / 2;
                    if (isForward) {
                        by = laneB.y - BUILDING_SIZE - 14;
                    } else {
                        by = laneB.y + laneB.height + 14;
                    }
                } else {
                    int yCenter = laneB.y + (int) (frac * laneB.height);
                    by = yCenter - BUILDING_SIZE / 2;
                    if (isForward) {
                        bx = laneB.x - BUILDING_SIZE - 14;
                    } else {
                        bx = laneB.x + laneB.width + 14;
                    }
                }
                bounds.put(bid, new Rectangle(bx, by, BUILDING_SIZE, BUILDING_SIZE));
            }
        }

        int offsetY = 0;
        for (Map.Entry<String, Object> e : standalone) {
            String bid = e.getKey();
            if (bounds.containsKey(bid)) continue;
            bounds.put(bid, new Rectangle(MARGIN,
                    getMapBounds().height + 20 + offsetY,
                    BUILDING_SIZE, BUILDING_SIZE));
            offsetY += BUILDING_SIZE + 10;
        }
    }

    /**
     * Megkeresi azt a roadot, amelynek forward vagy backward savjai
     * kozott szerepel az adott Lane.
     *
     * @param lane A keresett Lane.
     * @return A Road, vagy null.
     */
    private Road findRoadOf(Lane lane) {
        for (Map.Entry<String, Object> e : Context.objectManager.getAll().entrySet()) {
            if (e.getValue() instanceof Road) {
                Road r = (Road) e.getValue();
                if (r.forwardLanes.contains(lane) || r.backwardLanes.contains(lane)) {
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * Minden olyan Field, amely a meglevo pozícionalas utan sem
     * kapott pozíciót, fallback-kent egy BFS-rácsra kerul a layout
     * aljara.
     */
    public void autoLayoutMissing() {
        List<String> missing = new ArrayList<>();
        for (Map.Entry<String, Object> e : Context.objectManager.getAll().entrySet()) {
            Object o = e.getValue();
            if (!(o instanceof Field)) continue;
            String id = e.getKey();
            if (bounds.containsKey(id)) continue;
            missing.add(id);
        }
        if (missing.isEmpty()) {
            return;
        }
        List<String> ordered = new ArrayList<>();
        Set<String> done = new LinkedHashSet<>();
        for (String start : missing) {
            if (done.contains(start)) continue;
            Deque<String> queue = new ArrayDeque<>();
            queue.add(start);
            done.add(start);
            while (!queue.isEmpty()) {
                String cur = queue.poll();
                ordered.add(cur);
                Object o = Context.objectManager.getObject(cur);
                if (!(o instanceof Field)) continue;
                for (Field n : ((Field) o).getNeighbors()) {
                    String nid = Context.objectManager.getId(n);
                    if (nid == null) continue;
                    if (done.contains(nid)) continue;
                    if (!missing.contains(nid)) continue;
                    done.add(nid);
                    queue.add(nid);
                }
            }
        }
        int yBase = Math.max(getMapBounds().height + 20, MARGIN);
        int cols = 6;
        int colWidth = LANE_LENGTH + 14;
        int rowHeight = Math.max(LANE_BREADTH, BUILDING_SIZE) + 14;
        for (int i = 0; i < ordered.size(); i++) {
            String id = ordered.get(i);
            Object o = Context.objectManager.getObject(id);
            int col = i % cols;
            int row = i / cols;
            int x = MARGIN + col * colWidth;
            int y = yBase + row * rowHeight;
            int w = LANE_LENGTH;
            int h = LANE_BREADTH;
            if (o instanceof HomeBase || o instanceof Terminal || o instanceof Building) {
                w = BUILDING_SIZE;
                h = BUILDING_SIZE;
            }
            bounds.put(id, new Rectangle(x, y, w, h));
        }
    }
}
