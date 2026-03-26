package model;

import skeleton.Skeleton;

/**
 * A buszvezeto jatekos altal iranyitott jarmu, amely ket vegallomas
 * kozott kozlekedik. Forduokat teljesit; utkozes vagy csuszas eseten
 * meghatarozot ideig mozgaskeptelenne valik es elzarja a savot.
 *
 * SD-05: Bus.move() — a buszt a celmezore mozgatja,
 * ellenorzi hogy Terminal-ra erkezett-e, ha igen: registerArrival().
 */
public class Bus extends Vehicle {

    /** Hamis erteke eseten a busz utkozes miatt mozgaskeptelen. */
    private boolean isFunctioning = true;

    /** Hany korig marad meg mozgaskeptelen. */
    private int disabledTurnsLeft = 0;

    /** A teljesitett fordulok szama. */
    private int completedRounds = 0;

    /**
     * Mozgatja a buszt a tervezett kovetkezo mezo fele.
     * SD-05 alapjan: remove(bus) az elozo savrol, accept(bus) a celmezon,
     * majd I/N kerdes hogy a celmezo vegallomas-e.
     * Ha igen: terminal.registerArrival(bus) es completedRounds++.
     */
    @Override
    public void move() {
        Skeleton.enter("buszvezeto", "bus", "move()");

        // Elozo savrol eltavolitas
        Skeleton.enter("bus", "l1", "remove(bus)");
        Skeleton.exit("void");

        // Celmezon elfogadas
        Skeleton.enter("bus", "target", "accept(bus)");
        Skeleton.exit("void");

        // SD-05: Vegallomas ellenorzes
        boolean isTerminal = Skeleton.askQuestion(
                "A celmezo vegallomas (Terminal)?");
        if (isTerminal) {
            // Terminal hivja a registerArrival-t (sajat enter/exit-tel)
            Terminal t = new Terminal();
            t.registerArrival(this);
            completedRounds++;
        }

        Skeleton.exit("void");
    }

    /**
     * Utkozest kezel egy masik jarmuvel, beallitja a mozgaskeptelen allapotot.
     *
     * @param v A masik jarmu amivel az utkozes tortent.
     */
    public void collideWith(Vehicle v) {
        Skeleton.enter("car", "bus", "collideWith(v)");
        this.isFunctioning = false;
        Skeleton.exit("void");
    }

    /**
     * Jegpancelon torteno megcsuszast kezel.
     */
    public void slip() {
        Skeleton.enter("bus", "bus", "slip()");
        this.isFunctioning = false;
        Skeleton.exit("void");
    }

    /**
     * Visszaadja a busz nevet.
     *
     * @return A busz azonositoja.
     */
    public String getName() {
        Skeleton.enter("gameLogic", "bus", "getName()");
        Skeleton.exit("BusName");
        return "BusName";
    }

    /**
     * Visszaadja a teljesitett fordulok szamat.
     * SD-16-ban hasznalt a jatek vegi eredmeny osszeallitasahoz.
     *
     * @return A teljesitett fordulok szama.
     */
    public int getCompletedRounds() {
        Skeleton.enter("gameLogic", "bus", "getCompletedRounds()");
        Skeleton.exit(String.valueOf(completedRounds));
        return completedRounds;
    }
}
