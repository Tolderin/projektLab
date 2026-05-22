package view;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import model.Field;
import model.IObserver;
import model.Observable;

/**
 * Az osszes mezo-nezet kozos ososztalya. Tartalmazza a megfigyelo
 * mintat (IObserver) es a kozos geometriai informaciot (bounds).
 * A leszarmazottak feluldefinialjak a draw(Graphics2D)-t a sajat
 * megjelenitesukre.
 */
public abstract class FieldView implements IObserver {

    /** A megfigyelt modell-elem. */
    protected final Field model;

    /** A mezo befoglalo teglalapja a kepernyon. */
    protected Rectangle bounds;

    /** A mezo ID-ja (a stringkimenetekhez). */
    protected final String id;

    /** A befoglalo GamePanel referenciaja a repaint() hivashoz. */
    protected final JPanel host;

    /**
     * Letrehoz egy FieldView-t, beallitva a kozos parametereket.
     *
     * @param id     A mezo egyedi azonositoja.
     * @param model  A megfigyelt modell-elem.
     * @param bounds A befoglalo teglalap.
     * @param host   A repaint-celkent szolgalo Swing-panel.
     */
    protected FieldView(String id, Field model, Rectangle bounds, JPanel host) {
        this.id = id;
        this.model = model;
        this.bounds = bounds;
        this.host = host;
    }

    /**
     * Visszaadja a befoglalo teglalapot.
     *
     * @return A bounds-rectangle.
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Atallitja a befoglalo teglalapot (pl. layout valtoztatasanal).
     *
     * @param b Az uj teglalap.
     */
    public void setBounds(Rectangle b) {
        this.bounds = b;
    }

    /**
     * Visszaadja a mezo azonositojat.
     *
     * @return Az ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Visszaadja a megfigyelt modell-elemet.
     *
     * @return A model.
     */
    public Field getModel() {
        return model;
    }

    /**
     * Tartalmazza-e a befoglalo teglalap a megadott pontot.
     *
     * @param p A pixel-koordinata.
     * @return true ha a mezo lefedi a pontot.
     */
    public boolean contains(Point p) {
        return bounds.contains(p);
    }

    /**
     * Absztrakt rajzolas-metodus, a leszarmazottak implementaljak.
     *
     * @param g A celzott Graphics2D kontextus.
     */
    public abstract void draw(Graphics2D g);

    /**
     * A megfigyelt modell-elem allapotvaltozasakor egyszeruen
     * ujrarajzoltatja a befoglalo Swing-panelt.
     *
     * @param source A jelzes forrasa.
     * @param hint   A valtozas tipusa.
     */
    @Override
    public void update(Observable source, String hint) {
        if (host != null) {
            host.repaint();
        }
    }
}
