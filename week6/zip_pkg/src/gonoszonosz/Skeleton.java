package gonoszonosz;

import java.util.Scanner;

/**
 * A szkeleton program központi naplózó és vezérlő osztálya.
 * Felelős a metódushívások konzolra írásáért (behúzással), valamint
 * az I/N típusú felhasználói kérdések kezeléséért.
 * A kimenet egyértelműen összevethető az analízis modell szekvenciadiagramjaival.
 */
public class Skeleton {

    /** Az aktuális behúzási mélység (hívási verem mélysége). */
    private static int depth = 0;

    /** Bemeneti olvasó a felhasználói válaszokhoz. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Naplóz egy metódushívást a jelenlegi behúzási szinten,
     * majd növeli a mélységet a beágyazott hívásokhoz.
     * Formátum: [HívóOsztály] -> [HívottOsztály].metódusNév(paraméterek)
     *
     * @param caller  a hívó objektum osztályának neve
     * @param callee  a hívott objektum osztályának neve
     * @param method  a hívott metódus neve és paraméterei (pl. "move()")
     */
    public static void call(String caller, String callee, String method) {
        System.out.println(indent() + "[" + caller + "] -> [" + callee + "]." + method);
        depth++;
    }

    /**
     * Naplóz egy visszatérést az aktuális behúzási szintről,
     * majd csökkenti a mélységet.
     * Formátum: <-- visszatérési_érték
     *
     * @param returnValue a visszatérési érték szöveges reprezentációja (pl. "void", "true")
     */
    public static void ret(String returnValue) {
        depth--;
        System.out.println(indent() + "<-- " + returnValue);
    }

    /**
     * Feltesz egy I/N kérdést a felhasználónak a szkeletonon belüli elágazás eldöntéséhez.
     * A kérdés formátuma: ? [kérdés szövege] (I/N):
     *
     * @param question a felhasználónak feltett kérdés szövege
     * @return true, ha a felhasználó 'I'-t adott meg, false ha 'N'-t
     */
    public static boolean ask(String question) {
        System.out.print(indent() + "? " + question + " (I/N): ");
        String answer = scanner.nextLine().trim().toUpperCase();
        return answer.equals("I");
    }

    /**
     * Visszaadja az aktuális behúzási szintnek megfelelő szóköz-sorozatot.
     * Minden szint 2 szóköznek felel meg.
     *
     * @return a behúzást reprezentáló szöveg
     */
    private static String indent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    /**
     * Visszaállítja a behúzási mélységet nullára.
     * Teszt-szekvenciák közötti tisztításhoz használatos.
     */
    public static void resetDepth() {
        depth = 0;
    }
}
