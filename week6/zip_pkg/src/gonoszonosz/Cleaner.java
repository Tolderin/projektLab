package gonoszonosz;

import java.util.ArrayList;
import java.util.List;

/**
 * A takarító játékos osztálya.
 * Célja, hogy a hókotró flottájával minél több útszakaszt tisztítson meg,
 * és a bevételekből fejlessze felszerelését.
 */
public class Cleaner extends Player {

    /** A takarítóhoz tartozó hókotrók listája (maximum 3). */
    private List<SnowPlow> snowPlows;

    /** A takarító aktuális pénzmennyisége. */
    private int money;

    /**
     * Létrehoz egy új takarítót a megadott névvel, kezdőtőkével.
     * @param name  a takarító neve
     * @param money a kezdő pénzmennyiség
     */
    public Cleaner(String name, int money) {
        super(name);
        this.snowPlows = new ArrayList<>();
        this.money = money;
    }

    /**
     * Jelzi a takarító körének kezdetét.
     */
    @Override
    public void startTurn() {
        Skeleton.call("GameLogic", "Cleaner", "startTurn()");
        Skeleton.ret("void");
    }

    /**
     * Jelzi a takarító körének végét.
     */
    @Override
    public void endTurn() {
        Skeleton.call("Cleaner", "GameLogic", "endTurn()");
        Skeleton.ret("void");
    }

    /**
     * Levon a takarító pénzéből a megadott összeget (vásárláskor).
     * @param amount a levonandó összeg
     */
    public void deductMoney(int amount) {
        money -= amount;
        score = money;
    }

    /**
     * Hozzáad a takarító pénzéhez (útszakasz megtisztításáért kapott jutalom).
     * @param amount a hozzáadandó összeg
     */
    public void addMoney(int amount) {
        money += amount;
        score = money;
    }

    /**
     * Hozzáad egy hókotrót a takarító flottájához.
     * @param sp a hozzáadandó hókotró
     */
    public void addSnowPlow(SnowPlow sp) {
        snowPlows.add(sp);
    }

    /**
     * Visszaadja a takarító aktuális pénzmennyiségét.
     * @return a pénzmennyiség
     */
    public int getMoney() {
        return money;
    }

    /**
     * Visszaadja a takarítóhoz tartozó hókotrók listáját.
     * @return hókotrók listája
     */
    public List<SnowPlow> getSnowPlows() {
        return snowPlows;
    }
}
