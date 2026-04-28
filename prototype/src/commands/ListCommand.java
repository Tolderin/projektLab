package commands;

import java.util.List;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Bus;
import model.BusDriver;
import model.Car;
import model.Cleaner;
import model.HomeBase;
import model.Lane;
import model.Road;
import model.SnowPlow;
import model.Terminal;
import model.Vehicle;
import model.GeneralBuilding;

/**
 * A 'list <type>' parancs implementacioja. Kiirja az osszes adott
 * tipusu objektum ID-jat. Tamogatott tipusok: lanes, roads, buildings,
 * homebases, terminals, vehicles, cars, buses, snowplows, cleaners,
 * busdrivers.
 */
public class ListCommand implements ICommand {

    /**
     * Vegrehajtja a list parancsot.
     *
     * @param args Parancs, type.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            OutputFormatter.printError("list requires <type>");
            return;
        }
        String type = args[1];
        Class<?> cls = typeToClass(type);
        if (cls == null) {
            OutputFormatter.printError("unknown list type: " + type);
            return;
        }
        List<String> ids = Context.objectManager.getIdsByType(cls);
        OutputFormatter.printList(type, ids);
    }

    /**
     * Megfeleltetes a tipus-string es a Java-osztaly kozott.
     */
    private Class<?> typeToClass(String t) {
        switch (t) {
            case "lanes": return Lane.class;
            case "roads": return Road.class;
            case "buildings": return GeneralBuilding.class;
            case "homebases": return HomeBase.class;
            case "terminals": return Terminal.class;
            case "vehicles": return Vehicle.class;
            case "cars": return Car.class;
            case "buses": return Bus.class;
            case "snowplows": return SnowPlow.class;
            case "cleaners": return Cleaner.class;
            case "busdrivers": return BusDriver.class;
            default: return null;
        }
    }
}
