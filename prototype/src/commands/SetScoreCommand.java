package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Player;

/**
 * A 'set_score <player_id> <amount>' parancs implementacioja.
 * Beallitja a megadott jatekos pontszamat.
 */
public class SetScoreCommand implements ICommand {

    /**
     * Vegrehajtja a set_score parancsot.
     *
     * @param args Parancs, player_id, amount.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("set_score requires <player_id> <amount>");
            return;
        }
        Object o = Context.objectManager.getObject(args[1]);
        if (!(o instanceof Player)) {
            OutputFormatter.printError("player not found: " + args[1]);
            return;
        }
        try {
            int score = Integer.parseInt(args[2]);
            ((Player) o).score = score;
            OutputFormatter.printSuccess("score set to " + score + " for " + args[1]);
        } catch (NumberFormatException e) {
            OutputFormatter.printError("invalid score: " + args[2]);
        }
    }
}
