package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A takarito jatekos altal iranyitott munkagep. A raszerelt fej
 * segitsegevel takaritja a savokat, penzt termel a takaritonak.
 * Holancai miatt jegen sem csuszik (a slip() ures az osztalyban).
 *
 * Allapotok es atmenetek:
 * - AtHomeBase (Telephelyen): a telephelyen all, fejcsere es
 *   uzemanyag-utantoltes engedélyezett.
 * - OnRoad (Uton): uton halad, ezek a muveletek [ERROR]-t adnak.
 *   Az atmenetet a move() utani currentField tipusa hatarozza meg.
 */
public class SnowPlow extends Vehicle implements IPurchasable {

    /** A jelenleg birtokolt hokotro fejek listaja. */
    public final List<CleanerHead> attachments = new ArrayList<>();

    /** A jelenleg felszerelt hokotro fej. */
    public CleanerHead activeHead;

    /** Privat: a jarmu boltban lathato megvasarlasi ara. */
    private int price = 100;

    /** Privat: a hokotro azonosito neve. */
    private String name;

    /** A jarmu tulajdonosa, aki a donteseket hozza es a jutalmat kapja. */
    private Cleaner owner;

    /**
     * Round-cadence flag: true, ha a Cleaner mar lepett ezzel a
     * hokotrovel az aktualis korben. A GameLogic.advanceTurn() a
     * kor elejen visszaallitja false-ra, a MovePlowCommand pedig
     * sikeres lepes utan true-ra. Csak akkor van hatasa, ha a
     * GameLogic.roundCadenceEnabled = true.
     */
    public boolean hasMovedThisTurn = false;

    /**
     * Mozgatja a hokotrot a tervezett utvonal szerint.
     * remove(this) -> accept(this) -> cleanCurrentLane().
     */
    @Override
    public void move() {
        if (nextField != null) {
            currentField.remove(this);
            nextField.accept(this);
            currentField = nextField;
            cleanCurrentLane();
            nextField = null;
            notifyObservers("moved");
        }
    }

    /**
     * Kicsereli az aktiv fejet a megadottra. Csak akkor hat, ha a
     * megadott fej a tarolt attachments-ben szerepel (nincs varazslat:
     * eloszor meg kell venni a boltban, ami a buy parancs alatt
     * felveszi a listara).
     *
     * @param newHead Az uj felszerelendo fej.
     */
    public void changeHead(CleanerHead newHead) {
        if (attachments.contains(newHead)) {
            this.activeHead = newHead;
            notifyObservers("headSwapped");
        }
    }

    /**
     * A jelenlegi savon vegrehajtja az aktiv fejre jellemzo
     * takaritast. Csak akkor hat, ha a currentField Lane es van
     * aktiv fej.
     *
     * A 13. heti bovites: a takaritas elott/utan snapshot-olja a
     * sav allapotat, kiszamolja az eltavolitott ho mennyiseget es
     * azt, hogy a fej feltorte-e a jeget; majd a tulajdonos Cleaner-
     * nek penzt ad (snowRemoved * 10, ice-broken bonusz +25).
     */
    public void cleanCurrentLane() {
        if (activeHead == null || !(currentField instanceof Lane)) {
            return;
        }
        Lane lane = (Lane) currentField;
        double snowBefore = lane.snowDepth;
        boolean iceBefore = lane.isFrozen;
        activeHead.clean(lane);
        double snowAfter = lane.snowDepth;
        boolean iceAfter = lane.isFrozen;
        double removed = Math.max(0.0, snowBefore - snowAfter);
        int reward = (int) Math.round(removed * 10.0);
        if (iceBefore && !iceAfter) {
            reward += 25;
        }
        if (reward > 0 && owner != null) {
            owner.addMoney((double) reward);
        }
    }

    /**
     * Visszaadja a hokotro arat (a boltban valo megvasarlashoz).
     *
     * @return Az ar.
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Visszaadja a hokotro nevet.
     *
     * @return A nev.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Beallitja a hokotro nevet (a spawn parancs hivja).
     *
     * @param name Az uj nev.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Beallitja a tulajdonost (a Cleaner.addPlow() hivja).
     *
     * @param c A tulajdonos.
     */
    public void setOwner(Cleaner c) {
        this.owner = c;
    }

    /**
     * Visszaadja a tulajdonost.
     *
     * @return A Cleaner-tulajdonos.
     */
    public Cleaner getOwner() {
        return owner;
    }
}
