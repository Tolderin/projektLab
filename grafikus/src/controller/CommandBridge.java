package controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import cli.CommandParser;

/**
 * A GUI es a CLI parancsfeldolgozas kozotti fordito-osztaly.
 * Felelossege, hogy a GUI esemeny-kezeloi egyszeru, eros-tipusos
 * metodusokat hivjanak (pl. nextTurn(), moveSelectedTo(...)), a
 * CommandBridge pedig felepiti a megfelelo szoveges parancsot es
 * atadja a CommandParser.parseLine-nak.
 *
 * Ez a megoldas biztositja, hogy a GUI semmifele uj uton ne jusson
 * a modellhez -- minden hatas ugyanazon a parser-uton megy at, mint
 * amit a CLI hasznal -- igy a 20 db automatizalt CLI-teszt minden
 * forgatokonyve valtozatlan formaban fut.
 */
public class CommandBridge {

    /** A meglevo parancsertelmezo, amit a parser.parseLine-on at hivunk. */
    private final CommandParser parser;

    /**
     * Letrehoz egy CommandBridge-et a megadott parser-rel.
     *
     * @param parser A mar felkonfiguralt CommandParser.
     */
    public CommandBridge(CommandParser parser) {
        this.parser = parser;
    }

    /**
     * Mozgas-parancsot ad ki a megfelelo tipusra.
     *
     * @param vehicleId A jarmu azonositoja.
     * @param targetId  A celmezo azonositoja.
     * @param isPlow    true ha SnowPlow-rol van szo (move_plow), kulonben move_bus.
     */
    public void moveSelectedTo(String vehicleId, String targetId, boolean isPlow) {
        if (isPlow) {
            parser.parseLine("move_plow " + vehicleId + " " + targetId);
        } else {
            parser.parseLine("move_bus " + vehicleId + " " + targetId);
        }
    }

    /**
     * Vasarlas parancsot ad ki. amount > 0 esetén uzemanyag-mennyiseget
     * is athut a buy parancsnak (fej-vasarlasnal amount = 0).
     *
     * @param playerId A vasarlo Cleaner azonositoja.
     * @param plowId   A celzott hokotro azonositoja.
     * @param itemType Az aru tipusa (pl. "salt_head", "fuel:salt", ...).
     * @param amount   Mennyiseg (csak uzemanyagra).
     */
    public void buyItem(String playerId, String plowId, String itemType, int amount) {
        if (amount > 0) {
            parser.parseLine("buy " + playerId + " " + plowId + " " + itemType + " " + amount);
        } else {
            parser.parseLine("buy " + playerId + " " + plowId + " " + itemType);
        }
    }

    /**
     * Fejcsere-parancsot ad ki a HomeBase-en.
     *
     * @param plowId   A hokotro azonositoja.
     * @param headType A felszerelendo fej tipusa.
     */
    public void equipHead(String plowId, String headType) {
        parser.parseLine("equip " + plowId + " " + headType);
    }

    /**
     * Kor lepteto-parancs.
     */
    public void nextTurn() {
        parser.parseLine("next_turn");
    }

    /**
     * Konfiguracios fajl betoltese.
     *
     * @param filename A konfig fajl utvonala.
     */
    public void load(String filename) {
        parser.parseLine("load " + filename);
    }

    /**
     * Mentes-parancs (a meglevo save implementacio fajlba ir).
     */
    public void save() {
        parser.parseLine("save");
    }

    /**
     * A standard kimenetre kuldott stat-szoveg kapturalasa.
     * Atmenetileg atiranyitja a System.out-ot egy
     * ByteArrayOutputStream-re, vegrehajtja a stat parancsot,
     * majd visszaallitja.
     *
     * @param objectId A lekerdezett objektum ID-ja.
     * @return A stat parancs kimenete szovegkent.
     */
    public String stat(String objectId) {
        PrintStream origOut = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buf));
        try {
            parser.parseLine("stat " + objectId);
        } finally {
            System.setOut(origOut);
        }
        return buf.toString();
    }

    /**
     * Determinisztikus csuszas eloiras egy jarmuvon.
     *
     * @param vehicleId A jarmu azonositoja.
     * @param value     true ha kovetkezo lepesnel csusznia kell.
     */
    public void forceSlip(String vehicleId, boolean value) {
        parser.parseLine("force_slip " + vehicleId + " " + value);
    }

    /**
     * Random ki/be kapcsolasa (csuszas-determinisztika).
     *
     * @param on true ha bekapcsolva (alapertelmezett), false ha kikapcsolva.
     */
    public void setRandom(boolean on) {
        parser.parseLine("random " + (on ? "on" : "off"));
    }

    /**
     * Visszaadja a parser-t (a kozvetlen parseLine-hivasokhoz a
     * billentyukombinaciok kezelesehez).
     *
     * @return A becsomagolt CommandParser.
     */
    public CommandParser getParser() {
        return parser;
    }
}
