package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controller.CommandBridge;
import main.MapType;

/**
 * Az alkalmazás belépő képernyője. Négy menüpont: New Game, Load Game,
 * Settings, Exit. A választás után dispose-olja önmagát és átadja a
 * vezérlést a MainWindow-nak (Settings / Exit kivételével).
 *
 * Indulasakor a MainApp.main() ezt az ablakot mutatja először, a CLI
 * argumentum-betöltés (run.bat config.txt) MVP fázisban szándékosan
 * nincs bekötve -- mindig itt kezdődik a flow. Az ablak az F4-jellegű
 * "Main Menu" menüpontból a játék közben is újra elérhető.
 */
public class WelcomeWindow extends JFrame {

    /** A CommandBridge -- a MainWindow konstruktorának adjuk át, ha indul a játék. */
    private final CommandBridge bridge;

    /**
     * Létrehoz egy WelcomeWindow-ot a megadott CommandBridge-dzsel.
     *
     * @param bridge A felkonfigurált CommandBridge (MainApp.main inicializálja).
     */
    public WelcomeWindow(CommandBridge bridge) {
        super("Snow Plows – Főmenü (#48 GonoszOnosz)");
        this.bridge = bridge;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 540);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(buildCenterPanel(), BorderLayout.CENTER);
    }

    /**
     * Összerakja a középső panelt: cím, alcím, négy menügomb.
     *
     * @return A felépített JPanel.
     */
    private JPanel buildCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(48, 60, 40, 60));
        center.setBackground(new Color(48, 58, 55));

        JLabel title = new JLabel("Snow Plows");
        title.setFont(new Font("Sans-Serif", Font.BOLD, 38));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel(
                "Team #48 GonoszOnosz · BME projektlabor 2026 · 13. heti vegleges");
        subtitle.setFont(new Font("Sans-Serif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(180, 180, 180));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 4)));
        center.add(subtitle);
        center.add(Box.createRigidArea(new Dimension(0, 50)));

        center.add(menuButton("New Game", e -> onNewGame()));
        center.add(Box.createRigidArea(new Dimension(0, 14)));
        center.add(menuButton("Load Game", e -> onLoadGame()));
        center.add(Box.createRigidArea(new Dimension(0, 14)));
        center.add(menuButton("Settings", e -> onSettings()));
        center.add(Box.createRigidArea(new Dimension(0, 14)));
        center.add(menuButton("Exit", e -> onExit()));

        center.add(Box.createVerticalGlue());
        return center;
    }

    /**
     * Készít egy menüsor-stílusú gombot egységes méretben.
     *
     * @param label    A gomb felirata.
     * @param listener Az ActionListener.
     * @return A felépített JButton.
     */
    private JButton menuButton(String label, ActionListener listener) {
        JButton b = new JButton(label);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(280, 46));
        b.setPreferredSize(new Dimension(280, 46));
        b.setFont(new Font("Sans-Serif", Font.PLAIN, 16));
        b.setFocusPainted(false);
        b.addActionListener(listener);
        return b;
    }

    /**
     * "New Game" gomb kezelője. Megnyitja a MapChooserDialog-ot;
     * ha a felhasznalo választott pályát, bezárja a welcome-ot és új
     * MainWindow-ot indít a megfelelo Demo-val (EASY/MEDIUM/HARD).
     * Ha Mégse-t valasztott, a welcome ablak nyitva marad.
     */
    private void onNewGame() {
        MapChooserDialog chooser = new MapChooserDialog(this);
        chooser.setVisible(true);  // modal: blokkol amig a felhasznalo bezarja
        MapType chosen = chooser.getSelection();
        if (chosen == null) {
            return;
        }
        dispose();
        MainWindow w = new MainWindow(bridge);
        w.newGame(chosen);
        w.showWindow();
    }

    /**
     * "Load Game" gomb kezelője. Megnyit egy file-chooser-t a
     * prototype/tests könyvtárban; ha a felhasználó választott fájlt,
     * bezárja a welcome-ot és új MainWindow-ot indít a betöltött
     * pálya-konfiggal.
     */
    private void onLoadGame() {
        File baseDir = guessTestDir();
        JFileChooser fc = new JFileChooser(baseDir);
        fc.setDialogTitle("Pálya-konfig fájl megnyitása (*_in.txt / save.txt)");
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fc.getSelectedFile();
        dispose();
        MainWindow w = new MainWindow(bridge);
        w.loadMapAndBind(f);
        w.showWindow();
    }

    /**
     * "Settings" gomb kezelője. MVP fázisban tisztán placeholder dialog --
     * a beállítások panel későbbi verzióban kerül kidolgozásra.
     */
    private void onSettings() {
        JOptionPane.showMessageDialog(this,
                "Settings are not yet implemented.",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * "Exit" gomb kezelője. Bezárja az alkalmazást.
     */
    private void onExit() {
        dispose();
        System.exit(0);
    }

    /**
     * Megpróbálja megtalálni a "prototype/tests" könyvtárat a file-chooser
     * kezdőértékének. Ha nincs, a CWD-t adja vissza. Ugyanaz a heuristika
     * mint a MainWindow.guessTestDir()-ben.
     *
     * @return Egy könyvtár a file-chooser kezdőértékének.
     */
    private File guessTestDir() {
        File[] candidates = new File[] {
                new File("saves"),
                new File(System.getProperty("user.dir"), "saves"),
                new File("prototype/tests"),
                new File("../prototype/tests"),
                new File(System.getProperty("user.dir"), "prototype/tests"),
                new File(System.getProperty("user.dir")),
        };
        for (File c : candidates) {
            if (c.isDirectory()) {
                return c;
            }
        }
        return new File(".");
    }
}
