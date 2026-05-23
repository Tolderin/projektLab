package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
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

        // Jeg overlay: erosen lathato, 13. heti vizualis ujitas.
        // KLIP: a hatch-vonalak es a szilank-mintazat a bounds-on belul
        // maradnak, igy nem nyulnak at a szomszedos savokra / road
        // overshoot teruletekre.
        if (lane.isFrozen) {
            int w = bounds.width;
            int h = bounds.height;
            Shape oldClip = g.getClip();
            Stroke prevStroke = g.getStroke();
            g.setClip(bounds);
            // 1. Telitett kek base
            g.setColor(new Color(95, 165, 235, 230));
            g.fillRect(bounds.x, bounds.y, w, h);
            // 2. Atloos vilagosabb cyan hatchel-csikok (jeg-csillogas)
            g.setColor(new Color(210, 235, 255, 170));
            g.setStroke(new BasicStroke(1.6f));
            int step = 12;
            int hatchEnd = w + h;
            for (int i = -h; i < hatchEnd; i += step) {
                g.drawLine(bounds.x + i, bounds.y,
                        bounds.x + i + h, bounds.y + h);
            }
            // 3. Vastagabb jeg-szilank cross-minta
            g.setColor(new Color(245, 252, 255, 235));
            g.setStroke(new BasicStroke(2.2f));
            int shards = Math.max(5, (w / 28) + (h / 28));
            for (int i = 0; i < shards; i++) {
                int dx = bounds.x + 6 + ((i * 23) % Math.max(1, w - 12));
                int dy = bounds.y + 6 + ((i * 17) % Math.max(1, h - 12));
                int sz = 7;
                g.drawLine(dx, dy, dx + sz, dy - sz);
                g.drawLine(dx, dy, dx - sz, dy + sz);
            }
            // 4. Vastag sotetkek border (warning)
            g.setColor(new Color(30, 80, 165, 240));
            g.setStroke(new BasicStroke(3f));
            g.drawRect(bounds.x + 1, bounds.y + 1, w - 2, h - 2);
            g.setStroke(prevStroke);
            g.setClip(oldClip);
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
