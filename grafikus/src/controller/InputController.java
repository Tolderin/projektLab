package controller;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import cli.Context;
import model.Bus;
import model.Field;
import model.GameLogic;
import model.SnowPlow;
import model.Vehicle;
import view.GamePanel;
import view.MainWindow;
import view.VehicleView;

/**
 * A GamePanel egér- es billentyu-esemenyeinek kezelője.
 * Felelős: a kattintási koordináta alapján kiválasztani a fókuszált
 * pálya-objektumot vagy járművet; az aktuális ActionMode-ot
 * figyelembe venni; majd a CommandBridge-en keresztül a parancsot
 * kiadni a modellnek.
 *
 * Billentyűk:
 *  - SPACE: next_turn
 *  - ESC: kijelolés torlése + ActionMode.NONE
 *  - F1: rovid sugo dialog
 */
public class InputController implements MouseListener, KeyListener {

    /** A GamePanel, ahonnan az esemenyek erkeznek. */
    private final GamePanel panel;

    /** A parancs-bridge. */
    private final CommandBridge bridge;

    /** A befoglalo MainWindow. */
    private final MainWindow mainWindow;

    /** A jelenlegi kijelolt mezo ID-ja, vagy null. */
    private String selectedFieldId;

    /** A jelenlegi kijelolt jarmu ID-ja, vagy null. */
    private String selectedVehicleId;

    /** Aktualis akcio-mod. */
    private ActionMode actionMode = ActionMode.NONE;

    /**
     * Letrehoz egy InputController-t.
     *
     * @param panel      A megfigyelt GamePanel.
     * @param bridge     A CommandBridge.
     * @param mainWindow A befoglalo MainWindow.
     */
    public InputController(GamePanel panel, CommandBridge bridge, MainWindow mainWindow) {
        this.panel = panel;
        this.bridge = bridge;
        this.mainWindow = mainWindow;
    }

    /**
     * Visszaadja a kijelolt mezo ID-jat.
     *
     * @return Az ID, vagy null.
     */
    public String getSelectedFieldId() {
        return selectedFieldId;
    }

    /**
     * Visszaadja a kijelolt jarmu ID-jat.
     *
     * @return Az ID, vagy null.
     */
    public String getSelectedVehicleId() {
        return selectedVehicleId;
    }

    /**
     * Beallitja az aktualis akcio-modot.
     *
     * @param m Az uj mod.
     */
    public void setActionMode(ActionMode m) {
        this.actionMode = m;
    }

    /**
     * Visszaadja az aktualis akcio-modot.
     *
     * @return A mod.
     */
    public ActionMode getActionMode() {
        return actionMode;
    }

    /**
     * Egerkattintas kezelese. Bal-kattintas: kijelolés vagy
     * move parancs. Jobb-kattintas: stat parancs az adott objektumra.
     *
     * @param e A kattintas esemenye.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        boolean isRight = javax.swing.SwingUtilities.isRightMouseButton(e);
        // Eloszor megnezzuk, hogy egy jarmuvet talalunk-e (a "felso"
        // szinten); ha nem, csak akkor probaljuk a mezok kozotti
        // kijelolest.
        VehicleView vv = panel.pickVehicle(p);
        String fieldId = panel.getRenderer().getLayout().pickFieldId(p);

        if (isRight) {
            // Stat parancs a talalt objektumra
            String target = (vv != null) ? vv.getId() : fieldId;
            if (target != null) {
                String out = bridge.stat(target);
                javax.swing.JOptionPane.showMessageDialog(panel, out,
                        "Stat: " + target, javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }

        // A 13. heti turn-order rendszerben az auto-select mindig
        // MOVE modba allitja az aktualis vehicle-t. Bal kattintas
        // egyetlen feladata: ha highlightolt szomszedos mezore
        // kattintunk, mozgatjuk az aktualis jarmuvet es atvaltunk
        // a kovetkezo turn-slotra (autoSelectNextVehicle a doMove
        // vegen). Hibas (nem szomszedos) mezo eseten a CLI [ERROR]-t
        // ad de a MOVE mod marad. Jobb-kattintas: stat (inspekcio).
        if (actionMode == ActionMode.MOVE && selectedVehicleId != null
                && fieldId != null) {
            doMove(selectedVehicleId, fieldId);
            return;
        }
        // Egyebkent (nincs aktiv vehicle, vagy ures kattintas):
        // szandekosan nincs hatas. A felhasznalo a Skip turn gombbal
        // ugorhat a kovetkezo aktualis vehicle-re, a jobb-kattintasok
        // pedig a stat-dialogot nyitjak.
    }

    /**
     * Vegrehajtja a mozgas parancsot a megfelelo CommandBridge-
     * metoduson keresztul, az utan repaint.
     *
     * @param vehicleId A jarmu ID-ja.
     * @param targetId  A celmezo ID-ja.
     */
    private void doMove(String vehicleId, String targetId) {
        Object v = Context.objectManager.getObject(vehicleId);
        Object target = Context.objectManager.getObject(targetId);
        if (!(target instanceof Field)) {
            return;
        }
        boolean isPlow = (v instanceof SnowPlow);
        boolean isBus = (v instanceof Bus);
        if (!isPlow && !isBus) {
            return;
        }
        bridge.moveSelectedTo(vehicleId, targetId, isPlow);
        // Streamlined experience: a lepes utan (es az esetleges
        // CommandBridge-altal kivaltott auto-next_turn utan) a
        // kovetkezo "tovabb-mozdithato" jarmuvet automatikusan
        // kijeloljuk MOVE modban, hogy a jatekos azonnal kattinthat
        // a cel-mezore.
        autoSelectNextVehicle();
    }

