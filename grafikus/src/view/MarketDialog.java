package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cli.Context;
import controller.CommandBridge;
import model.CleanerHead;
import model.Cleaner;
import model.DragonHead;
import model.GravelHead;
import model.IObserver;
import model.IntegratedMarket;
import model.Observable;
import model.SaltHead;
import model.SnowPlow;

/**
 * A HomeBase aruhazat reprezentalo modal dialog. Felelos a:
 *  - tipikus fejek (sweephead, throwerhead, icebreakerhead,
 *    salthead, dragonhead, gravelhead) megvasarlasaert
 *  - uzemanyagok (salt, kerosine, gravel) feltoltesert
 *  - a Cleaner egyenleget kijelzo balanceLabel-ert
 *
 * Megfigyeli a Cleaner-t (Player.notifyObservers("moneyChanged"))
 * es a market-et (IntegratedMarket.notifyObservers("itemBought" /
 * "itemUnaffordable")).
 */
public class MarketDialog extends JDialog implements IObserver {

    /** A vasarlasi celzott hokotro. */
    private final SnowPlow target;

    /** A vasarlas atjarhata Cleaner. */
    private final Cleaner buyer;

    /** A megfigyelt market. */
    private final IntegratedMarket market;

    /** A parancsbridge a tenyleges vasarlas-parancsok kiadasahoz. */
    private final CommandBridge bridge;

    /** A jelenlegi egyenleg felirata. */
    private final JLabel balanceLabel = new JLabel();

    /** Fej-valaszto combobox. */
    private final JComboBox<String> headCombo = new JComboBox<>(new String[] {
            "sweephead", "throwerhead", "icebreakerhead",
            "salthead", "dragonhead", "gravelhead"
    });

    /** Uzemanyag-valaszto combobox. */
    private final JComboBox<String> fuelCombo = new JComboBox<>(new String[] {
            "salt", "kerosine", "gravel"
    });

    /** Uzemanyag-mennyiseg beviteli mezo. */
    private final JTextField fuelAmountField = new JTextField("5", 4);

    /** A befoglalo MainWindow (rebindAll hivasahoz uj hokotro vasarlasa utan). */
    private final MainWindow mainWindow;

    /**
     * Jelzo: a felhasznalo vett uj hokotrot a dialog session alatt.
     * Bezaraskor a MainWindow.rebindAll-t hivjuk, hogy az uj
     * hokotro nezete azonnal megjelenjen a karten.
     */
    private boolean newPlowBought = false;

    /**
     * Letrehoz egy MarketDialog-ot.
     *
     * @param parent A befoglalo MainWindow (a modal kapcsolathoz + rebind-hez).
     * @param target A celzott SnowPlow.
     * @param buyer  A vasarlo Cleaner.
     * @param bridge A parancs-bridge.
     */
    public MarketDialog(MainWindow parent, SnowPlow target, Cleaner buyer,
                        CommandBridge bridge) {
        super(parent, "HomeBase Áruház", true);
        this.mainWindow = parent;
        this.target = target;
        this.buyer = buyer;
        this.bridge = bridge;
        // A target HomeBase market-jenek figyelmeztetese
        IntegratedMarket m = ((model.HomeBase) target.currentField).market;
        this.market = m;
        m.addObserver(this);
        buyer.addObserver(this);
        build();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Felepiti a dialog UI-tartalmat.
     */
    private void build() {
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBackground(new Color(245, 240, 220));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("Vásárlás – " + nameOf(target));
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        content.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 1, 4, 4));
        grid.setOpaque(false);
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        grid.add(balanceLabel);

        // Fej-sor
        JPanel headRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        headRow.setOpaque(false);
        headRow.add(new JLabel("Fej:"));
        headRow.add(headCombo);
        JButton headBuy = new JButton("Vásárol");
        headBuy.addActionListener(e -> onBuyHead());
        headRow.add(headBuy);
        grid.add(headRow);

        // Uzemanyag-sor
        JPanel fuelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fuelRow.setOpaque(false);
        fuelRow.add(new JLabel("Üzemanyag:"));
        fuelRow.add(fuelCombo);
        fuelRow.add(new JLabel("Mennyiség:"));
        fuelRow.add(fuelAmountField);
        JButton fuelBuy = new JButton("Tankol");
        fuelBuy.addActionListener(e -> onBuyFuel());
        fuelRow.add(fuelBuy);
        grid.add(fuelRow);

        // Uj hokotro-vasarlasi sor (a 13. heti spec szerint a legdragabb)
        JPanel plowRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        plowRow.setOpaque(false);
        plowRow.add(new JLabel("Új hókotró (1000):"));
        JButton plowBuy = new JButton("Vásárol");
        plowBuy.addActionListener(e -> onBuySnowPlow());
        plowRow.add(plowBuy);
        grid.add(plowRow);

        content.add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton close = new JButton("Bezár");
        close.addActionListener(e -> doClose());
        bottom.add(close);
        content.add(bottom, BorderLayout.SOUTH);

