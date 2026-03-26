package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Az uthalozat egy utjat reprezentalja.
 * Felelossege az utak vegpontjainak es szomszedainak szamontartasa,
 * valamint osszefogja a savokat es epuleteket.
 * A Road nem implementalja a Field interfeszt — kontenerkent
 * fogja ossze a Lane-eket es Building-eket.
 */
public class Road {

    /** A szomszedos utak listaja a kezdoponti oldalon. */
    private List<Road> startPoint = new ArrayList<>();

    /** A szomszedos utak listaja a vegponti oldalon. */
    private List<Road> endPoint = new ArrayList<>();

    /** A startbol endbe halado savok listaja. */
    private List<Lane> forwardLanes = new ArrayList<>();

    /** Az endbol startba halado savok listaja. */
    private List<Lane> backwardLanes = new ArrayList<>();

    /** Az utcahoz csatlakozo epuletek. */
    private List<Building> buildings = new ArrayList<>();

    /** Az ut hossza. */
    private Double length = 0.0;

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
}
