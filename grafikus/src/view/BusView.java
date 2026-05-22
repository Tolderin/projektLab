package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import model.Bus;

/**
 * A busz vizualis megjelenitese. Sarga hosszukas teglalap, fekete
 * "B" betuvel; ha a busz nem mukodik (collide/slip utan), a tonus
 * fakul es disabledTurnsLeft kis szammal jelenik meg.
 */
public class BusView extends VehicleView {

    /** A buszra kasztolt referencia. */
    private final Bus bus;

    /** A busz dobozszelessege (kicsivel hosszabb, mint a Car/Plow). */
    private static final int BUS_W = 36;

    /**
     * Letrehoz egy BusView-t.
     *
     * @param id     A busz ID-ja.
     * @param bus    A megfigyelt Bus.
     * @param layout Az aktiv MapLayout.
     * @param host   A befoglalo Swing-panel.
     */
    public BusView(String id, Bus bus, MapLayout layout, JPanel host) {
        super(id, bus, layout, host);
        this.bus = bus;
    }

    /**
     * Sarga teglalap fekete "B" betuvel; disabled allapot vizualisan
     * is megkulonboztetve.
     *
     * @param g A celzott Graphics2D.
     */
    @Override
    public void draw(Graphics2D g) {
        Point c = computeCenter();
        if (c == null) {
            return;
        }
        Color body = bus.isFunctioning
                ? new Color(245, 200, 60)
                : new Color(170, 145, 60);
        g.setColor(body);
        g.fillRoundRect(c.x - BUS_W / 2, c.y - H / 2, BUS_W, H, 6, 6);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("B", c.x - 4, c.y + 5);
        if (!bus.isFunctioning) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.drawString(String.valueOf(bus.disabledTurnsLeft),
                    c.x + BUS_W / 2 - 8, c.y + H / 2 - 2);
        }
        g.drawRoundRect(c.x - BUS_W / 2, c.y - H / 2, BUS_W, H, 6, 6);
    }
}
