package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.swing.JPanel;

import model.Lane;

/**
 * Egy Lane (forgalmi sáv) grafikus megjelenitoje. A snowDepth,
 * isFrozen, gravelDepth, saltEffect es isBlocked attributumokat
 * overlay-kkel abrazolja a spec 11.1.2 szerint.
 *
 * A sáv NEM rajzol kulon hattert -- a road-aszfalt (a RoadView altal
 * rajzolt sotetebb szurke 60,62,68) latszik a sáv mogul. Ezzel az
 * utak FOLYTONOSAN latszanak a keresztezodeseken is, nem csikozodva
 * a vilagosabb sáv-hattértol.
 */
public class LaneView extends FieldView {

    /** A megfigyelt Lane-objektum tipus-pontos referenciaja. */
    private final Lane lane;

    /** Az irány-jelzo iránya: 0=jobb, 1=le, 2=bal, 3=fel. */
    private int direction = 0;

    /**
     * Letrehoz egy LaneView-t.
     *
     * @param id     A sav azonositoja.
     * @param lane   A megfigyelt Lane.
     * @param bounds A befoglalo teglalap.
     * @param host   A befoglalo Swing-panel a repaint-hez.
     */
    public LaneView(String id, Lane lane, Rectangle bounds, JPanel host) {
        super(id, lane, bounds, host);
        this.lane = lane;
    }

    /**
     * Beallitja a direction-arrow iranyat.
     *
     * @param direction 0=jobbra, 1=le, 2=balra, 3=fel.
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Kirajzolja a sáv overlay-jeit a road-aszfalt fole.
     *
     * @param g A celzott Graphics2D.
     */
    @Override
    public void draw(Graphics2D g) {
        // Ho overlay: feher reteg, snowDepth-aranyosan
        if (lane.snowDepth > 0) {
            float ratio = Math.min(1.0f, (float) (lane.snowDepth / 5.0));
            int alpha = (int) (160 + ratio * 80);
            g.setColor(new Color(255, 255, 255, alpha));
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        // Jeg overlay: kekes, fel-attetszo
        if (lane.isFrozen) {
            g.setColor(new Color(140, 200, 245, 200));
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            // Jegszilank-mintazat
            g.setColor(new Color(220, 240, 255, 200));
            for (int i = 0; i < 8; i++) {
                int dx = bounds.x + 4 + ((i * 19) % (bounds.width - 8));
                int dy = bounds.y + 4 + ((i * 11) % (bounds.height - 8));
                g.drawLine(dx, dy, dx + 4, dy - 4);
                g.drawLine(dx, dy, dx - 4, dy + 4);
            }
        }

        // Zuzalek: barnasszurke pottyok
        if (lane.gravelDepth > 0) {
            g.setColor(new Color(110, 90, 60));
            int dots = Math.min(20, (int) (lane.gravelDepth * 5));
            for (int i = 0; i < dots; i++) {
                int dx = bounds.x + 5 + ((i * 17) % Math.max(1, bounds.width - 10));
                int dy = bounds.y + 5 + ((i * 13) % Math.max(1, bounds.height - 10));
                g.fillOval(dx, dy, 4, 4);
            }
        }

        // Sohatas: zoldes glow keret
        if (lane.saltEffect > 0) {
            Stroke prev = g.getStroke();
            g.setStroke(new BasicStroke(3f));
            g.setColor(new Color(120, 220, 120, 200));
            g.drawRect(bounds.x + 2, bounds.y + 2,
                    bounds.width - 4, bounds.height - 4);
            g.setStroke(prev);
        }

        // Blocked: piros atloscsikok
        if (lane.isBlocked()) {
            g.setColor(new Color(220, 60, 60, 200));
            Stroke prev = g.getStroke();
            g.setStroke(new BasicStroke(3f));
            g.drawLine(bounds.x, bounds.y,
                    bounds.x + bounds.width, bounds.y + bounds.height);
            g.drawLine(bounds.x + bounds.width, bounds.y,
                    bounds.x, bounds.y + bounds.height);
            g.setStroke(prev);
        }

        // Iranymutato nyil a sav elso negyeden (NEM a kozepen, hogy ne
        // legyen a keresztezodesben). A felhasznaloi sav-ID-t a stat
        // parancs adja ki -- itt a sávra NEM rajzolunk feliratot.
        if (lane.snowDepth < 0.5 && !lane.isFrozen) {
            drawDirectionArrow(g);
        }
    }

    /**
     * Egy iranyjelzo nyilat rajzol a sáv elso negyeden, hogy ne
     * essen a keresztezodesbe (ami a sáv kozepen helyezkedik el).
     *
     * @param g A celzott Graphics2D.
     */
    private void drawDirectionArrow(Graphics2D g) {
        int cx, cy;
        if (bounds.width >= bounds.height) {
            // Horizontalis sáv: elso negyed
            cx = bounds.x + bounds.width / 4;
            cy = bounds.y + bounds.height / 2;
        } else {
            // Vertikalis sáv: elso negyed
            cx = bounds.x + bounds.width / 2;
            cy = bounds.y + bounds.height / 4;
        }
        int sz = 7;
        g.setColor(new Color(255, 255, 255, 150));
        int[] xs, ys;
        switch (direction) {
            case 0: // jobbra
                xs = new int[] { cx - sz, cx + sz, cx - sz };
                ys = new int[] { cy - sz / 2, cy, cy + sz / 2 };
                break;
            case 1: // le
                xs = new int[] { cx - sz / 2, cx + sz / 2, cx };
                ys = new int[] { cy - sz, cy - sz, cy + sz };
                break;
            case 2: // balra
                xs = new int[] { cx + sz, cx - sz, cx + sz };
                ys = new int[] { cy - sz / 2, cy, cy + sz / 2 };
                break;
            case 3: // fel
                xs = new int[] { cx - sz / 2, cx + sz / 2, cx };
                ys = new int[] { cy + sz, cy + sz, cy - sz };
                break;
            default:
                return;
        }
        g.fillPolygon(xs, ys, 3);
    }
}
