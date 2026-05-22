package controller;

/**
 * Az InputController altal hasznalt akcio-modok felsorolasa.
 * Az aktualis mod befolyasolja, hogy egy egerkattintas vagy
 * billentyu lenyomas hogyan ertelmezodik. A felhasznalo az
 * ActionPanel gombjaival lephet at egy-egy modba; az ESC
 * visszaallit NONE-ra.
 */
public enum ActionMode {

    /**
     * Semmilyen aktiv akcio nincs kivalasztva. A kattintas csak
     * mezo- vagy jarmu-kijelolesre szolgal.
     */
    NONE,

    /**
     * Mozgas-mod: a kovetkezo kattintas egy szomszedos mezon
     * elinditja a kijelolt jarmu mozgatasat.
     */
    MOVE
}
