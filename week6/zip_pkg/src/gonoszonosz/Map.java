package gonoszonosz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Összefogja és kezeli az úthálózat összes csomópontját (Field-eket) és útját (Road-okat).
 * Gráfként kezeli a pályát, és biztosít útvonal-kereső funkcionalitást.
 * A havazást is koordinálja a snow() metóduson keresztül.
 * Az úthálózat felépítése Road objektumokon keresztül történik;
 * az addRoad() automatikusan regisztrálja a sávokat és épületeket a Field-gráfba.
 */
public class Map {

    /** A térkép összes mezője (Lane, Building leszármazottjai) – a BFS gráf csomópontjai. */
    private List<Field> fields;

    /** A térkép összes útja (Road objektumok) – struktúrális egységek. */
    private List<Road> roads;

    /**
     * Létrehoz egy új, üres térképet.
     */
    public Map() {
        this.fields = new ArrayList<>();
        this.roads = new ArrayList<>();
    }

    /**
     * Hozzáad egy utat (Road) a térkép úthálózatához.
     * Az SD-01 szekvenciának megfelelően a Map létrehozza a Road-ot,
     * amely maga hozza létre és tartalmazza a Lane objektumokat.
     * Az úton lévő összes Lane-t és Building-et automatikusan bejegyzi
     * a Field-gráfba is, hogy a BFS és a snow() elérje őket.
     * @param road a hozzáadandó út
     */
    public void addRoad(Road road) {
        roads.add(road);
        for (Lane l : road.getAllLanes()) {
            if (!fields.contains(l)) fields.add(l);
        }
        for (Building b : road.getBuildings()) {
            if (!fields.contains(b)) fields.add(b);
        }
    }

    /**
     * Visszaadja a térkép összes útját.
     * @return az összes Road objektum listája
     */
    public List<Road> getRoads() {
        return roads;
    }

    /**
     * Hozzáad egy mezőt közvetlenül a térkép Field-gráfjához
     * (pl. telephelyek, végállomások, amelyek nem kötődnek egyetlen úthoz sem).
     * @param f a hozzáadandó mező
     */
    public void addField(Field f) {
        if (!fields.contains(f)) fields.add(f);
    }

    /**
     * BFS algoritmussal megkeresi a legrövidebb járható útvonalat
     * két mező között a gráfban.
     * @param from kiindulási mező
     * @param to   célmező
     * @return a legrövidebb út mezőinek listája (from-tól to-ig), vagy üres lista, ha nincs út
     */
    public List<Field> findShortestPath(Field from, Field to) {
        Skeleton.call("Car", "Map", "findShortestPath(from, to)");
        List<Field> path = bfs(from, to);
        Skeleton.ret("List<Field>(size=" + path.size() + ")");
        return path;
    }

    /**
     * BFS (szélességi keresés) megvalósítása a legrövidebb út megtalálásához.
     * @param from kiindulási mező
     * @param to   célmező
     * @return az útvonal mezőinek listája
     */
    private List<Field> bfs(Field from, Field to) {
        if (from == null || to == null) return new ArrayList<>();
        java.util.Map<Field, Field> parent = new HashMap<>();
        Queue<Field> queue = new LinkedList<>();
        queue.add(from);
        parent.put(from, null);
        while (!queue.isEmpty()) {
            Field current = queue.poll();
            if (current == to) {
                return reconstructPath(parent, from, to);
            }
            for (Field neighbor : current.getNeighbors()) {
                if (!parent.containsKey(neighbor)) {
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Visszaépíti az útvonalat a BFS szülőtérkép alapján.
     * @param parent a BFS által épített szülőtérkép
     * @param from   kiindulási mező
     * @param to     célmező
     * @return az útvonal listája
     */
    private List<Field> reconstructPath(java.util.Map<Field, Field> parent, Field from, Field to) {
        LinkedList<Field> path = new LinkedList<>();
        Field current = to;
        while (current != null) {
            path.addFirst(current);
            current = parent.get(current);
        }
        return path;
    }

    /**
     * Minden Lane típusú mezőn növeli a hóréteg vastagságát (havazás szimulációja).
     * Road-okon keresztül iterál, így csak a ténylegesen az úthálózathoz tartozó
     * sávokon hív addSnow()-t. Building és egyéb Field típusokon nem hat.
     */
    public void snow() {
        Skeleton.call("GameLogic", "Map", "snow()");
        for (Road road : roads) {
            for (Lane l : road.getAllLanes()) {
                l.addSnow(1.0);
            }
        }
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a térkép összes mezőjét (Field-gráf csomópontjai).
     * @return az összes Field objektum listája
     */
    public List<Field> getFields() {
        return fields;
    }
}
