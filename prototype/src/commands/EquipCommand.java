package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.CleanerHead;
import model.DragonHead;
import model.GravelHead;
import model.HomeBase;
import model.IcebreakerHead;
import model.SaltHead;
import model.SnowPlow;
import model.SweepHead;
import model.ThrowerHead;

/**
 * Az 'equip <plow_id> <head_type>' parancs implementacioja.
 * Lecsereli a hokotro aktiv tisztitofejet egy megvasarolt (az
 * attachments listaban szereplo) fejre. Csak a HomeBase mezon
 * tartozkodva engedelyezett.
 */
public class EquipCommand implements ICommand {

    /**
     * Vegrehajtja az equip parancsot.
     *
     * @param args Parancs, plow_id, head_type.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            OutputFormatter.printError("equip requires <plow_id> <head_type>");
            return;
        }
        Object spObj = Context.objectManager.getObject(args[1]);
        if (!(spObj instanceof SnowPlow)) {
            OutputFormatter.printError("snowplow not found: " + args[1]);
            return;
        }
        SnowPlow sp = (SnowPlow) spObj;
        // HomeBase ellenorzes
        if (!(sp.currentField instanceof HomeBase)) {
            OutputFormatter.printError(args[1] + " cannot change head outside homebase");
            return;
        }
        String headType = args[2];
        Class<?> targetClass = headTypeClass(headType);
        if (targetClass == null) {
            OutputFormatter.printError("unknown head type: " + headType);
            return;
        }
        // Megkeresi az attachments-ben es athelyezi az activeHead-be
        CleanerHead chosen = null;
        for (CleanerHead h : sp.attachments) {
            if (targetClass.isInstance(h)) {
                chosen = h;
                break;
            }
        }
        if (chosen == null) {
            OutputFormatter.printError(args[1] + " does not have " + headType + " in attachments");
            return;
        }
        sp.attachments.remove(chosen);
        // A regi activeHead "eldobasra" kerul (egyszerusitett logika)
        sp.activeHead = chosen;
        OutputFormatter.printSuccess(args[1] + " equipped with " + headType);
    }

    /**
     * Tipusnev -> Java osztaly.
     */
    private Class<?> headTypeClass(String t) {
        switch (t) {
            case "sweephead": return SweepHead.class;
            case "throwerhead": return ThrowerHead.class;
            case "icebreakerhead": return IcebreakerHead.class;
            case "salthead": return SaltHead.class;
            case "dragonhead": return DragonHead.class;
            case "gravelhead": return GravelHead.class;
            default: return null;
        }
    }
}
