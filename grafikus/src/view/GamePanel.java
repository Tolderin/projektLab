package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import model.IObserver;
import model.Observable;

/**
 * A palya rajzfelulete. paintComponent() ciklusban a GameRenderer
 * vegigmegy a regisztralt nezetekel; az egér- és billentyu-
 * esemenyeket az InputController-nek tovabbitja (az InputController
 * a getRenderer()/getLayout()-on keresztul fer hozza a kijelolt
 * elemekhez).
 *
 * Ket extra rajzelem: a kijelolt mezo zold kerete (ha van), es
 * mozgas-mod kek lebbeno keret a celpontokon (megjelenik a
 * legkozelebbi szomszedos mezoket emelve ki).
 */
public class GamePanel extends JPanel implements IObserver {

    /** A renderer, amely a regisztralt nezetekkel a rajzolast vegzi. */
    private GameRenderer renderer;

    /** Az aktualis kijelolt mezo ID-ja (zold keret). */
    private String selectedFieldId;

    /** Az aktualis kijelolt jarmu ID-ja (zold keret a karteren). */
    private String selectedVehicleId;

    /** A mozgas-mod aktiv-e (kek keretes celpontok jelennek meg). */
    private boolean moveModeHint;

    /**
     * Letrehoz egy GamePanel-t. A renderer-t kesobb a setRenderer
     * allitja be (a MainWindow-bol).
     */
    public GamePanel() {
        // Halvanyabb park-szürke hatter: a road-aszfalt (60,62,68)
        // viszonylag vilagosabb, igy az utak kontrasztosan kiallnak.
        setBackground(new Color(48, 58, 55));
        setFocusable(true);
    }

    /**
     * Beallitja az aktiv renderert.
     *
     * @param renderer A hasznalando GameRenderer.
     */
    public void setRenderer(GameRenderer renderer) {
        this.renderer = renderer;
        refreshPreferredSize();
    }

    /**
     * Visszaadja az aktiv renderert.
     *
     * @return A renderer.
     */
    public GameRenderer getRenderer() {
        return renderer;
    }

    /**
     * Beallitja a kijelolt mezo ID-jat.
     *
     * @param id Az ID, vagy null ha nincs kijeloles.
     */
    public void setSelectedFieldId(String id) {
        this.selectedFieldId = id;
        repaint();
    }

    /**
     * Beallitja a kijelolt jarmu ID-jat.
     *
     * @param id Az ID, vagy null ha nincs kijeloles.
     */
    public void setSelectedVehicleId(String id) {
        this.selectedVehicleId = id;
        repaint();
    }

    /**
     * Visszaadja a kijelolt mezo ID-jat.
     *
     * @return A kijelolt mezo ID-ja, vagy null.
     */
    public String getSelectedFieldId() {
        return selectedFieldId;
    }

    /**
     * Visszaadja a kijelolt jarmu ID-jat.
     *
     * @return A kijelolt jarmu ID-ja, vagy null.
     */
    public String getSelectedVehicleId() {
        return selectedVehicleId;
    }

    /**
     * Aktivalja/deaktivalja a mozgas-mod kijelzest (kek keretes
     * celpontok a kijelolt jarmu szomszedos mezoin).
     *
     * @param on true ha bekapcsolva.
     */
    public void setMoveModeHint(boolean on) {
        this.moveModeHint = on;
        repaint();
    }

    /**
     * Visszaszamolja a panel preferalt meretet a layout szerint.
     */
    public void refreshPreferredSize() {
        if (renderer != null) {
            Rectangle r = renderer.getLayout().getMapBounds();
            setPreferredSize(new Dimension(r.width, r.height));
        } else {
            setPreferredSize(new Dimension(800, 600));
        }
        revalidate();
    }

    /**
     * Felulirt rajzolas: a teljes terkep + ket kijeloles overlay.
     *
     * @param g A celzott Graphics.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (renderer == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderer.render(g2);

        // Kijelolt mezo overlay
        if (selectedFieldId != null) {
            Rectangle r = renderer.getLayout().getBounds(selectedFieldId);
            if (r != null) {
                g2.setColor(new Color(100, 230, 100, 200));
                g2.drawRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);
            }
        }

        // Kijelolt jarmu jelzes
        if (selectedVehicleId != null) {
            for (VehicleView vv : renderer.getVehicleViews()) {
                if (vv.getId().equals(selectedVehicleId)) {
                    Rectangle b = vv.getDrawBounds();
                    if (b != null) {
                        g2.setColor(new Color(100, 230, 100, 220));
                        g2.drawRect(b.x - 2, b.y - 2, b.width + 4, b.height + 4);
                    }
                    break;
                }
            }
        }

        // Mozgas-mod: a kijelolt jarmu szomszedos mezoit kekkel emeljük ki
        if (moveModeHint && selectedVehicleId != null) {
            highlightNeighbors(g2);
        }
    }

    /**
     * Kek keret-overlay-ek a kijelolt jarmu szomszedos mezoinek
     * jelzesehez (mozgas-cel hint).
     *
     * @param g2 A celzott Graphics2D.
     */
    private void highlightNeighbors(Graphics2D g2) {
        VehicleView vv = findVehicleView(selectedVehicleId);
        if (vv == null || vv.getVehicle().currentField == null) {
            return;
        }
        for (model.Field n : vv.getVehicle().currentField.getNeighbors()) {
            String nid = cli.Context.objectManager.getId(n);
            if (nid != null) {
                Rectangle r = renderer.getLayout().getBounds(nid);
                if (r != null) {
                    g2.setColor(new Color(80, 160, 255, 220));
                    g2.drawRect(r.x - 1, r.y - 1, r.width + 2, r.height + 2);
                }
            }
        }
    }

    /**
     * Visszakeresi a megadott azonositoju VehicleView-t.
     *
     * @param id A jarmu ID-ja.
     * @return A VehicleView, vagy null ha nem talalhato.
     */
    private VehicleView findVehicleView(String id) {
        if (id == null || renderer == null) {
            return null;
        }
        for (VehicleView vv : renderer.getVehicleViews()) {
            if (vv.getId().equals(id)) {
                return vv;
            }
        }
        return null;
    }

    /**
     * Egy adott kepernyo-kattintasi pontra eso jarmu-nezetet keresi
     * a regisztralt vehicleViews listaban -- visszafele iteralva, igy
     * az utoljara felvett (a felso) lesz a talalat. Az InputController
     * hasznalja a kijelolesi logikahoz.
     *
     * @param p A kattintasi pont (panel-koordinatakban).
     * @return A talalt VehicleView, vagy null.
     */
    public VehicleView pickVehicle(Point p) {
        if (renderer == null) {
            return null;
        }
        for (int i = renderer.getVehicleViews().size() - 1; i >= 0; i--) {
            VehicleView vv = renderer.getVehicleViews().get(i);
            Rectangle r = vv.getDrawBounds();
            if (r != null && r.contains(p)) {
                return vv;
            }
        }
        return null;
    }

    /**
     * Push-jelzesre csak ujrarajzol (a modell-elemek konkret kezelese
     * a sajat FieldView/VehicleView IObserver-eken keresztul tortenik).
     *
     * @param source A jelzes forrasa.
     * @param hint   A valtozas tipusa.
     */
    @Override
    public void update(Observable source, String hint) {
        repaint();
    }
}
