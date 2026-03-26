package main;

import java.util.Scanner;
import model.*;
import skeleton.Skeleton;

/**
 * A szkeleton alkalmazas belepesi pontja.
 * Felelossege a fomenu megjelenitese es a tesztesetek (Use-case-ek) inditasa.
 * (A terminalos megjelenites miatt ekezetmentesitve!)
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
        GameLogic gl = new GameLogic();
        gl.startGame();
    }

    private static void runTestCase2() {
        System.out.println("\n--- [Fut] SD-02: Havazas szimulacio (koronkent) ---");
        GameLogic gl = new GameLogic();
        // A szimulaciohoz az advanceTurn() hivodik, ami belul a Map.snow()-t hivja
        gl.advanceTurn();
    }

    private static void runTestCase3() {
        System.out.println("\n--- [Fut] SD-03: Auto mozgasa es letaposas ---");
        Car car = new Car();
        // A belso interakciokat (jeges-e, stb.) a Car osztaly kerdezi meg futas kozben
        car.move();
    }

    private static void runTestCase4() {
        System.out.println("\n--- [Fut] SD-04: Auto csuszasa jegen ---");
        Car car = new Car();
        // A slip() hivast akkor naplozza, ha az elagazasnal "Igen"-nel valaszolunk a
        // jegessegre
        car.move();
    }

    private static void runTestCase5() {
        System.out.println("\n--- [Fut] SD-05: Buszvezeto kor - navigacio es fordulo ---");
        Bus bus = new Bus();
        bus.move();
    }

    private static void runTestCase6() {
        System.out.println("\n--- [Fut] SD-06: Hokotro mozgasa es takaritas ---");
        SnowPlow sp = new SnowPlow();
        sp.move();
    }

    private static void runTestCase7() {
        System.out.println("\n--- [Fut] SD-07: Sopro fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        SweepHead sh = new SweepHead();
        sp.changeHead(sh);
        sp.cleanCurrentLane();
    }

    private static void runTestCase8() {
        System.out.println("\n--- [Fut] SD-08: Hanyo fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        ThrowerHead th = new ThrowerHead();
        sp.changeHead(th);
        sp.cleanCurrentLane();
    }

    private static void runTestCase9() {
        System.out.println("\n--- [Fut] SD-09: Soszoro fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        SaltHead salt = new SaltHead();
        sp.changeHead(salt);
        sp.cleanCurrentLane();
    }

    private static void runTestCase10() {
        System.out.println("\n--- [Fut] SD-10: Sarkany fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        DragonHead dh = new DragonHead();
        sp.changeHead(dh);
        sp.cleanCurrentLane();
    }

    private static void runTestCase11() {
        System.out.println("\n--- [Fut] SD-11: Jegtoro fej takaritasa ---");
        SnowPlow sp = new SnowPlow();
        IcebreakerHead ih = new IcebreakerHead();
        sp.changeHead(ih);
        sp.cleanCurrentLane();
    }

    private static void runTestCase12() {
        System.out.println("\n--- [Fut] SD-12: Fejcsere a telephelyen ---");
        IntegratedMarket market = new IntegratedMarket();
        SnowPlow sp = new SnowPlow();
        SweepHead newHead = new SweepHead();
        HomeBase hb = new HomeBase();

        boolean success = market.buyItem(sp, newHead);
        if (success) {
            hb.swapHead(sp, newHead);
        }
    }

    private static void runTestCase13() {
        System.out.println("\n--- [Fut] SD-13: Uzemanyag vasarlas es toltes ---");
        IntegratedMarket market = new IntegratedMarket();
        SnowPlow sp = new SnowPlow();
        SaltHead head = new SaltHead();
        sp.changeHead(head);
        HomeBase hb = new HomeBase();

        // Szimulaljuk a vasarlast, utana az utantoltest
        boolean success = market.buyItem(sp, head);
        if (success) {
            hb.refuelSnowPlow(sp, 50.0);
        }
    }

    private static void runTestCase14() {
        System.out.println("\n--- [Fut] SD-14: Hokotro vasarlasa ---");
        IntegratedMarket market = new IntegratedMarket();
        SnowPlow existingSp = new SnowPlow();
        SnowPlow newSp = new SnowPlow();
        HomeBase hb = new HomeBase();

        boolean success = market.buyItem(existingSp, newSp);
        if (success) {
            hb.acceptSnowPlow(newSp);
        }
    }

    private static void runTestCase15() {
        System.out.println("\n--- [Fut] SD-15: Auto elakadasa es atsorolas ---");
        Car car = new Car();
        // A move() belsejeben fognak lefutni az isBlocked() elagazasok a Skeleton
        // kerdessel
        car.move();
    }

    private static void runTestCase16() {
        System.out.println("\n--- [Fut] SD-16: Jatek vege es eredmenyhirdetes ---");
        GameLogic gl = new GameLogic();
        // Ha az advanceTurn()-nel "I"-t nyomunk a max kor kerdesre, meghivja az
        // endGame()-t
        gl.advanceTurn();
    }
}