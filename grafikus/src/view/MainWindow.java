package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import cli.Context;
import controller.CommandBridge;
import controller.InputController;
import controller.ViewBinder;
import main.DefaultDemo;
import model.Cleaner;
import model.HomeBase;
import model.IObserver;
import model.Observable;
import model.Player;
import model.SnowPlow;

/**
 * A program főablaka (JFrame). A 11.3.1/3. (MainWindow) szerint
 * felelős a felső menüsor, a központi GamePanel, a jobb oldali
 * HUDPanel és az alsó ActionPanel elhelyezéséért, az ablak
 * életciklusáért és a globális menük ActionListener-eiÉrt. A
 * GameLogic "gameEnded" jelzésére az EndGameDialog-ot nyitja meg.
 */
public class MainWindow extends JFrame implements IObserver {

    /** A térkép rajzfelülete. */
    private final GamePanel gamePanel = new GamePanel();

    /** A status-panel. */
    private final HUDPanel hudPanel = new HUDPanel();

    /** Az akcio-toolbar. */
    private ActionPanel actionPanel;

    /** A parancsbridge a menüpontokhoz. */
    private final CommandBridge bridge;

    /** A view-binder, hogy újra-load-kor kötni tudjon. */
    private final ViewBinder viewBinder = new ViewBinder();

    /** A GameRenderer. */
    private GameRenderer renderer;

    /** Az InputController. */
    private InputController input;

    /**
     * Letrehoz egy MainWindow-ot.
     *
     * @param bridge A CommandBridge.
     */
    public MainWindow(CommandBridge bridge) {
        super("Snow Plows – Grafikus változat (#48 GonoszOnosz)");
        this.bridge = bridge;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 880);
        setLayout(new BorderLayout());

        MapLayout layout = new MapLayout();
        renderer = new GameRenderer(layout);
        gamePanel.setRenderer(renderer);

        input = new InputController(gamePanel, bridge, this);
        gamePanel.addMouseListener(input);
        gamePanel.addKeyListener(input);

        actionPanel = new ActionPanel(bridge, input, this);

        add(buildMenuBar(), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(gamePanel);
        scroll.getViewport().setBackground(new Color(48, 58, 55));
        add(scroll, BorderLayout.CENTER);
        add(hudPanel, BorderLayout.EAST);
        add(actionPanel, BorderLayout.SOUTH);
    }

    /**
     * Megjelenítí az ablakot.
     */
    public void showWindow() {
        setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    /**
     * Visszaadja a GamePanel-t.
     *
     * @return A jatekpanel.
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Visszaadja a HUDPanel-t.
     *
     * @return A HUD.
     */
    public HUDPanel getHudPanel() {
        return hudPanel;
    }

    /**
     * Visszaadja a GameRenderer-t.
     *
     * @return A renderer.
     */
    public GameRenderer getRenderer() {
        return renderer;
    }

    /**
     * Visszaadja az InputController-t.
     *
     * @return Az input-vezerlo.
     */
    public InputController getInput() {
        return input;
    }

    /**
     * Visszaadja a ViewBinder-t.
     *
     * @return A view-binder.
     */
    public ViewBinder getViewBinder() {
        return viewBinder;
    }

    /**
     * Felépíti a menüsort és bekötögeti az ActionListener-eket
     * a 11.3.1/3. spec szerint:
     *  - File: New / Load Map / Save / Exit.
     *  - Game: Next Turn (SPACE) / Random On / Random Off / Rebind.
     *  - Help: About / Billentyűk (F1).
     *
     * @return Az új JMenuBar.
     */
    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.setAccelerator(KeyStroke.getKeyStroke("control N"));
        newGame.addActionListener(e -> newDefaultGame());
        JMenuItem loadMap = new JMenuItem("Load Map…");
        loadMap.setAccelerator(KeyStroke.getKeyStroke("control O"));
        loadMap.addActionListener(e -> onLoadMap());
        JMenuItem save = new JMenuItem("Save…");
        save.setAccelerator(KeyStroke.getKeyStroke("control S"));
        save.addActionListener(e -> onSave());
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> dispose());
        file.add(newGame);
        file.add(loadMap);
        file.add(save);
        file.addSeparator();
        file.add(exit);

