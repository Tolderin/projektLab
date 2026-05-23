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
import model.Bus;
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
import model.Vehicle;

/**
 * A jobb oldalt elhelyezkedo status-panel: a 13. heti turn-order
 * rendszerben kizarolag a JELENLEGI AKTUALIS jatekos stat-jait
 * mutatja, a jelenlegi jarmu-tipusra szabott reszletekkel:
 *   - aktiv jatekos (szin-kodolva): nev, role, penz, pont
 *   - aktiv vehicle: ID, ha SnowPlow akkor head+tank,
 *     ha Bus akkor functioning + disabledTurnsLeft.
 *
 * Kornyezet kor alatt (nincs aktiv jatekos) "Környezet köre" felirat.
 */
public class HUDPanel extends JPanel implements IObserver {

    /** A kor felirat. */
    private final JLabel turnLabel = new JLabel("Kör: 0");

    /** Szinezett kis sav a jatekos szinevel (oszlop-szelessegu). */
    private final JPanel colorStripe = new JPanel();

    /** Jatekos role + nev. */
    private final JLabel playerLabel = new JLabel("-");

    /** Jatekos penze. */
    private final JLabel moneyLabel = new JLabel("Pénz: -");

    /** Jatekos pontszama. */
    private final JLabel scoreLabel = new JLabel("Pont: -");

    /** Az aktiv vehicle felirata (ID + tipus). */
    private final JLabel vehicleLabel = new JLabel("Jármű: -");

    /** Az aktiv SnowPlow fej-rovidítese (csak ha plow). */
    private final JLabel headLabel = new JLabel("Fej: -");

    /** Az aktiv SnowPlow tank-allapota (csak ha plow). */
    private final JLabel fuelLabel = new JLabel("Tank: -");

    /** Az aktiv Bus functioning-allapota (csak ha bus). */
    private final JLabel busStatusLabel = new JLabel("Status: -");

    /** A kijelolt jatekos ID-ja (backward-compat fallback). */
    @SuppressWarnings("unused")
    private String displayedPlayerId;

    /** A kijelolt hokotro ID-ja (backward-compat). */
    @SuppressWarnings("unused")
    private String displayedPlowId;

    /**
     * Letrehoz egy HUDPanel-t alap-formazassal.
     */
    public HUDPanel() {
        setPreferredSize(new Dimension(240, 100));
        setBackground(new Color(45, 50, 60));
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setLayout(new BorderLayout(6, 6));
        JLabel title = new JLabel("Állapot");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(title, BorderLayout.NORTH);

        // Bal oldali kis szin-sav
        colorStripe.setBackground(PlayerColors.NEUTRAL);
        colorStripe.setPreferredSize(new Dimension(6, 0));
        add(colorStripe, BorderLayout.WEST);

        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 3));
        grid.setOpaque(false);
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel[] labels = {
                turnLabel, playerLabel, moneyLabel, scoreLabel,
                vehicleLabel, headLabel, fuelLabel, busStatusLabel
        };
        for (JLabel l : labels) {
            l.setForeground(Color.WHITE);
            if (l != playerLabel) {
                l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            }
            grid.add(l);
        }
        add(grid, BorderLayout.CENTER);
    }

    /** Backward-compat: nincs effektje a 13. heti refactor utan. */
    public void setDisplayedPlayerId(String id) {
        this.displayedPlayerId = id;
        refresh();
    }

    /** Backward-compat: nincs effektje a 13. heti refactor utan. */
    public void setDisplayedPlowId(String id) {
        this.displayedPlowId = id;
        refresh();
    }

    /**
     * Ujraszamolja az osszes feliratot: a GameLogic-tol lekeri a
     * jelenlegi aktualis vehicle / player parost, es csak azt mutatja.
     */
    public void refresh() {
        // Turn count
        if (Context.gameLogic instanceof GameLogic) {
            GameLogic gl = (GameLogic) Context.gameLogic;
            turnLabel.setText("Kör: " + gl.turnCount + " / " + gl.maxTurns);
        }
        if (!(Context.gameLogic instanceof GameLogic)) {
            renderNoGame();
            return;
        }
        GameLogic gl = (GameLogic) Context.gameLogic;
        Vehicle v = gl.getCurrentTurnVehicle();
        Player p = gl.getCurrentPlayer();
        if (v == null || p == null) {
            renderEnvironmentPhase();
            return;
        }
        renderActive(p, v);
    }

    /**
     * Render-segedmetodus: "nincs aktiv jatek" allapot.
     */
    private void renderNoGame() {
        colorStripe.setBackground(PlayerColors.NEUTRAL);
        playerLabel.setText("-");
        moneyLabel.setText("Pénz: -");
        scoreLabel.setText("Pont: -");
        vehicleLabel.setText("Jármű: -");
        headLabel.setText("Fej: -");
        fuelLabel.setText("Tank: -");
        busStatusLabel.setText("Status: -");
        headLabel.setVisible(true);
        fuelLabel.setVisible(true);
        busStatusLabel.setVisible(false);
    }

    /**
     * Render-segedmetodus: kornyezet (env) kor.
     */
    private void renderEnvironmentPhase() {
        colorStripe.setBackground(PlayerColors.NEUTRAL);
        playerLabel.setText("Környezet köre");
        moneyLabel.setText("Pénz: -");
        scoreLabel.setText("Pont: -");
        vehicleLabel.setText("Jármű: -");
        headLabel.setText("Fej: -");
        fuelLabel.setText("Tank: -");
        busStatusLabel.setText("Status: -");
        headLabel.setVisible(true);
        fuelLabel.setVisible(true);
        busStatusLabel.setVisible(false);
    }

    /**
     * Render-segedmetodus: aktiv jatekos + vehicle.
     *
     * @param p A jatekos.
     * @param v A jelenlegi vehicle.
     */
    private void renderActive(Player p, Vehicle v) {
        colorStripe.setBackground(PlayerColors.forPlayer(p));
        String role = (p instanceof Cleaner) ? "Cleaner"
                : (p instanceof BusDriver) ? "BusDriver" : "Player";
        playerLabel.setText(role + ": " + p.name);
        moneyLabel.setText(String.format("Pénz: %.1f", p.money));
        scoreLabel.setText("Pont: " + p.score);
        // 13. heti UI-finomitas: a Cleaner csak penzben gondolkodik
        // (nincs pont), a BusDriver pedig pontokat szerez (nincs penz).
        if (p instanceof Cleaner) {
            moneyLabel.setVisible(true);
            scoreLabel.setVisible(false);
        } else if (p instanceof BusDriver) {
            moneyLabel.setVisible(false);
            scoreLabel.setVisible(true);
        } else {
            moneyLabel.setVisible(true);
            scoreLabel.setVisible(true);
        }
        String vid = Context.objectManager.getId(v);
        vehicleLabel.setText("Jármű: " + (vid != null ? vid : "?"));
        if (v instanceof SnowPlow) {
            SnowPlow sp = (SnowPlow) v;
            if (sp.activeHead != null) {
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
            headLabel.setVisible(true);
            fuelLabel.setVisible(true);
            busStatusLabel.setVisible(false);
        } else if (v instanceof Bus) {
            Bus bus = (Bus) v;
            String status = bus.isFunctioning
                    ? "rendben"
                    : "letiltva (" + bus.disabledTurnsLeft + " kör)";
            busStatusLabel.setText("Status: " + status);
            headLabel.setVisible(false);
            fuelLabel.setVisible(false);
            busStatusLabel.setVisible(true);
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
