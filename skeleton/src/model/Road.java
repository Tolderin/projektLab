package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * Az úthálózat egy útját reprezentálja[cite: 1438, 1439].
 * Felelőssége az utak végpontjainak és szomszédainak számontartása,
 * valamint összefogja a sávokat, és épületeket[cite: 1440].
 */
public class Road {
    private List<Road> startPoint = new ArrayList<>();
    private List<Road> endPoint = new ArrayList<>();
    private List<Lane> forwardLanes = new ArrayList<>();
    private List<Lane> backwardLanes = new ArrayList<>();
    private List<Building> buildings = new ArrayList<>();
    private Double length = 0.0;

    /**
     * Visszaadja az út elején a szomszédos utak listáját[cite: 1456, 1457].
     */
    public List<Road> getStartConnections() {
        Skeleton.enter("Hívó", "road", "getStartConnections()");
        Skeleton.exit("List<Road>");
        return startPoint;
    }

    /**
     * Visszaadja az út végén a szomszédos utak listáját[cite: 1457].
     */
    public List<Road> getEndConnections() {
        Skeleton.enter("Hívó", "road", "getEndConnections()");
        Skeleton.exit("List<Road>");
        return endPoint;
    }

    /**
     * Visszaadja a startból endbe haladó sávok listáját[cite: 1458].
     */
    public List<Lane> getForwardLanes() {
        Skeleton.enter("Hívó", "road", "getForwardLanes()");
        Skeleton.exit("List<Lane>");
        return forwardLanes;
    }

    /**
     * Visszaadja az endből startba haladó sávok listáját[cite: 1459].
     */
    public List<Lane> getBackwardLanes() {
        Skeleton.enter("Hívó", "road", "getBackwardLanes()");
        Skeleton.exit("List<Lane>");
        return backwardLanes;
    }
}