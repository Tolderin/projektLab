package gonoszonosz;

/**
 * A buszjáratok végpontja, egyben Field a gráfban (Building leszármazott).
 * Felelőssége a fordulók regisztrálása: amikor egy busz megérkezik,
 * jelzi a buszvezetőnek a forduló teljesítését.
 */
public class Terminal extends Building {

    /**
     * Létrehoz egy új végállomást.
     */
    public Terminal() {
        super();
    }

    /**
     * Fogadja az érkező buszt a végállomásra és regisztrálja az érkezést.
     * @param v az érkező jármű (jellemzően busz)
     */
    @Override
    public void accept(Vehicle v) {
        Skeleton.call("Bus", "Terminal", "accept(" + v.getClass().getSimpleName() + ")");
        if (v instanceof Bus) {
            registerArrival((Bus) v);
        }
        Skeleton.ret("void");
    }

    /**
     * Eltávolítja a távozó buszt a végállomásról.
     * @param v a távozó jármű
     */
    @Override
    public void remove(Vehicle v) {
        Skeleton.call("Bus", "Terminal", "remove(" + v.getClass().getSimpleName() + ")");
        Skeleton.ret("void");
    }

    /**
     * Regisztrálja a busz érkezését és jelzi a fordulót a buszvezetőnek.
     * Növeli a buszvezető completedRounds számlálóját.
     * @param bus az érkező busz
     */
    public void registerArrival(Bus bus) {
        Skeleton.call("Terminal", "Bus", "registerArrival(bus)");
        BusDriver driver = bus.getDriver();
        if (driver != null) {
            driver.incrementRounds();
        }
        Skeleton.ret("void");
    }
}
