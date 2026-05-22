package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Az osszes allapotvaltozast kozlo modell-osztaly kozos ososztalya.
 * Felelossege a megfigyelok (IObserver) nyilvantartasa es ertesitese,
 * push-alapu Observer minta szerint.
 *
 * Az osztaly nem tud arrol, hogy a megfigyelok grafikus elemek; csak
 * az IObserver interfeszen keresztul kommunikal velük, igy a modell
 * teljes egeszeben elkulonul a megjelenitestol. A modell-reteg
 * konkret osztalyai (Lane, Vehicle, Player, GameLogic, HomeBase,
 * IntegratedMarket) extends Observable a notifyObservers(hint)
 * hivashoz az allapotvaltoztato metodusaik vegen.
 */
public abstract class Observable {

    /** A feliratkozott megfigyelok listaja. */
    private final List<IObserver> observers = new ArrayList<>();

    /**
     * Felvesz egy uj megfigyelot, ha meg nem szerepel a listaban.
     *
     * @param o A felveendo megfigyelo.
     */
    public void addObserver(IObserver o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    /**
     * Kivesz egy megfigyelot a listabol. Ha nincs benne, nincs hatasa.
     *
     * @param o Az eltavolitando megfigyelo.
     */
    public void removeObserver(IObserver o) {
        observers.remove(o);
    }

    /**
     * Sorra ertesiti a megfigyeloket; a hint rovid jelzes a valtozas
     * tipusarol (pl. "snowChanged", "moved"). Snapshot-listan iteral,
     * hogy a hivas alatt feliratkozas vagy leiratkozas ne dobjon
     * ConcurrentModificationException-t.
     *
     * @param hint A valtozas rovid azonositoja.
     */
    protected void notifyObservers(String hint) {
        // Snapshot, mert egy update() implementaciok meghivhatjak az
        // addObserver/removeObserver-t (pl. view leiratkozas).
        List<IObserver> snapshot = new ArrayList<>(observers);
        for (IObserver o : snapshot) {
            o.update(this, hint);
        }
    }
}
