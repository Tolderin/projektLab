package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Field;
import model.Lane;
import model.SnowPlow;

/**
 * A 'move_plow <plow_id> <target_field_id>' parancs implementacioja.
 * Utasitja a hokotrot a kovetkezo mezore lepesre. A lepes utan
 * automatikusan meghivodik az aktiv tisztitofej clean() metodusa
 * az uj savon (a SnowPlow.move() teszi ezt).
 */
public class MovePlowCommand implements ICommand {

    /**
     * Vegrehajtja a move_plow parancsot.
     *
     * @param args Parancs, plow_id, target_id.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("move_plow requires <plow_id> <target_id>");
            return;
        }
        Object spObj = Context.objectManager.getObject(args[1]);
        Object targetObj = Context.objectManager.getObject(args[2]);
        if (!(spObj instanceof SnowPlow)) {
            OutputFormatter.printError("snowplow not found: " + args[1]);
            return;
        }
        if (!(targetObj instanceof Field)) {
            OutputFormatter.printError("field not found: " + args[2]);
            return;
        }
        SnowPlow sp = (SnowPlow) spObj;
        Field target = (Field) targetObj;
        if (sp.currentField == null
                || !sp.currentField.getNeighbors().contains(target)) {
            String currentId = Context.objectManager.getId(sp.currentField);
            OutputFormatter.printError(
                    args[2] + " is not a neighbor of "
                            + (currentId != null ? currentId : args[1]));
            return;
        }
        OutputFormatter.printSuccess(args[1] + " moved to " + args[2]);
        sp.nextField = target;
        sp.move();
    }
}
