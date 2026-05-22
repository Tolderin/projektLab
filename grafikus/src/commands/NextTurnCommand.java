package commands;

import java.util.ArrayList;
import java.util.List;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.GameLogic;
import model.Lane;
import model.Vehicle;

/**
 * A 'next_turn [N]' parancs implementacioja. Szimulalja az ido
 * mulasat. Egy korben:
 * 1) FORCE-SLIP fazis: a force_slip true beallitassal jeges Lane-en
 *    levo jarmuvek azonnali csuszas-mechanizmusa (test 6 viselkedes).
 * 2) A GameLogic.advanceTurn() futtatasa: havazas + jarmuvek mozgasa
 *    + Lane.updateTurnEffects().
 *
 * Az opcionalis parameter: hany kort szimulaljon (default 1).
 */
public class NextTurnCommand implements ICommand {

    /**
     * Vegrehajtja a next_turn parancsot.
     *
     * @param args Parancs, [N].
     */
    @Override
    public void execute(String[] args) {
        int n = 1;
        if (args.length >= 2) {
            try {
                n = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                OutputFormatter.printError("invalid turn count: " + args[1]);
                return;
            }
        }
        if (!(Context.gameLogic instanceof GameLogic)) {
            OutputFormatter.printError("game logic not initialized");
            return;
        }
        GameLogic gl = (GameLogic) Context.gameLogic;
        for (int i = 0; i < n; i++) {
            forceSlipPhase(gl);
            gl.advanceTurn();
        }
    }

    /**
     * A 'force-slip' fazis: a force_slip true beallitassal jeges
     * Lane-en levo jarmuvekre azonnali slip()-et hivunk.
     *
     * @param gl A jatek-logika.
     */
    private void forceSlipPhase(GameLogic gl) {
        // Snapshot lista a ConcurrentModificationException elkerulesehez
        List<Vehicle> snapshot = new ArrayList<>(gl.vehicles);
        for (Vehicle v : snapshot) {
            if (v.currentField instanceof Lane
                    && ((Lane) v.currentField).isFrozen) {
                String vid = Context.objectManager.getId(v);
                if (Context.determinism.isForceSlipped(vid)) {
                    v.slip((Lane) v.currentField);
                }
            }
        }
    }
}
