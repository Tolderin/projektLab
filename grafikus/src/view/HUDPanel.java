package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cli.Context;
import model.BusDriver;
import model.Cleaner;
import model.DragonHead;
import model.GameLogic;
import model.GravelHead;
import model.IObserver;
import model.Observable;
import model.Player;
import model.SaltHead;
import model.SnowPlow;

/**
 * A jobb oldalt elhelyezkedo status-panel: aktualis kor, aktiv
 * jatekos, penz, pontszam, aktiv hokotro-fej es tank-allapot.
 * Megfigyelo a GameLogic-on es minden Player-en: barmelyik
 * valtozasara ujraszamolja a megjelenitett ertekeket.
 */
public class HUDPanel extends JPanel implements IObserver {

    /** A kor felirat. */
    private final JLabel turnLabel = new JLabel("Kör: 0");

    /** Az aktiv (kijelolt) jatekos felirata. */
    private final JLabel currentPlayerLabel = new JLabel("Játékos: -");

    /** A jatekos penz-allapota. */
    private final JLabel moneyLabel = new JLabel("Pénz: 0");

    /** A jatekos pontszam-allapota. */
    private final JLabel scoreLabel = new JLabel("Pont: 0");

    /** Az aktiv hokotro-fej rovidítese. */
    private final JLabel headLabel = new JLabel("Fej: -");

    /** A tank-allapot (uzemanyag-mennyiseg). */
    private final JLabel fuelLabel = new JLabel("Tank: -");

    /** A kijelolt jatekos ID-ja (a HUD frissiteshez). */
    private String displayedPlayerId;

    /** A kijelolt hokotro ID-ja (a HUD frissiteshez). */
    private String displayedPlowId;

    /**
     * Letrehoz egy HUDPanel-t alap-formazassal.
     */
    public HUDPanel() {
        setPreferredSize(new Dimension(220, 100));
        setBackground(new Color(45, 50, 60));
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Állapot");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(title, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 4));
        grid.setOpaque(false);
        for (JLabel l : new JLabel[] {
                turnLabel, currentPlayerLabel, moneyLabel,
                scoreLabel, headLabel, fuelLabel }) {
            l.setForeground(Color.WHITE);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            grid.add(l);
        }
        add(grid, BorderLayout.CENTER);
    }

    /**
     * Beallitja a HUD aktualis jatekos ID-jat.
     *
     * @param id Az ID, vagy null.
     */
    public void setDisplayedPlayerId(String id) {
        this.displayedPlayerId = id;
        refresh();
    }

    /**
     * Beallitja a HUD aktualis hokotro ID-jat.
     *
     * @param id Az ID, vagy null.
     */
    public void setDisplayedPlowId(String id) {
        this.displayedPlowId = id;
        refresh();
    }

    /**
     * Ujraszamolja az osszes feliratot a Context.objectManager-bol.
     */
    public void refresh() {
        if (Context.gameLogic instanceof GameLogic) {
            GameLogic gl = (GameLogic) Context.gameLogic;
            turnLabel.setText("Kör: " + gl.turnCount + " / " + gl.maxTurns);
        }
        Player p = null;
        if (displayedPlayerId != null) {
            Object o = Context.objectManager.getObject(displayedPlayerId);
            if (o instanceof Player) {
                p = (Player) o;
            }
        }
        if (p != null) {
            String t = (p instanceof Cleaner) ? "Cleaner"
                    : (p instanceof BusDriver) ? "BusDriver"
                    : "Player";
            currentPlayerLabel.setText("Játékos: " + p.name + " (" + t + ")");
            moneyLabel.setText(String.format("Pénz: %.1f", p.money));
            scoreLabel.setText("Pont: " + p.score);
        } else {
            currentPlayerLabel.setText("Játékos: -");
            moneyLabel.setText("Pénz: -");
            scoreLabel.setText("Pont: -");
        }
        SnowPlow sp = null;
        if (displayedPlowId != null) {
            Object o = Context.objectManager.getObject(displayedPlowId);
            if (o instanceof SnowPlow) {
                sp = (SnowPlow) o;
            }
        }
        if (sp != null && sp.activeHead != null) {
            headLabel.setText("Fej: " + sp.activeHead.getName());
            double fuel = 0.0;
            if (sp.activeHead instanceof SaltHead) {
                fuel = ((SaltHead) sp.activeHead).fuelAmount;
            } else if (sp.activeHead instanceof DragonHead) {
                fuel = ((DragonHead) sp.activeHead).fuelAmount;
            } else if (sp.activeHead instanceof GravelHead) {
                fuel = ((GravelHead) sp.activeHead).fuelAmount;
            }
            fuelLabel.setText(String.format("Tank: %.1f", fuel));
        } else {
            headLabel.setText("Fej: -");
            fuelLabel.setText("Tank: -");
        }
    }

    /**
     * Push-jelzesre frissiti az osszes feliratot.
     *
     * @param source A jelzes forrasa.
     * @param hint   A valtozas tipusa.
     */
    @Override
    public void update(Observable source, String hint) {
        refresh();
    }
}
