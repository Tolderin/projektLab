package commands;

import cli.Context;
import cli.ICommand;
import io.OutputFormatter;
import model.CleanerHead;
import model.Cleaner;
import model.DragonHead;
import model.GameLogic;
import model.GravelHead;
import model.HomeBase;
import model.IcebreakerHead;
import model.IPurchasable;
import model.Player;
import model.SaltHead;
import model.SnowPlow;
import model.SweepHead;
import model.ThrowerHead;

/**
 * A 'buy <player_id> <plow_id> <item_type> [amount]' parancs
 * implementacioja. Ket szintaxist tamogat:
 *  - 4 argumentum: fej-vasarlas (uj fej kerul az sp.attachments-be)
 *  - 5 argumentum: uzemanyag-vasarlas (a meglevo megfelelo fej
 *    fuelAmount-jara felton). Tamogatott uzemanyagok: salt, kerosine, gravel.
 */
public class BuyCommand implements ICommand {

    /**
     * Vegrehajtja a buy parancsot.
     *
     * @param args Parancs, player_id, plow_id, item_type, [amount].
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 4) {
            OutputFormatter.printError("buy requires <player_id> <plow_id> <item_type> [amount]");
            return;
        }
        Object playerObj = Context.objectManager.getObject(args[1]);
        Object plowObj = Context.objectManager.getObject(args[2]);
        if (!(playerObj instanceof Player)) {
            OutputFormatter.printError("player not found: " + args[1]);
            return;
        }
        if (!(plowObj instanceof SnowPlow)) {
            OutputFormatter.printError("snowplow not found: " + args[2]);
            return;
        }
        Player player = (Player) playerObj;
        SnowPlow sp = (SnowPlow) plowObj;
        String itemType = args[3];

        if (args.length >= 5) {
            // Uzemanyag-vasarlas
            buyFuel(player, sp, itemType, args);
        } else if ("snowplow".equals(itemType)) {
            // Uj hokotro vasarlasa -- "legdragabb segedeszköz"
            buySnowPlow(player, sp);
        } else {
            // Fej-vasarlas
            buyHead(player, sp, itemType);
        }
    }

    /**
     * Uj hokotrot vasarol a Cleaner-nek. A `sp` az a hokotro, amely
     * jelenleg a HomeBase-en all es ahonnan a vasarlas kezdemenyezve
     * van -- az uj hokotrot ugyanarra a HomeBase-re tesszuk, es a
     * vasarlo Cleaner flotillajaba adjuk. Default fej: ThrowerHead.
     *
     * @param player   A vasarlo (Cleaner kell legyen).
     * @param buyerSp  Egy meglevo hokotro a Cleaner-fel, a HomeBase
     *                 azonositasahoz.
     */
    private void buySnowPlow(Player player, SnowPlow buyerSp) {
        final int price = 1000;
        if (!(player instanceof Cleaner)) {
            OutputFormatter.printError("only cleaner can buy snowplow");
            return;
        }
        if (!(buyerSp.currentField instanceof HomeBase)) {
            OutputFormatter.printError(
                    "snowplow purchase requires existing plow at homebase");
            return;
        }
        if (player.money < price) {
            String pid = Context.objectManager.getId(player);
            OutputFormatter.printError(
                    (pid != null ? pid : "?")
                            + " has insufficient funds for snowplow");
            return;
        }
        String newId = nextSnowPlowId();
        SnowPlow newSp = new SnowPlow();
        newSp.setName(newId);
        newSp.activeHead = new ThrowerHead();
        Context.objectManager.registerObject(newId, newSp);
        ((Cleaner) player).addPlow(newSp);
        HomeBase hb = (HomeBase) buyerSp.currentField;
        newSp.currentField = hb;
        hb.accept(newSp);
        if (Context.gameLogic instanceof GameLogic) {
            ((GameLogic) Context.gameLogic).addVehicle(newSp);
        }
        player.addMoney(-(double) price);
        String pid = Context.objectManager.getId(player);
        OutputFormatter.printSuccess(
                (pid != null ? pid : "?")
                        + " bought new snowplow " + newId);
    }

    /**
     * Ujabb, meg nem foglalt SnowPlow ID-t general. Konvencio: sp1,
     * sp2, sp3, ...
     *
     * @return Az uj egyedi azonosito.
     */
    private String nextSnowPlowId() {
        int n = 1;
        while (Context.objectManager.hasObject("sp" + n)) {
            n++;
        }
        return "sp" + n;
    }

