package main;

/**
 * Az "Uj jatek" pálya-választás opcioi. A WelcomeWindow.MapChooser
 * ezt kerdezi a felhasznalo-tol; a MainWindow.newGame(MapType) ennek
 * megfeleloen a DefaultDemo / MediumDemo / BigDemo egyiket alkalmazza.
 */
public enum MapType {
    /** 1 Cleaner (1 plow) + 1 BusDriver (1 bus), 4-sarku negyzet. */
    EASY,
    /** 1 Cleaner (2 plow) + 2 BusDriver (1 bus mind), 2x3 racs. */
    MEDIUM,
    /** 2 Cleaner (1 plow mind) + 2 BusDriver (1 bus mind), 3x3 racs. */
    HARD
}
