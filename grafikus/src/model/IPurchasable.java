package model;

/**
 * Kozos interfesz minden olyan objektum szamara, amely a jatekbeli
 * boltban (IntegratedMarket) megvasarolhato. Peldaul a CleanerHead
 * leszarmazottjai (SweepHead, ThrowerHead, ...) es a SnowPlow.
 * Biztositja, hogy a bolt egysegesen le tudja kerdezni az arucikkek
 * arat es nevet a tranzakciok lebonyolitasahoz.
 */
public interface IPurchasable {

    /**
     * Visszaadja az adott termek megvasarolasahoz szukseges osszeget.
     *
     * @return A termek ara egesz szamkent.
     */
    int getPrice();

    /**
     * Visszaadja a termek nevet vagy tipusat, amelyet a bolt a
     * listazaskor es a nyugtazaskor hasznal.
     *
     * @return A termek neve.
     */
    String getName();
}
