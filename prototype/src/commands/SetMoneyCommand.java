package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Player;

/**
 * A 'set_money <player_id> <amount>' parancs implementacioja.
 * Beallitja a megadott jatekos penzkesletet a megadott osszegre.
 */
public class SetMoneyCommand implements ICommand {

    /**
     * Vegrehajtja a set_money parancsot.
     *
     * @param args Parancs, player_id, amount.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("set_money requires <player_id> <amount>");
            return;
        }
        Object o = Context.objectManager.getObject(args[1]);
        if (!(o instanceof Player)) {
            OutputFormatter.printError("player not found: " + args[1]);
            return;
        }
        try {
            double amount = Double.parseDouble(args[2]);
            ((Player) o).money = amount;
            OutputFormatter.printSuccess(
                    "money set to "
                            + String.format(java.util.Locale.US, "%.1f", amount)
                            + " for " + args[1]);
        } catch (NumberFormatException e) {
            OutputFormatter.printError("invalid amount: " + args[2]);
        }
    }
}
