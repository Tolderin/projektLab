package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import view.MapLayout;

/**
 * 'set_road_pos &lt;id&gt; &lt;x&gt; &lt;y&gt; &lt;length&gt; &lt;horizontal|vertical&gt;'
 * command. Beallitja egy meglevo road pixel-pozicioját a MapLayout-on
 * (azonos hatas mint a Demo-osztalyok placeRoadHorizontal /
 * placeRoadVertical hivasa). A 13. heti save/load reszekent a
 * SaveCommand emittalja ezeket, igy a betoltott palya kepkockahuen
 * visszaall, autoLayout helyett.
 *
 * Ha a Context.mapLayout null (pl. ProtoApp CLI mod), no-op.
 */
public class SetRoadPosCommand implements ICommand {

    /**
     * Vegrehajtja a parancsot.
     *
     * @param args Parancs, id, x, y, length, orientation ("horizontal" / "vertical").
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 6) {
            OutputFormatter.printError(
                    "set_road_pos requires <id> <x> <y> <length> <horizontal|vertical>");
            return;
        }
        if (!(Context.mapLayout instanceof MapLayout)) {
            // GUI nincs aktiv (pl. ProtoApp) -- a layout-info irrevelans.
            return;
        }
        MapLayout layout = (MapLayout) Context.mapLayout;
        String id = args[1];
        try {
            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            int length = Integer.parseInt(args[4]);
            String orient = args[5];
            if ("horizontal".equalsIgnoreCase(orient)) {
                layout.placeRoadHorizontal(id, x, y, length);
            } else if ("vertical".equalsIgnoreCase(orient)) {
                layout.placeRoadVertical(id, x, y, length);
            } else {
                OutputFormatter.printError(
                        "set_road_pos: unknown orientation: " + orient);
                return;
            }
            OutputFormatter.printSuccess("road position set: " + id);
        } catch (NumberFormatException e) {
            OutputFormatter.printError(
                    "set_road_pos: invalid number in: " + String.join(" ", args));
        }
    }
}
