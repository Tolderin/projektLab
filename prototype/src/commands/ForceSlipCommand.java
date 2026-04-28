package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;

/**
 * A 'force_slip <vehicle_id> [true|false]' parancs implementacioja.
 * Determinisztikus modban explicit kikenyszeriti vagy megakadalyozza
 * egy adott jarmu jegen valo megcsuszasat.
 */
public class ForceSlipCommand implements ICommand {

    /**
     * Vegrehajtja a force_slip parancsot.
     *
     * @param args Parancs, vehicle_id, value (true|false).
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("force_slip requires <vehicle_id> <true|false>");
            return;
        }
        String vehicleId = args[1];
        String value = args[2];
        boolean shouldSlip;
        if ("true".equalsIgnoreCase(value)) {
            shouldSlip = true;
        } else if ("false".equalsIgnoreCase(value)) {
            shouldSlip = false;
        } else {
            OutputFormatter.printError("invalid force_slip value: " + value);
            return;
        }
        Context.determinism.setForceSlip(vehicleId, shouldSlip);
        OutputFormatter.printSuccess("force_slip " + vehicleId + " set to " + value);
    }
}
