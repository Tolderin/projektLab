package commands;

import java.awt.Rectangle;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Building;
import model.Bus;
import model.BusDriver;
import model.Car;
import model.Cleaner;
import model.Field;
import model.GeneralBuilding;
import model.HomeBase;
import model.Lane;
import model.Road;
import model.SnowPlow;
import model.Terminal;
import view.MapLayout;

/**
 * A 'save [filename]' parancs implementacioja. Lekerdezi az
 * ObjectManager-bol az aktualis memoriakepet, es visszageneralja
 * belolUk azokat a bemeneti parancsokat (create, spawn, set_*,
 * connect_fields), amikkel a jelenlegi allapot ujra felepitheto.
 *
 * Ha nincs filename argumentum, a Standard Outputra ir.
 */
public class SaveCommand implements ICommand {

    /**
     * Vegrehajtja a save parancsot.
     *
     * @param args Parancs, [filename].
     */
    @Override
    public void execute(String[] args) {
        PrintWriter out;
        boolean toFile = args.length >= 2;
        if (toFile) {
            try {
                out = new PrintWriter(new FileWriter(args[1]));
            } catch (IOException e) {
                OutputFormatter.printError("cannot open file: " + args[1]);
                return;
            }
        } else {
            out = new PrintWriter(System.out);
        }
        try {
            writeAll(out);
        } finally {
            if (toFile) {
                out.close();
                OutputFormatter.printSuccess("saved to " + args[1]);
            } else {
                out.flush();
            }
        }
    }

