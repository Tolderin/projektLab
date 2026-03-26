package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * Összefogja és kezeli az úthálózat összes csomópontját (Field-eket)[cite:
 * 1417].
 * Gráfként kezeli a pályát, és biztosít útvonal-kereső funkcionalitást[cite:
 * 1417].
 * A havazást is ő koordinálja a snow() metóduson keresztül[cite: 1418].
 */
public class Map {
    private List<Field> fields = new ArrayList<>();

    public Map() {
        // Alapértelmezett inicializálás a szkeletonhoz
    }

    /**
     * Visszaadja a legrövidebb járható útvonalat[cite: 1424].
     */
    public List<Field> findShortestPath(Field from, Field to) {
        Skeleton.enter("Hívó", "map", "findShortestPath(from, to)");
        Skeleton.exit("[L2, TARGET]"); // Szkeleton szerinti visszatérés
        return new ArrayList<>();
    }

    /**
     * Minden Lane típusú mezőn növeli a hóréteg vastagságát[cite: 1425].
     */
    public void snow() {
        Skeleton.enter("gameLogic", "map", "snow()");

        // Szimuláljuk, hogy végigmegyünk a mezőkön
        boolean hasLanes = Skeleton.askQuestion("Vannak Lane típusú mezők a térképen?");
        if (hasLanes) {
            // Létrehozunk egy dummy lane-t a teszt hívás kedvéért
            Lane dummyLane1 = new Lane();
            dummyLane1.addSnow(1.0);

            Lane dummyLane2 = new Lane();
            dummyLane2.addSnow(1.0);
        }

        Skeleton.exit("void");
    }
}