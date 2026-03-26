package main;

import java.util.Scanner;
import model.*;
import skeleton.Skeleton;

/**
 * A szkeleton alkalmazas belepesi pontja.
 * Felelossege a fomenu megjelenitese es a tesztesetek (Use-case-ek) inditasa.
 * Minden teszteset a week4 szekvenciadiagramok (SD-01 – SD-16) es
 * a week5 kommunikacios diagramok (CD-01 – CD-16) alapjan van felepitve.
 */
public class Main {

    /**
     * A program belepesi pontja. Megjeleniti a fomenut
     * es a felhasznalo altal valasztott tesztesetet futtatja.
     *
     * @param args Parancssori argumentumok (nem hasznalt).
     */
    public static void main(String[] args) {
        Scanner scanner = Skeleton.getScanner();
        boolean running = true;

        while (running) {
            System.out.println("\n=== GonoszOnosz Szkeleton Fomenu ===");
            System.out.println(" 1. SD-01: Jatek inicializalasa");
            System.out.println(" 2. SD-02: Havazas szimulacio (koronkent)");
            System.out.println(" 3. SD-03: Auto mozgasa es letaposas");
            System.out.println(" 4. SD-04: Auto csuszasa jegen");
            System.out.println(" 5. SD-05: Buszvezeto kor - navigacio es fordulo");
            System.out.println(" 6. SD-06: Hokotro mozgasa es takaritas");
            System.out.println(" 7. SD-07: Sopro fej takaritasa");
            System.out.println(" 8. SD-08: Hanyo fej takaritasa");
            System.out.println(" 9. SD-09: Soszoro fej takaritasa");
            System.out.println("10. SD-10: Sarkany fej takaritasa");
            System.out.println("11. SD-11: Jegtoro fej takaritasa");
            System.out.println("12. SD-12: Fejcsere a telephelyen");
            System.out.println("13. SD-13: Uzemanyag vasarlas es toltes");
            System.out.println("14. SD-14: Hokotro vasarlasa");
            System.out.println("15. SD-15: Auto elakadasa es atsorolas");
            System.out.println("16. SD-16: Jatek vege es eredmenyhirdetes");
            System.out.println(" 0. Kilepes");
            System.out.print("Valassz egy tesztesetet (0-16): ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":  runTestCase1();  break;
                case "2":  runTestCase2();  break;
                case "3":  runTestCase3();  break;
                case "4":  runTestCase4();  break;
                case "5":  runTestCase5();  break;
                case "6":  runTestCase6();  break;
                case "7":  runTestCase7();  break;
                case "8":  runTestCase8();  break;
                case "9":  runTestCase9();  break;
                case "10": runTestCase10(); break;
                case "11": runTestCase11(); break;
                case "12": runTestCase12(); break;
                case "13": runTestCase13(); break;
                case "14": runTestCase14(); break;
                case "15": runTestCase15(); break;
                case "16": runTestCase16(); break;
                case "0":
                    running = false;
                    System.out.println("Kilepes a programbol...");
                    break;
                default:
                    System.out.println("Ervenytelen valasztas! Kerlek 0 es 16 kozott valassz.");
            }
        }
    }

    /**
     * SD-01: Jatek inicializalasa.
     * CD-01 alapjan: GameLogic.startGame() letrehozza a teljes jatekvilagot.
     */
    private static void runTestCase1() {
        System.out.println("\n--- [Fut] SD-01: Jatek inicializalasa ---");
        GameLogic gl = new GameLogic();
        gl.startGame();
    }

    /**
     * SD-02: Havazas szimulacio (koronkent).
     * CD-02 alapjan: GameLogic.advanceTurn() -> Map.snow() -> Lane.addSnow()
     */
    private static void runTestCase2() {
        System.out.println("\n--- [Fut] SD-02: Havazas szimulacio ---");
        GameLogic gl = new GameLogic();
        Map m = new Map();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        m.addField(l1);
        m.addField(l2);
        gl.setMap(m);
        gl.advanceTurn();
    }

    /**
     * SD-03: Auto mozgasa es letaposas.
     * CD-03 alapjan: Car.move() -> calculatePath -> remove -> accept
     * -> applyCompaction -> checkFreeze
     */
    private static void runTestCase3() {
        System.out.println("\n--- [Fut] SD-03: Auto mozgasa es letaposas ---");
        Car car = new Car();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        Map m = new Map();
        GeneralBuilding target = new GeneralBuilding();
        car.move();
    }

