package cli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A rendszerben levo objektumok (mezok, jarmuvek, jatekosok)
 * szoveges azonosito (ID) alapu nyilvantartasa. A CLI parancsok
 * stringeket hasznalnak, a belso jatekvilag referenciakat -- ez
 * az osztaly teremti meg a kapcsolatot kettejuk kozott.
 *
 * Beilleszteskor az objektum beillesztesi sorrendjet megorzi
 * (LinkedHashMap), igy a list parancs es a save kimenete
 * determinisztikus.
 */
public class ObjectManager {

    /** ID -> objektum tarolo, beillesztesi sorrendet megorzi. */
    private final Map<String, Object> objects = new LinkedHashMap<>();

    /**
     * Egy uj objektum regisztralasa. Ha az ID mar foglalt,
     * false-szal ter vissza es nem felulirja.
     *
     * @param id  Az uj objektum egyedi azonositoja.
     * @param obj A regisztralando objektum.
     * @return true ha sikeres, false ha az ID mar foglalt.
     */
    public boolean registerObject(String id, Object obj) {
        if (objects.containsKey(id)) {
            return false;
        }
        objects.put(id, obj);
        return true;
    }

    /**
     * Visszaad egy objektumot az azonositoja alapjan.
     * Ha nincs ilyen ID, null-t ad vissza -- a hivo dolga eldonteni
     * mit ir ki hibanak (a kontextus ott ismert).
     *
     * @param id A keresett objektum ID-ja.
     * @return Az objektum, vagy null ha nincs ilyen ID.
     */
    public Object getObject(String id) {
        return objects.get(id);
    }

    /**
     * Visszaadja egy mar regisztralt objektum ID-jat (reverse lookup).
     * A domain rétegnek (pl. Vehicle.slip()) szüksége van ra ahhoz
     * hogy event-uzenetbe beleirja sajat magat es a sajat mezojet.
     *
     * @param obj A keresett objektum referencia.
     * @return Az objektum ID-ja, vagy null ha nincs regisztralva.
     */
    public String getId(Object obj) {
        for (Map.Entry<String, Object> e : objects.entrySet()) {
            if (e.getValue() == obj) {
                return e.getKey();
            }
        }
        return null;
    }

    /**
     * Igaz, ha az adott ID mar regisztralva van.
     *
     * @param id A vizsgalt ID.
     * @return true ha letezik az ID-hez tartozo objektum.
     */
    public boolean hasObject(String id) {
        return objects.containsKey(id);
    }

    /**
     * Visszaadja az adott osztaly (vagy leszarmazott) tipusu
     * objektumok ID-jainak listajat, regisztracios sorrendben.
     * A list parancs hasznalja.
     *
     * @param type Az osszuralt osztaly.
     * @return A megfelelo tipusu objektumok ID-jainak listaja.
     */
    public List<String> getIdsByType(Class<?> type) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Object> e : objects.entrySet()) {
            if (type.isInstance(e.getValue())) {
                result.add(e.getKey());
            }
        }
        return result;
    }

    /**
     * Visszaadja az osszes regisztralt (id, obj) parost,
     * regisztracios sorrendben. A save parancs hasznalja.
     *
     * @return Az osszes regisztralt objektum.
     */
    public Map<String, Object> getAll() {
        return objects;
    }

    /**
     * Torli a teljes memoriakepet. A load parancs hasznalja
     * uj palya betoltese elott.
     */
    public void clearAll() {
        objects.clear();
    }
}
