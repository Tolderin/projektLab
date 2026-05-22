package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.util.List;

/**
 * Egy Road grafikus megjelenitese: az aszfalt-szinu hatter, amelyre
 * a sávok rajzolódnak. A road maga nem Field; itt csak a kontener-
 * keretert es a sárga középvonalert felel.
 *
 * Az ut FOLYTONOSAN latszik: az aszfalt-hatter az egesz road bounds-
 * on, beleertve a keresztezodeseket is (mert a sav-hatter atlatszik
 * a road-aszfalton). A sárga kozepvonal csak a keresztezodeseken
 * kívul rajzolodik (clip-pel kivagva), igy a kereszteződéseknél
 * tisztán latszik az aszfalt.
 */
public class RoadView {

    /** A road azonositoja a feliratozashoz. */
    private final String id;

    /** A road befoglalo teglalapja (a sávok egyutt). */
    private final Rectangle bounds;

    /** Horizontalisan helyezkedik-e el a road. */
    private final boolean horizontal;

    /** Hany forward lane van. */
    private final int forwardCount;

    /** Hany backward lane van. */
    private final int backwardCount;

    /** A layout, hogy a kereszteződés-teglalapok lekerdezhetok legyenek. */
    private final MapLayout layout;

    /**
     * Letrehoz egy RoadView-t.
     *
     * @param id            A road azonositoja.
     * @param bounds        A road befoglalo teglalapja.
     * @param horizontal    true ha horizontalis.
     * @param forwardCount  A forwardLanes meret.
     * @param backwardCount A backwardLanes meret.
     * @param layout        A MapLayout (intersection-info lekerdezesehez).
     */
    public RoadView(String id, Rectangle bounds, boolean horizontal,
                    int forwardCount, int backwardCount, MapLayout layout) {
        this.id = id;
        this.bounds = bounds;
        this.horizontal = horizontal;
        this.forwardCount = forwardCount;
        this.backwardCount = backwardCount;
        this.layout = layout;
    }

    /**
     * Kirajzolja a roadot: aszfalt-szinu hatter + sárga közepvonal a
     * keresztezodeseken kivul.
     *
     * @param g A celzott Graphics2D.
     */
    public void draw(Graphics2D g) {
        // 1. Aszfalt hatter az egesz road bounds-on
        g.setColor(new Color(60, 62, 68));
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // 2. Sárga középvonal (csak ha mind forward, mind backward sáv letezik),
        //    a keresztezodeseken kivagva.
        if (forwardCount > 0 && backwardCount > 0) {
            Shape oldClip = g.getClip();
            // Egy area-t epitunk, ami a road bounds MINUSZ a kereszteződés-teglalapok
            Area roadArea = new Area(bounds);
            if (layout != null) {
                List<Rectangle> intersects = layout.getIntersectionRects();
                for (Rectangle ir : intersects) {
                    // Sajat magat ne vegye figyelembe
                    if (ir.equals(bounds)) continue;
                    roadArea.subtract(new Area(ir));
                }
            }
            g.setClip(roadArea);
            Stroke prev = g.getStroke();
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10f,
                    new float[] { 12f, 6f }, 0f));
            g.setColor(new Color(245, 215, 80));
            if (horizontal) {
                int yMid = bounds.y + forwardCount * MapLayout.LANE_BREADTH;
                g.drawLine(bounds.x + 4, yMid, bounds.x + bounds.width - 4, yMid);
            } else {
                int xMid = bounds.x + forwardCount * MapLayout.LANE_BREADTH;
                g.drawLine(xMid, bounds.y + 4, xMid, bounds.y + bounds.height - 4);
            }
            g.setStroke(prev);
            g.setClip(oldClip);
        }
    }

    /**
     * Visszaadja a road ID-jat.
     *
     * @return Az azonosito.
     */
    public String getId() {
        return id;
    }

    /**
     * Visszaadja a befoglalo teglalapot.
     *
     * @return A bounds.
     */
    public Rectangle getBounds() {
        return bounds;
    }
}
