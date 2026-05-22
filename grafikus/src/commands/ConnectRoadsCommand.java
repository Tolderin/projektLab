package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Road;

/**
 * A 'connect_roads <road1> <pos1> <road2> <pos2>' parancs
 * implementacioja. Beallitja, hogy az egyik ut melyik vege a masik
 * ut melyik vegehez csatlakozik (pos = "start" vagy "end").
 */
public class ConnectRoadsCommand implements ICommand {

    /**
     * Vegrehajtja a connect_roads parancsot.
     *
     * @param args Parancs, road1_id, pos1, road2_id, pos2.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 5) {
            OutputFormatter.printError("connect_roads requires <road1> <pos1> <road2> <pos2>");
            return;
        }
        Road r1 = asRoad(args[1]);
        Road r2 = asRoad(args[3]);
        if (r1 == null || r2 == null) {
            return;
        }
        r1.connectTo(r2, args[2], args[4]);
        // Szimmetrikus: a masik utat is osszekotjuk
        r2.connectTo(r1, args[4], args[2]);
        OutputFormatter.printSuccess(
                "roads " + args[1] + " " + args[2] + " connected to " + args[3] + " " + args[4]);
    }

    /**
     * Felodja az ID-t Road-ra.
     *
     * @param id Road id.
     * @return Road vagy null hibaval.
     */
    private Road asRoad(String id) {
        Object o = Context.objectManager.getObject(id);
        if (!(o instanceof Road)) {
            OutputFormatter.printError("road not found: " + id);
            return null;
        }
        return (Road) o;
    }
}
