package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cli.Context;
import model.BusDriver;
import model.Cleaner;
import model.GameLogic;
import model.IObserver;
import model.Observable;
import model.Player;
import model.Vehicle;

/**
 * Az ablak tetejen elhelyezkedo szin-kodolt panel: vilagosan jelzi,
 * MELY jatekos KOVETKEZO mozgathato jarmuvenek a kore. A hatterszin
 * a jelenlegi jatekosra jellemzo (PlayerColors), a felirat a
 * jatekos szerepere + neve + a kovetkezo jarmu ID-jara hivatkozik.
 *
 * Auto-refresh: a panel megfigyelheti a GameLogic-ot ("turnAdvanced")
 * es a Player-eket ("moneyChanged" / "scoreChanged"); de mivel a
 * "kovetkezo mozgathato jarmu" valtozasa nem trigger-el observer-
 * jelzest (csak egy bool mezo modosul), a MainWindow.onSelectionChanged
 * a kozponti hely, ahol manualisan hivjuk a refresh()-t.
 */
public class TurnIndicatorPanel extends JPanel implements IObserver {

    /** A felirat. */
    private final JLabel label = new JLabel(" ");

    /**
     * Letrehoz egy TurnIndicatorPanel-t.
     */
    public TurnIndicatorPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 38));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        add(label, BorderLayout.CENTER);
        refresh();
    }

    /**
     * Ujraszamolja a feliratot es a hatterszint a GameLogic
     * jelenlegi aktualis vehicle/player parosabol.
     */
    public void refresh() {
        if (!(Context.gameLogic instanceof GameLogic)) {
            label.setText("(nincs aktiv jatek)");
            setBackground(PlayerColors.NEUTRAL);
            return;
        }
        GameLogic gl = (GameLogic) Context.gameLogic;
        Vehicle v = gl.getCurrentTurnVehicle();
        Player p = gl.getCurrentPlayer();
        if (v == null || p == null) {
            label.setText("Környezet köre …");
            setBackground(PlayerColors.NEUTRAL);
            return;
        }
        String vid = Context.objectManager.getId(v);
        String role = (p instanceof Cleaner) ? "Cleaner"
                : (p instanceof BusDriver) ? "BusDriver" : "Player";
        label.setText(role + " " + p.name
                + "  –  következik: " + (vid != null ? vid : "?"));
        setBackground(PlayerColors.forPlayer(p));
    }

    /**
     * Observer-callback: minden modell-frissitesre frissit.
     *
     * @param source A jelzes forrasa.
     * @param hint   A valtozas tipusa.
     */
    @Override
    public void update(Observable source, String hint) {
        refresh();
    }
}
