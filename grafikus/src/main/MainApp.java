package main;

import javax.swing.SwingUtilities;

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
import commands.SetBuildingPosCommand;
import commands.SetMoneyCommand;
import commands.SetRoadLengthCommand;
import commands.SetRoadPosCommand;
import commands.SetSaltEffectCommand;
import commands.SetScoreCommand;
import commands.SpawnCommand;
import commands.StatCommand;
import controller.CommandBridge;
import model.GameLogic;
import model.Map;
import view.WelcomeWindow;

/**
 * A grafikus alkalmazas belepesi pontja. A 11. heti specifikacio
 * (11.4.1) szerint ugyanazokat a CLI-objektumfelepito lepeseket vegzi,
 * mint a ProtoApp -- nincs grafika-specifikus modell vagy
 * proceduralis pályageneralo. A pálya CLI parancsokon keresztul
 * (create / connect_fields / spawn / set_money) jon letre.
 *
 * MVP flow (13. heti spec): indulaskor mindig a WelcomeWindow jelenik
 * meg (New Game / Load Game / Settings / Exit). A CLI argumentumok
 * jelenleg figyelmen kivul maradnak -- a pálya betöltése a welcome
 * "Load Game" gombján keresztül történik.
 */
public class MainApp {

    /**
     * A program belepesi pontja.
     *
     * @param args Jelenleg figyelmen kivul maradnak (a welcome flow
     *             tölti be a pályát).
     */
    public static void main(String[] args) {
        ObjectManager om = new ObjectManager();
        Determinism det = new Determinism();
        GameLogic gl = new GameLogic();
        gl.gameMap = new Map();
        // A GUI-modban a round-cadence aktiv: 1 SnowPlow-mozdulat + 1
        // Bus-mozdulat per kor, utana auto-next_turn (lasd CommandBridge).
        gl.roundCadenceEnabled = true;
        CommandParser parser = new CommandParser();

        Context.objectManager = om;
        Context.determinism = det;
        Context.gameLogic = gl;
        Context.commandParser = parser;

        parser.registerCommand("create", new CreateCommand());
        parser.registerCommand("add_to_road", new AddToRoadCommand());
        parser.registerCommand("connect_roads", new ConnectRoadsCommand());
        parser.registerCommand("connect_fields", new ConnectFieldsCommand());
        parser.registerCommand("set_road_length", new SetRoadLengthCommand());
        parser.registerCommand("set_road_pos", new SetRoadPosCommand());
        parser.registerCommand("set_building_pos", new SetBuildingPosCommand());
        parser.registerCommand("set_lane_state", new SetLaneStateCommand());
        parser.registerCommand("set_money", new SetMoneyCommand());
        parser.registerCommand("set_score", new SetScoreCommand());
        parser.registerCommand("set_salt_effect", new SetSaltEffectCommand());
        parser.registerCommand("spawn", new SpawnCommand());
        parser.registerCommand("move_bus", new MoveBusCommand());
        parser.registerCommand("move_plow", new MovePlowCommand());
        parser.registerCommand("buy", new BuyCommand());
        parser.registerCommand("equip", new EquipCommand());
        parser.registerCommand("next_turn", new NextTurnCommand());
        parser.registerCommand("random", new RandomCommand());
        parser.registerCommand("force_slip", new ForceSlipCommand());
        parser.registerCommand("stat", new StatCommand());
        parser.registerCommand("list", new ListCommand());
        parser.registerCommand("load", new LoadCommand());
        parser.registerCommand("save", new SaveCommand());
        parser.registerCommand("exit", new ExitCommand());

        CommandBridge bridge = new CommandBridge(parser);

        SwingUtilities.invokeLater(() -> new WelcomeWindow(bridge).setVisible(true));
    }
}