    /**
     * Kilogol egy create-/spawn-/set_*-/connect_fields-szekvenciat
     * az adott PrintWriter-re.
     *
     * @param out A cel-folyam.
     */
    private void writeAll(PrintWriter out) {
        // 1) Object create-ek tipus szerinti sorrendben (fuggosegekhez)
        Map<String, Object> all = Context.objectManager.getAll();
        MapLayout layout = (Context.mapLayout instanceof MapLayout)
                ? (MapLayout) Context.mapLayout : null;

        // a) Roadok
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof Road) {
                Road r = (Road) e.getValue();
                out.println("create road " + e.getKey());
                if (r.length > 0) {
                    out.println(String.format(Locale.US,
                            "set_road_length %s %.1f", e.getKey(), r.length));
                }
            }
        }
        // b) Lane-ek
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof Lane) {
                out.println("create lane " + e.getKey());
            }
        }
        // b2) add_to_road minden sav -> road kapcsolatra
        // (a Road forwardLanes/backwardLanes szerinti rekonstrukciohoz)
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof Road) {
                Road r = (Road) e.getValue();
                String roadId = e.getKey();
                for (Lane fwd : r.forwardLanes) {
                    String lid = Context.objectManager.getId(fwd);
                    if (lid != null) {
                        out.println("add_to_road " + roadId + " lane " + lid + " forward");
                    }
                }
                for (Lane bwd : r.backwardLanes) {
                    String lid = Context.objectManager.getId(bwd);
                    if (lid != null) {
                        out.println("add_to_road " + roadId + " lane " + lid + " backward");
                    }
                }
            }
        }
        // c) Buildingek
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof HomeBase) {
                out.println("create homebase " + e.getKey());
            } else if (e.getValue() instanceof Terminal) {
                out.println("create terminal " + e.getKey());
            } else if (e.getValue() instanceof GeneralBuilding) {
                out.println("create building " + e.getKey());
            }
        }
        // d) Players
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof Cleaner) {
                out.println("create cleaner " + e.getKey());
            } else if (e.getValue() instanceof BusDriver) {
                out.println("create busdriver " + e.getKey());
            }
        }

        // 2) Lane allapotok
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof Lane) {
                Lane l = (Lane) e.getValue();
                out.println(String.format(Locale.US,
                        "set_lane_state %s %.1f %s 0.0 %.1f",
                        e.getKey(), l.snowDepth, String.valueOf(l.isFrozen), l.gravelDepth));
                if (l.saltEffect > 0) {
                    out.println(String.format(Locale.US,
                            "set_salt_effect %s %.1f", e.getKey(), l.saltEffect));
                }
            }
        }

        // 3) connect_fields
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof Field) {
                Field f = (Field) e.getValue();
                String id = e.getKey();
                for (Field n : f.getNeighbors()) {
                    String nId = Context.objectManager.getId(n);
                    if (nId != null && id.compareTo(nId) < 0) {
                        // Csak egy iranyba, hogy ne duplikaljunk
                        out.println("connect_fields " + id + " " + nId);
                    }
                }
            }
        }

        // 4) Player money/score
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof Cleaner) {
                Cleaner c = (Cleaner) e.getValue();
                out.println(String.format(Locale.US,
                        "set_money %s %.1f", e.getKey(), c.money));
                if (c.score != 0) {
                    out.println("set_score " + e.getKey() + " " + c.score);
                }
            } else if (e.getValue() instanceof BusDriver) {
                BusDriver b = (BusDriver) e.getValue();
                if (b.score != 0) {
                    out.println("set_score " + e.getKey() + " " + b.score);
                }
            }
        }

        // 5) Spawnok
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof SnowPlow) {
                SnowPlow sp = (SnowPlow) e.getValue();
                String fieldId = sp.currentField != null
                        ? Context.objectManager.getId(sp.currentField) : null;
                String ownerId = sp.getOwner() != null
                        ? Context.objectManager.getId(sp.getOwner()) : null;
                if (fieldId != null && ownerId != null) {
                    out.println("spawn snowplow " + e.getKey() + " " + fieldId + " " + ownerId);
                }
            } else if (e.getValue() instanceof Car) {
                Car c = (Car) e.getValue();
                String fieldId = c.currentField != null
                        ? Context.objectManager.getId(c.currentField) : null;
                String home = c.homeBuilding != null
                        ? Context.objectManager.getId(c.homeBuilding) : null;
                String work = c.workBuilding != null
                        ? Context.objectManager.getId(c.workBuilding) : null;
                if (fieldId != null && home != null && work != null) {
                    out.println("spawn car " + e.getKey() + " " + fieldId + " "
                            + home + " " + work);
                }
            } else if (e.getValue() instanceof Bus) {
                Bus b = (Bus) e.getValue();
                String fieldId = b.currentField != null
                        ? Context.objectManager.getId(b.currentField) : null;
                String ownerId = b.owner != null
                        ? Context.objectManager.getId(b.owner) : null;
                String tAId = b.routeTerminalA != null
                        ? Context.objectManager.getId(b.routeTerminalA) : null;
                String tBId = b.routeTerminalB != null
                        ? Context.objectManager.getId(b.routeTerminalB) : null;
                if (fieldId != null) {
                    StringBuilder sb = new StringBuilder("spawn bus ");
                    sb.append(e.getKey()).append(' ').append(fieldId);
                    if (ownerId != null) {
                        sb.append(' ').append(ownerId);
                        if (tAId != null && tBId != null) {
                            sb.append(' ').append(tAId).append(' ').append(tBId);
                        }
                    }
                    out.println(sb.toString());
                }
            }
        }

        // 6) Layout-info: pixel-pozicik megorzeshez (csak GUI-modban
        // van Context.mapLayout aktiv -- CLI esetén kihagyjuk)
        if (layout != null) {
            for (Map.Entry<String, Object> e : all.entrySet()) {
                if (!(e.getValue() instanceof Road)) continue;
                String roadId = e.getKey();
                Rectangle rb = layout.getRoadBounds(roadId);
                if (rb == null) continue;
                boolean horiz = layout.isRoadHorizontal(roadId);
                int len = horiz ? rb.width : rb.height;
                out.println("set_road_pos " + roadId + " "
                        + rb.x + " " + rb.y + " " + len + " "
                        + (horiz ? "horizontal" : "vertical"));
            }
            for (Map.Entry<String, Object> e : all.entrySet()) {
                if (!(e.getValue() instanceof Building)) continue;
                String bid = e.getKey();
                Rectangle bb = layout.getBounds(bid);
                if (bb == null) continue;
                out.println("set_building_pos " + bid + " "
                        + bb.x + " " + bb.y + " " + bb.width + " " + bb.height);
            }
        }
    }
}
