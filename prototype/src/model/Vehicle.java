package model;

/**
 * A jatekban szereplo osszes jarmu absztrakt ososztalya. Tarolja a
 * jarmu aktualis mezojet es a tervezett kovetkezo mezot, es eloirja
 * a move() metodust. A slip() es collideWith() default ures
 * implementacioval rendelkezik -- a Bus es Car felulbiraljak;
 * a SnowPlow holancai miatt megorzi az ures viselkedest.
 */
public abstract class Vehicle {

    /** A jarmu aktualis mezoje (lehet Lane vagy Building). */
    public Field currentField;

    /** A tervezett kovetkezo mezo (a kovetkezo move() alatt fog
     *  a jarmu odakerulni). null ha nincs tervezett lepes. */
    public Field nextField;

    /**
     * A jarmu mozgatasa a jelenlegi helyzetrol a kovetkezo
     * tervezett mezore. Minden konkret leszarmazott (Car, Bus,
     * SnowPlow) felulbiralja a sajat logikajaval.
     */
    public abstract void move();

    /**
     * Jegen torteno megcsuszas kezelese. Default ures
     * (a SnowPlow holancai miatt jegen sem csuszik).
     * A Bus es a Car felulbiralja.
     *
     * @param onLane A sav, amelyiken a csuszas megtortent. Erre
     *               vonatkozik a setBlocked es az [EVENT] uzenet
     *               -- a hivo szempontjabol ez vagy az erkezesi
     *               cel-Lane (Lane.accept-bol), vagy a jelenlegi
     *               sav (NextTurnCommand force-slip fazisabol).
     */
    public void slip(Lane onLane) {
        // default: nem csuszik
    }

    /**
     * Utkozes egy masik jarmuvel. Default ures implementacio.
     *
     * @param v A masik jarmu.
     */
    public void collideWith(Vehicle v) {
        // default: nem reagal
    }
}
