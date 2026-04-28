package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.BusDriver;
import model.Cleaner;
import model.GeneralBuilding;
import model.HomeBase;
import model.Lane;
import model.Road;
import model.SnowPlow;
import model.Terminal;

/**
 * A 'create <type> <id>' parancs implementacioja. Letrehoz egy
 * uj parameterezetlen objektumot a memoriaban, a megadott
 * azonositoval, es regisztralja az ObjectManager-ben.
 *
 * Tamogatott tipusok:
 *  - road, lane, building, homebase, terminal
 *  - cleaner, busdriver
 *  - snowplow (lasd: a tenyleges palya-elhelyezeshez a spawn parancs hasznalatos)
 */
public class CreateCommand implements ICommand {

    /**
     * Vegrehajtja a create parancsot.
     *
     * @param args Parancs, tipus, id.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("create requires <type> <id>");
            return;
        }
        String type = args[1];
        String id = args[2];
        if (Context.objectManager.hasObject(id)) {
            OutputFormatter.printError("id already exists: " + id);
            return;
        }
        Object obj = createTypedObject(type, id);
        if (obj == null) {
            OutputFormatter.printError("unknown type: " + type);
            return;
        }
        Context.objectManager.registerObject(id, obj);
        // Mezok hozzaadasa a Map-hez ha Field
        if (obj instanceof model.Field && Context.gameLogic instanceof model.GameLogic) {
            ((model.GameLogic) Context.gameLogic).gameMap.addField((model.Field) obj);
        }
        // Player hozzaadasa
        if (obj instanceof model.Player && Context.gameLogic instanceof model.GameLogic) {
            ((model.GameLogic) Context.gameLogic).addPlayer((model.Player) obj);
        }
        OutputFormatter.printSuccess(type + " " + id + " created");
    }

    /**
     * Letrehozza a megfelelo tipusu objektumot. Player-szeru tipusok
     * (cleaner, busdriver) esetén a name a megadott id-vel
     * inicializalodik.
     *
     * @param type A kert tipus szoveges neve.
     * @param id   A letrehozando objektum azonositoja.
     * @return Az uj objektum, vagy null ha ismeretlen tipus.
     */
    private Object createTypedObject(String type, String id) {
        switch (type) {
            case "road":
                return new Road();
            case "lane":
                return new Lane();
            case "building":
                return new GeneralBuilding();
            case "homebase":
                return new HomeBase();
            case "terminal":
                return new Terminal();
            case "cleaner": {
                Cleaner c = new Cleaner();
                c.name = id;
                return c;
            }
            case "busdriver": {
                BusDriver bd = new BusDriver();
                bd.name = id;
                return bd;
            }
            case "snowplow": {
                SnowPlow sp = new SnowPlow();
                sp.setName(id);
                // Default fej (sweephead) a SpawnCommand-hez kepest
                // itt mar beallitva, hogy a 'create snowplow' utan az
                // sp activeHead-je ne null legyen.
                sp.activeHead = new model.SweepHead();
                return sp;
            }
            case "car":
                return new model.Car();
            case "bus": {
                model.Bus b = new model.Bus();
                b.name = id;
                return b;
            }
            default:
                return null;
        }
    }
}