    /**
     * A 13. heti turn-order: a GameLogic.getCurrentTurnVehicle-tol
     * kapja meg a kovetkezo lepheto jarmuvet (Cleaner-osszes-plow
     * elobb, BusDriver-osszes-bus utana, players regisztracios
     * sorrendjeben). Ha nincs ilyen, a kornyezet kor automatikusan
     * vegrehajtodik (bridge.nextTurn) es utana ujra megnezzuk.
     *
     * Akkor hivjuk:
     *  - jatek inditasakor (MainWindow.rebindAll),
     *  - sikeres lepes utan (doMove),
     *  - Skip turn / SPACE / next_turn utan (skipCurrentVehicleAndAdvance).
     */
    public void autoSelectNextVehicle() {
        if (!(Context.gameLogic instanceof GameLogic)) {
            clearSelection();
            return;
        }
        GameLogic gl = (GameLogic) Context.gameLogic;
        Vehicle v = gl.getCurrentTurnVehicle();
        if (v == null) {
            // Mindenki lepett -> kornyezet kor automatikusan vegre-
            // hajtodik (advanceTurn nullazza a hasMovedThisTurn flageket).
            bridge.nextTurn();
            v = gl.getCurrentTurnVehicle();
        }
        if (v == null) {
            // Tovabbra is nincs lepheto jarmu (pl. game over). Toroljuk
            // a kijelolest.
            clearSelection();
            return;
        }
        String nextId = Context.objectManager.getId(v);
        if (nextId == null) {
            clearSelection();
            return;
        }
        selectedVehicleId = nextId;
        selectedFieldId = null;
        actionMode = ActionMode.MOVE;
        panel.setSelectedFieldId(null);
        panel.setSelectedVehicleId(nextId);
        panel.setMoveModeHint(true);
        mainWindow.onSelectionChanged(nextId, null);
        panel.repaint();
    }

    /**
     * Eltavolitja az aktualis kijelolest, kikapcsolja a MOVE mod
     * highlightot. Atmeneti allapot pl. game-over utan.
     */
    private void clearSelection() {
        selectedVehicleId = null;
        selectedFieldId = null;
        actionMode = ActionMode.NONE;
        panel.setSelectedFieldId(null);
        panel.setSelectedVehicleId(null);
        panel.setMoveModeHint(false);
        mainWindow.onSelectionChanged(null, null);
        panel.repaint();
    }

    /**
     * Kihagyja a jelenlegi aktualis jarmu kor-lepeset es atvalt a
     * kovetkezo turn-slotra. Ha nincs tobb lepheto jarmu, a kornyezet
     * kor automatikusan vegrehajtodik (autoSelectNextVehicle gondoskodik
     * errol).
     *
     * Hivjak: SPACE / Skip turn gomb / Game menu Next Turn.
     */
    public void skipCurrentVehicleAndAdvance() {
        if (Context.gameLogic instanceof GameLogic) {
            GameLogic gl = (GameLogic) Context.gameLogic;
            Vehicle current = gl.getCurrentTurnVehicle();
            if (current instanceof SnowPlow) {
                ((SnowPlow) current).hasMovedThisTurn = true;
            } else if (current instanceof Bus) {
                ((Bus) current).hasMovedThisTurn = true;
            }
        }
        autoSelectNextVehicle();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        panel.requestFocusInWindow();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Billentyu-lenyomas kezelese.
     *
     * @param e Az esemeny.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                // SPACE = "Skip turn" a 13. heti turn-rendben: kihagyja
                // a jelenlegi aktualis jarmu lepeset, automatikusan
                // valt a kovetkezo turn-slotra (es ha mindenki kihagyott,
                // a kornyezet kor is vegrehajtodik).
                skipCurrentVehicleAndAdvance();
                break;
            case KeyEvent.VK_ESCAPE:
                // A 13. heti turn-rendben az ESC visszaall a turn-order
                // szerinti aktualis vehicle MOVE-mod kijelolesére (a
                // manualis "clear selection" kevesbé hasznos, mivel
                // az auto-select azonnal visszairna).
                autoSelectNextVehicle();
                break;
            case KeyEvent.VK_F1:
                mainWindow.showHelp();
                break;
            default:
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
