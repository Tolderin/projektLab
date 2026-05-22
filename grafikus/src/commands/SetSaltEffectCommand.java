package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Lane;

/**
 * A 'set_salt_effect <lane_id> <duration>' parancs implementacioja.
 * 8. heti uj parancs: kozvetlenul beallitja a sav saltEffect
 * attributumat -- igy lehet tesztelni a sohatas idoben valo
 * lejartat (test 17).
 */
public class SetSaltEffectCommand implements ICommand {

    /**
     * Vegrehajtja a set_salt_effect parancsot.
     *
     * @param args Parancs, lane_id, duration.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("set_salt_effect requires <lane_id> <duration>");
            return;
        }
        Object o = Context.objectManager.getObject(args[1]);
        if (!(o instanceof Lane)) {
            OutputFormatter.printError("lane not found: " + args[1]);
            return;
        }
        try {
            double duration = Double.parseDouble(args[2]);
            ((Lane) o).saltEffect = duration;
            OutputFormatter.printSuccess(
                    "salt effect set to "
                            + String.format(java.util.Locale.US, "%.1f", duration)
                            + " for " + args[1]);
        } catch (NumberFormatException e) {
            OutputFormatter.printError("invalid duration: " + args[2]);
        }
    }
}
