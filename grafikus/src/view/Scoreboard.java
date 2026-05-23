package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cli.Context;
import model.BusDriver;
import model.Cleaner;
import model.IObserver;
import model.Observable;
import model.Player;

/**
 * Persistens mini-eredmenytabla a felso savban: minden Cleaner-t
 * (pénz) es minden BusDriver-t (pont) egy kis szin-kodolt chip-pel
 * jelenit meg. A panel automatikusan ujraepiti magat minden
 * jatekos-modositasra (a ViewBinder feliratkoztatja a Player-ekre
 * es a GameLogic-ra).
 *
 * A 13. heti vizualis ujitas: a HUD csak a JELENLEGI jatekos
 * stat-jat mutatja, de a Scoreboard mindenkit egyutt mindig.
 */
public class Scoreboard extends JPanel implements IObserver {

    /**
     * Letrehoz egy Scoreboard panelt.
     */
    public Scoreboard() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 35, 45));
        setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));
        setPreferredSize(new Dimension(0, 30));
        refresh();
    }

    /**
     * Ujraepiti a mini-eredmenytablat az osszes Player-bol.
     */
    public void refresh() {
        removeAll();

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chips.setOpaque(false);

        List<Player> cleaners = new ArrayList<>();
        List<Player> busDrivers = new ArrayList<>();
        for (Object o : Context.objectManager.getAll().values()) {
            if (o instanceof Cleaner) {
                cleaners.add((Player) o);
            } else if (o instanceof BusDriver) {
                busDrivers.add((Player) o);
            }
        }

        for (Player p : cleaners) {
            chips.add(playerChip(p, String.format("€ %.0f", p.money)));
        }
        for (Player p : busDrivers) {
            chips.add(playerChip(p, p.score + " pt"));
        }
        if (cleaners.isEmpty() && busDrivers.isEmpty()) {
            JLabel empty = new JLabel("(nincs jatekos)");
            empty.setForeground(new Color(160, 165, 175));
            empty.setFont(new Font("SansSerif", Font.PLAIN, 11));
            chips.add(empty);
        }

        add(chips, BorderLayout.WEST);
        revalidate();
        repaint();
    }

    /**
     * Letrehoz egy szin-kodolt chip-szeru panelt egy jatekosnak.
     *
     * @param p      A jatekos.
     * @param metric A megjelenitendo szam-szoveg.
     * @return A felépített chip-panel.
     */
    private static JPanel playerChip(Player p, String metric) {
        JPanel chip = new JPanel(new GridBagLayout());
        chip.setBackground(PlayerColors.forPlayer(p));
        chip.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 0, 6);
        JLabel name = new JLabel(p.name != null ? p.name : "?");
        name.setFont(new Font("SansSerif", Font.BOLD, 11));
        name.setForeground(Color.WHITE);
        chip.add(name, gc);
        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        JLabel metricL = new JLabel(metric);
        metricL.setFont(new Font("SansSerif", Font.PLAIN, 11));
        metricL.setForeground(Color.WHITE);
        chip.add(metricL, gc);
        return chip;
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
