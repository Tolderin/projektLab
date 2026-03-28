package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * Az autók által felkeresett általános épület (lakás vagy munkahely).
 * A Building absztrakt osztályból származik, tehát maga is Field a gráfban.
 * Felelőssége parkolóhelyet biztosítani az autóknak és navigációs célpontként szolgálni.
 */
public class GeneralBuilding extends Building {

    /** Az épületben parkoló autók listája. */
    private List<Vehicle> parkedVehicles;

    /**
     * Létrehoz egy új általános épületet.
     */
    public GeneralBuilding() {
        super();
        this.parkedVehicles = new ArrayList<>();
    }

    /**
     * Fogadja az érkező autót, parkolóba helyezi.
     * @param v az érkező jármű (jellemzően autó)
     */
    @Override
    public void accept(Vehicle v) {
        Skeleton.call("Car", "GeneralBuilding", "accept(" + v.getClass().getSimpleName() + ")");
        parkedVehicles.add(v);
        Skeleton.ret("void");
    }

    /**
     * Eltávolítja a távozó autót az épületből.
     * @param v a távozó jármű
     */
    @Override
    public void remove(Vehicle v) {
        Skeleton.call("Car", "GeneralBuilding", "remove(" + v.getClass().getSimpleName() + ")");
        parkedVehicles.remove(v);
        Skeleton.ret("void");
    }
}
