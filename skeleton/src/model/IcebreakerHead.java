package model;

import skeleton.Skeleton;

/**
 * A mechanikus jégtörő fej (IcebreakerHead) a sávon kialakult jégpáncél
 * fizikai feltörésére és eltávolítására szolgál.
 * Működéséhez üzemanyag szükséges.
 */
public class IcebreakerHead extends CleanerHead {

    public IcebreakerHead() {
        this.fuelAmount = 100.0; // Kezdeti üzemanyag
    }

    /**
     * Feltöri és eltávolítja a jeget az adott sávról, ha van üzemanyag.
     */
    @Override
    public void clean(Lane l) {
        Skeleton.enter("snowPlow", "icebreakerHead", "clean(l)");

        boolean isFueled = Skeleton.askQuestion("Van elegendő üzemanyag a jégtörőben (fuelAmount > 0)?");

        if (isFueled) {
            Skeleton.enter("icebreakerHead", "l", "breakIce()");
            l.breakIce(); // Vagy removeIce(), attól függően, hogy az analízisben melyiket terveztétek a
                          // sávnak
            Skeleton.exit("void");

            this.fuelAmount -= 25.0; // Fogyasztás
        } else {
            Skeleton.enter("icebreakerHead", "Skeleton", "A fej Empty állapotban van, nem csinál semmit.");
            Skeleton.exit("");
        }

        Skeleton.exit("void");
    }

    @Override
    public int getPrice() {
        Skeleton.enter("Hívó", "icebreakerHead", "getPrice()");
        Skeleton.exit("3500");
        return 3500;
    }

    @Override
    public String getName() {
        Skeleton.enter("Hívó", "icebreakerHead", "getName()");
        Skeleton.exit("Jégtörő");
        return "Jégtörő";
    }
}