package view;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import cli.Context;
import model.IObserver;
import model.Lane;
import model.Observable;
import model.Vehicle;

/**
 * A jarmu-nezetek kozos ososztalya. A jarmuvet az aktualis Lane vagy
 * Building pozicio kozepere rajzolja, kis offset-tel. Megfigyelo
 * (IObserver) tipusu, igy a Vehicle.notifyObservers("moved") jelzes
 * azonnal kivaltja a repaint-et.
 */
public abstract class VehicleView implements IObserver {

    /** A megfigyelt jarmu. */
    protected final Vehicle vehicle;

    /** A jarmu ID-ja a megjelenitett azonositohoz. */
    protected final String id;

    /** A befoglalo MapLayout, hogy a pozicio kerdezheto legyen. */
    protected final MapLayout layout;

    /** A befoglalo panel a repaint-hez. */
    protected final JPanel host;

    /** A jarmuvet abrazolo doboz szelessege pixelben. */
    public static final int W = 28;

    /** A jarmuvet abrazolo doboz magassaga pixelben. */
    public static final int H = 20;

    /**
     * Letrehoz egy VehicleView-t.
     *
     * @param id      A jarmu ID-ja.
     * @param vehicle A megfigyelt Vehicle.
     * @param layout  Az aktiv MapLayout.
     * @param host    A befoglalo panel (repaint cel).
     */
    protected VehicleView(String id, Vehicle vehicle, MapLayout layout, JPanel host) {
        this.id = id;
        this.vehicle = vehicle;
        this.layout = layout;
        this.host = host;
    }

    /**
     * Visszaadja a jarmu ID-jat.
     *
     * @return Az azonosito.
     */
    public String getId() {
        return id;
    }

    /**
     * Visszaadja a megfigyelt Vehicle-t.
     *
     * @return A jarmu.
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Lekerdezi a jarmu kepernyo-pozicio kozepet az aktualis
     * currentField bounds-ja alapjan. A sav-bounds mar a sarki
     * keresztezodes nelkul van (lasd MapLayout.shrinkLanesAtInter-
     * sections), igy a jarmu a sav KOZEPERE kerul.
     *
     * Ha tobb jarmu all ugyanazon a savon, az index alapjan a
     * sav fo-tengelye menten elosztva jelennek meg -- igy
     * egymas mellett, nem egymas tetejen.
     *
     * @return A kozep-pont a kepernyon, vagy null ha nincs hely.
     */
    public Point computeCenter() {
        if (vehicle.currentField == null) {
            return null;
        }
        String fieldId = Context.objectManager.getId(vehicle.currentField);
        if (fieldId == null) {
            return null;
        }
        Rectangle r = layout.getBounds(fieldId);
        if (r == null) {
            return null;
        }
        int cx = r.x + r.width / 2;
        int cy = r.y + r.height / 2;
        // Tobb-jarmu offset: ha Lane-en allunk es tobb jarmu is van
        // a savon, a fo-tengely menten elosztva helyezzuk el oket.
        if (vehicle.currentField instanceof Lane) {
            Lane lane = (Lane) vehicle.currentField;
            int total = lane.vehicles.size();
            int idx = lane.vehicles.indexOf(vehicle);
            if (total > 1 && idx >= 0) {
                int spacing = W + 4;
                int offset = (idx - (total - 1) / 2) * spacing;
                // Paratlan szamnal a kozepso pontosan a kozepen,
                // paroatlan szamnal egy fel-spacing offset:
                if (total % 2 == 0) {
                    offset += spacing / 2;
                }
                boolean horiz = r.width >= r.height;
                if (horiz) {
                    cx += offset;
                } else {
                    cy += offset;
                }
            }
        }
        return new Point(cx, cy);
    }

    /**
     * Visszaadja a jarmu kepi befoglalo teglalapjat az aktualis
     * pozicio koruli W x H meretu dobozkent.
     *
     * @return A befoglalo teglalap, vagy null ha nincs pozicio.
     */
    public Rectangle getDrawBounds() {
        Point c = computeCenter();
        if (c == null) {
            return null;
        }
        return new Rectangle(c.x - W / 2, c.y - H / 2, W, H);
    }

    /**
     * A megfigyelt jarmu valtozasakor (pl. "moved") ujrarajzolja
     * a befoglalo panelt.
     *
     * @param source A jelzes forrasa.
     * @param hint   A jelzes tipusa.
     */
    @Override
    public void update(Observable source, String hint) {
        if (host != null) {
            host.repaint();
        }
    }

    /**
     * Absztrakt rajzolas metodus a leszarmazottak szamara.
     *
     * @param g A celzott Graphics2D kontextus.
     */
    public abstract void draw(Graphics2D g);
}