    /**
     * SD-04: Auto csuszasa jegen.
     * CD-04 alapjan: Car.move() jeges savon -> slip() -> collideWith()
     * Az I/N kerdesek vezerlik a csuszas agat.
     */
    private static void runTestCase4() {
        System.out.println("\n--- [Fut] SD-04: Auto csuszasa jegen ---");
        Car car = new Car();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        Car otherVehicle = new Car();
        car.move();
    }

    /**
     * SD-05: Buszvezeto kor — navigacio es fordulo.
     * CD-05 alapjan: Buszvezeto aktor -> Bus.move() -> Terminal ellenorzes
     */
    private static void runTestCase5() {
        System.out.println("\n--- [Fut] SD-05: Buszvezeto kor ---");
        GameLogic gl = new GameLogic();
        Bus bus = new Bus();
        Lane l1 = new Lane();
        Terminal t = new Terminal();
        bus.move();
    }

    /**
     * SD-06: Hokotro mozgasa es takaritas.
     * CD-06 alapjan: Takarito aktor -> SnowPlow.move()
     * -> remove -> accept -> cleanCurrentLane -> head.clean()
     */
    private static void runTestCase6() {
        System.out.println("\n--- [Fut] SD-06: Hokotro mozgasa es takaritas ---");
        GameLogic gl = new GameLogic();
        SnowPlow sp = new SnowPlow();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        SweepHead head = new SweepHead();
        sp.changeHead(head);
        System.out.println("--- Hokotro mozgasa: ---");
        sp.move();
    }

