package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.Field;

/**
 * A 'connect_fields <field1_id> <field2_id>' parancs implementacioja.
 * Ket Field interfeszt megvalosito objektumot (Lane, Building) ket
 * iranyban osszekot a navigacios graf eleinek letrehozasahoz.
 *
 * A kimenet formatum-valasztas: "[SUCCESS] <id1> connected to <id2>"
 * a 7. heti specifikacio "[SUCCESS] <ID> <Akcio> <Eredmeny>" mintajat
 * kovete (a 8. heti tesztekben mindket alak elofordul, ezt valasztottuk
 * mert konzisztens a tobbi parancs kimenetevel).
 */
public class ConnectFieldsCommand implements ICommand {

    /**
     * Vegrehajtja a connect_fields parancsot.
     *
     * @param args Parancs es ket field id.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("connect_fields requires <field1> <field2>");
            return;
        }
        Object o1 = Context.objectManager.getObject(args[1]);
        Object o2 = Context.objectManager.getObject(args[2]);
        if (!(o1 instanceof Field) || !(o2 instanceof Field)) {
            OutputFormatter.printError("field not found: " + args[1] + " or " + args[2]);
            return;
        }
        Field f1 = (Field) o1;
        Field f2 = (Field) o2;
        // Kolcsonos szomszedossag
        if (!f1.getNeighbors().contains(f2)) {
            f1.getNeighbors().add(f2);
        }
        if (!f2.getNeighbors().contains(f1)) {
            f2.getNeighbors().add(f1);
        }
        OutputFormatter.printSuccess(args[1] + " connected to " + args[2]);
    }
}
