package main;

import cli.CommandParser;
import cli.Context;
import cli.Determinism;
import cli.ObjectManager;
import commands.AddToRoadCommand;
import commands.BuyCommand;
import commands.ConnectFieldsCommand;
import commands.ConnectRoadsCommand;
import commands.CreateCommand;
import commands.EquipCommand;
import commands.ExitCommand;
import commands.ForceSlipCommand;
import commands.ListCommand;
import commands.LoadCommand;
import commands.MoveBusCommand;
import commands.MovePlowCommand;
import commands.NextTurnCommand;
import commands.RandomCommand;
import commands.SaveCommand;
import commands.SetLaneStateCommand;
import commands.SetMoneyCommand;
import commands.SetRoadLengthCommand;
import commands.SetSaltEffectCommand;
import commands.SetScoreCommand;
import commands.SpawnCommand;
import commands.StatCommand;
import model.GameLogic;
import model.Map;

/**
 * A prototipus alkalmazas fo belepesi pontja. Osszeszereli a
 * keretrendszert: peldanyositja az ObjectManager-t, a Determinism-t,
 * a GameLogic-ot es a CommandParser-t, beregisztralja a parancsokat,
 * majd elinditja a Standard Input olvasasat.
 *
 * A komponensek kozotti hozzaferest a cli.Context statikus
 * referenciatar biztositja -- a domain reteg (Vehicle, Lane stb.)
 * ezen keresztul jut hozza az ObjectManager-hez (ID-lookup) es a
 * Determinism-hez (csuszas-eldontes).
 */
public class ProtoApp {

    /**
     * A program belepesi pontja.
     *
     * @param args Parancssori argumentumok (jelenleg nem hasznalt).
     */
    public static void main(String[] args) {
        // Globalis kontextus inicializalasa
        ObjectManager om = new ObjectManager();
        Determinism det = new Determinism();
        GameLogic gl = new GameLogic();
        gl.gameMap = new Map();
        CommandParser parser = new CommandParser();

        Context.objectManager = om;
        Context.determinism = det;
        Context.gameLogic = gl;
        Context.commandParser = parser;

        // Parancsregisztracio
        // -- Konfiguracios parancsok
        parser.registerCommand("create", new CreateCommand());
        parser.registerCommand("add_to_road", new AddToRoadCommand());
        parser.registerCommand("connect_roads", new ConnectRoadsCommand());
        parser.registerCommand("connect_fields", new ConnectFieldsCommand());
        parser.registerCommand("set_road_length", new SetRoadLengthCommand());
        parser.registerCommand("set_lane_state", new SetLaneStateCommand());
        parser.registerCommand("set_money", new SetMoneyCommand());
        parser.registerCommand("set_score", new SetScoreCommand());
        parser.registerCommand("set_salt_effect", new SetSaltEffectCommand());
        parser.registerCommand("spawn", new SpawnCommand());

        // -- Akciok
        parser.registerCommand("move_bus", new MoveBusCommand());
        parser.registerCommand("move_plow", new MovePlowCommand());
        parser.registerCommand("buy", new BuyCommand());
        parser.registerCommand("equip", new EquipCommand());

        // -- Szimulacio
        parser.registerCommand("next_turn", new NextTurnCommand());
        parser.registerCommand("random", new RandomCommand());
        parser.registerCommand("force_slip", new ForceSlipCommand());

        // -- Lekerdezes / IO
        parser.registerCommand("stat", new StatCommand());
        parser.registerCommand("list", new ListCommand());
        parser.registerCommand("load", new LoadCommand());
        parser.registerCommand("save", new SaveCommand());
        parser.registerCommand("exit", new ExitCommand());

        // Folyam-feldolgozas Standard Input-bol
        parser.parseStream(System.in);
    }
}
