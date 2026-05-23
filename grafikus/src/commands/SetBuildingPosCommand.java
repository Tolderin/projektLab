package commands;

import java.awt.Rectangle;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import view.MapLayout;

/**
 * 'set_building_pos &lt;id&gt; &lt;x&gt; &lt;y&gt; &lt;w&gt; &lt;h&gt;'
 * command. Beallitja egy meglevo epulet pixel-bounding-boxat a
 * MapLayout-on (placeBuildings amugy is kihagyja ha mar van bounds).
 * A 13. heti save/load reszekent a SaveCommand emittalja ezeket.
 *
 * Ha a Context.mapLayout null (CLI mod), no-op.
 */
public class SetBuildingPosCommand implements ICommand {

    /**
     * Vegrehajtja a parancsot.
     *
     * @param args Parancs, id, x, y, w, h.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 6) {
            OutputFormatter.printError(
                    "set_building_pos requires <id> <x> <y> <w> <h>");
            return;
        }
        if (!(Context.mapLayout instanceof MapLayout)) {
            return;
        }
        MapLayout layout = (MapLayout) Context.mapLayout;
        String id = args[1];
        try {
            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            int w = Integer.parseInt(args[4]);
            int h = Integer.parseInt(args[5]);
            layout.setBounds(id, new Rectangle(x, y, w, h));
            OutputFormatter.printSuccess("building position set: " + id);
        } catch (NumberFormatException e) {
            OutputFormatter.printError(
                    "set_building_pos: invalid number in: " + String.join(" ", args));
        }
    }
}
