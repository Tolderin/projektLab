package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Bus;
import model.Field;

/**
 * A 'move_bus <bus_id> <target_field_id>' parancs implementacioja.
 * Kikeresi az ID-k alapjan a Bus es Field objektumokat, ellenorzi
 * a szomszedsagi kapcsolatot, majd meghivja a busz move() metodusat.
 */
public class MoveBusCommand implements ICommand {

    /**
     * Vegrehajtja a move_bus parancsot.
     *
     * @param args Parancs, bus_id, target_id.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("move_bus requires <bus_id> <target_id>");
            return;
        }
        Object busObj = Context.objectManager.getObject(args[1]);
        Object targetObj = Context.objectManager.getObject(args[2]);
        if (!(busObj instanceof Bus)) {
            OutputFormatter.printError("bus not found: " + args[1]);
            return;
        }
        if (!(targetObj instanceof Field)) {
            OutputFormatter.printError("field not found: " + args[2]);
            return;
        }
        Bus bus = (Bus) busObj;
        Field target = (Field) targetObj;
        if (bus.currentField == null
                || !bus.currentField.getNeighbors().contains(target)) {
            String currentId = Context.objectManager.getId(bus.currentField);
            OutputFormatter.printError(
                    args[2] + " is not a neighbor of "
                            + (currentId != null ? currentId : args[1]));
            return;
        }
        // SUCCESS elobb -- igy a slip event utana sorba kerul
        OutputFormatter.printSuccess(args[1] + " moved to " + args[2]);
        bus.nextField = target;
        bus.move();
    }
}
