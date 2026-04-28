package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Bus;
import model.Building;
import model.Car;
import model.Cleaner;
import model.Field;
import model.Lane;
import model.SnowPlow;
import model.SweepHead;

/**
 * A 'spawn <type> ...' parancs implementacioja. Jarmuvet helyez a
 * palyara egy adott csomopontba (Field), valamint beallitja a
 * tipusspecifikus parametereit.
 *
 * Variansok:
 *  - spawn snowplow <id> <field_id> <owner_id>
 *  - spawn car <id> <field_id> <home_building_id> <work_building_id>
 *  - spawn bus <id> <field_id>
 */
public class SpawnCommand implements ICommand {

    /**
     * Vegrehajtja a spawn parancsot.
     *
     * @param args Parancs, type, id, ...tobbi parameter typusoktol fuggoen.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 4) {
            OutputFormatter.printError("spawn requires <type> <id> <field_id> ...");
            return;
        }
        String type = args[1];
        String id = args[2];
        String fieldId = args[3];
        Object fieldObj = Context.objectManager.getObject(fieldId);
        if (!(fieldObj instanceof Field)) {
            OutputFormatter.printError("field not found: " + fieldId);
            return;
        }
        Field field = (Field) fieldObj;

        switch (type) {
            case "snowplow":
                spawnSnowPlow(args, id, field, fieldId);
                return;
            case "car":
                spawnCar(args, id, field, fieldId);
                return;
            case "bus":
                spawnBus(args, id, field, fieldId);
                return;
            default:
                OutputFormatter.printError("unknown spawn type: " + type);
        }
    }

    /**
     * Letrehoz vagy kiegeszit egy meglevo SnowPlow objektumot,
     * elhelyezi a megadott mezon, es hozzakoti a tulajdonos Cleaner-hez.
     *
     * @param args    Parancs argumentumok.
     * @param id      Az sp azonositoja.
     * @param field   A target mezo.
     * @param fieldId A target mezo id-ja (kimenethez).
     */
    private void spawnSnowPlow(String[] args, String id, Field field, String fieldId) {
        if (args.length < 5) {
            OutputFormatter.printError("spawn snowplow requires <id> <field_id> <owner_id>");
            return;
        }
        SnowPlow sp;
        Object existing = Context.objectManager.getObject(id);
        if (existing instanceof SnowPlow) {
            // Mar letezik (create snowplow elotte) -- csak elhelyezzuk
            sp = (SnowPlow) existing;
        } else {
            sp = new SnowPlow();
            sp.setName(id);
            sp.activeHead = new SweepHead();
            Context.objectManager.registerObject(id, sp);
        }
        // Tulajdonos beallitasa
        Object ownerObj = Context.objectManager.getObject(args[4]);
        if (!(ownerObj instanceof Cleaner)) {
            OutputFormatter.printError("cleaner not found: " + args[4]);
            return;
        }
        ((Cleaner) ownerObj).addPlow(sp);
        // Direct elhelyezes (NEM Lane.accept -- nem aktivaljuk a slipet)
        sp.currentField = field;
        if (field instanceof Lane) {
            ((Lane) field).vehicles.add(sp);
        } else if (field instanceof Building) {
            // A Building-be bekerul (a HomeBase a dockedPlows-ba is)
            field.accept(sp);
        }
        if (Context.gameLogic instanceof model.GameLogic) {
            ((model.GameLogic) Context.gameLogic).addVehicle(sp);
        }
        OutputFormatter.printSuccess(id + " spawned at " + fieldId);
    }

    /**
     * Letrehoz egy Car-t es elhelyezi.
     */
    private void spawnCar(String[] args, String id, Field field, String fieldId) {
        if (args.length < 6) {
            OutputFormatter.printError("spawn car requires <id> <field_id> <home_id> <work_id>");
            return;
        }
        Car car;
        Object existing = Context.objectManager.getObject(id);
        if (existing instanceof Car) {
            car = (Car) existing;
        } else {
            car = new Car();
            Context.objectManager.registerObject(id, car);
        }
        Object home = Context.objectManager.getObject(args[4]);
        Object work = Context.objectManager.getObject(args[5]);
        if (!(home instanceof Building) || !(work instanceof Building)) {
            OutputFormatter.printError("building not found");
            return;
        }
        car.homeBuilding = (Building) home;
        car.workBuilding = (Building) work;
        if (Context.gameLogic instanceof model.GameLogic) {
            car.gameMap = ((model.GameLogic) Context.gameLogic).gameMap;
        }
        car.currentField = field;
        if (field instanceof Lane) {
            ((Lane) field).vehicles.add(car);
        } else {
            field.accept(car);
        }
        if (Context.gameLogic instanceof model.GameLogic) {
            ((model.GameLogic) Context.gameLogic).addVehicle(car);
        }
        OutputFormatter.printSuccess(id + " spawned at " + fieldId);
    }

    /**
     * Letrehoz egy Bus-t es elhelyezi.
     */
    private void spawnBus(String[] args, String id, Field field, String fieldId) {
        Bus bus;
        Object existing = Context.objectManager.getObject(id);
        if (existing instanceof Bus) {
            bus = (Bus) existing;
        } else {
            bus = new Bus();
            bus.name = id;
            Context.objectManager.registerObject(id, bus);
        }
        bus.currentField = field;
        if (field instanceof Lane) {
            ((Lane) field).vehicles.add(bus);
        } else {
            field.accept(bus);
        }
        if (Context.gameLogic instanceof model.GameLogic) {
            ((model.GameLogic) Context.gameLogic).addVehicle(bus);
        }
        OutputFormatter.printSuccess(id + " spawned at " + fieldId);
    }
}
