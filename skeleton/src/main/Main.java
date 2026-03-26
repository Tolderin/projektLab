package main;

import java.util.Scanner;
import model.*;
import skeleton.Skeleton;

/**
 * A szkeleton alkalmazas belepesi pontja.
 * Felelossege a fomenu megjelenitese es a tesztesetek (Use-case-ek) inditasa.
 * (A terminalos megjelenites miatt ekezetmentesitve, inicializalas a CD-k
 * alapjan!)
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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
                case "1":
                    runTestCase1();
                    break;
                case "2":
                    runTestCase2();
                    break;
                case "3":
                    runTestCase3();
                    break;
                case "4":
                    runTestCase4();
                    break;
                case "5":
                    runTestCase5();
                    break;
                case "6":
                    runTestCase6();
                    break;
                case "7":
                    runTestCase7();
                    break;
                case "8":
                    runTestCase8();
                    break;
                case "9":
                    runTestCase9();
                    break;
                case "10":
                    runTestCase10();
                    break;
                case "11":
                    runTestCase11();
                    break;
                case "12":
                    runTestCase12();
                    break;
                case "13":
                    runTestCase13();
                    break;
                case "14":
                    runTestCase14();
                    break;
                case "15":
                    runTestCase15();
                    break;
                case "16":
                    runTestCase16();
                    break;
                case "0":
                    running = false;
                    System.out.println("Kilepes a programbol...");
                    break;
                default:
                    System.out.println("Ervenytelen valasztas! Kerlek 0 es 16 kozott valassz.");
            }
        }
    }

    private static void runTestCase1() {
        System.out.println("\n--- [Fut] SD-01: Jatek inicializalasa ---");
        // Inicializalas CD-01 alapjan
        GameLogic gl = new GameLogic();
        Map m = new Map();
        Road r = new Road();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        HomeBase hb = new HomeBase();
        Terminal t = new Terminal();
        GeneralBuilding gb = new GeneralBuilding();
        SnowPlow sp = new SnowPlow();
        Bus bus = new Bus();
        Car car = new Car();

        // Futtatas
        gl.startGame();
    }

    private static void runTestCase2() {
        System.out.println("\n--- [Fut] SD-02: Havazas szimulacio (koronkent) ---");
        // Inicializalas CD-02 alapjan
        GameLogic gl = new GameLogic();
        Map m = new Map();
        Lane l1 = new Lane();
        Lane l2 = new Lane();

        // Futtatas
        gl.advanceTurn();
    }

    private static void runTestCase3() {
        System.out.println("\n--- [Fut] SD-03: Auto mozgasa es letaposas ---");
        // Inicializalas CD-03 alapjan
        GameLogic gl = new GameLogic();
        Car car = new Car();
        Map m = new Map();
        Lane l1 = new Lane();
        Lane l2 = new Lane();

        // Futtatas
        car.move();
    }

    private static void runTestCase4() {
        System.out.println("\n--- [Fut] SD-04: Auto csuszasa jegen ---");
        // Inicializalas CD-04 alapjan
        GameLogic gl = new GameLogic();
        Car car = new Car();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        // other:Vehicle (szimulalva Car-ral)
        Car otherVehicle = new Car();

        // Futtatas
        car.move();
    }

    private static void runTestCase5() {
        System.out.println("\n--- [Fut] SD-05: Buszvezeto kor - navigacio es fordulo ---");
        // Inicializalas CD-05 alapjan
        GameLogic gl = new GameLogic();
        Bus bus = new Bus();
        Lane l1 = new Lane();
        Terminal t = new Terminal();

        // Futtatas (a Skeleton-ban hivjuk meg a megfelelo logolassal a dokumentacio
        // alapjan)
        Skeleton.enter("Buszvezeto", "bus", "move()");
        bus.move();
        Skeleton.exit("void");
    }

    private static void runTestCase6() {
        System.out.println("\n--- [Fut] SD-06: Hokotro mozgasa es takaritas ---");
        // Inicializalas CD-06 alapjan
        GameLogic gl = new GameLogic();
        SnowPlow sp = new SnowPlow();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        SweepHead head = new SweepHead();

        // Futtatas
        sp.move();
    }

    private static void runTestCase7() {
        System.out.println("\n--- [Fut] SD-07: Sopro fej takaritasa ---");
        // Inicializalas CD-07 alapjan
        SnowPlow sp = new SnowPlow();
        SweepHead head = new SweepHead();
        Lane l = new Lane();
        Lane neighbor = new Lane();

        sp.changeHead(head);

        // Futtatas
        sp.cleanCurrentLane();
    }

    private static void runTestCase8() {
        System.out.println("\n--- [Fut] SD-08: Hanyo fej takaritasa ---");
        // Inicializalas CD-08 alapjan
        SnowPlow sp = new SnowPlow();
        ThrowerHead head = new ThrowerHead();
        Lane l = new Lane();

        sp.changeHead(head);

        // Futtatas
        sp.cleanCurrentLane();
    }

    private static void runTestCase9() {
        System.out.println("\n--- [Fut] SD-09: Soszoro fej takaritasa ---");
        // Inicializalas CD-09 alapjan
        SnowPlow sp = new SnowPlow();
        SaltHead head = new SaltHead();
        Lane l = new Lane();

        sp.changeHead(head);

        // Futtatas
        sp.cleanCurrentLane();
    }

    private static void runTestCase10() {
        System.out.println("\n--- [Fut] SD-10: Sarkany fej takaritasa ---");
        // Inicializalas CD-10 alapjan
        SnowPlow sp = new SnowPlow();
        DragonHead head = new DragonHead();
        Lane l = new Lane();

        sp.changeHead(head);

        // Futtatas
        sp.cleanCurrentLane();
    }

    private static void runTestCase11() {
        System.out.println("\n--- [Fut] SD-11: Jegtoro fej takaritasa ---");
        // Inicializalas CD-11 alapjan
        SnowPlow sp = new SnowPlow();
        IcebreakerHead head = new IcebreakerHead();
        Lane l = new Lane();

        sp.changeHead(head);

        // Futtatas
        sp.cleanCurrentLane();
    }

    private static void runTestCase12() {
        System.out.println("\n--- [Fut] SD-12: Fejcsere a telephelyen ---");
        // Inicializalas CD-12 alapjan
        IntegratedMarket market = new IntegratedMarket();
        HomeBase hb = new HomeBase();
        SnowPlow sp = new SnowPlow();
        SweepHead newHead = new SweepHead();

        // Futtatas
        boolean success = market.buyItem(sp, newHead);
        if (success) {
            hb.swapHead(sp, newHead);
        }
    }

    private static void runTestCase13() {
        System.out.println("\n--- [Fut] SD-13: Uzemanyag vasarlas es toltes ---");
        // Inicializalas CD-13 alapjan
        IntegratedMarket market = new IntegratedMarket();
        HomeBase hb = new HomeBase();
        SnowPlow sp = new SnowPlow();
        SaltHead head = new SaltHead();
        sp.changeHead(head);

        // Futtatas
        boolean success = market.buyItem(sp, head);
        if (success) {
            hb.refuelSnowPlow(sp, 50.0);
        }
    }

    private static void runTestCase14() {
        System.out.println("\n--- [Fut] SD-14: Hokotro vasarlasa ---");
        // Inicializalas CD-14 alapjan
        IntegratedMarket market = new IntegratedMarket();
        HomeBase hb = new HomeBase();
        SnowPlow existingSp = new SnowPlow();
        SnowPlow newSp = new SnowPlow();

        // Futtatas
        boolean success = market.buyItem(existingSp, newSp);
        if (success) {
            hb.acceptSnowPlow(newSp);
        }
    }

    private static void runTestCase15() {
        System.out.println("\n--- [Fut] SD-15: Auto elakadasa es atsorolas ---");
        // Inicializalas CD-15 alapjan
        GameLogic gl = new GameLogic();
        Car car = new Car();
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        Lane neighbor = new Lane();

        // Futtatas
        car.move();
    }

    private static void runTestCase16() {
        System.out.println("\n--- [Fut] SD-16: Jatek vege es eredmenyhirdetes ---");
        // Inicializalas CD-16 alapjan
        GameLogic gl = new GameLogic();
        Bus bus1 = new Bus();
        Bus bus2 = new Bus();
        SnowPlow sp1 = new SnowPlow();
        SnowPlow sp2 = new SnowPlow();

        // Futtatas
        gl.advanceTurn();
    }
}