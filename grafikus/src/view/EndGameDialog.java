package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import cli.Context;
import model.BusDriver;
import model.Cleaner;
import model.GameLogic;
import model.Player;

/**
 * A jatek vegen megjelenik egy modal dialog, amelyen a jatekosok
 * pontszam/penz-szerinti rangsora lathato, valamint a kor-szam.
 * A 13. heti vizualis frissites: szin-kodolt jatekos-sorok, nagyobb
 * cim, kulon "Bezar" es "Vissza a fomenube" gombok. A "Vissza a
 * fomenube" automatikusan megjeleniti a WelcomeWindow-ot.
 */
public class EndGameDialog extends JDialog {

    /** A befoglalo MainWindow (a back-to-menu hivasahoz). */
    private final MainWindow owner;

    /**
     * Letrehozza es megjeleniti a vegjatek-dialogot.
     *
     * @param parent A befoglalo MainWindow.
     */
    public EndGameDialog(MainWindow parent) {
        super((JFrame) parent, "Játék vége", true);
        this.owner = parent;
        build();
        pack();
        setMinimumSize(new Dimension(420, 360));
        setLocationRelativeTo(parent);
    }

    /**
     * Felepiti az UI-t.
     */
    private void build() {
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(new Color(35, 40, 50));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        // ----- Cim -----
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        JLabel title = new JLabel("Vége a játéknak");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title);

        String turnText = "";
        if (Context.gameLogic instanceof GameLogic) {
            GameLogic gl = (GameLogic) Context.gameLogic;
            turnText = "Lejátszott kör: " + gl.turnCount + " / " + gl.maxTurns;
        }
        JLabel subtitle = new JLabel(turnText);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(180, 185, 195));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(subtitle);

        content.add(header, BorderLayout.NORTH);

        // ----- Rangsor -----
        JPanel ranking = new JPanel();
        ranking.setLayout(new BoxLayout(ranking, BoxLayout.Y_AXIS));
        ranking.setBackground(new Color(48, 55, 68));
        ranking.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        List<Player> players = new ArrayList<>();
        for (Object o : Context.objectManager.getAll().values()) {
            if (o instanceof Player) {
                players.add((Player) o);
            }
        }
        // Cleaner-ek penz szerint, BusDriver-ek score szerint --
        // ket csoport, mindegyik a sajat metric szerint csokkenoen.
        List<Player> cleaners = new ArrayList<>();
        List<Player> busDrivers = new ArrayList<>();
        for (Player p : players) {
            if (p instanceof Cleaner) {
                cleaners.add(p);
            } else if (p instanceof BusDriver) {
                busDrivers.add(p);
            }
        }
        Collections.sort(cleaners, new Comparator<Player>() {
            @Override
            public int compare(Player a, Player b) {
                return Double.compare(b.money, a.money);
            }
        });
        Collections.sort(busDrivers, new Comparator<Player>() {
            @Override
            public int compare(Player a, Player b) {
                return Integer.compare(b.score, a.score);
            }
        });

        if (!cleaners.isEmpty()) {
            ranking.add(sectionHeader("Cleaners (rangsor pénz szerint)"));
            int rank = 1;
            for (Player p : cleaners) {
                ranking.add(playerRow(rank++, p, true));
                ranking.add(javax.swing.Box.createRigidArea(new Dimension(0, 4)));
            }
            ranking.add(javax.swing.Box.createRigidArea(new Dimension(0, 8)));
        }
        if (!busDrivers.isEmpty()) {
            ranking.add(sectionHeader("BusDrivers (rangsor pont szerint)"));
            int rank = 1;
            for (Player p : busDrivers) {
                ranking.add(playerRow(rank++, p, false));
                ranking.add(javax.swing.Box.createRigidArea(new Dimension(0, 4)));
            }
        }
        if (cleaners.isEmpty() && busDrivers.isEmpty()) {
            JLabel none = new JLabel("(nincs játékos)");
            none.setForeground(Color.WHITE);
            ranking.add(none);
        }

        JScrollPane scroll = new JScrollPane(ranking);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(48, 55, 68));
        content.add(scroll, BorderLayout.CENTER);

        // ----- Gombok -----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        bottom.setOpaque(false);
        JButton close = new JButton("Bezár");
        close.addActionListener(e -> dispose());
        JButton backMenu = new JButton("Vissza a főmenübe");
        backMenu.addActionListener(e -> {
            dispose();
            if (owner != null) {
                owner.backToMainMenu();
            }
        });
        bottom.add(close);
        bottom.add(backMenu);
        content.add(bottom, BorderLayout.SOUTH);

        setContentPane(content);
    }

    /**
     * Egy szekcio-cim-cimke.
     *
     * @param text A cim szovege.
     * @return A felépített JLabel.
     */
    private static JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(220, 225, 240));
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /**
     * Egy szin-kodolt jatekos-sor (rank + nev + metric).
     *
     * @param rank        A rangsor pozicio.
     * @param p           A jatekos.
     * @param showMoney   true ha penzt mutatunk (Cleaner), false ha pontot (BusDriver).
     * @return A felépített sorpanel.
     */
    private static JPanel playerRow(int rank, Player p, boolean showMoney) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        Color base = PlayerColors.forPlayer(p);
        row.setBackground(base);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel name = new JLabel(rank + ". " + (p.name != null ? p.name : "?"));
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SansSerif", Font.BOLD, 13));
        row.add(name, BorderLayout.WEST);

        String metric;
        if (showMoney) {
            metric = String.format("Pénz: %.0f", p.money);
        } else {
            metric = "Pont: " + p.score;
        }
        JLabel metricLabel = new JLabel(metric);
        metricLabel.setForeground(Color.WHITE);
        metricLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        row.add(metricLabel, BorderLayout.EAST);

        return row;
    }
}
