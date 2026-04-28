package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Az uthalozat egy utjat reprezentalja. Felelossege az utak
 * vegpontjainak es szomszedainak szamontartasa, valamint osszefogja
 * a savokat es az utcahoz csatlakozo epuleteket.
 *
 * A Road NEM implementalja a Field interfeszt -- konteneri szerepe
 * van: a forwardLanes/backwardLanes listai a tenyleges Field
 * csomopontok.
 */
public class Road {

    /** A szomszedos utak listaja a kezdoponti oldalon. */
    public final List<Road> startPoint = new ArrayList<>();

    /** A szomszedos utak listaja a vegponti oldalon. */
    public final List<Road> endPoint = new ArrayList<>();

    /** A startbol endbe halado savok listaja. */
    public final List<Lane> forwardLanes = new ArrayList<>();

    /** Az endbol startba halado savok listaja. */
    public final List<Lane> backwardLanes = new ArrayList<>();

    /** Az utcahoz csatlakozo epuletek. */
    public final List<Building> buildings = new ArrayList<>();

    /** Az ut hossza. */
    public double length = 0.0;

    /**
     * Visszaadja az ut elejen a szomszedos utak listajat.
     *
     * @return A kezdoponti szomszedos utak.
     */
    public List<Road> getStartConnections() {
        return startPoint;
    }

    /**
     * Visszaadja az ut vegen a szomszedos utak listajat.
     *
     * @return A vegponti szomszedos utak.
     */
    public List<Road> getEndConnections() {
        return endPoint;
    }

    /**
     * Visszaadja a startbol endbe halado savok listajat.
     *
     * @return Az elore halado savok.
     */
    public List<Lane> getForwardLanes() {
        return forwardLanes;
    }

    /**
     * Visszaadja az endbol startba halado savok listajat.
     *
     * @return A hatra halado savok.
     */
    public List<Lane> getBackwardLanes() {
        return backwardLanes;
    }

    /**
     * Hozzaad egy savot a megfelelo iranyu savlistahoz.
     *
     * @param lane      A hozzaadando sav.
     * @param direction "forward" vagy "backward".
     */
    public void addLane(Lane lane, String direction) {
        if ("forward".equals(direction)) {
            forwardLanes.add(lane);
        } else {
            backwardLanes.add(lane);
        }
    }

    /**
     * Hozzaad egy epuletet az utcahoz.
     *
     * @param b A hozzaadando epulet.
     */
    public void addBuilding(Building b) {
        buildings.add(b);
    }

    /**
     * Beallitja az ut hosszat.
     *
     * @param length Az uj hossz.
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Ket ut logikai osszekotese a kezd- vagy vegponton.
     *
     * @param other    A masik ut.
     * @param myPos    "start" vagy "end" -- ennek az utnak a vege.
     * @param otherPos "start" vagy "end" -- a masik ut vege.
     */
    public void connectTo(Road other, String myPos, String otherPos) {
        if ("start".equals(myPos)) {
            startPoint.add(other);
        } else {
            endPoint.add(other);
        }
    }
}
