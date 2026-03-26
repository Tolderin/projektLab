package model;

import skeleton.Skeleton;

/**
 * A varoslakok automatizalt jarmuve, amely onalloan navigal lakasa es
 * munkahelye kozott a legrovidebb jarhato utvonalon.
 * Ha vekony horetegen halad, letapossa a havat; jegen csuszhat es balesetet
 * okozhat; mely hoban elakad, de szomszedos szabad savba atsorolhat.
 *
 * SD-03: Auto mozgasa es letaposas.
 * SD-04: Auto csuszasa jegen.
 * SD-15: Auto elakadasa es atsorolas.
 */
public class Car extends Vehicle {

    /** A lakas epulete, ahonnan indul. */
    private Building homeBuilding;

    /** A munkahely epulete, ahova tart. */
    private Building workBuilding;

    /** A terkep referenciaja az utvonal-szamitashoz. */
    private Map gameMap;

    /**
     * A kocsi a kiszamolt utvonal alapjan lep a kovetkezo mezore.
     * SD-03 flow: calculatePath -> remove -> accept -> applyCompaction -> checkFreeze
     * SD-04 flow (folytatas): jeges sav eseten csuszas lehetoseg
     * SD-15 flow: lezart sav eseten atsorolas
     *
     * Az I/N kerdesek vezerlik, melyik ag fut le.
     */
    @Override
    public void move() {
        Skeleton.enter("gameLogic", "car", "move()");

        // Utvonal szamitas
        calculatePath();

        // SD-15: Elakadas ellenorzes
        boolean isNextBlocked = Skeleton.askQuestion(
                "Le van zarva a kovetkezo sav (l2)?");

        if (isNextBlocked) {
            // SD-15: Atsorolas
            changeLane();
        } else {
            // SD-03: Normal mozgas
            Skeleton.enter("car", "l1", "remove(car)");
            Skeleton.exit("void");

            Skeleton.enter("car", "l2", "accept(car)");
            Skeleton.exit("void");

            // Letaposas
            Skeleton.enter("car", "l2", "applyCompaction()");
            Skeleton.exit("void");

            // Fagyas ellenorzes — checkFreeze sajat enter/exit-tel
            // (Lane.checkFreeze()-nek sajat naplozasa van)
            Lane l2 = new Lane();
            l2.checkFreeze();

            // SD-04: Csuszas ellenorzes
            boolean isFrozen = Skeleton.askQuestion(
                    "Jeges a sav (isFrozen == true)?");
            if (isFrozen) {
                boolean slips = Skeleton.askQuestion("Megcsuszik az auto?");
                if (slips) {
                    slip();
                }
            }
        }

        Skeleton.exit("void");
    }

    /**
     * Meghivja a gameMap.findShortestPath()-t es beallitja a nextField-et.
     * SD-03: car -> car.calculatePath() -> map.findShortestPath(l1, target)
     */
    public void calculatePath() {
        Skeleton.enter("car", "car", "calculatePath()");

        Skeleton.enter("car", "map", "findShortestPath(l1, target)");
        Skeleton.exit("[l2, target]");

        Skeleton.enter("car", "car", "nextField = l2");
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    /**
     * Utkozes kezelese, savlezaras meghatarozot korre.
     *
     * @param v A masik jarmu amivel az utkozes tortent.
     */
    public void collideWith(Vehicle v) {
        Skeleton.enter("car", "other", "collideWith(car)");
        Skeleton.exit("void");
    }

    /**
     * Jegpancelon megcsuszas kezelese.
     * SD-04: I/N kerdes — van masik jarmu a savban?
     * Ha igen: collideWith() hivas, ha nem: savlezaras.
     */
    public void slip() {
        Skeleton.enter("car", "car", "slip()");

        boolean hasOtherVehicle = Skeleton.askQuestion(
                "Van masik jarmu a savban, amivel utkozhet?");

        if (hasOtherVehicle) {
            // Utkozes a masik jarmuvel
            Skeleton.enter("car", "other", "collideWith(car)");
            Skeleton.exit("void");

            Skeleton.enter("car", "l2", "isBlocked()");
            Skeleton.exit("true");
        } else {
            Skeleton.enter("car", "l2", "setBlocked(true)");
            Skeleton.exit("void");
        }

        Skeleton.exit("void");
    }

    /**
     * Atsorolas a szomszedos szabad savba elakadas eseten.
     * SD-15 alapjan: getNeighbors -> isBlocked(neighbor) -> remove -> accept
     * -> applyCompaction -> checkFreeze.
     */
    public void changeLane() {
        Skeleton.enter("car", "car", "changeLane()");

        // Szomszedok lekerdezese
        Skeleton.enter("car", "l1", "getNeighbors()");
        Skeleton.exit("[l2, l3]");

        // Szomszedos sav ellenorzese
        boolean neighborFree = Skeleton.askQuestion(
                "Szabad a szomszedos sav?");

        if (neighborFree) {
            // Atsorolas a szabad savba
            Skeleton.enter("car", "l1", "remove(car)");
            Skeleton.exit("void");

            Skeleton.enter("car", "neighbor", "accept(car)");
            Skeleton.exit("void");

            Skeleton.enter("car", "neighbor", "applyCompaction()");
            Skeleton.exit("void");

            // checkFreeze sajat enter/exit-tel
            Lane neighbor = new Lane();
            neighbor.checkFreeze();
        }

        Skeleton.exit("void");
    }
}
