package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import io.OutputFormatter;

/**
 * Osszefogja es kezeli az uthalozat osszes csomopontjat (Field-eket).
 * Grafkent kezeli a palyat, biztosit utvonal-kereso funkciot
 * (BFS), es a havazas koordinaciojat.
 */
public class Map {

    /** A terkep osszes mezoje (Lane-ek + Building leszarmazottak). */
    public final List<Field> fields = new ArrayList<>();

    /** Konstans: koronkent hullo ho mennyisege. */
    private static final double SNOW_AMOUNT_PER_TURN = 1.0;

    /**
     * Hozzaad egy mezot a terkephez.
     *
     * @param f A hozzaadando mezo.
     */
    public void addField(Field f) {
        fields.add(f);
    }

    /**
     * Visszaadja a legrovidebb jarhato utvonalat ket mezo kozott
     * BFS-sel. A blokkolt Lane-eket atugorja. Az eredmeny a from
     * UTANI elso lepest tartalmazza elso elemkent (a from-ot nem),
     * es a to-t az utolsokent.
     *
     * @param from A kiindulasi mezo.
     * @param to   A celmezo.
     * @return Az utvonal mezoinek listaja, vagy ures lista ha nincs ut.
     */
    public List<Field> findShortestPath(Field from, Field to) {
        if (from == null || to == null || from == to) {
            return new ArrayList<>();
        }
        java.util.Map<Field, Field> parent = new HashMap<>();
        Set<Field> visited = new HashSet<>();
        Queue<Field> queue = new LinkedList<>();
        queue.add(from);
        visited.add(from);

        boolean found = false;
        while (!queue.isEmpty()) {
            Field cur = queue.poll();
            if (cur == to) {
                found = true;
                break;
            }
            for (Field n : cur.getNeighbors()) {
                if (visited.contains(n)) {
                    continue;
                }
                // Blokkolt Lane-t kihagyunk (kiveve ha a celmezo)
                if (n instanceof Lane && ((Lane) n).isBlocked() && n != to) {
                    continue;
                }
                visited.add(n);
                parent.put(n, cur);
                queue.add(n);
            }
        }
        List<Field> path = new ArrayList<>();
        if (!found) {
            return path;
        }
        // Visszafele rekonstrukcio: to -> ... -> first hop after from
        Field cur = to;
        while (cur != null && cur != from) {
            path.add(0, cur);
            cur = parent.get(cur);
        }
        return path;
    }

    /**
     * Minden Lane tipusu mezon noveli a horeteg vastagsagat,
     * majd kiir egy [EVENT] uzenetet a kornyezeti valtozasrol.
     */
    public void snow() {
        for (Field f : fields) {
            if (f instanceof Lane) {
                ((Lane) f).addSnow(SNOW_AMOUNT_PER_TURN);
            }
        }
        OutputFormatter.printEvent("Map snowed. Global depth increased.");
    }
}
