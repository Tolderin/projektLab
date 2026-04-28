package cli;

/**
 * Egy futtathato CLI parancs interfesze. Minden konkret bemeneti
 * parancs (pl. MoveBus, Load, Spawn) ezt valositja meg, igy a
 * CommandParser egysegesen, a kod modositasa nelkul tud uj
 * parancsokat regisztralni es futtatni.
 */
public interface ICommand {

    /**
     * Vegrehajtja a parancsot a kapott string parameter tomb alapjan.
     * Az args[0] mindig a parancs neve.
     *
     * @param args A parancs es a parameterek (szokoz alapjan darabolva).
     */
    void execute(String[] args);
}
