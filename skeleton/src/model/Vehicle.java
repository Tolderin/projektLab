package model;

/**
 * A jatekban szereplo osszes jarmu absztrakt ososztalya.
 * Tarolja a jarmu aktualis savjat es a tervezett kovetkezo mezot,
 * es eloirja a move() metodust.
 */
public abstract class Vehicle {

    /** A jarmu aktualis savja. */
    protected Lane currentLane;

    /** A tervezett kovetkezo mezo. */
    protected Field nextField;

    /**
     * A jarmu mozgatasa a jelenlegi helyzetrol a kovetkezo tervezett mezore.
     * Minden konkret jarmunek (Car, Bus, SnowPlow) felul kell definialnia.
     */
    public abstract void move();

    /**
     * Beallitja a jarmu aktualis savjat.
     * A szkeleton tesztesetekhez szukseges az objektumok osszekotesehez.
     *
     * @param lane Az aktualis sav.
     */
    public void setCurrentLane(Lane lane) {
        this.currentLane = lane;
    }
}
