package controller;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import cli.Context;
import model.Bus;
import model.Field;
import model.SnowPlow;
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

        if (actionMode == ActionMode.MOVE && selectedVehicleId != null) {
            // A clickelt mezore mozgas-parancsot adunk ki
            if (fieldId != null) {
                doMove(selectedVehicleId, fieldId);
            }
            actionMode = ActionMode.NONE;
            panel.setMoveModeHint(false);
            return;
        }

        // Egyebkent kijeloles
        if (vv != null) {
            selectedVehicleId = vv.getId();
            selectedFieldId = null;
        } else if (fieldId != null) {
            selectedFieldId = fieldId;
            selectedVehicleId = null;
        } else {
            selectedFieldId = null;
            selectedVehicleId = null;
        }
        panel.setSelectedFieldId(selectedFieldId);
        panel.setSelectedVehicleId(selectedVehicleId);
        mainWindow.onSelectionChanged(selectedVehicleId, selectedFieldId);
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
                bridge.nextTurn();
                break;
            case KeyEvent.VK_ESCAPE:
                selectedFieldId = null;
                selectedVehicleId = null;
                actionMode = ActionMode.NONE;
                panel.setSelectedFieldId(null);
                panel.setSelectedVehicleId(null);
                panel.setMoveModeHint(false);
                mainWindow.onSelectionChanged(null, null);
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
