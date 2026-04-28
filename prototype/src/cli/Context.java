package cli;

/**
 * Globalis statikus referenciatar a prototipus alkalmazas
 * komponenseihez (ObjectManager, Determinism, GameLogic, parser).
 *
 * Indok: a domain reteg (Vehicle, Lane stb.) reszeben szukseg van
 * az ObjectManager-re (ID lookup esemeny-uzenetekhez) es a
 * Determinism-ra (csuszas-eldontes). A Context statikus mezokkel
 * lehetove teszi ezeket a hivatkozasokat anelkul, hogy minden
 * domain konstruktoraba dependency-injekcios lancot kellene huzni.
 *
 * A ProtoApp.main() inicializalja a peldanyositott referenciakkal.
 * Tesztben felulirhato.
 */
public class Context {

    /** Az aktiv ObjectManager. */
    public static ObjectManager objectManager;

    /** Az aktiv Determinism beallitas. */
    public static Determinism determinism;

    /** Az aktiv GameLogic. Tipusa Object hogy elkeruljuk a
     *  cli -> model csomagok kozotti ciklikus fordito-fuggosegi gondot
     *  -- a hasznalo helyen kell kasztolni. */
    public static Object gameLogic;

    /** Az aktiv CommandParser (a load parancs hasznalja a betoltott
     *  fajl tartalmanak ujrafeldolgozasahoz). Tipusa Object ugyanazert. */
    public static Object commandParser;

    private Context() {
        // statikus osztaly, nem peldanyosithato
    }
}
