package model;

import java.util.ArrayList;
import java.util.List;
import skeleton.Skeleton;

/**
 * A játék menetét vezérlő osztály[cite: 1314].
 * Felelőssége: a körök szervezése (tervezési fázis -> végrehajtási fázis),
 * az autók navigálásának indítása, a játék végének detektálása és az eredmény
 * összesítése[cite: 1314].
 * A havazás szimulálása a Map.snow() metóduson keresztül történik[cite: 1315].
 */
public class GameLogic {
    private Map gameMap;
    // A Player és Vehicle listákat most dummy objektumként kezeljük
    private int turnCount = 0;
    private int maxTurns = 10;

    /** Inicializálja és elindítja a játékot[cite: 1326]. */
    public void startGame() {
        Skeleton.enter("Skeleton", "gameLogic", "startGame()");

        gameMap = new Map();
        Skeleton.enter("gameLogic", "Map", "new Map()");
        Skeleton.exit("m:Map");

        // Ide kerülhet a többi SD-01 inicializációs lépés (Road, HomeBase, Terminal
        // stb. létrehozása)

        Skeleton.exit("void");
    }

    /**
     * Eggyel növeli a körszámlálót, végrehajtja a végrehajtási fázist[cite: 1327].
     */
    public void advanceTurn() {
        Skeleton.enter("Skeleton", "gameLogic", "advanceTurn()");

        turnCount++;
        boolean isGameOver = Skeleton.askQuestion("Elérte a körök száma a maximumot?");

        if (isGameOver) {
            endGame();
        } else {
            // Ha nincs vége, akkor pl. havazás szimuláció jön
            if (gameMap != null) {
                gameMap.snow();
            }
        }

        Skeleton.exit("result");
    }

    /** Kezeli az aktív játékosok döntési fázisát[cite: 1328]. */
    public void manageTurn() {
        Skeleton.enter("Skeleton", "gameLogic", "manageTurn()");
        Skeleton.exit("void");
    }

    /** Befejezi a játékot és kiszámítja az eredményt[cite: 1329]. */
    public void endGame() {
        Skeleton.enter("gameLogic", "gameLogic", "endGame()");
        // Itt történne az eredmények összesítése (buszvezetők completedRounds,
        // takarítók money)
        Skeleton.exit("void");
    }
}