        JMenu game = new JMenu("Game");
        JMenuItem next = new JMenuItem("Next Turn");
        next.setAccelerator(KeyStroke.getKeyStroke("SPACE"));
        next.addActionListener(e -> bridge.nextTurn());
        JMenuItem randomOff = new JMenuItem("Random Off");
        randomOff.addActionListener(e -> bridge.setRandom(false));
        JMenuItem randomOn = new JMenuItem("Random On");
        randomOn.addActionListener(e -> bridge.setRandom(true));
        JMenuItem rebind = new JMenuItem("Rebind Views (refresh)");
        rebind.addActionListener(e -> rebindAll());
        game.add(next);
        game.addSeparator();
        game.add(randomOn);
        game.add(randomOff);
        game.addSeparator();
        game.add(rebind);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Snow Plows szimulator – grafikus változat\n"
                        + "Team #48 GonoszOnosz, BME projektlabor 2026.",
                "About", JOptionPane.INFORMATION_MESSAGE));
        JMenuItem keys = new JMenuItem("Billentyűk");
        keys.setAccelerator(KeyStroke.getKeyStroke("F1"));
        keys.addActionListener(e -> showHelp());
        help.add(about);
        help.add(keys);

        mb.add(file);
        mb.add(game);
        mb.add(help);
        return mb;
    }

    /**
     * "Load Map" menüpont kezelője. Megnyit egy fájl-választót, majd
     * a kiválasztott fájlt a load parancson keresztül betölteti.
     * A pálya CLI-kompatibilis kell legyen (lasd: prototype/tests/*_in.txt).
     */
    private void onLoadMap() {
        File baseDir = guessTestDir();
        JFileChooser fc = new JFileChooser(baseDir);
        fc.setDialogTitle("Pálya-konfig fájl megnyitása (*_in.txt)");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            // Reset elotte: modell + layout torlese, hogy az
            // autoLayout fusson a betöltött pályára
            Context.objectManager.clearAll();
            if (Context.gameLogic instanceof model.GameLogic) {
                model.GameLogic gl = (model.GameLogic) Context.gameLogic;
                gl.players.clear();
                gl.vehicles.clear();
                gl.gameMap = new model.Map();
                gl.turnCount = 0;
            }
            renderer.getLayout().clear();
            bridge.load(f.getAbsolutePath());
            rebindAll();
        }
    }

    /**
     * "Save" menüpont kezelője. Megnyit egy file-chooser-t, majd a
     * mentést egy szöveges fájlba végzi (a save parancs file
     * argumentummal).
     */
    private void onSave() {
        JFileChooser fc = new JFileChooser(new File("."));
        fc.setDialogTitle("Mentés -- save parancs kimenete");
        fc.setSelectedFile(new File("save.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            bridge.getParser().parseLine("save " + f.getAbsolutePath());
            showMessage("Mentés sikeres: " + f.getName());
        }
    }

    /**
     * Megprobalja megtalalni a "prototype/tests" konyvtarat a JFile-
     * Chooser kezdoertekekent. Ha nem talalja, a CWD-t hasznalja.
     *
     * @return Egy konyvtar a fájl-chooser-hez.
     */
    private File guessTestDir() {
        File[] candidates = new File[] {
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

    /**
     * Újrarendezi a layoutot és minden nézetet a frissített
     * ObjectManager-tartalom szerint. A spec 11.3.1/24. ViewBinder
     * pontja szerint a bind-fazis BFS-szel végigjárja az
     * ObjectManager elemeit, és minden modellhez letrehoz egy nezetet.
     */
    public void rebindAll() {
        viewBinder.bindAll(renderer, gamePanel, hudPanel);
        if (Context.gameLogic instanceof Observable) {
            ((Observable) Context.gameLogic).addObserver(this);
        }
        gamePanel.refreshPreferredSize();
        gamePanel.repaint();
        // Default HUD-jatekos / hokotro kivalasztasa, ha van
        for (Object o : Context.objectManager.getAll().values()) {
            if (o instanceof Player) {
                String id = Context.objectManager.getId(o);
                hudPanel.setDisplayedPlayerId(id);
                break;
            }
        }
        for (Object o : Context.objectManager.getAll().values()) {
            if (o instanceof SnowPlow) {
                String id = Context.objectManager.getId(o);
                hudPanel.setDisplayedPlowId(id);
                break;
            }
        }
        hudPanel.refresh();
    }

    /**
     * Üzenetet jelenít meg a felhasználónak.
     *
     * @param msg Az üzenet.
     */
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    /**
     * Új játék indítása a DefaultDemo 3x3 racs-szerkezetu varosaval.
     * A pálya CLI parancsokon keresztul jon letre (create /
     * connect_fields / spawn / set_money), majd a MapLayout-on
     * explicit pixel-pozíciókkal beallitva. 9 keresztezodes, 6
     * epulet, 1 hokotro + 1 busz + 2 NPC auto.
     */
    public void newDefaultGame() {
        // Modell-allapot torlese
        Context.objectManager.clearAll();
        if (Context.gameLogic instanceof model.GameLogic) {
            model.GameLogic gl = (model.GameLogic) Context.gameLogic;
            gl.players.clear();
            gl.vehicles.clear();
            gl.gameMap = new model.Map();
            gl.turnCount = 0;
        }
        // Layout torlese, hogy a manualis beallitas friss legyen
        renderer.getLayout().clear();
        // CLI parancsok + manualis layout pozíciók egyutt
        DefaultDemo.apply(bridge.getParser(), renderer.getLayout());
        rebindAll();
    }

    /**
     * Súgó-dialog megnyitása.
     */
    public void showHelp() {
        String text = "Billentyűk:\n"
                + "  SPACE  – next_turn\n"
                + "  ESC    – kijelölés törlése\n"
                + "  F1     – súgó\n"
                + "  Ctrl+N – új játék (default pálya)\n"
                + "  Ctrl+O – pálya betöltése (*_in.txt)\n"
                + "  Ctrl+S – mentés\n\n"
                + "Egér:\n"
                + "  Bal kattintás: mező / jármű kijelölés\n"
                + "  Bal kattintás (Move módban): mozgás a célmezőre\n"
                + "  Jobb kattintás: az adott objektum [STATE] kimenete";
        JOptionPane.showMessageDialog(this, text, "Súgó",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Az InputController hívja, amikor a felhasználó másik objektumot
     * jelöl ki -- a HUD frissítés célja a kijelölt SnowPlow / Cleaner.
     *
     * @param vehicleId A kijelölt jármű ID-ja, vagy null.
     * @param fieldId   A kijelölt mező ID-ja, vagy null.
     */
    public void onSelectionChanged(String vehicleId, String fieldId) {
        if (vehicleId != null) {
            Object o = Context.objectManager.getObject(vehicleId);
            if (o instanceof SnowPlow) {
                SnowPlow sp = (SnowPlow) o;
                hudPanel.setDisplayedPlowId(vehicleId);
                if (sp.getOwner() != null) {
                    String oid = Context.objectManager.getId(sp.getOwner());
                    if (oid != null) {
                        hudPanel.setDisplayedPlayerId(oid);
                    }
                }
            }
        }
        hudPanel.refresh();
    }

    /**
     * Vásárlás-dialog megnyitása a kijelölt SnowPlow-hoz.
     *
     * @param sp A célzott hokotro.
     */
    public void openMarketDialog(SnowPlow sp) {
        if (!(sp.currentField instanceof HomeBase)) {
            showMessage("Csak HomeBase-en lehet vasarolni.");
            return;
        }
        Cleaner buyer = sp.getOwner();
        if (buyer == null) {
            showMessage("A hokotrohoz nincs tulajdonos Cleaner.");
            return;
        }
        new MarketDialog(this, sp, buyer, bridge).setVisible(true);
    }

    /**
     * Push-jelzés kezelése: csak a "gameEnded" hint-re jelenítjük
     * meg az EndGameDialog-ot.
     *
     * @param source A jelzés forrása.
     * @param hint   A jelzés típusa.
     */
    @Override
    public void update(Observable source, String hint) {
        if ("gameEnded".equals(hint)) {
            javax.swing.SwingUtilities.invokeLater(
                    () -> new EndGameDialog(this).setVisible(true));
        }
    }

    /**
     * Visszaadja a panel preferalt méretét.
     *
     * @return A preferalt meret.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1200, 880);
    }
}
