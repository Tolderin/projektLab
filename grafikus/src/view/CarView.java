package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import model.Car;

/**
 * NPC auto megjelenitoje. Kek negyzet, fehér "C" betuvel,
 * waitTurns esetén fakó tónus.
 */
public class CarView extends VehicleView {

    /** Az autora kasztolt referencia. */
    private final Car car;

    /**
     * Letrehoz egy CarView-t.
     *
     * @param id     A car ID-ja.
     * @param car    A megfigyelt Car.
     * @param layout Az aktiv MapLayout.
     * @param host   A befoglalo Swing-panel.
     */
    public CarView(String id, Car car, MapLayout layout, JPanel host) {
        super(id, car, layout, host);
        this.car = car;
    }

    /**
     * Kek negyzet feher "C" betuvel; waitTurns > 0 esetén fakó.
     *
     * @param g A celzott Graphics2D.
     */
    @Override
    public void draw(Graphics2D g) {
        Point c = computeCenter();
        if (c == null) {
            return;
        }
        boolean waiting = car.getWaitTurns() > 0;
        g.setColor(waiting ? new Color(60, 90, 150) : new Color(40, 110, 220));
        g.fillRoundRect(c.x - W / 2, c.y - H / 2, W, H, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("C", c.x - 4, c.y + 5);
        g.setColor(Color.BLACK);
        g.drawRoundRect(c.x - W / 2, c.y - H / 2, W, H, 6, 6);
    }
}
