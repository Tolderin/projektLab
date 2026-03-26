package skeleton;

import java.util.Scanner;

/**
 * A szkeleton program központi naplózó és beolvasó osztálya.
 * Célja a tervezett analízis modell belső üzenetváltásainak (metódushívásainak)
 * ellenőrzése interaktív teszteseteken keresztül.
 */
public class Skeleton {

    /** A hívási verem mélysége, a megfelelő tabulátoros behúzáshoz. */
    private static int depth = 0;

    /** A szabványos bemenetet olvasó Scanner objektum. */
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Naplózza egy metódushívás kezdetét a megfelelő behúzással.
     * Formátum: [Hívó_Objektum] -> [Hívott_Objektum].metódusNév(paraméterek)
     *
     * @param caller A hívó objektum neve.
     * @param callee A hívott objektum neve.
     * @param method A meghívott metódus neve és paraméterei.
     */
    public static void enter(String caller, String callee, String method) {
        printIndent();
        System.out.println("[" + caller + "] -> [" + callee + "]." + method);
        depth++;
    }

    /**
     * Naplózza a visszatérést egy metódusból.
     * Formátum: <-- visszatérési érték
     *
     * @param returnValue A metódus visszatérési értéke (vagy "void").
     */
    public static void exit(String returnValue) {
        depth--;
        printIndent();
        System.out.println("<-- " + returnValue);
    }

    /**
     * Feltesz egy Igen/Nem kérdést a tesztelőnek (felhasználónak), és visszaadja a
     * választ.
     * Formátum: ? [Kérdés szövege] (I/N):
     *
     * @param question A felteendő kérdés szövege.
     * @return true, ha a válasz 'I', false, ha 'N' (vagy bármi más).
     */
    public static boolean askQuestion(String question) {
        System.out.print("? " + question + " (I/N): ");
        String answer = scanner.nextLine().trim().toUpperCase();
        return answer.equals("I");
    }

    /**
     * Segédmetódus a behúzások (tabulátorok) kiíratásához a konzolra.
     */
    private static void printIndent() {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
    }
}