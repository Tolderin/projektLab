package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * Az úthálózat egyetlen forgalmi sávját reprezentálja, egyben Field a gráfban.
 * Felelőssége nyilvántartani a hóréteg vastagságát, a jégpáncél állapotát
 * és a rajta tartózkodó járműveket. Kezeli a letaposás miatti jégképződési
 * mechanizmust is.
 */
public class Lane implements Field {

    /** A sávon lévő hóréteg vastagsága. */
    private double snowDepth;

    /** Igaz, ha a sávon jégpáncél van. */
    private boolean isFrozen;

    /** A jégpáncél vastagsága. */
    private double iceThickness;

    /**
     * Hányszor hajtottak át járművek; egy küszöb felett jégpáncél képződik.
     */
    private int compactionCount;

    /** A sávon tartózkodó járművek listája. */
    private List<Vehicle> vehicles;

    /** A sóhatás fennmaradásának ideje körökben. */
    private double saltEffect;

    /** A szomszédos mezők listája (Field interfészből). */
    private List<Field> neighbors;

    /** Igaz, ha a sáv ütközés miatt le van zárva. */
    private boolean blocked;

    /** Hány körig marad még lezárva a sáv. */
    private int blockedTurnsLeft;

    /** Az a letaposásszám-küszöb, ami felett jégpáncél képződik. */
    private static final int FREEZE_THRESHOLD = 5;

    /**
     * Létrehoz egy új, üres (tiszta) sávot.
     */
    public Lane() {
        this.snowDepth = 0.0;
        this.isFrozen = false;
        this.iceThickness = 0.0;
        this.compactionCount = 0;
        this.vehicles = new ArrayList<>();
        this.saltEffect = 0.0;
        this.neighbors = new ArrayList<>();
        this.blocked = false;
        this.blockedTurnsLeft = 0;
    }

    /**
     * Fogadja az érkező járművet erre a sávra.
     * @param v az érkező jármű
     */
    @Override
    public void accept(Vehicle v) {
        Skeleton.call("Lane", "Lane", "accept(" + v.getClass().getSimpleName() + ")");
        vehicles.add(v);
        v.setCurrentLane(this);
        Skeleton.ret("void");
    }

    /**
     * Eltávolítja a távozó járművet erről a sávról.
     * @param v a távozó jármű
     */
    @Override
    public void remove(Vehicle v) {
        Skeleton.call("Lane", "Lane", "remove(" + v.getClass().getSimpleName() + ")");
        vehicles.remove(v);
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a szomszédos mezők listáját.
     * @return szomszédos Field objektumok listája
     */
    @Override
    public List<Field> getNeighbors() {
        Skeleton.call("Lane", "Lane", "getNeighbors()");
        Skeleton.ret("List<Field>(size=" + neighbors.size() + ")");
        return neighbors;
    }

    /**
     * Növeli a hóréteg vastagságát a megadott mennyiséggel.
     * @param amount a hozzáadandó hómennyiség
     */
    public void addSnow(double amount) {
        Skeleton.call("Map", "Lane", "addSnow(" + amount + ")");
        snowDepth += amount;
        Skeleton.ret("void");
    }

    /**
     * Csökkenti a hóréteg vastagságát a megadott mennyiséggel.
     * @param amount az eltávolítandó hómennyiség
     */
    public void removeSnow(double amount) {
        Skeleton.call("CleanerHead", "Lane", "removeSnow(" + amount + ")");
        snowDepth = Math.max(0.0, snowDepth - amount);
        Skeleton.ret("void");
    }

    /**
     * Feltöri a jégpáncélt és hóvá alakítja azt.
     * Az isFrozen értéke hamisra áll, a jégvastagság hóvá konvertálódik.
     */
    public void breakIce() {
        Skeleton.call("IcebreakerHead", "Lane", "breakIce()");
        isFrozen = false;
        snowDepth += iceThickness * 1.5;
        iceThickness = 0.0;
        Skeleton.ret("void");
    }

    /**
     * Eltávolítja a jégpáncélt a sávról (pl. sárkány fej hatására).
     */
    public void removeIce() {
        Skeleton.call("DragonHead", "Lane", "removeIce()");
        isFrozen = false;
        iceThickness = 0.0;
        Skeleton.ret("void");
    }

    /**
     * Ellenőrzi, hogy kell-e jégpáncél képződjön a letaposások alapján.
     * A Skeleton megkérdezi a felhasználót, hogy elérte-e a küszöböt.
     */
    public void checkFreeze() {
        Skeleton.call("Car", "Lane", "checkFreeze()");
        boolean shouldFreeze = Skeleton.ask("Elérte a letaposások száma a küszöböt, és van hó a sávon?");
        if (shouldFreeze) {
            isFrozen = true;
            iceThickness = snowDepth;
            snowDepth = 0.0;
            System.out.println(Skeleton.class.getSimpleName() + ": A sáv jegessé vált (isFrozen = true).");
        }
        Skeleton.ret("void");
    }

    /**
     * Növeli a letaposás-számlálót, amikor egy jármű áthalad a sávon.
     */
    public void applyCompaction() {
        Skeleton.call("Car", "Lane", "applyCompaction()");
        compactionCount++;
        Skeleton.ret("void");
    }

    /**
     * Beállítja a sóhatás időtartamát a sávon.
     * @param duration a sóhatás fennmaradásának ideje körökben
     */
    public void applySaltEffect(double duration) {
        Skeleton.call("SaltHead", "Lane", "applySaltEffect(" + duration + ")");
        saltEffect = duration;
        Skeleton.ret("void");
    }

    /**
     * Meghatározza, hogy a sáv járhatatlan-e (lezárt ütközés miatt, vagy túl mély hó).
     * A Skeleton megkérdezi a felhasználót.
     * @return true, ha a sáv járhatatlan
     */
    public boolean isBlocked() {
        Skeleton.call("Car", "Lane", "isBlocked()");
        boolean result = Skeleton.ask("Le van zárva ez a sáv?");
        Skeleton.ret(String.valueOf(result));
        return result;
    }

    /**
     * Beállítja a sáv lezárt állapotát ütközés következtében.
     * @param turns hány körig maradjon lezárva a sáv
     */
    public void setBlocked(int turns) {
        this.blocked = true;
        this.blockedTurnsLeft = turns;
    }

    /**
     * Hozzáad egy szomszédos mezőt a sáv szomszédsági listájához.
     * @param f a hozzáadandó szomszéd mező
     */
    public void addNeighbor(Field f) {
        neighbors.add(f);
    }

    /**
     * Visszaadja a sávon lévő hóréteg vastagságát.
     * @return hóréteg vastagsága
     */
    public double getSnowDepth() {
        return snowDepth;
    }

    /**
     * Visszaadja, hogy a sáv jeges-e.
     * @return true, ha jégpáncél van a sávon
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * Visszaadja a sávon tartózkodó járművek listáját.
     * @return járművek listája
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }
}
