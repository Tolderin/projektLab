package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * A játék menetét vezérlő osztály.
 * Felelőssége: a körök szervezése (tervezési fázis → végrehajtási fázis),
 * az autók navigálásának indítása, a játék végének detektálása és
 * az eredmény összesítése.
 * CD-01 alapján az inicializálás sorrendje:
 * new Map() → new HomeBase() → new Terminal() → new GeneralBuilding() →
 * new Road() → Road-on new Lane()-ek → setNeighbors() →
 * new SnowPlow() → acceptSnowPlow(sp) → new Bus() → accept(BUS) →
 * new Car() → accept(CAR).
 */
public class GameLogic {

    /** A játék térképe. */
    private Map gameMap;

    /** A játékban részt vevő játékosok listája. */
    private List<Player> players;

    /** Az aktuális kör sorszáma. */
    private int turnCount;

    /** A játék hossza körökben. */
    private int maxTurns;

    /** Az összes jármű listája. */
    private List<Vehicle> vehicles;

    /**
     * Létrehoz egy új GameLogic példányt.
     * @param gameMap  a játék térképe
     * @param maxTurns a játék maximális körszáma
     */
    public GameLogic(Map gameMap, int maxTurns) {
        this.gameMap = gameMap;
        this.maxTurns = maxTurns;
        this.turnCount = 0;
        this.players = new ArrayList<>();
        this.vehicles = new ArrayList<>();
    }

    /**
     * Inicializálja és elindítja a játékot a CD-01 kommunikációs diagram szerint.
     * Sorrend: new Map() → new HomeBase() → new Terminal() → new GeneralBuilding() →
     * new Road() → new Lane() (l1, l2) → setNeighbors() →
     * new SnowPlow() → acceptSnowPlow(sp) → new Bus() → accept(BUS) →
     * new Car() → accept(CAR).
     */
    public void startGame() {
        Skeleton.call("Main", "GameLogic", "startGame()");

        // CD-01: 1. new Map()
        // (gameMap már létezik, konstruktorban adtuk át)

        // CD-01: 2. new HomeBase(), 3. new Terminal(), 4. new GeneralBuilding()
        HomeBase homeBase = new HomeBase(3);
        Terminal terminal = new Terminal();
        GeneralBuilding home = new GeneralBuilding();
        GeneralBuilding work = new GeneralBuilding();

        // CD-01: 1.1 new Road() → 1.1.1 new Lane() (l1), 1.1.2 new Lane() (l2)
        Road road = new Road("Fo-utca", 100.0);
        Lane l1 = new Lane();
        Lane l2 = new Lane();
        road.addForwardLane(l1);
        road.addForwardLane(l2);
        gameMap.addRoad(road);

        // CD-01: setNeighbors() – szomszédsági kapcsolatok beállítása
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
        gameMap.addField(homeBase);
        gameMap.addField(terminal);
        gameMap.addField(home);
        gameMap.addField(work);

        // CD-01: 4. new SnowPlow() → 5. acceptSnowPlow(sp)
        Cleaner cleaner = new Cleaner("Takarito", 500);
        players.add(cleaner);
        SnowPlow sp = new SnowPlow("SP-01", 200, cleaner);
        cleaner.addSnowPlow(sp);
        vehicles.add(sp);
        homeBase.acceptSnowPlow(sp);
        sp.setCurrentLane(l1);

        // CD-01: 6. new Bus() → 7. accept(BUS) – l1 fogadja
        BusDriver busDriver = new BusDriver("Buszvezeto");
        players.add(busDriver);
        Bus bus = new Bus(busDriver);
        busDriver.setBus(bus);
        vehicles.add(bus);
        l1.accept(bus);

        // CD-01: 8. new Car() → 9. accept(CAR) – l1 fogadja
        Car car = new Car(home, work, gameMap);
        vehicles.add(car);
        l1.accept(car);

        Skeleton.ret("void");
    }

