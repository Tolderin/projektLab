package controller;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import cli.Context;
import model.Building;
import model.Bus;
import model.Car;
import model.IObserver;
import model.Lane;
import model.Observable;
import model.Player;
import model.Road;
import model.SnowPlow;
import view.BuildingView;
import view.BusView;
import view.CarView;
import view.GameRenderer;
import view.HUDPanel;
import view.LaneView;
import view.MapLayout;
import view.RoadView;
import view.Scoreboard;
import view.SnowPlowView;
import view.TurnIndicatorPanel;

/**
 * Load utan vagy uj jatek inditasakor osszerendeli a modell-elemeket
 * a megfelelo nezetekkel. Minden modell-elemre letrehoz egy
 * IObserver-t, beallitja a megfigyelo-kapcsolatot es beregisztralja
 * a GameRenderer-be.
 */
public class ViewBinder {

    /** A regisztralt megfigyelok listaja, kesobbi unbindAll-hoz. */
    private final List<Bind> registered = new ArrayList<>();

    /**
     * Belso segedrekord: egy IObserver es a forrasa, hogy unbind
     * eseten le tudjuk iratkoztatni.
     */
    private static class Bind {

        /** A megfigyelt forras. */
        final Observable source;

        /** A feliratkozott megfigyelo. */
        final IObserver obs;

        /**
         * Egy bind-rekord letrehozasa.
         *
         * @param source A megfigyelt forras.
         * @param obs    A megfigyelo.
         */
        Bind(Observable source, IObserver obs) {
            this.source = source;
            this.obs = obs;
        }
    }

    /**
     * Letrehozza es osszerendel minden modell-elemhez egy nezetet.
     *
     * @param renderer            Az aktiv GameRenderer.
     * @param host                A befoglalo Swing-panel (repaint cel).
     * @param hud                 A frissitendo HUDPanel.
     * @param turnIndicatorPanel  A frissitendo TurnIndicatorPanel,
     *                            ami a GameLogic-ra es a Player-ekre
     *                            iratkozik fel (lehet null is).
     */
    public void bindAll(GameRenderer renderer, JPanel host, HUDPanel hud,
                        TurnIndicatorPanel turnIndicatorPanel,
                        Scoreboard scoreboard) {
        unbindAll();
        renderer.clear();
        MapLayout layout = renderer.getLayout();
        // Ha a hivо (pl. DefaultDemo) mar elozetesen beallitotta a
        // road-pozíciókat, NE futtassuk az autoLayout-ot (csak a
        // building-eket és maradek field-eket helyezzuk el). Egyebkent
        // a connect_fields graf alapjan generaljuk a teljes layoutot.
        if (layout.getAllRoadBounds().isEmpty()) {
            layout.autoLayout();
        } else {
            layout.placeBuildingsAndMissing();
        }

        // Roadok elsokent (a renderer-hez)
        for (java.util.Map.Entry<String, Rectangle> e
                : layout.getAllRoadBounds().entrySet()) {
            String rid = e.getKey();
            Object o = Context.objectManager.getObject(rid);
            if (o instanceof Road) {
                Road r = (Road) o;
                RoadView rv = new RoadView(rid, e.getValue(),
                        layout.isRoadHorizontal(rid),
                        r.forwardLanes.size(), r.backwardLanes.size(),
                        layout);
                renderer.registerRoad(rv);
            }
        }

        for (java.util.Map.Entry<String, Object> e
                : Context.objectManager.getAll().entrySet()) {
            String id = e.getKey();
            Object obj = e.getValue();
            Rectangle b = layout.getBounds(id);
            if (obj instanceof Lane) {
                LaneView lv = new LaneView(id, (Lane) obj, b, host);
                lv.setDirection(directionForLane(id, layout));
                ((Lane) obj).addObserver(lv);
                registered.add(new Bind((Lane) obj, lv));
                renderer.registerField(lv);
            } else if (obj instanceof Building) {
                BuildingView bv = new BuildingView(id, (Building) obj, b, host);
                ((Building) obj).addObserver(bv);
                registered.add(new Bind((Building) obj, bv));
                renderer.registerField(bv);
            } else if (obj instanceof SnowPlow) {
                SnowPlowView sv = new SnowPlowView(id, (SnowPlow) obj, layout, host);
                ((SnowPlow) obj).addObserver(sv);
                registered.add(new Bind((SnowPlow) obj, sv));
                renderer.registerVehicle(sv);
            } else if (obj instanceof Car) {
                CarView cv = new CarView(id, (Car) obj, layout, host);
                ((Car) obj).addObserver(cv);
                registered.add(new Bind((Car) obj, cv));
                renderer.registerVehicle(cv);
            } else if (obj instanceof Bus) {
                BusView bv = new BusView(id, (Bus) obj, layout, host);
                ((Bus) obj).addObserver(bv);
                registered.add(new Bind((Bus) obj, bv));
                renderer.registerVehicle(bv);
            } else if (obj instanceof Player) {
                ((Player) obj).addObserver(hud);
                registered.add(new Bind((Player) obj, hud));
                if (turnIndicatorPanel != null) {
                    ((Player) obj).addObserver(turnIndicatorPanel);
                    registered.add(new Bind((Player) obj, turnIndicatorPanel));
                }
                if (scoreboard != null) {
                    ((Player) obj).addObserver(scoreboard);
                    registered.add(new Bind((Player) obj, scoreboard));
                }
            }
        }
        if (Context.gameLogic instanceof Observable) {
            Observable gl = (Observable) Context.gameLogic;
            gl.addObserver(hud);
            registered.add(new Bind(gl, hud));
            if (turnIndicatorPanel != null) {
                gl.addObserver(turnIndicatorPanel);
                registered.add(new Bind(gl, turnIndicatorPanel));
            }
            if (scoreboard != null) {
                gl.addObserver(scoreboard);
                registered.add(new Bind(gl, scoreboard));
            }
        }
    }

    /**
     * Visszaadja egy adott sav irányát a befoglalo road alakja es a
     * sáv pozicioja alapjan. A road forward sávjai a kanonikus
     * irányba (jobb / le); a backward fordítva.
     *
     * @param laneId A sáv ID-ja.
     * @param layout Az aktiv MapLayout.
     * @return 0=jobb, 1=le, 2=bal, 3=fel; default 0.
     */
    private int directionForLane(String laneId, MapLayout layout) {
        for (java.util.Map.Entry<String, Object> e
                : Context.objectManager.getAll().entrySet()) {
            if (!(e.getValue() instanceof Road)) continue;
            Road r = (Road) e.getValue();
            String rid = e.getKey();
            boolean horiz = layout.isRoadHorizontal(rid);
            for (int i = 0; i < r.forwardLanes.size(); i++) {
                if (Context.objectManager.getId(r.forwardLanes.get(i)).equals(laneId)) {
                    return horiz ? 0 : 1; // jobb vagy le
                }
            }
            for (int i = 0; i < r.backwardLanes.size(); i++) {
                if (Context.objectManager.getId(r.backwardLanes.get(i)).equals(laneId)) {
                    return horiz ? 2 : 3; // bal vagy fel
                }
            }
        }
        return 0;
    }

    /**
     * Az osszes korabban felvett megfigyelot leiratkoztatja.
     */
    public void unbindAll() {
        for (Bind b : registered) {
            b.source.removeObserver(b.obs);
        }
        registered.clear();
    }
}
