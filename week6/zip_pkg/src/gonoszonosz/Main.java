package gonoszonosz;

import java.util.Scanner;

/**
 * A szkeleton program belépési pontja.
 * Főmenüt jelenít meg, amelyből a felhasználó kiválaszthatja
 * a lefuttatni kívánt tesztszekvenciát (SD-01 – SD-16).
 * Minden menüpont az analízis modell egy-egy use-case-ét demonstrálja,
 * az 5. heti szkeleton-tervező dokumentum use-case leírásai alapján.
 * Az úthálózat felépítése Road objektumokon keresztül történik.
 */
public class Main {

    /** Bemeneti olvasó a menüválasztáshoz. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * A program belépési pontja. Megjeleníti a főmenüt és kezeli a felhasználó választásait.
     * @param args parancssori argumentumok (nem használt)
     */
    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("  GonoszOnosz - Szkeleton Tesztelő Program");
        System.out.println("  48-as csoport | 2026. tavaszi felev");
        System.out.println("================================================");

        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();
            Skeleton.resetDepth();
            System.out.println();
            switch (input) {
                case "1":  runSD01(); break;
                case "2":  runSD02(); break;
                case "3":  runSD03(); break;
                case "4":  runSD04(); break;
                case "5":  runSD05(); break;
                case "6":  runSD06(); break;
                case "7":  runSD07(); break;
                case "8":  runSD08(); break;
                case "9":  runSD09(); break;
                case "10": runSD10(); break;
                case "11": runSD11(); break;
                case "12": runSD12(); break;
                case "13": runSD13(); break;
                case "14": runSD14(); break;
                case "15": runSD15(); break;
                case "16": runSD16(); break;
                case "0":  running = false; System.out.println("Kilepes."); break;
                default:   System.out.println("Ervenytelen valasztas!"); break;
            }
            if (running && !input.equals("0")) {
                System.out.println("\n[Nyomj ENTER-t a folytatáshoz...]");
                scanner.nextLine();
            }
        }
    }

    /** Kiírja a főmenüt. */
    private static void printMenu() {
        System.out.println("\n--- FOMENU ---");
        System.out.println(" 1  SD-01: Jatek inicializalasa");
        System.out.println(" 2  SD-02: Havazas szimulacio");
        System.out.println(" 3  SD-03: Auto mozgasa es letaposas");
        System.out.println(" 4  SD-04: Auto csuszasa jeden");
        System.out.println(" 5  SD-05: Buszvezeto kor - navigacio es fordulo");
        System.out.println(" 6  SD-06: Hokotro mozgasa es takaritas");
        System.out.println(" 7  SD-07: Sopro fej takaritasa");
        System.out.println(" 8  SD-08: Hanyo fej takaritasa");
        System.out.println(" 9  SD-09: Soszoro fej takaritasa");
        System.out.println("10  SD-10: Sarkany fej takaritasa");
        System.out.println("11  SD-11: Jegto fej takaritasa");
        System.out.println("12  SD-12: Fejcsere a telephelyen");
        System.out.println("13  SD-13: Uzemanyag vasarlas es toltes");
        System.out.println("14  SD-14: Hokotro vasarlasa");
        System.out.println("15  SD-15: Auto elakadasa es atsorolas");
        System.out.println("16  SD-16: Jatek vege es eredmenyhirdetes");
        System.out.println(" 0  Kilepes");
        System.out.print("Valasztas: ");
    }

    /**
     * Segédmetódus: létrehozza az alap tesztkörnyezetet Road-dal,
     * két forward sávval (l1, l2), HomeBase-szel, Terminállal,
     * GeneralBuilding lakással és munkahellyel.
     * Az 5. heti SD-01 leírás alapján: a Map létrehozza a Road-ot,
     * a Road létrehozza a Lane objektumokat.
     * @return a felépített tesztkörnyezet egy tömbben:
     *         [0]=Map, [1]=Road, [2]=Lane l1, [3]=Lane l2,
     *         [4]=HomeBase, [5]=Terminal, [6]=GeneralBuilding home, [7]=GeneralBuilding work
     */
    private static Object[] buildTestEnv() {
        Map map = new Map();

        // SD-01: A Map létrehoz egy Road-ot, amely létrehozza a Lane objektumokat (l1, l2)
        Road road = new Road("Teszt-utca", 100.0);
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        road.addForwardLane(l1);   // l1 és l2 szomszédosak lesznek (sávváltás)
        road.addForwardLane(l2);
        map.addRoad(road);         // regisztrálja l1, l2-t a Field-gráfba

        // SD-01: A Map létrehozza a HomeBase, Terminal és GeneralBuilding objektumokat
        HomeBase homeBase = new HomeBase(3);
        Terminal terminal = new Terminal();
        GeneralBuilding home = new GeneralBuilding();
        GeneralBuilding work = new GeneralBuilding();

        // SD-01: Beállítódnak a szomszédsági kapcsolatok (getNeighbors-on keresztül elérhető gráf)
        homeBase.addNeighbor(l1);
        l1.addNeighbor(homeBase);
        l2.addNeighbor(terminal);
        terminal.addNeighbor(l2);
        home.addNeighbor(l1);
        l1.addNeighbor(home);
        work.addNeighbor(l2);
        l2.addNeighbor(work);

        road.addBuilding(homeBase);
        road.addBuilding(terminal);
        road.addBuilding(home);
        road.addBuilding(work);
        map.addField(homeBase);
        map.addField(terminal);
        map.addField(home);
        map.addField(work);

        return new Object[]{map, road, l1, l2, homeBase, terminal, home, work};
    }

    // =========================================================
    // SD-01: Játék inicializálása
    // =========================================================
    /**
     * SD-01: A GameLogic létrehozza a Map-et, feltölti Field objektumokkal
     * Road-on keresztül. Beállítódnak a szomszédsági kapcsolatok.
     * Létrejön egy SnowPlow (HomeBase fogadja), egy Bus (l1 fogadja),
     * egy Car (l1 fogadja).
     */
    private static void runSD01() {
        System.out.println("=== SD-01: Jatek inicializalasa ===");
        Map map = new Map();
        GameLogic gl = new GameLogic(map, 10);
        gl.startGame();
    }

    // =========================================================
    // SD-02: Havazás szimuláció
    // =========================================================
    /**
     * SD-02: A GameLogic.advanceTurn() meghívja a Map.snow() metódust.
     * A Map.snow() végigiterál a Road-okon, minden Lane-en meghívja
     * az addSnow(amount) metódust. Building típusokon nem hív addSnow()-t.
     */
    private static void runSD02() {
        System.out.println("=== SD-02: Havazas szimulacio ===");
        Object[] env = buildTestEnv();
        Map map = (Map) env[0];
        Lane l1 = (Lane) env[2];
        Lane l2 = (Lane) env[3];

        System.out.println("l1 ho elotte: " + l1.getSnowDepth());
        System.out.println("l2 ho elotte: " + l2.getSnowDepth());
        // SD-02: GameLogic.advanceTurn() -> Map.snow()
        map.snow();
        System.out.println("l1 ho utana: " + l1.getSnowDepth() + " (Building-eken nem nott)");
        System.out.println("l2 ho utana: " + l2.getSnowDepth());
    }

    // =========================================================
    // SD-03: Autó mozgása és letaposás
    // =========================================================
    /**
     * SD-03: A GameLogic.advanceTurn() meghívja a Car.move()-t.
     * A Car.calculatePath() meghívja a gameMap.findShortestPath()-t.
     * A nextField beállítódik (l2). remove(car) hívódik l1-en, accept(car) l2-n.
     * applyCompaction() hívódik, majd checkFreeze() – a Skeleton megkérdezi:
     * "Elérte a letaposások száma a küszöböt, és van hó a sávon?"
     */
    private static void runSD03() {
        System.out.println("=== SD-03: Auto mozgasa es letaposas ===");
        Object[] env = buildTestEnv();
        Map map = (Map) env[0];
        Lane l1 = (Lane) env[2];
        GeneralBuilding home = (GeneralBuilding) env[6];
        GeneralBuilding work = (GeneralBuilding) env[7];

        Car car = new Car(home, work, map);
        l1.accept(car);
        // SD-03: GameLogic.advanceTurn() -> Car.move()
        car.move();
    }

    // =========================================================
    // SD-04: Autó csúszása jégen
    // =========================================================
    /**
     * SD-04: A Car.move() kiszámítja az útvonalat, nextField = l2 (isFrozen==true).
     * remove(car) hívódik l1-en, accept(car) l2-n.
     * A Skeleton megkérdezi: "Jeges a sáv?" -> I -> "Megcsúszik az autó?" -> I ->
     * Car.slip() -> "Van másik jármű a sávban, amivel ütközhet?" -> I ->
     * collideWith(car) -> isBlocked() igazzá válik.
     */
    private static void runSD04() {
        System.out.println("=== SD-04: Auto csuszasa jeden ===");
        Object[] env = buildTestEnv();
        Map map = (Map) env[0];
        Lane l1 = (Lane) env[2];
        GeneralBuilding home = (GeneralBuilding) env[6];
        GeneralBuilding work = (GeneralBuilding) env[7];

        Car car = new Car(home, work, map);
        l1.accept(car);
        // A Skeleton a move() belsejéből kérdez jég/csúszás/ütközés esetén
        car.move();
    }

    // =========================================================
    // SD-05: Buszvezető kör – navigáció és forduló
    // =========================================================
    /**
     * SD-05: A GameLogic.manageTurn() jelzi a buszvezető aktornak a kör kezdetét (startTurn()).
     * A buszvezető kiválasztja a következő célmezőt, meghívja a Bus.move()-t.
     * remove(bus) hívódik az előző Lane-en, accept(bus) a célmezőn.
     * A Skeleton megkérdezi: "A célmező végállomás (Terminal)?" -> I ->
     * Terminal.registerArrival(bus) -> completedRounds nő -> endTurn().
     */
    private static void runSD05() {
        System.out.println("=== SD-05: Buszvezeto kor - navigacio es fordulo ===");
        Object[] env = buildTestEnv();
        Lane l1 = (Lane) env[2];
        Lane l2 = (Lane) env[3];
        Terminal terminal = (Terminal) env[5];

        BusDriver bd = new BusDriver("Kovacs");
        Bus bus = new Bus(bd);
        bd.setBus(bus);

        // SD-05: startTurn() jelzi a kor kezdetet
        bd.startTurn();

        // SD-05: A buszvezető kiválasztja a következő célmezőt -> Bus.move()
        l1.accept(bus);
        bus.setNextField(terminal);
        Skeleton.call("BusDriver", "Bus", "move()");
        l1.remove(bus);
        terminal.accept(bus);
        Skeleton.ret("void");

        // SD-05: A Skeleton megkérdezi: "A célmező végállomás (Terminal)?"
        boolean atTerminal = Skeleton.ask("A celmezo vegallomas (Terminal)?");
        if (atTerminal) {
            terminal.registerArrival(bus);
        }

        // SD-05: endTurn()
        bd.endTurn();
        System.out.println("Teljesitett fordulok: " + bd.getCompletedRounds());
    }

    // =========================================================
    // SD-06: Hókotró mozgása és takarítás
    // =========================================================
    /**
     * SD-06: startTurn() jelzi a takarítónak a kör kezdetét.
     * A takarító beállítja: nextField = l2. endTurn() jelzi a döntési fázis végét.
     * A GameLogic.advanceTurn() meghívja a SnowPlow.move()-t.
     * remove(sp) hívódik l1-en, accept(sp) hívódik l2-n.
     * SnowPlow.cleanCurrentLane() meghívja az activeHead.clean(l2) metódust.
     */
    private static void runSD06() {
        System.out.println("=== SD-06: Hokotro mozgasa es takaritas ===");
        Object[] env = buildTestEnv();
        Lane l1 = (Lane) env[2];
        Lane l2 = (Lane) env[3];

        Cleaner cleaner = new Cleaner("Takacs", 500);
        SnowPlow sp = new SnowPlow("SP-01", 200, cleaner);
        cleaner.addSnowPlow(sp);
        l2.addSnow(3.0);
        l1.accept(sp);

        // SD-06: startTurn() -> takarító beállítja nextField-et -> endTurn()
        cleaner.startTurn();
        sp.setNextField(l2);
        cleaner.endTurn();

        // SD-06: GameLogic.advanceTurn() -> SnowPlow.move()
        sp.move();
        System.out.println("l2 ho utana: " + l2.getSnowDepth());
    }

    // =========================================================
    // SD-07: Söprő fej takarítása
    // =========================================================
    /**
     * SD-07: SnowPlow.cleanCurrentLane() meghívja a SweepHead.clean(lane)-t.
     * A SweepHead meghívja lane.removeSnow(amount)-t: a hóréteg csökken.
     * lane.getNeighbors() visszaadja a szomszédos sávokat.
     * A szomszéd neighborLane-en addSnow(amount) hívódik – hó átkerül.
     */
    private static void runSD07() {
        System.out.println("=== SD-07: Sopro fej takaritasa ===");
        Object[] env = buildTestEnv();
        Lane l1 = (Lane) env[2];
        Lane l2 = (Lane) env[3];
        l1.addSnow(5.0);

        SweepHead head = new SweepHead();
        System.out.println("l1 ho elotte: " + l1.getSnowDepth());
        System.out.println("l2 ho elotte: " + l2.getSnowDepth());
        // SD-07: SweepHead.clean(lane)
        head.clean(l1);
        System.out.println("l1 ho utana:  " + l1.getSnowDepth());
        System.out.println("l2 ho utana:  " + l2.getSnowDepth() + " (atseperte)");
    }

    // =========================================================
    // SD-08: Hányó fej takarítása
    // =========================================================
    /**
     * SD-08: SnowPlow.cleanCurrentLane() meghívja a ThrowerHead.clean(lane)-t.
     * A ThrowerHead meghívja lane.removeSnow(amount)-t.
     * A throwDistance értéknek megfelelően a hó messzire kerül (járdára).
     * Szomszédos Lane-en nem történik addSnow() hívás.
     */
    private static void runSD08() {
        System.out.println("=== SD-08: Hanyo fej takaritasa ===");
        Object[] env = buildTestEnv();
        Lane l1 = (Lane) env[2];
        Lane l2 = (Lane) env[3];
        l1.addSnow(5.0);

        ThrowerHead head = new ThrowerHead(150, 10.0);
        System.out.println("l1 ho elotte: " + l1.getSnowDepth());
        System.out.println("l2 ho elotte: " + l2.getSnowDepth());
        // SD-08: ThrowerHead.clean(lane) -> removeSnow, szomszéd sáv NEM módosul
        head.clean(l1);
        System.out.println("l1 ho utana:  " + l1.getSnowDepth());
        System.out.println("l2 ho utana:  " + l2.getSnowDepth() + " (valtozatlan, jardan van a ho)");
    }

    // =========================================================
    // SD-09: Sószóró fej takarítása
    // =========================================================
    /**
     * SD-09: SnowPlow.cleanCurrentLane() meghívja a SaltHead.clean(lane)-t.
     * A Skeleton megkérdezi: "Van elegendő só a tartályban (fuelAmount > 0)?"
     * I válasz: lane.applySaltEffect(activeTime) hívódik, fuelAmount csökken.
     * N válasz: a metódus visszatér hatás nélkül.
     */
    private static void runSD09() {
        System.out.println("=== SD-09: Soszoro fej takaritasa ===");
        Object[] env = buildTestEnv();
        Lane l1 = (Lane) env[2];
        l1.addSnow(3.0);

        SaltHead head = new SaltHead(300, 5.0, 50.0);
        // SD-09: SaltHead.clean(lane) – Skeleton kérdez a só szintről
        head.clean(l1);
    }

    // =========================================================
    // SD-10: Sárkány fej takarítása
    // =========================================================
    /**
     * SD-10: SnowPlow.cleanCurrentLane() meghívja a DragonHead.clean(lane)-t.
     * A Skeleton megkérdezi: "Van elegendő biokerozin a tartályban (fuelAmount > 0)?"
     * I válasz: removeSnow() és removeIce() lefutnak, fuelAmount csökken.
     * N válasz: a metódus visszatér.
     */
    private static void runSD10() {
        System.out.println("=== SD-10: Sarkany fej takaritasa ===");
        Object[] env = buildTestEnv();
        Lane l1 = (Lane) env[2];
        l1.addSnow(8.0);

        DragonHead head = new DragonHead(800, 10.0, 200.0);
        System.out.println("l1 ho elotte: " + l1.getSnowDepth());
        // SD-10: DragonHead.clean(lane) – Skeleton kérdez a biokerozin szintről
        head.clean(l1);
        System.out.println("l1 ho utana:  " + l1.getSnowDepth());
    }

    // =========================================================
    // SD-11: Jégtörő fej takarítása
    // =========================================================
    /**
     * SD-11: SnowPlow.cleanCurrentLane() meghívja az IcebreakerHead.clean(lane)-t.
     * A Skeleton megkérdezi: "Jeges a sáv (isFrozen == true)?"
     * I válasz: breakIce() és addSnow() lefut – jég hóvá alakul.
     * N válasz: a metódus visszatér.
     */
    private static void runSD11() {
        System.out.println("=== SD-11: Jegtoro fej takaritasa ===");
        Object[] env = buildTestEnv();
        Lane l1 = (Lane) env[2];

        IcebreakerHead head = new IcebreakerHead(400);
        // SD-11: IcebreakerHead.clean(lane) – Skeleton kérdez a jég állapotáról
        head.clean(l1);
    }

    // =========================================================
    // SD-12: Fejcsere a telephelyen
    // =========================================================
    /**
     * SD-12: A takarító meghívja az IntegratedMarket.buyItem(sp, newHead)-et.
     * A Skeleton megkérdezi: "Van elegendő pénze a játékosnak a vásárláshoz?"
     * I -> tranzakció létrejön. N -> false-szal tér vissza.
     * A hókotró visszatér a telephelyre (SD-06 alapján).
     * HomeBase.swapHead(sp, newHead) -> SnowPlow.changeHead(newHead) -> activeHead = newHead.
     */
    private static void runSD12() {
        System.out.println("=== SD-12: Fejcsere a telephelyen ===");
        Object[] env = buildTestEnv();
        Lane l1 = (Lane) env[2];
        HomeBase homeBase = (HomeBase) env[4];

        Cleaner cleaner = new Cleaner("Takacs", 1000);
        SnowPlow sp = new SnowPlow("SP-01", 200, cleaner);
        cleaner.addSnowPlow(sp);
        l1.accept(sp);

        IcebreakerHead newHead = new IcebreakerHead(400);
        homeBase.getMarket().addItem(newHead);

        // SD-12: IntegratedMarket.buyItem() – Skeleton kérdez a pénzről
        boolean bought = homeBase.getMarket().buyItem(cleaner, newHead);
        if (bought) {
            // SD-12: HomeBase.swapHead(sp, newHead) -> SnowPlow.changeHead(newHead)
            homeBase.swapHead(sp, newHead);
            System.out.println("Aktiv fej: " + sp.getActiveHead().getName());
        }
    }

    // =========================================================
    // SD-13: Üzemanyag vásárlás és töltés
    // =========================================================
    /**
     * SD-13: A takarító meghívja az IntegratedMarket.buyItem(sp, fuelItem)-et.
     * A Skeleton megkérdezi: "Van elegendő pénze a játékosnak a vásárláshoz?"
     * I -> tranzakció létrejön. N -> false.
     * HomeBase.refuelSnowPlow(sp, amount) -> SaltHead.refillFuel(amount) vagy
     * DragonHead.refillFuel(amount): fuelAmount += amount.
     */
    private static void runSD13() {
        System.out.println("=== SD-13: Uzemanyag vasarlas es toltes ===");
        Object[] env = buildTestEnv();
        HomeBase homeBase = (HomeBase) env[4];

        Cleaner cleaner = new Cleaner("Takacs", 1000);
        SaltHead saltHead = new SaltHead(300, 0.0, 50.0);
        SnowPlow sp = new SnowPlow("SP-01", 200, cleaner);
        sp.changeHead(saltHead);
        cleaner.addSnowPlow(sp);

        homeBase.getMarket().addItem(saltHead);

        // SD-13: IntegratedMarket.buyItem() – Skeleton kérdez a pénzről
        boolean bought = homeBase.getMarket().buyItem(cleaner, saltHead);
        if (bought) {
            // SD-13: HomeBase.refuelSnowPlow(sp, amount) -> SaltHead.refillFuel(amount)
            homeBase.refuelSnowPlow(sp, 5.0);
            System.out.println("So mennyiseg toltes utan: " + saltHead.getFuelAmount());
        }
    }

    // =========================================================
    // SD-14: Hókotró vásárlása
    // =========================================================
    /**
     * SD-14: A takarító meghívja az IntegratedMarket.buyItem(existingSp, newSp)-et.
     * A Skeleton megkérdezi: "Van elegendő pénze a játékosnak a vásárláshoz?"
     * I -> tranzakció létrejön (new SnowPlow). N -> false.
     */
    /**
     * SD-14 / CD-14: buyItem(existingSp, newSp) – Skeleton kérdez.
     * I: new SnowPlow() létrejön, existingSp.money -= getPrice(),
     *    HomeBase.acceptSnowPlow(newSp) fogadja. N: false.
     */
    private static void runSD14() {
        System.out.println("=== SD-14: Hokotro vasarlasa ===");
        Object[] env = buildTestEnv();
        HomeBase homeBase = (HomeBase) env[4];

        Cleaner cleaner = new Cleaner("Takacs", 1000);
        // CD-14: existingSp – a meglévő hókotró, aki vásárol
        SnowPlow existingSp = new SnowPlow("SP-01", 200, cleaner);
        cleaner.addSnowPlow(existingSp);

        // CD-14: buyItem(existingSp, newSp) – az új hókotró még csak az itemlistán van
        // A SnowPlow egyben IPurchasable, ezért adható a bolthoz
        SnowPlow newSp = new SnowPlow("SP-02", 200, cleaner);
        homeBase.getMarket().addItem(newSp);

        // CD-14: 1. buyItem(existingSp, newSp) -> Skeleton kérdez
        boolean bought = homeBase.getMarket().buyItem(cleaner, newSp);
        if (bought) {
            // CD-14: 1.1 new SnowPlow() – az objektum már létrejött fent
            // CD-14: 1.2 existingSp.money -= getPrice() – már megtörtént a buyItem-ben
            // CD-14: 1.3 acceptSnowPlow(newSp)
            cleaner.addSnowPlow(newSp);
            homeBase.acceptSnowPlow(newSp);
            System.out.println("Hokotrok szama: " + cleaner.getSnowPlows().size());
        }
    }

    // =========================================================
    // SD-15: Autó elakadása és átsorolás
    // =========================================================
    /**
     * SD-15: A Car.move() kiszámítja az útvonalat: nextField = l2.
     * Az isBlocked() hívásakor az l2 sávon a Skeleton megkérdezi:
     * "Le van zárva az l2 sáv?" -> I -> changeLane() hívódik.
     * Car.changeLane() -> lane.getNeighbors() -> szomszéd sávon isBlocked():
     * "Szabad a szomszédos sáv?" -> I -> remove(car) és accept(car) hívódnak.
     */
    private static void runSD15() {
        System.out.println("=== SD-15: Auto elakadasa es atsorolas ===");
        Object[] env = buildTestEnv();
        Map map = (Map) env[0];
        Lane l1 = (Lane) env[2];
        GeneralBuilding home = (GeneralBuilding) env[6];
        GeneralBuilding work = (GeneralBuilding) env[7];

        Car car = new Car(home, work, map);
        l1.accept(car);
        // SD-15: Az isBlocked() kérdést a Car.move() belsejéből a Skeleton kezeli
        car.move();
    }

    // =========================================================
    // SD-16: Játék vége és eredményhirdetés
    // =========================================================
    /**
     * SD-16: Az advanceTurn() hívásakor a Skeleton megkérdezi:
     * "Elérte a körök száma a maximumot?" -> I -> endGame() meghívódik.
     * Minden Bus: getName() és completedRounds lekérdezése.
     * Minden SnowPlow: getName() és money lekérdezése.
     * Eredmények összesítése és kiírása.
     */
    /**
     * SD-16 / UC-16: A diagramban bus1:Bus, bus2:Bus, sp1:SnowPlow, sp2:SnowPlow szerepel.
     * advanceTurn() → Skeleton: "Elérte a körök száma a maximumot?" → I →
     * endGame() → getName()/getCompletedRounds() bus1-en, bus2-n →
     * getName()/getMoney() sp1-en, sp2-n → evaluateWinners() → result.
     */
    private static void runSD16() {
        System.out.println("=== SD-16: Jatek vege es eredmenyhirdetes ===");
        Map map = new Map();
        GameLogic gl = new GameLogic(map, 3);

        // SD-16 diagram: bus1, bus2, sp1, sp2 szereplők
        // A startGame() létrehozza az alap objektumokat, majd manuálisan hozzáadjuk a 2. buszt és hókotrót
        gl.startGame();

        // Második buszvezető és busz hozzáadása (SD-16 diagramban bus2:Bus is szerepel)
        BusDriver bd2 = new BusDriver("Kovacs");
        Bus bus2 = new Bus(bd2);
        bd2.setBus(bus2);
        bd2.incrementRounds(); // szimulált 1 forduló
        gl.addPlayer(bd2);
        gl.addVehicle(bus2);

        // Második hókotró hozzáadása (SD-16 diagramban sp2:SnowPlow is szerepel)
        Cleaner cleaner2 = new Cleaner("Szabo", 800);
        SnowPlow sp2 = new SnowPlow("SP-02", 200, cleaner2);
        cleaner2.addSnowPlow(sp2);
        gl.addPlayer(cleaner2);
        gl.addVehicle(sp2);

        // SD-16: advanceTurn() -> Skeleton kérdez -> endGame() -> evaluateWinners()
        gl.advanceTurn();
    }
}
