package skeleton;

import java.util.Scanner;

/**
 * A szkeleton program kozponti naplozo es beolvaso osztalya.
 * Celja a tervezett analizis modell belso uzenetvaltasainak (metodushivasainak)
 * ellenorzese interaktiv teszteseteken keresztul.
 *
 * A kimenet formatuma a szekvenciadiagramok mintajat koveti:
 * - Hivas: [Hivo_Objektum] -> [Hivott_Objektum].metodusNev(parameterek)
 * - Visszateres: &lt;-- visszateresi_ertek
 * - Behuzas: "| " karakterekkel jelzi a hivasi verem melyseget.
 * - Kerdes: ? [Kerdes szovege] (I/N):
 */
public class Skeleton {

    /** A hivasi verem melysege, a megfelelo vizualis behuzashoz. */
    private static int depth = 0;

    /** A szabvanyos bemenetet olvaso Scanner objektum. */
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Naplozza egy metodushivas kezdetet a megfelelo behuzassal.
     * Formatum: [Hivo_Objektum] -> [Hivott_Objektum].metodusNev(parameterek)
     *
     * @param caller A hivo objektum neve.
     * @param callee A hivott objektum neve.
     * @param method A meghivott metodus neve es parameterei.
     */
    public static void enter(String caller, String callee, String method) {
        printIndent();
        System.out.println("[" + caller + "] -> [" + callee + "]." + method);
        depth++;
    }

    /**
     * Naplozza a visszaterest egy metodusbol.
     * Formatum: &lt;-- visszateresi_ertek
     *
     * @param returnValue A metodus visszateresi erteke (vagy "void").
     */
    public static void exit(String returnValue) {
        depth--;
        printIndent();
        System.out.println("<-- " + returnValue);
    }

    /**
     * Feltesz egy Igen/Nem kerdest a tesztelonek (felhasznalonak),
     * es visszaadja a valaszt.
     * Formatum: ? [Kerdes szovege] (I/N):
     *
     * @param question A felteendo kerdes szovege (ekezet nelkul).
     * @return true, ha a valasz 'I', false, ha 'N' (vagy barmi mas).
     */
    public static boolean askQuestion(String question) {
        printIndent();
        System.out.print("? " + question + " (I/N): ");
        String answer = scanner.nextLine().trim().toUpperCase();
        return answer.equals("I");
    }

    /**
     * Visszaadja a kozos Scanner objektumot.
     * A Main es a Skeleton ugyanazt a Scanner-t hasznalja,
     * elkerulendo a ket Scanner altal okozott input-utkozes problema.
     *
     * @return A kozos Scanner objektum.
     */
    public static Scanner getScanner() {
        return scanner;
    }

    /**
     * Segedmetodus a behuzasok kiiratasahoz a konzolra.
     * "| " (cso es szokozok) hasznalata a tabulator helyett,
     * igy sokkal atlathatobb es hasonlit az UML szekvenciadiagramok
     * eletvonalaira.
     */
    private static void printIndent() {
        for (int i = 0; i < depth; i++) {
            System.out.print("|  ");
        }
    }
}