    /**
     * Eggyel növeli a körszámlálót, végrehajtja a végrehajtási fázist.
     * SD-16: Skeleton kérdez: "Elérte a körök száma a maximumot?" → I → endGame().
     */
    public void advanceTurn() {
        Skeleton.call("Main", "GameLogic", "advanceTurn()");
        turnCount++;

        boolean gameOver = Skeleton.ask("Elérte a körök száma a maximumot?");
        if (gameOver) {
            endGame();
            Skeleton.ret("void");
            return;
        }

        gameMap.snow();

        for (Vehicle v : vehicles) {
            v.move();
        }

        Skeleton.ret("void");
    }

    /**
     * Kezeli az aktív játékosok döntési fázisát.
     */
    public void manageTurn() {
        Skeleton.call("Main", "GameLogic", "manageTurn()");
        for (Player p : players) {
            p.startTurn();
            p.endTurn();
        }
        Skeleton.ret("void");
    }

    /**
     * SD-16 alapján: endGame() → getName()/getCompletedRounds() minden Bus-on →
     * getName()/getMoney() minden SnowPlow-on → evaluateWinners().
     */
    public void endGame() {
        Skeleton.call("GameLogic", "GameLogic", "endGame()");

        // SD-16: minden Bus: getName() és getCompletedRounds()
        for (Vehicle v : vehicles) {
            if (v instanceof Bus) {
                Bus bus = (Bus) v;
                BusDriver bd = bus.getDriver();
                if (bd != null) {
                    Skeleton.call("GameLogic", "Bus", "getName()");
                    Skeleton.ret(bd.getName());
                    Skeleton.call("GameLogic", "Bus", "getCompletedRounds()");
                    Skeleton.ret(String.valueOf(bd.getCompletedRounds()));
                }
            }
        }

        // SD-16: minden SnowPlow: getName() és getMoney()
        for (Vehicle v : vehicles) {
            if (v instanceof SnowPlow) {
                SnowPlow sp = (SnowPlow) v;
                Skeleton.call("GameLogic", "SnowPlow", "getName()");
                Skeleton.ret(sp.getName());
                Skeleton.call("GameLogic", "SnowPlow", "getMoney()");
                Skeleton.ret(String.valueOf(sp.getMoney()));
            }
        }

        // SD-16: evaluateWinners()
        evaluateWinners();

        Skeleton.ret("void");
    }

    /**
     * SD-16: evaluateWinners() – összesíti az eredményeket és kihirdeti a győztest.
     * Legtöbb forduló = buszvezető győztes. Legtöbb pénz = takarító győztes.
     */
    private void evaluateWinners() {
        Skeleton.call("GameLogic", "GameLogic", "evaluateWinners()");
        System.out.println("\n=== JATEK VEGE – EREDMENYEK ===");
        int maxRounds = -1;
        String busWinner = "-";
        int maxMoney = -1;
        String cleanerWinner = "-";

        for (Player p : players) {
            if (p instanceof BusDriver) {
                BusDriver bd = (BusDriver) p;
                if (bd.getCompletedRounds() > maxRounds) {
                    maxRounds = bd.getCompletedRounds();
                    busWinner = bd.getName();
                }
            } else if (p instanceof Cleaner) {
                Cleaner c = (Cleaner) p;
                if (c.getMoney() > maxMoney) {
                    maxMoney = c.getMoney();
                    cleanerWinner = c.getName();
                }
            }
        }

        System.out.println("Buszvezeto gyoztes: " + busWinner + " (" + maxRounds + " fordulo)");
        System.out.println("Takarito gyoztes:   " + cleanerWinner + " (" + maxMoney + " penz)");
        Skeleton.ret("result");
    }

    /** Hozzáad egy játékost. @param p a játékos */
    public void addPlayer(Player p) { players.add(p); }

    /** Hozzáad egy járművet. @param v a jármű */
    public void addVehicle(Vehicle v) { vehicles.add(v); }

    /** @return az aktuális körszám */
    public int getTurnCount() { return turnCount; }
}
