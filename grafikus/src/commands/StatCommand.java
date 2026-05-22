package commands;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Bus;
import model.BusDriver;
import model.Car;
import model.Cleaner;
import model.DragonHead;
import model.GravelHead;
import model.Lane;
import model.SaltHead;
import model.SnowPlow;

/**
 * A 'stat <id>' parancs implementacioja. Lekerdezi es a kimenetre
 * irja egy adott objektum belso allapotat (attributumait) struktural
 * [STATE] blokk formajaban.
 *
 * Tamogatott objektumtipusok:
 *  - Lane: snowDepth, isFrozen, gravelDepth, saltEffect, isBlocked, vehicles
 *  - SnowPlow: currentField, activeHead, fuelAmount, attachments
 *  - Car: currentField, homeBuilding, workBuilding, isGoingToWork, waitTurns
 *  - Cleaner: name, money, score
 *  - BusDriver: name, score, completedRounds
 */
public class StatCommand implements ICommand {

    /**
     * Vegrehajtja a stat parancsot.
     *
     * @param args Parancs, id.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            OutputFormatter.printError("stat requires <id>");
            return;
        }
        Object obj = Context.objectManager.getObject(args[1]);
        if (obj == null) {
            OutputFormatter.printError("object not found: " + args[1]);
            return;
        }
        if (obj instanceof Lane) {
            statLane(args[1], (Lane) obj);
        } else if (obj instanceof SnowPlow) {
            statSnowPlow(args[1], (SnowPlow) obj);
        } else if (obj instanceof Car) {
            statCar(args[1], (Car) obj);
        } else if (obj instanceof Cleaner) {
            statCleaner(args[1], (Cleaner) obj);
        } else if (obj instanceof BusDriver) {
            statBusDriver(args[1], (BusDriver) obj);
        } else if (obj instanceof Bus) {
            statBus(args[1], (Bus) obj);
        } else {
            OutputFormatter.printError("unsupported stat type for: " + args[1]);
        }
    }

    /** Lane stat. */
    private void statLane(String id, Lane l) {
        Map<String, String> attrs = new LinkedHashMap<>();
        attrs.put("snowDepth", fmt(l.snowDepth));
        attrs.put("isFrozen", String.valueOf(l.isFrozen));
        attrs.put("gravelDepth", fmt(l.gravelDepth));
        attrs.put("saltEffect", fmt(l.saltEffect));
        attrs.put("isBlocked", String.valueOf(l.isBlocked()));
        attrs.put("vehicles", l.getVehiclesAsString());
        OutputFormatter.printState(id, "Lane", attrs);
    }

    /** SnowPlow stat. */
    private void statSnowPlow(String id, SnowPlow sp) {
        Map<String, String> attrs = new LinkedHashMap<>();
        String fieldId = sp.currentField != null
                ? Context.objectManager.getId(sp.currentField) : "null";
        attrs.put("currentField", fieldId != null ? fieldId : "null");
        String headName = sp.activeHead != null ? sp.activeHead.getName() : "null";
        attrs.put("activeHead", headName);
        // fuelAmount csak az aktiv fej tipusatol fugg
        double fuel = 0.0;
        if (sp.activeHead instanceof SaltHead) {
            fuel = ((SaltHead) sp.activeHead).fuelAmount;
        } else if (sp.activeHead instanceof DragonHead) {
            fuel = ((DragonHead) sp.activeHead).fuelAmount;
        } else if (sp.activeHead instanceof GravelHead) {
            fuel = ((GravelHead) sp.activeHead).fuelAmount;
        }
        attrs.put("fuelAmount", fmt(fuel));
        // Attachments-ben levo fejek nev-listaja
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < sp.attachments.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(sp.attachments.get(i).getName());
        }
        sb.append("]");
        attrs.put("attachments", sb.toString());
        OutputFormatter.printState(id, "SnowPlow", attrs);
    }

    /** Car stat. */
    private void statCar(String id, Car c) {
        Map<String, String> attrs = new LinkedHashMap<>();
        String fieldId = c.currentField != null
                ? Context.objectManager.getId(c.currentField) : "null";
        attrs.put("currentField", fieldId != null ? fieldId : "null");
        String home = c.homeBuilding != null
                ? Context.objectManager.getId(c.homeBuilding) : "null";
        attrs.put("homeBuilding", home != null ? home : "null");
        String work = c.workBuilding != null
                ? Context.objectManager.getId(c.workBuilding) : "null";
        attrs.put("workBuilding", work != null ? work : "null");
        attrs.put("isGoingToWork", String.valueOf(c.isGoingToWork()));
        attrs.put("waitTurns", String.valueOf(c.getWaitTurns()));
        OutputFormatter.printState(id, "Car", attrs);
    }

    /** Cleaner stat. */
    private void statCleaner(String id, Cleaner c) {
        Map<String, String> attrs = new LinkedHashMap<>();
        attrs.put("name", c.name != null ? c.name : id);
        attrs.put("money", fmt(c.money));
        attrs.put("score", String.valueOf(c.score));
        OutputFormatter.printState(id, "Cleaner", attrs);
    }

    /** BusDriver stat. */
    private void statBusDriver(String id, BusDriver bd) {
        Map<String, String> attrs = new LinkedHashMap<>();
        attrs.put("name", bd.name != null ? bd.name : id);
        attrs.put("score", String.valueOf(bd.score));
        attrs.put("completedRounds", String.valueOf(bd.getCompletedRounds()));
        OutputFormatter.printState(id, "BusDriver", attrs);
    }

    /** Bus stat. */
    private void statBus(String id, Bus b) {
        Map<String, String> attrs = new LinkedHashMap<>();
        String fieldId = b.currentField != null
                ? Context.objectManager.getId(b.currentField) : "null";
        attrs.put("currentField", fieldId != null ? fieldId : "null");
        attrs.put("isFunctioning", String.valueOf(b.isFunctioning));
        attrs.put("disabledTurnsLeft", String.valueOf(b.disabledTurnsLeft));
        OutputFormatter.printState(id, "Bus", attrs);
    }

    /** US-locale szam-formazas (1 tizedessel). */
    private String fmt(double v) {
        return String.format(Locale.US, "%.1f", v);
    }
}
