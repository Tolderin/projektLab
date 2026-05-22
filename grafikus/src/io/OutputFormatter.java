package io;

import java.util.List;
import java.util.Map;

/**
 * Kozpontositott segedosztaly a konzolos kimenetek formazasara.
 * Biztositja, hogy a prototipus kimenete megfeleljen a specifikacio
 * altal elvart [SUCCESS], [ERROR], [EVENT], [LIST] es [STATE] prefixumoknak.
 *
 * Statikus osztaly -- nincs allapota, nem kell peldanyositani.
 */
public class OutputFormatter {

    private OutputFormatter() {
        // statikus osztaly, nem peldanyosithato
    }

    /**
     * Sikeres akcio kiirasa.
     *
     * @param message Az uzenet az ID-val es az akcio leirassal.
     */
    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    /**
     * Hibauzenet kiirasa.
     *
     * @param message A hiba okat leiro szoveg.
     */
    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    /**
     * Rendszeresemeny kiirasa (jellemzoen next_turn altal valtott).
     *
     * @param message Az esemeny leirasa.
     */
    public static void printEvent(String message) {
        System.out.println("[EVENT] " + message);
    }

    /**
     * Belso allapot dump (stat parancs kimenete).
     * Az attribute Map sorrendje megorzodik a kiirasban,
     * ezert a hivonak LinkedHashMap-et celszeru atadnia.
     *
     * @param id        A lekerdezett objektum ID-ja.
     * @param className A megjelenitett osztalynev (pl. "Lane").
     * @param attrs     Attribut nev -> ertek parok (sorrend megorzodik).
     */
    public static void printState(String id, String className, Map<String, String> attrs) {
        System.out.println("[STATE] " + id + " (" + className + ")");
        for (Map.Entry<String, String> e : attrs.entrySet()) {
            System.out.println(" " + e.getKey() + ": " + e.getValue());
        }
    }

    /**
     * Objektumok felsorolasa (list parancs kimenete).
     * Ures lista eseten csak a fejlec keszul el, vesszo nelkul.
     *
     * @param type A felsorolt osztaly neve (pl. "lanes").
     * @param ids  A talalat ID-k listaja.
     */
    public static void printList(String type, List<String> ids) {
        StringBuilder sb = new StringBuilder("[LIST] ");
        sb.append(type).append(": ");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(ids.get(i));
        }
        System.out.println(sb.toString());
    }
}
