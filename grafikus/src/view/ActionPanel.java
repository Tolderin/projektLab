package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import controller.ActionMode;
import controller.CommandBridge;
import controller.InputController;

/**
 * Az ablak aljan elhelyezett akcio-gombsor (Move, Buy, Equip,
 * Next turn, Save, Stat). A gombokra kotott ActionListener-ek a
 * CommandBridge megfelelo metodusait hivjak; a Move es a Buy a
 * GUI-belso modot is allitja (kek keret-overlay-eket aktival).
 */
public class ActionPanel extends JPanel {

    /** A kapcsolodo CommandBridge a parancs-kiadashoz. */
    private final CommandBridge bridge;

    /** Az InputController a kijeloles olvasasahoz / mod allitasahoz. */
    private final InputController input;

    /** A befoglalo MainWindow referencia (Buy dialog megnyitasahoz). */
    private final MainWindow mainWindow;

    /**
     * Letrehoz egy ActionPanel-t.
     *
     * @param bridge     A CommandBridge a parancs-kiadashoz.
     * @param input      Az InputController.
     * @param mainWindow A befoglalo MainWindow.
     */
    public ActionPanel(CommandBridge bridge, InputController input, MainWindow mainWindow) {
        this.bridge = bridge;
        this.input = input;
        this.mainWindow = mainWindow;
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        setBackground(new Color(40, 45, 55));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        // A 13. heti turn-order rendszerben a "Move" mar nem szukseges:
        // az autoSelectNextVehicle automatikusan MOVE modba allitja a
        // jelenlegi vehicle-t, a kek highlightolt mezo-kattintas
        // azonnal mozgat es atvalt a kovetkezo aktualis vehicle-re.
        add(makeButton("Buy",       e -> onBuyClick()));
        add(makeButton("Equip",     e -> onEquipClick()));
        add(makeButton("Skip turn", e -> onNextTurnClick()));
        add(makeButton("End game",  e -> mainWindow.endGameNow()));
        add(makeButton("Save",      e -> onSaveClick()));
        add(makeButton("Stat",      e -> onStatClick()));
    }

    /**
     * Letrehoz egy egyseges stilusu JButton-t.
     *
     * @param label A gomb felirata.
     * @param l     Az ActionListener.
     * @return Az uj gomb.
     */
    private JButton makeButton(String label, java.awt.event.ActionListener l) {
        JButton b = new JButton(label);
        b.setFocusable(false);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.addActionListener(l);
        return b;
    }

    /**
     * Buy-mod: ha a kijelolt SnowPlow HomeBase-en all, megnyitja a
     * MarketDialog-ot; egyebkent hibauzenetet ad.
     */
    public void onBuyClick() {
        String plowId = input.getSelectedVehicleId();
        if (plowId == null) {
            mainWindow.showMessage("Eloszor valassz ki egy hokotrot.");
            return;
        }
        Object o = cli.Context.objectManager.getObject(plowId);
        if (!(o instanceof model.SnowPlow)) {
            mainWindow.showMessage("A kijelolt jarmu nem hokotro.");
            return;
        }
        model.SnowPlow sp = (model.SnowPlow) o;
        if (!(sp.currentField instanceof model.HomeBase)) {
            mainWindow.showMessage("A hokotrot a HomeBase-re kell vinned a vasarlashoz.");
            return;
        }
        mainWindow.openMarketDialog(sp);
    }

    /**
     * Equip: a kijelolt SnowPlow attachments-eibol egy popup-menus
     * dialog ban (JOptionPane.showInputDialog) lehet valasztani.
     */
    public void onEquipClick() {
        String plowId = input.getSelectedVehicleId();
        if (plowId == null) {
            mainWindow.showMessage("Eloszor valassz ki egy hokotrot.");
            return;
        }
        Object o = cli.Context.objectManager.getObject(plowId);
        if (!(o instanceof model.SnowPlow)) {
            mainWindow.showMessage("A kijelolt jarmu nem hokotro.");
            return;
        }
        model.SnowPlow sp = (model.SnowPlow) o;
        if (!(sp.currentField instanceof model.HomeBase)) {
            mainWindow.showMessage("Equip csak HomeBase-en lehetseges.");
            return;
        }
        java.util.List<String> names = new java.util.ArrayList<>();
        for (model.CleanerHead h : sp.attachments) {
            names.add(h.getName());
        }
        if (names.isEmpty()) {
            mainWindow.showMessage("Nincs felszerelheto fej az attachments-ben.");
            return;
        }
        String[] options = names.toArray(new String[0]);
        String pick = (String) javax.swing.JOptionPane.showInputDialog(
                mainWindow, "Valassz fejet:", "Equip",
                javax.swing.JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (pick != null) {
            bridge.equipHead(plowId, pick);
        }
    }

    /**
     * "Skip turn": kihagyja a jelenlegi aktualis jarmu kor-lepeset
     * es atall a kovetkezo aktualis vehicle-re. Ha az utolso jarmu
     * is kimaradt, a kornyezet kor automatikusan vegrehajtodik (lasd
     * InputController.skipCurrentVehicleAndAdvance).
     */
    public void onNextTurnClick() {
        input.skipCurrentVehicleAndAdvance();
    }

    /**
     * Save-parancs (a save() a meglevo implementacioban consoleba ir).
     */
    public void onSaveClick() {
        bridge.save();
        mainWindow.showMessage("Mentes parancs kiadva (lasd konzol).");
    }

    /**
     * Stat: a kijelolt objektum [STATE] kimenetet egy JOptionPane-ben
     * mutatja.
     */
    public void onStatClick() {
        String id = input.getSelectedVehicleId();
        if (id == null) {
            id = input.getSelectedFieldId();
        }
        if (id == null) {
            mainWindow.showMessage("Eloszor valassz ki egy objektumot.");
            return;
        }
        String out = bridge.stat(id);
        if (out == null || out.isEmpty()) {
            out = "(nincs allapot-kimenet)";
        }
        javax.swing.JOptionPane.showMessageDialog(mainWindow, out,
                "Stat: " + id, javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