    /**
     * Uj fejet vasarol es hozzaadja az sp.attachments-hez ha sikeres.
     */
    private void buyHead(Player player, SnowPlow sp, String itemType) {
        CleanerHead head = createHead(itemType);
        if (head == null) {
            OutputFormatter.printError("unknown head type: " + itemType);
            return;
        }
        // Vasarlas: a market csokkenti a player penzet
        if (player.money < head.getPrice()) {
            String pid = Context.objectManager.getId(player);
            OutputFormatter.printError(
                    (pid != null ? pid : "?")
                            + " has insufficient funds for " + itemType);
            return;
        }
        // 13. heti: addMoney(-price) triggereli a notifyObservers-t,
        // igy a HUD / MarketDialog egyenleg azonnal frissul.
        player.addMoney(-(double) head.getPrice());
        sp.attachments.add(head);
        String pid = Context.objectManager.getId(player);
        String spId = Context.objectManager.getId(sp);
        OutputFormatter.printSuccess(
                (pid != null ? pid : "?")
                        + " bought " + itemType + " for "
                        + (spId != null ? spId : "?"));
    }

    /**
     * Uzemanyagot vasarol es a megfelelo fej-tipusra felti.
     * Az uzemanyag a megfelelo tipusu fejre (akar attachments,
     * akar activeHead) tolt, az ar darabszam * fuelPrice.
     */
    private void buyFuel(Player player, SnowPlow sp, String fuelType, String[] args) {
        double amount;
        try {
            amount = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            OutputFormatter.printError("invalid amount: " + args[4]);
            return;
        }

        // Megfelelo fej kikeresese -- elobb activeHead, aztan attachments
        CleanerHead targetHead = findFuelTarget(sp, fuelType);
        if (targetHead == null) {
            OutputFormatter.printError("no compatible head for fuel: " + fuelType);
            return;
        }
        double pricePer = getFuelPrice(targetHead);
        double totalCost = pricePer * amount;
        if (player.money < totalCost) {
            String pid = Context.objectManager.getId(player);
            OutputFormatter.printError(
                    (pid != null ? pid : "?")
                            + " has insufficient funds for " + fuelType);
            return;
        }
        player.addMoney(-totalCost);
        // Toltes
        if (targetHead instanceof SaltHead) {
            ((SaltHead) targetHead).refillFuel(amount);
        } else if (targetHead instanceof DragonHead) {
            ((DragonHead) targetHead).refillFuel(amount);
        } else if (targetHead instanceof GravelHead) {
            ((GravelHead) targetHead).refillFuel(amount);
        }
        String pid = Context.objectManager.getId(player);
        String spId = Context.objectManager.getId(sp);
        OutputFormatter.printSuccess(
                (pid != null ? pid : "?")
                        + " bought "
                        + String.format(java.util.Locale.US, "%.1f", amount)
                        + " " + fuelType
                        + " for " + (spId != null ? spId : "?"));
    }

    /**
     * Ujabb fej-objektum letrehozasa a tipusnev alapjan.
     */
    private CleanerHead createHead(String type) {
        switch (type) {
            case "sweephead": return new SweepHead();
            case "throwerhead": return new ThrowerHead();
            case "icebreakerhead": return new IcebreakerHead();
            case "salthead": return new SaltHead();
            case "dragonhead": return new DragonHead();
            case "gravelhead": return new GravelHead();
            default: return null;
        }
    }

    /**
     * Megtalalja az uzemanyaghoz tartozo cel-fejet:
     *  salt -> SaltHead, kerosine -> DragonHead, gravel -> GravelHead.
     */
    private CleanerHead findFuelTarget(SnowPlow sp, String fuelType) {
        Class<?> targetClass = null;
        if ("salt".equals(fuelType)) {
            targetClass = SaltHead.class;
        } else if ("kerosine".equals(fuelType) || "kerosene".equals(fuelType)) {
            targetClass = DragonHead.class;
        } else if ("gravel".equals(fuelType)) {
            targetClass = GravelHead.class;
        }
        if (targetClass == null) {
            return null;
        }
        if (sp.activeHead != null && targetClass.isInstance(sp.activeHead)) {
            return sp.activeHead;
        }
        for (CleanerHead h : sp.attachments) {
            if (targetClass.isInstance(h)) {
                return h;
            }
        }
        return null;
    }

    /**
     * Az uzemanyag egysegara a megfelelo fej-objektumtol.
     */
    private double getFuelPrice(CleanerHead h) {
        if (h instanceof SaltHead) return ((SaltHead) h).fuelPrice;
        if (h instanceof DragonHead) return ((DragonHead) h).fuelPrice;
        if (h instanceof GravelHead) return ((GravelHead) h).fuelPrice;
        return 0.0;
    }
}
