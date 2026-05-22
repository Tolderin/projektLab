package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.swing.JPanel;

import model.Building;
import model.HomeBase;
import model.Terminal;

/**
 * Az altalanos epulet (Building, HomeBase, Terminal) megjelenitoje
 * a spec 11.3.1/13-15. (BuildingView, HomeBaseView, TerminalView)
 * szerint:
 *  - kitoltott teglalap a fillColor-rel
 *  - fekete keret
 *  - kozepre igazitott label (az ID)
 *  - HomeBase: sarga + "Áruház" jelveny
 *  - Terminal: kek + busz-pictogram
 */
public class BuildingView extends FieldView {

    /** A megfigyelt Building. */
    private final Building building;

    /**
     * Letrehoz egy BuildingView-t.
     *
     * @param id       Az epulet azonositoja.
     * @param building A megfigyelt Building.
     * @param bounds   A befoglalo teglalap.
     * @param host     A befoglalo Swing-panel.
     */
    public BuildingView(String id, Building building, Rectangle bounds, JPanel host) {
        super(id, building, bounds, host);
        this.building = building;
    }

    /**
     * Kirajzolja az epuletet a spec 11.3.1/13. algoritmusa szerint:
     *   1. g.setColor(fillColor); g.fillRect(bounds).
     *   2. g.setColor(BLACK); g.drawRect(bounds).
     *   3. g.drawString(label, kozeppont).
     *
     * Tipus-specifikus extra:
     *   - HomeBase: sarga $ jelveny a sarokba.
     *   - Terminal: BUS sav-pictogram a sarokba.
     *
     * @param g A celzott Graphics2D.
     */
    @Override
    public void draw(Graphics2D g) {
        Color fill;
        String label;
        if (building instanceof HomeBase) {
            fill = new Color(245, 215, 90);
            label = "HomeBase";
        } else if (building instanceof Terminal) {
            fill = new Color(110, 175, 230);
            label = "Terminal";
        } else {
            fill = new Color(190, 190, 195);
            label = "Building";
        }

        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;

        // 1. Kitoltott teglalap
        g.setColor(fill);
        g.fillRect(x, y, w, h);

        // 2. Fekete keret
        g.setColor(Color.BLACK);
        Stroke prev = g.getStroke();
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x, y, w, h);
        g.setStroke(prev);

        // Tipus-specifikus jelveny (kis ikon a bal-felso sarokba)
        if (building instanceof HomeBase) {
            // Sarga $ "aruhaz" jelveny
            g.setColor(new Color(255, 240, 90));
            g.fillOval(x + 4, y + 4, 18, 18);
            g.setColor(new Color(80, 50, 0));
            g.drawOval(x + 4, y + 4, 18, 18);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString("$", x + 9, y + 18);
        } else if (building instanceof Terminal) {
            // BUS jel
            g.setColor(new Color(245, 220, 90));
            g.fillRoundRect(x + 4, y + 4, 26, 14, 4, 4);
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 9));
            g.drawString("BUS", x + 7, y + 14);
        }

        // 3. Kozepre igazitott label (az ID a fontosabb, alatta a tipus)
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fmId = g.getFontMetrics();
        int idW = fmId.stringWidth(id);
        g.drawString(id, x + (w - idW) / 2, y + h / 2 + 4);

        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fmL = g.getFontMetrics();
        int lw = fmL.stringWidth(label);
        g.drawString(label, x + (w - lw) / 2, y + h - 6);
    }
}
