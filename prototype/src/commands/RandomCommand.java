package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;

/**
 * A 'random on|off' parancs implementacioja. Be- vagy kikapcsolja
 * a vletlen csuszas-szimulaciot. Determinisztikus ('off') modban
 * a force_slip parancs explicit overide-jaira tamaszkodunk.
 */
public class RandomCommand implements ICommand {

    /**
     * Vegrehajtja a random parancsot.
     *
     * @param args Parancs es 1 argumentum: "on" vagy "off".
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            OutputFormatter.printError("random requires on|off");
            return;
        }
        String mode = args[1];
        if ("on".equals(mode)) {
            Context.determinism.setRandomEnabled(true);
            OutputFormatter.printSuccess("random mode set to on");
        } else if ("off".equals(mode)) {
            Context.determinism.setRandomEnabled(false);
            OutputFormatter.printSuccess("random mode set to off");
        } else {
            OutputFormatter.printError("invalid random mode: " + mode);
        }
    }
}
