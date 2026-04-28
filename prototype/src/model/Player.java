package model;

/**
 * A jatekos absztrakt osztaly, a Cleaner es a BusDriver kozos
 * ososztalya. Tarolja az alapveto tulajdonsagokat: nev, pontszam,
 * penz. A konkret leszarmazottak felulbiraljak a startTurn() es
 * endTurn() metodusokat a sajat dontesi logikajuk szerint.
 */
public abstract class Player {

    /** A jatekos neve (jellemzoen az ID-val megegyezik). */
    public String name;

    /** A jatekos aktualis pontszama. */
    public int score = 0;

    /** A jatekos aktualis penze. */
    public double money = 0.0;

    /**
     * Elkezdi az adott jatekos koret. A prototipus reteg most
     * automatizalt parancs-feldolgozasi modot hasznal, ezert ez
     * egy ures hely-kitolto (a CLI parancsok lepnek a jatekos
     * helyett).
     */
    public abstract void startTurn();

    /**
     * Befejezi az adott jatekos koret. Lasd startTurn() megjegyzes.
     */
    public abstract void endTurn();
}
