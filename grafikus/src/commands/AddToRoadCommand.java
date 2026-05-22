package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Building;
import model.Lane;
import model.Road;

/**
 * Az 'add_to_road <road_id> <item_type> <item_id> [direction]'
 * parancs implementacioja. Kialakitja a tartalmazasi (kompozicios)
 * hierarchiat a modellben: hozzarendel egy savot vagy epuletet
 * egy mar letrehozott uthoz.
 */
public class AddToRoadCommand implements ICommand {

    /**
     * Vegrehajtja az add_to_road parancsot.
     *
     * @param args Parancs, road_id, item_type, item_id, [direction].
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 4) {
            OutputFormatter.printError("add_to_road requires <road_id> <item_type> <item_id> [direction]");
            return;
        }
        String roadId = args[1];
        String itemType = args[2];
        String itemId = args[3];
        String direction = args.length > 4 ? args[4] : "forward";

        Object roadObj = Context.objectManager.getObject(roadId);
        if (!(roadObj instanceof Road)) {
            OutputFormatter.printError("road not found: " + roadId);
            return;
        }
        Road road = (Road) roadObj;

        if ("lane".equals(itemType)) {
            Object laneObj = Context.objectManager.getObject(itemId);
            if (!(laneObj instanceof Lane)) {
                OutputFormatter.printError("lane not found: " + itemId);
                return;
            }
            road.addLane((Lane) laneObj, direction);
            OutputFormatter.printSuccess("lane " + itemId + " added to road " + roadId + " as " + direction);
        } else if ("building".equals(itemType)) {
            Object bObj = Context.objectManager.getObject(itemId);
            if (!(bObj instanceof Building)) {
                OutputFormatter.printError("building not found: " + itemId);
                return;
            }
            road.addBuilding((Building) bObj);
            OutputFormatter.printSuccess("building " + itemId + " added to road " + roadId);
        } else {
            OutputFormatter.printError("unknown item_type: " + itemType);
        }
    }
}