    /**
     * SD-07: Sopro fej takaritasa.
     * CD-07 alapjan: SnowPlow.cleanCurrentLane() -> SweepHead.clean(l)
     * -> removeSnow -> getNeighbors -> addSnow(neighbor)
     */
    private static void runTestCase7() {
        System.out.println("\n--- [Fut] SD-07: Sopro fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        SweepHead head = new SweepHead();
        Lane l = new Lane();
        Lane neighbor = new Lane();
        // Szomszedsagi kapcsolat beallitasa
        l.getNeighbors().add(neighbor);
        sp.changeHead(head);
        sp.setCurrentLane(l);
        System.out.println("--- Sopro fej mukodesben: ---");
        sp.cleanCurrentLane();
    }

    /**
     * SD-08: Hanyo fej takaritasa.
     * CD-08 alapjan: SnowPlow.cleanCurrentLane() -> ThrowerHead.clean(l)
     * -> removeSnow (szomszed savot nem modosit)
     */
    private static void runTestCase8() {
        System.out.println("\n--- [Fut] SD-08: Hanyo fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        ThrowerHead head = new ThrowerHead();
        Lane l = new Lane();
        sp.changeHead(head);
        sp.setCurrentLane(l);
        System.out.println("--- Hanyo fej mukodesben: ---");
        sp.cleanCurrentLane();
    }

    /**
     * SD-09: Soszoro fej takaritasa.
     * CD-09 alapjan: SnowPlow.cleanCurrentLane() -> SaltHead.clean(l)
     * -> I/N: "Van so?" -> applySaltEffect
     */
    private static void runTestCase9() {
        System.out.println("\n--- [Fut] SD-09: Soszoro fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        SaltHead head = new SaltHead();
        Lane l = new Lane();
        sp.changeHead(head);
        sp.setCurrentLane(l);
        System.out.println("--- Soszoro fej mukodesben: ---");
        sp.cleanCurrentLane();
    }

    /**
     * SD-10: Sarkany fej takaritasa.
     * CD-10 alapjan: SnowPlow.cleanCurrentLane() -> DragonHead.clean(l)
     * -> I/N: "Van biokerozin?" -> removeSnow + removeIce
     */
    private static void runTestCase10() {
        System.out.println("\n--- [Fut] SD-10: Sarkany fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        DragonHead head = new DragonHead();
        Lane l = new Lane();
        sp.changeHead(head);
        sp.setCurrentLane(l);
        System.out.println("--- Sarkany fej mukodesben: ---");
        sp.cleanCurrentLane();
    }

    /**
     * SD-11: Jegtoro fej takaritasa.
     * CD-11 alapjan: SnowPlow.cleanCurrentLane() -> IcebreakerHead.clean(l)
     * -> I/N: "Jeges a sav?" -> breakIce + addSnow
     */
    private static void runTestCase11() {
        System.out.println("\n--- [Fut] SD-11: Jegtoro fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        IcebreakerHead head = new IcebreakerHead();
        Lane l = new Lane();
        sp.changeHead(head);
        sp.setCurrentLane(l);
        System.out.println("--- Jegtoro fej mukodesben: ---");
        sp.cleanCurrentLane();
    }

    /**
     * SD-12: Fejcsere a telephelyen.
     * CD-12 alapjan: Takarito -> market.buyItem() -> I/N penz
     * -> ha sikeres: homeBase.swapHead(sp, newHead)
     */
    private static void runTestCase12() {
        System.out.println("\n--- [Fut] SD-12: Fejcsere a telephelyen ---");
        IntegratedMarket market = new IntegratedMarket();
        HomeBase hb = new HomeBase();
        SnowPlow sp = new SnowPlow();
        SweepHead newHead = new SweepHead();

        boolean success = market.buyItem(sp, newHead);
        if (success) {
            hb.swapHead(sp, newHead);
        }
    }

    /**
     * SD-13: Uzemanyag vasarlas es toltes.
     * CD-13 alapjan: Takarito -> market.buyItem() -> I/N penz
     * -> ha sikeres: homeBase.refuelSnowPlow(sp, amount)
     */
    private static void runTestCase13() {
        System.out.println("\n--- [Fut] SD-13: Uzemanyag vasarlas es toltes ---");
        IntegratedMarket market = new IntegratedMarket();
        HomeBase hb = new HomeBase();
        SnowPlow sp = new SnowPlow();
        SaltHead head = new SaltHead();
        sp.changeHead(head);

        System.out.println("--- Vasarlas: ---");
        boolean success = market.buyItem(sp, head);
        if (success) {
            hb.refuelSnowPlow(sp, 50.0);
        }
    }

    /**
     * SD-14: Hokotro vasarlasa.
     * CD-14 alapjan: Takarito -> market.buyItem() -> I/N penz
     * -> ha sikeres: homeBase.acceptSnowPlow(newSp)
     */
    private static void runTestCase14() {
        System.out.println("\n--- [Fut] SD-14: Hokotro vasarlasa ---");
        IntegratedMarket market = new IntegratedMarket();
        HomeBase hb = new HomeBase();
        SnowPlow existingSp = new SnowPlow();
        SnowPlow newSp = new SnowPlow();

        boolean success = market.buyItem(existingSp, newSp);
        if (success) {
            hb.acceptSnowPlow(newSp);
        }
    }

    /**
     * SD-15: Auto elakadasa es atsorolas.
     * CD-15 alapjan: Car.move() -> isBlocked -> changeLane
     * -> getNeighbors -> isBlocked(neighbor) -> remove -> accept
     */
    private static void runTestCase15() {
        System.out.println("\n--- [Fut] SD-15: Auto elakadasa es atsorolas ---");
        Car car = new Car();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        Lane neighbor = new Lane();
        car.move();
    }

    /**
     * SD-16: Jatek vege es eredmenyhirdetes.
     * CD-16 alapjan: GameLogic.advanceTurn() -> I/N max korok
     * -> endGame() -> getName/getCompletedRounds/getMoney -> evaluateWinners()
     */
    private static void runTestCase16() {
        System.out.println("\n--- [Fut] SD-16: Jatek vege es eredmenyhirdetes ---");
        GameLogic gl = new GameLogic();
        Bus bus1 = new Bus();
        Bus bus2 = new Bus();
        SnowPlow sp1 = new SnowPlow();
        SnowPlow sp2 = new SnowPlow();

        // advanceTurn -> I/N: max korok -> endGame
        Skeleton.enter("skeleton", "gameLogic", "advanceTurn()");

        boolean isGameOver = Skeleton.askQuestion(
                "Elerte a korok szama a maximumot?");

        if (isGameOver) {
            Skeleton.enter("gameLogic", "gameLogic", "endGame()");

            // Buszok eredmenyei
            bus1.getName();
            bus1.getCompletedRounds();
            bus2.getName();
            bus2.getCompletedRounds();

            // Hokotrok eredmenyei
            sp1.getName();
            sp1.getMoney();
            sp2.getName();
            sp2.getMoney();

            // Eredmenyek osszesitese
            gl.evaluateWinners();

            Skeleton.exit("void");
        }

        Skeleton.exit("result");
    }
}
