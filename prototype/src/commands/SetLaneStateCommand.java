package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Lane;

/**
 * A 'set_lane_state <lane_id> <snowDepth> <isFrozen> <iceThickness> <gravelDepth>'
 * parancs implementacioja. Tesztelesi celra fizikai ertekeket allit
 * be egy savon.
 *
 * A 8. heti spec szerint az iceThickness paramater kompatibilitasbol
 * a parancs szignatorajaban maradt, de a belso modellben mar nincs
 * iceThickness attributum -- itt is csak eldobjuk az erteket.
 */
public class SetLaneStateCommand implements ICommand {

    /**
     * Vegrehajtja a set_lane_state parancsot.
     *
     * @param args Parancs, lane_id, snowDepth, isFrozen, iceThickness, gravelDepth.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 6) {
            OutputFormatter.printError(
                    "set_lane_state requires <lane_id> <snowDepth> <isFrozen> <iceThickness> <gravelDepth>");
            return;
        }
        Object o = Context.objectManager.getObject(args[1]);
        if (!(o instanceof Lane)) {
            OutputFormatter.printError("lane not found: " + args[1]);
            return;
        }
        Lane lane = (Lane) o;
        try {
            lane.snowDepth = Double.parseDouble(args[2]);
            lane.isFrozen = Boolean.parseBoolean(args[3]);
            // args[4] = iceThickness -- eldobjuk
            lane.gravelDepth = Double.parseDouble(args[5]);
            OutputFormatter.printSuccess("lane " + args[1] + " state set");
        } catch (NumberFormatException e) {
            OutputFormatter.printError("invalid numeric value");
        }
    }
}
