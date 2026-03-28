package gonoszonosz;

/**
 * Speciális hókotró fej, amely a jégpáncél feltörésére specializálódott.
 * A feltört jeget hó formájában az úton hagyja – a csúszásveszély megszűnik,
 * de a sáv havassá válik. Havat nem takarít el, üzemanyagot nem igényel.
 */
public class IcebreakerHead extends CleanerHead {

    /**
     * Létrehoz egy új jégtörő fejet a megadott árral.
     * @param price a fej ára
     */
    public IcebreakerHead(int price) {
        super(price, false);
    }

    /**
     * A sávon lévő jégpáncélt feltöri és havává alakítja; ha nincs jég, nem csinál semmit.
     * A Skeleton megkérdezi a felhasználót, hogy jeges-e a sáv.
     * @param lane a takarítandó sáv
     */
    @Override
    public void clean(Lane lane) {
        Skeleton.call("SnowPlow", "IcebreakerHead", "clean(lane)");
        boolean icy = Skeleton.ask("Jeges a sáv (isFrozen == true)?");
        if (icy) {
            lane.breakIce();
        }
        Skeleton.ret("void");
    }

    /**
     * Visszaadja a fej nevét.
     * @return "IcebreakerHead"
     */
    @Override
    public String getName() {
        return "IcebreakerHead";
    }
}
