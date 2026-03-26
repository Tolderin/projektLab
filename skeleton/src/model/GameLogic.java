package model;

import skeleton.Skeleton;

/**
 * A jatek menetet vezerlo osztaly.
 * Felelossege: a korok szervezese (tervezesi fazis -> vegrehajtasi fazis),
 * az autok navigalasanak inditasa, a jatek vegenek detektalasa
 * es az eredmeny osszesitese.
 * A havazas szimulalasa a Map.snow() metoduson keresztul tortenik.
 */
public class GameLogic {

    /** A jatek terkepe. */
    private Map gameMap;

    /** Az aktualis kor sorszama. */
    private int turnCount = 0;

    /** A jatek hossza korokban. */
    private int maxTurns = 10;

    /**
     * Inicializalja es elinditja a jatekot.
     * SD-01 alapjan: Map, Road, Lane-ek, Building-ek, jarmuvek letrehozasa
     * es elhelyezese a grafban.
     */
    public void startGame() {
        Skeleton.enter("skeleton", "gameLogic", "startGame()");

        // Map letrehozasa
        Skeleton.enter("gameLogic", "map", "new Map()");
        Skeleton.exit("m:Map");

        // Road letrehozasa a Map-en belul
        Skeleton.enter("map", "road", "new Road()");
        Skeleton.exit("r:Road");

        // Lane-ek letrehozasa
        Skeleton.enter("road", "l1", "new Lane()");
        Skeleton.exit("l1:Lane");
        Skeleton.enter("road", "l2", "new Lane()");
        Skeleton.exit("l2:Lane");

        // HomeBase letrehozasa
        Skeleton.enter("gameLogic", "homeBase", "new HomeBase()");
        Skeleton.exit("hb:HomeBase");

        // Terminal letrehozasa
        Skeleton.enter("gameLogic", "terminal", "new Terminal()");
        Skeleton.exit("t:Terminal");

        // GeneralBuilding letrehozasa
        Skeleton.enter("gameLogic", "generalBuilding", "new GeneralBuilding()");
        Skeleton.exit("gb:GeneralBuilding");

        // Szomszedsagi kapcsolatok beallitasa
        Skeleton.enter("gameLogic", "map", "szomszedsagi kapcsolatok beallitasa");
        Skeleton.exit("void");

        // SnowPlow letrehozasa es elhelyezese
        Skeleton.enter("gameLogic", "snowPlow", "new SnowPlow()");
        Skeleton.exit("sp:SnowPlow");
        Skeleton.enter("gameLogic", "homeBase", "acceptSnowPlow(sp)");
        Skeleton.exit("void");

        // Bus letrehozasa es elhelyezese
        Skeleton.enter("gameLogic", "bus", "new Bus()");
        Skeleton.exit("b:Bus");
        Skeleton.enter("gameLogic", "l1", "accept(bus)");
        Skeleton.exit("void");

        // Car letrehozasa es elhelyezese
        Skeleton.enter("gameLogic", "car", "new Car()");
        Skeleton.exit("car:Car");
        Skeleton.enter("gameLogic", "l2", "accept(car)");
        Skeleton.exit("void");

        Skeleton.exit("void");
    }

    /**
     * Eggyel noveli a korszamlalot, vegrehajtja a vegrehajtasi fazist.
     * SD-02: havazas szimulacio — map.snow()
     * SD-16: jatek vege ellenorzes — ha turnCount >= maxTurns: endGame()
     */
    public void advanceTurn() {
        Skeleton.enter("skeleton", "gameLogic", "advanceTurn()");

        turnCount++;

        boolean isGameOver = Skeleton.askQuestion(
                "Elerte a korok szama a maximumot?");

        if (isGameOver) {
            endGame();
        } else {
            // Havazas szimulacio
            if (gameMap != null) {
                gameMap.snow();
            }
        }

        Skeleton.exit("void");
    }

    /**
     * Kezeli az aktiv jatekosok dontesi fazisat.
     */
    public void manageTurn() {
        Skeleton.enter("skeleton", "gameLogic", "manageTurn()");
        Skeleton.exit("void");
    }

    /**
     * Befejezi a jatekot es kiszamitja az eredmenyt.
     * SD-16 alapjan: minden Bus eseten getName() es getCompletedRounds(),
     * minden SnowPlow eseten getName() es getMoney(),
     * vegul evaluateWinners().
     */
    public void endGame() {
        Skeleton.enter("gameLogic", "gameLogic", "endGame()");

        // Buszok eredmenyei — szkeleton: dummy objektumok
        // A tenyleges hivasok a Main-bol jon majd parameterkent
        // itt csak a naplozas tortenik

        Skeleton.exit("void");
    }

    /**
     * Osszesiti az eredmenyeket es kihirdeti a gyoztest.
     * SD-16: legtobb fordulo = buszvezeto gyoztes,
     * legtobb penz = takarito gyoztes.
     */
    public void evaluateWinners() {
        Skeleton.enter("gameLogic", "gameLogic", "evaluateWinners()");
        Skeleton.exit("result");
    }

    /**
     * Beallitja a jatek terkepet.
     *
     * @param map A terkep objektum.
     */
    public void setMap(Map map) {
        this.gameMap = map;
    }
}