        setContentPane(content);
        refreshBalance();
    }

    /**
     * A vasarlasi parancs kiadasa a kivalasztott fej-tipusra.
     * Sikeres vasarlas utan (attachments melyseg novekedett) auto-
     * matikusan equip-olja az uj fejet, majd ha az uj fej uzem-
     * anyagot igenyel es a tartaly ures, figyelmeztetest jelenit meg.
     */
    private void onBuyHead() {
        String head = (String) headCombo.getSelectedItem();
        int before = target.attachments.size();
        bridge.buyItem(idOf(buyer), idOf(target), head, 0);
        boolean bought = target.attachments.size() > before;
        if (bought) {
            bridge.equipHead(idOf(target), head);
            warnIfActiveHeadNeedsFuel();
        }
    }

    /**
     * Ha a frissen felszerelt activeHead uzemanyagot igenyel
     * (Salt/Dragon/Gravel) es a tartaly ures, modal figyelmezteto
     * dialog jelenik meg.
     */
    private void warnIfActiveHeadNeedsFuel() {
        CleanerHead h = target.activeHead;
        if (h == null) {
            return;
        }
        double fuel;
        String fuelType;
        if (h instanceof SaltHead) {
            fuel = ((SaltHead) h).fuelAmount;
            fuelType = "salt";
        } else if (h instanceof DragonHead) {
            fuel = ((DragonHead) h).fuelAmount;
            fuelType = "kerosine";
        } else if (h instanceof GravelHead) {
            fuel = ((GravelHead) h).fuelAmount;
            fuelType = "gravel";
        } else {
            return; // nem uzemanyag-fuggo fej
        }
        if (fuel <= 0.0) {
            JOptionPane.showMessageDialog(this,
                    "A felszerelt " + h.getName() + " fej üzemanyagot igényel,\n"
                            + "de a tartály üres. Tankolj " + fuelType
                            + "-ből a használathoz!",
                    "Figyelmeztetés",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Uj hokotro vasarlas a Cleaner-nek. A `target` SnowPlow a vasarlo-
     * agensként szolgal: az uj hokotro az o HomeBase-ere kerul. Sikeres
     * vasarlas eseten beallitjuk a newPlowBought flag-et, hogy a dialog
     * bezarasakor a MainWindow.rebindAll megjelenitse az uj hokotrot.
     */
    private void onBuySnowPlow() {
        int before = buyer.getControlledPlows().size();
        bridge.buyItem(idOf(buyer), idOf(target), "snowplow", 0);
        int after = buyer.getControlledPlows().size();
        if (after > before) {
            newPlowBought = true;
            JOptionPane.showMessageDialog(this,
                    "Új hókotró sikeresen megvásárolva.\n"
                            + "A flotta a dialog bezárásakor frissül a térképen.",
                    "Vásárlás", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Az uzemanyag-vasarlasi parancs kiadasa a kivalasztott
     * tipusra es mennyisegre.
     */
    private void onBuyFuel() {
        String fuel = (String) fuelCombo.getSelectedItem();
        int amount;
        try {
            amount = Math.max(1, Integer.parseInt(fuelAmountField.getText().trim()));
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Erveny szam kell.");
            return;
        }
        bridge.buyItem(idOf(buyer), idOf(target), fuel, amount);
    }

    /**
     * Bezarja a dialog-ot es leiratkoztatja a megfigyeloket. Ha uj
     * hokotro vasarlas tortent, a MainWindow.rebindAll-t hivjuk, hogy
     * a karten megjelenjen a friss flotta.
     */
    private void doClose() {
        market.removeObserver(this);
        buyer.removeObserver(this);
        boolean shouldRebind = newPlowBought;
        dispose();
        if (shouldRebind && mainWindow != null) {
            mainWindow.rebindAll();
        }
    }

    /**
     * Frissiti az egyenleg-feliratot a Cleaner aktualis penzevel.
     */
    private void refreshBalance() {
        balanceLabel.setText(String.format("Egyenleg: %.1f", buyer.money));
    }

    /**
     * Az IObserver implementacioja: penz vagy market valtozasakor
     * UI-thread-en frissit.
     *
     * @param source A jelzes forrasa.
     * @param hint   A jelzes tipusa.
     */
    @Override
    public void update(Observable source, String hint) {
        javax.swing.SwingUtilities.invokeLater(this::refreshBalance);
    }

    /**
     * Komponens lekapcsolasakor a megfigyeloket is leiratkoztatja.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        market.removeObserver(this);
        buyer.removeObserver(this);
    }

    /**
     * Visszaadja egy modell-objektum ID-jat a Context.objectManager
     * lookup utjan.
     *
     * @param obj A modell-elem.
     * @return Az ID, vagy a "?" ha nincs regisztralva.
     */
    private static String idOf(Object obj) {
        String id = Context.objectManager.getId(obj);
        return id != null ? id : "?";
    }

    /**
     * A celzott SnowPlow ember-olvashato nevét adja vissza
     * (alapertelmezetten az ID-ja).
     *
     * @param sp A hokotro.
     * @return A megjelenitendo nev.
     */
    private static String nameOf(SnowPlow sp) {
        String n = sp.getName();
        return (n != null) ? n : idOf(sp);
    }

    /**
     * Component-tipus visszaad parameterre, hogy a Java import-szel a
     * Component osztaly utak miatt elerheto legyen. (Nincs kulonleges
     * elletes az osztaly tobbi reszével.)
     *
     * @return Maga a dialog (a kornyezete tudja kasztolni).
     */
    public Component asComponent() {
        return this;
    }
}
