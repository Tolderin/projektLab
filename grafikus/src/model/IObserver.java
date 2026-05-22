package model;

/**
 * Kozos interfesz minden olyan komponensnek, amely modell-elemek
 * allapotvaltozasairol kivan ertesulni. A view-reteg implementalja:
 * a megvalosito tipikusan a forras-objektum referenciajat tarolja,
 * es a frissiteskor abbol kerdezi le a kirajzolashoz szukseges
 * attributumokat.
 *
 * Az interfesz a modell-rétegben helyezkedik el, hogy az Observable
 * ososztalynak ne kelljen a view csomagra forditasi-fuggosegben lennie.
 */
public interface IObserver {

    /**
     * Az Observable.notifyObservers hivja minden feliratkozottra,
     * amikor az allapota megvaltozik.
     *
     * @param source A valtozas forrasa (a megfigyelt objektum).
     * @param hint   Rovid azonosito a valtozas tipusarol
     *               (pl. "snowChanged", "moved", "moneyChanged").
     */
    void update(Observable source, String hint);
}
