package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A hokotrot iranyito jatekos. Celja a savok takaritasa, amiert
 * penzjutalmat kap (money attributumban). A megszerzett penzbol a
 * telephelyen (HomeBase) levo boltban (IntegratedMarket) uj
 * tisztitofejeket es uzemanyagot vasarolhat.
 */
public class Cleaner extends Player {

    /** A jatekos iranyitasa alatt allo hokotrok listaja. */
    private final List<SnowPlow> controlledPlows = new ArrayList<>();

    /**
     * Elkezdi a takarito koret. Prototipus modban ures
     * (a parancs-feldolgozas vezerli a logikat).
     */
    @Override
    public void startTurn() {
        // Prototipus mod: a CLI parancsok lepnek a jatekos helyett
    }

    /**
     * Befejezi a takarito koret.
     */
    @Override
    public void endTurn() {
        // Prototipus mod: lasd startTurn()
    }

    /**
     * Penz hozzaadasa sikeres takaritas utan. A SnowPlow vagy a
     * mar takaritott savok pontszamitasa hivja.
     *
     * @param amount A jutalom merteke.
     */
    public void addMoney(int amount) {
        this.money += amount;
    }

    /**
     * Hozzaad egy hokotrot a jatekos flottajahoz, es egyben
     * kolcsonosen rogziti a tulajdonosi kapcsolatot.
     *
     * @param sp Az ujonnan tulajdonba vett hokotro.
     */
    public void addPlow(SnowPlow sp) {
        controlledPlows.add(sp);
        sp.setOwner(this);
    }

    /**
     * Visszaadja a jatekos kontrollalt hokotroinak listajat.
     *
     * @return Hokotrok listaja (modositasok elkerulesere a hivo
     *         olvasasi modban hasznalja).
     */
    public List<SnowPlow> getControlledPlows() {
        return controlledPlows;
    }
}
