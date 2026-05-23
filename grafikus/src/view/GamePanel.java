package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

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
        // 13. heti vegleges: havas tel-tema hatter (vilagosabb,
        // hideg kekes-szurke), igy a road-aszfalt (sotetebb szurke)
        // kontrasztosan kiall, es a fak/hegyek dekoraciok lathatoak.
        setBackground(new Color(64, 80, 96));
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

        // 1. Tel-hangulatu hattermintazat (kis feher hopelyhe-szeru
        // pontok elosztva a panel teljes teruleten). Statikus, igy a
        // panel meretetol fugg, nem a kor-szamtol.
        drawSnowBackground(g2);

        // 2. Background dekoraciok (fak, sziklak, hopelyhek) -- a
        // road-ok ELOTT rajzolva, igy az aszfalt felulrol fed.
        MapLayout layout = renderer.getLayout();
        for (Decoration d : layout.getDecorations()) {
            if (!d.isOverlay()) {
                drawDecoration(g2, d);
            }
        }

        // 3. A modell-elemek (road -> field -> vehicle) renderelese
        renderer.render(g2);

        // 4. Overlay dekoraciok (hegyek, hid-ivek) -- a road-okra
        // RÁTÉVE, az alagut/hid illuzio adása végett.
        for (Decoration d : layout.getDecorations()) {
            if (d.isOverlay()) {
                drawDecoration(g2, d);
            }
        }

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

    /**
     * A panel hátteren elosztott apro feher pontokkal egy enyhe
     * "havazas" textura. Static raster: kiszamolt pontok rogzitettek
     * a panel x/y koordinatak alapjan, nem mozognak.
     *
     * @param g2 A Graphics2D context.
     */
    private void drawSnowBackground(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        // Halvanyabb feher pontok suru elosztasban: a panel haver
        // bevilagitott teli-erzese
        g2.setColor(new Color(220, 230, 240, 110));
        int step = 18;
        for (int y = 6; y < h; y += step) {
            int offset = ((y / step) % 2 == 0) ? 0 : step / 2;
            for (int x = 6 + offset; x < w; x += step) {
                g2.fillOval(x, y, 2, 2);
            }
        }
    }

    /**
     * Kirajzol egy adott dekoraciot a tipusa szerint.
     *
     * @param g2 A Graphics2D context.
     * @param d  A dekoracio.
     */
    private void drawDecoration(Graphics2D g2, Decoration d) {
        Rectangle b = d.bounds;
        switch (d.type) {
            case TREE:
                drawTree(g2, b);
                break;
            case ROCK:
                drawRock(g2, b);
                break;
            case SNOWFLAKE:
                drawSnowflake(g2, b);
                break;
            case MOUNTAIN:
                drawMountain(g2, b);
                break;
            case BRIDGE:
                drawBridge(g2, b);
                break;
            default:
        }
    }

    /**
     * Fenyofa: barna torzs + zold haromszog.
     */
    private void drawTree(Graphics2D g2, Rectangle b) {
        int trunkW = Math.max(3, b.width / 4);
        int trunkH = Math.max(4, b.height / 4);
        // Torzs
        g2.setColor(new Color(90, 60, 30));
        g2.fillRect(b.x + (b.width - trunkW) / 2, b.y + b.height - trunkH,
                trunkW, trunkH);
        // Tujak (haromszog)
        int[] xs = { b.x + b.width / 2,
                     b.x,
                     b.x + b.width };
        int[] ys = { b.y,
                     b.y + b.height - trunkH,
                     b.y + b.height - trunkH };
        g2.setColor(new Color(45, 110, 60));
        g2.fillPolygon(xs, ys, 3);
        // Hokarak (kis feher pont a tujak tetejen)
        g2.setColor(new Color(245, 250, 255, 220));
        g2.fillOval(b.x + b.width / 2 - 2, b.y - 1, 4, 4);
    }

    /**
     * Szikla: szurke ovalis + sotetebb arnyek.
     */
    private void drawRock(Graphics2D g2, Rectangle b) {
        g2.setColor(new Color(110, 110, 120));
        g2.fillOval(b.x, b.y, b.width, b.height);
        g2.setColor(new Color(80, 80, 90));
        g2.drawOval(b.x, b.y, b.width, b.height);
        // Felso fenyfolt
        g2.setColor(new Color(180, 180, 195, 180));
        g2.fillArc(b.x + 2, b.y + 1, b.width - 4, b.height / 2, 20, 140);
    }

    /**
     * Hopelyhe: feher "*" karakter.
     */
    private void drawSnowflake(Graphics2D g2, Rectangle b) {
        int cx = b.x + b.width / 2;
        int cy = b.y + b.height / 2;
        int r = Math.min(b.width, b.height) / 2;
        Stroke prev = g2.getStroke();
        g2.setStroke(new BasicStroke(1.6f));
        g2.setColor(new Color(245, 250, 255, 230));
        g2.drawLine(cx - r, cy, cx + r, cy);
        g2.drawLine(cx, cy - r, cx, cy + r);
        int d = (int) (r * 0.7);
        g2.drawLine(cx - d, cy - d, cx + d, cy + d);
        g2.drawLine(cx - d, cy + d, cx + d, cy - d);
        g2.setStroke(prev);
    }

    /**
     * Hegy: szurke haromszog feher havas csuccsal. Egy nagyobb
     * objektum, amely a road-ra ratevodve "alagut" illuziot ad
     * (a road tovabbra is reszben latszik).
     */
    private void drawMountain(Graphics2D g2, Rectangle b) {
        int peakX = b.x + b.width / 2;
        int peakY = b.y;
        // Sotetszurke hegy-test
        int[] xs = { b.x, b.x + b.width, peakX };
        int[] ys = { b.y + b.height, b.y + b.height, peakY };
        g2.setColor(new Color(95, 95, 105, 235));
        g2.fillPolygon(xs, ys, 3);
        g2.setColor(new Color(40, 40, 50, 235));
        g2.drawPolygon(xs, ys, 3);
        // Havas csucs (kisebb haromszog felulrol)
        int snowH = b.height / 3;
        int[] xsSnow = {
                b.x + b.width / 3,
                b.x + 2 * b.width / 3,
                peakX
        };
        int[] ysSnow = {
                b.y + snowH,
                b.y + snowH,
                peakY
        };
        g2.setColor(new Color(245, 250, 255, 230));
        g2.fillPolygon(xsSnow, ysSnow, 3);
        // Alagut-iv jelolesere a hegy aljara (sotetebb iv)
        int archW = Math.max(20, b.width / 4);
        int archH = Math.max(12, b.height / 4);
        int archX = peakX - archW / 2;
        int archY = b.y + b.height - archH;
        g2.setColor(new Color(20, 20, 30, 245));
        g2.fillArc(archX, archY, archW, archH * 2, 0, 180);
        // "TUNNEL" felirat halvanyan
        g2.setColor(new Color(245, 240, 220, 210));
        g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 9));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String txt = "TUNNEL";
        g2.drawString(txt,
                peakX - fm.stringWidth(txt) / 2,
                b.y + b.height - archH - 4);
    }

    /**
     * Hid: ko-szinu iv a road folott, ket pillarrel a vegein.
     */
    private void drawBridge(Graphics2D g2, Rectangle b) {
        // Ko-szinu pillar bal
        int pillarW = Math.max(8, b.width / 8);
        g2.setColor(new Color(150, 145, 130));
        g2.fillRect(b.x, b.y, pillarW, b.height);
        g2.setColor(new Color(95, 90, 80));
        g2.drawRect(b.x, b.y, pillarW, b.height);
        // Pillar jobb
        g2.setColor(new Color(150, 145, 130));
        g2.fillRect(b.x + b.width - pillarW, b.y, pillarW, b.height);
        g2.setColor(new Color(95, 90, 80));
        g2.drawRect(b.x + b.width - pillarW, b.y, pillarW, b.height);
        // Iv (felso resz)
        int archH = Math.max(8, b.height / 3);
        Stroke prev = g2.getStroke();
        g2.setStroke(new BasicStroke(3.5f));
        g2.setColor(new Color(170, 165, 150));
        g2.drawArc(b.x, b.y - archH / 2,
                b.width, archH, 0, 180);
        g2.setStroke(prev);
        // "BRIDGE" felirat halvanyan
        g2.setColor(new Color(245, 240, 220, 210));
        g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 9));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String txt = "BRIDGE";
        g2.drawString(txt,
                b.x + b.width / 2 - fm.stringWidth(txt) / 2,
                b.y - archH / 2 - 4);
    }
}
