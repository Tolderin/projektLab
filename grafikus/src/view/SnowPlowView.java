package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import model.CleanerHead;
import model.DragonHead;
import model.GravelHead;
import model.IcebreakerHead;
import model.SaltHead;
import model.SnowPlow;
import model.SweepHead;
import model.ThrowerHead;

/**
 * A hokotro grafikus megjelenitese. Piros tank-szeru forma, az aktiv
 * fej ket-karakteres rovid kodjaval (SW/TH/IB/SA/DR/GR).
 */
public class SnowPlowView extends VehicleView {

    /** A SnowPlow-ra kasztolt referencia. */
    private final SnowPlow plow;

    /**
     * Letrehoz egy SnowPlowView-t.
     *
     * @param id     A hokotro ID-ja.
     * @param plow   A megfigyelt SnowPlow.
     * @param layout Az aktiv MapLayout.
     * @param host   A befoglalo Swing-panel.
     */
    public SnowPlowView(String id, SnowPlow plow, MapLayout layout, JPanel host) {
        super(id, plow, layout, host);
        this.plow = plow;
    }

    /**
     * Piros tank-forma a fej kétkarakteres rövidítésével.
     *
     * @param g A celzott Graphics2D.
     */
    @Override
    public void draw(Graphics2D g) {
        Point c = computeCenter();
        if (c == null) {
            return;
        }
        g.setColor(new Color(195, 50, 50));
        g.fillRoundRect(c.x - W / 2, c.y - H / 2, W, H, 4, 4);
        // Tank-szeru kis kup ele
        g.setColor(new Color(155, 35, 35));
        int[] xs = { c.x + W / 2, c.x + W / 2 + 6, c.x + W / 2 };
        int[] ys = { c.y - 4, c.y, c.y + 4 };
        g.fillPolygon(xs, ys, 3);
        // Aktiv fej kodja
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        String code = headCode(plow.activeHead);
        g.drawString(code, c.x - 8, c.y + 4);
        g.setColor(Color.BLACK);
        g.drawRoundRect(c.x - W / 2, c.y - H / 2, W, H, 4, 4);
    }

    /**
     * Visszaadja a fej kétkarakteres kódját a 11.1.2 alapján.
     *
     * @param head Az aktiv fej.
     * @return A "SW"/"TH"/"IB"/"SA"/"DR"/"GR" kod.
     */
    private String headCode(CleanerHead head) {
        if (head instanceof SweepHead) return "SW";
        if (head instanceof ThrowerHead) return "TH";
        if (head instanceof IcebreakerHead) return "IB";
        if (head instanceof SaltHead) return "SA";
        if (head instanceof DragonHead) return "DR";
        if (head instanceof GravelHead) return "GR";
        return "?";
    }
}
