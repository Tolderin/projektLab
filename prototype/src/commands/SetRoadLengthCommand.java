package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Road;

/**
 * A 'set_road_length <road_id> <length>' parancs implementacioja.
 * Beallitja egy ut fizikai/matematikai hosszat (utvonal-keresesi
 * sulyozashoz hasznalt).
 */
public class SetRoadLengthCommand implements ICommand {

    /**
     * Vegrehajtja a set_road_length parancsot.
     *
     * @param args Parancs, road_id, length.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("set_road_length requires <road_id> <length>");
            return;
        }
        Object o = Context.objectManager.getObject(args[1]);
        if (!(o instanceof Road)) {
            OutputFormatter.printError("road not found: " + args[1]);
            return;
        }
        try {
            double len = Double.parseDouble(args[2]);
            ((Road) o).setLength(len);
            OutputFormatter.printSuccess("road " + args[1] + " length set to " + args[2]);
        } catch (NumberFormatException e) {
            OutputFormatter.printError("invalid length: " + args[2]);
        }
    }
}
