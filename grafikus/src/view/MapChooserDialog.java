package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.MapType;

/**
 * Modal pálya-választó dialog, ami a WelcomeWindow "New Game" gombja
 * utan jelenik meg. A felhasznalo a 3 lehetoseg kozul valaszthat
 * (EASY / MEDIUM / HARD); a getSelection() a kiválasztott MapType-ot
 * adja vissza vagy null-t ha mégse.
 */
public class MapChooserDialog extends JDialog {

    /** A felhasznalo valasza (lehet null = megse). */
    private MapType selection = null;

    /**
     * Letrehoz egy MapChooserDialog-ot.
     *
     * @param parent A szuelo JFrame.
     */
    public MapChooserDialog(JFrame parent) {
        super(parent, "Új játék – Pálya választása", true);
        build();
        pack();
        setMinimumSize(new Dimension(480, 380));
        setLocationRelativeTo(parent);
    }

    /**
     * Visszaadja a felhasznalo valasztasat.
     *
     * @return A MapType, vagy null ha mégse.
     */
    public MapType getSelection() {
        return selection;
    }

    /**
     * Felepiti a dialog UI-t: 3 kartya (mapping MapType-ra).
     */
    private void build() {
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(new Color(40, 45, 55));
        content.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));

        JLabel title = new JLabel("Válassz pályát:");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        content.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setOpaque(false);

        cards.add(card("Easy – 4 sarok",
                "1 Cleaner 1 hokotrovel + 1 BusDriver 1 busszal. " +
                "A legalapabb pálya: gyors tanuló-jatekhoz.",
                MapType.EASY));
        cards.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));
        cards.add(card("Medium – 2×3 racs",
                "1 Cleaner 2 hokotrovel + 2 BusDriver 1-1 busszal. " +
                "Kozepes meret, tobb terminal, tobb forgalom.",
                MapType.MEDIUM));
        cards.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));
        cards.add(card("Hard – 3×3 racs",
                "2 Cleaner 1-1 hokotrovel + 2 BusDriver 1-1 busszal. " +
                "Nagy meretu pálya, 2 HomeBase, 4 terminal, 3 NPC auto.",
                MapType.HARD));

        content.add(cards, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton cancel = new JButton("Mégse");
        cancel.addActionListener(e -> {
            selection = null;
            dispose();
        });
        bottom.add(cancel);
        content.add(bottom, BorderLayout.SOUTH);

        setContentPane(content);
    }

    /**
     * Egy "kartya" panel (cim + leiras + valaszt-gomb).
     *
     * @param header    A cím.
     * @param desc      A leiras.
     * @param mapType   A hozzatartozo MapType.
     * @return A felepitett kartya.
     */
    private JPanel card(String header, String desc, MapType mapType) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(new Color(55, 62, 75));
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

        JLabel headerL = new JLabel(header);
        headerL.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerL.setForeground(Color.WHITE);
        card.add(headerL, BorderLayout.NORTH);

        JLabel descL = new JLabel("<html><body style='width:340px'>"
                + desc + "</body></html>");
        descL.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descL.setForeground(new Color(190, 195, 205));
        card.add(descL, BorderLayout.CENTER);

        JButton choose = new JButton("Választ");
        choose.addActionListener(e -> {
            selection = mapType;
            dispose();
        });
        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(choose, BorderLayout.NORTH);
        card.add(rightWrap, BorderLayout.EAST);
        return card;
    }
}
