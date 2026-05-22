package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cli.Context;
import model.Player;

/**
 * A jatek vegen megjelenik egy modal dialog, amelyen a jatekosok
 * pontszam-szerinti rangsorolt listaja lathato, valamint a vegso
 * statisztika (turnCount).
 */
public class EndGameDialog extends JDialog {

    /**
     * Letrehozza es megjeleniti a vegjatek-dialogot.
     *
     * @param parent A befoglalo JFrame.
     */
    public EndGameDialog(JFrame parent) {
        super(parent, "Játék vége", true);
        build();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Felepiti az UI-t.
     */
    private void build() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Végeredmény");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        content.add(title, BorderLayout.NORTH);

        List<Player> ranking = new ArrayList<>();
        for (Object o : Context.objectManager.getAll().values()) {
            if (o instanceof Player) {
                ranking.add((Player) o);
            }
        }
        Collections.sort(ranking, new Comparator<Player>() {
            @Override
            public int compare(Player a, Player b) {
                return Integer.compare(b.score, a.score);
            }
        });
        java.util.Vector<String> rows = new java.util.Vector<>();
        int i = 1;
        for (Player p : ranking) {
            rows.add(String.format("%d. %s — pont: %d, pénz: %.1f",
                    i++, p.name != null ? p.name : "?", p.score, p.money));
        }
        if (rows.isEmpty()) {
            rows.add("(nincs jatekos)");
        }
        JList<String> list = new JList<>(rows);
        list.setFont(new Font("SansSerif", Font.PLAIN, 12));
        content.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Bezár");
        close.addActionListener(e -> dispose());
        bottom.add(close);
        content.add(bottom, BorderLayout.SOUTH);

        setContentPane(content);
    }
}
