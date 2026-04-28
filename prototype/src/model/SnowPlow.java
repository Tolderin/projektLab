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
        }
    }

    /**
     * A jelenlegi savon vegrehajtja az aktiv fejre jellemzo
     * takaritast. Csak akkor hat, ha a currentField Lane es van
     * aktiv fej.
     */
    public void cleanCurrentLane() {
        if (activeHead != null && currentField instanceof Lane) {
            activeHead.clean((Lane) currentField);
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
