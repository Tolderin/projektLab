package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * Az úthálózat egy útját reprezentálja.
 * Összefogja a sávokat és az épületeket, nyilvántartja az út végpontjait
 * (szomszédos utakat) és az irányított sávokat.
 * A Map gráfjában az utak struktúrális egységekként szerepelnek;
 * a Lane-ek az úton belüli forgalmi sávok, amelyeket a Road hoz létre és tart nyilván.
 */
public class Road {

    /** Az út neve (azonosításhoz). */
    private String name;

    /** Az út hossza. */
    private double length;

    /** A szomszédos utak listája az út start végén. */
    private List<Road> startPoint;

    /** A szomszédos utak listája az út end végén. */
    private List<Road> endPoint;

    /** A startból endbe haladó sávok listája. */
    private List<Lane> forwardLanes;

    /** Az endből startba haladó sávok listája. */
    private List<Lane> backwardLanes;

    /** Az utcához csatlakozó épületek listája. */
    private List<Building> buildings;

    /**
     * Létrehoz egy új utat a megadott névvel és hosszal.
     * @param name   az út neve
     * @param length az út hossza
     */
    public Road(String name, double length) {
        this.name = name;
        this.length = length;
        this.startPoint = new ArrayList<>();
        this.endPoint = new ArrayList<>();
        this.forwardLanes = new ArrayList<>();
        this.backwardLanes = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    /**
     * Hozzáad egy forwardLane sávot az úthoz (start→end irány).
     * Az újonnan hozzáadott sáv szomszédos lesz az összes többi forwardLane-nel
     * (sávváltás lehetővé tétele).
     * @param lane a hozzáadandó sáv
     */
    public void addForwardLane(Lane lane) {
        for (Lane existing : forwardLanes) {
            existing.addNeighbor(lane);
            lane.addNeighbor(existing);
        }
        forwardLanes.add(lane);
    }

    /**
     * Hozzáad egy backwardLane sávot az úthoz (end→start irány).
     * @param lane a hozzáadandó sáv
     */
    public void addBackwardLane(Lane lane) {
        for (Lane existing : backwardLanes) {
            existing.addNeighbor(lane);
            lane.addNeighbor(existing);
        }
        backwardLanes.add(lane);
    }

    /**
     * Hozzáad egy épületet az úthoz.
     * @param b a csatlakozó épület
     */
    public void addBuilding(Building b) {
        buildings.add(b);
    }

    /**
     * Szomszédos utat kapcsol az út start végéhez.
     * @param road a csatlakozó út
     */
    public void connectAtStart(Road road) {
        startPoint.add(road);
    }

    /**
     * Szomszédos utat kapcsol az út end végéhez.
     * @param road a csatlakozó út
     */
    public void connectAtEnd(Road road) {
        endPoint.add(road);
    }

    /**
     * Visszaadja az út elején csatlakozó szomszédos utak listáját.
     * @return szomszédos Road objektumok listája a start végponton
     */
    public List<Road> getStartConnections() {
        Skeleton.call("Map", "Road", "getStartConnections()");
        Skeleton.ret("List<Road>(size=" + startPoint.size() + ")");
        return startPoint;
    }

    /**
     * Visszaadja az út végén csatlakozó szomszédos utak listáját.
     * @return szomszédos Road objektumok listája az end végponton
     */
    public List<Road> getEndConnections() {
        Skeleton.call("Map", "Road", "getEndConnections()");
        Skeleton.ret("List<Road>(size=" + endPoint.size() + ")");
        return endPoint;
    }

    /**
     * Visszaadja a startból endbe haladó sávokat.
     * @return forwardLanes listája
     */
    public List<Lane> getForwardLanes() {
        Skeleton.call("Map", "Road", "getForwardLanes()");
        Skeleton.ret("List<Lane>(size=" + forwardLanes.size() + ")");
        return forwardLanes;
    }

    /**
     * Visszaadja az endből startba haladó sávokat.
     * @return backwardLanes listája
     */
    public List<Lane> getBackwardLanes() {
        Skeleton.call("Map", "Road", "getBackwardLanes()");
        Skeleton.ret("List<Lane>(size=" + backwardLanes.size() + ")");
        return backwardLanes;
    }

    /**
     * Visszaadja az összes sávot (forward + backward) egyetlen listában.
     * Segédmetódus a Map gráfépítéséhez és a snow() iterálásához.
     * @return az összes Lane objektum listája ezen az úton
     */
    public List<Lane> getAllLanes() {
        List<Lane> all = new ArrayList<>();
        all.addAll(forwardLanes);
        all.addAll(backwardLanes);
        return all;
    }

    /**
     * Visszaadja az úthoz csatlakozó épületek listáját.
     * @return épületek listája
     */
    public List<Building> getBuildings() {
        return buildings;
    }

    /**
     * Visszaadja az út nevét.
     * @return az út neve
     */
    public String getName() {
        return name;
    }

    /**
     * Visszaadja az út hosszát.
     * @return az út hossza
     */
    public double getLength() {
        return length;
    }
}
