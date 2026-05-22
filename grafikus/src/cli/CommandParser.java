package cli;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import io.OutputFormatter;

/**
 * A bemeneti folyam (Standard Input vagy fajl) olvasasa, a sorok
 * darabolasa es a megfelelo ICommand implementacio kikeresese a
 * regisztracios szotar alapjan.
 *
 * Hibas szintaxis vagy ismeretlen parancs eseten [ERROR] uzenet.
 */
public class CommandParser {

    /** Parancsnev -> ICommand szotar. */
    private final Map<String, ICommand> commandRegistry = new HashMap<>();

    /**
     * Regisztral egy uj parancsot a rendszerbe.
     *
     * @param name    A parancs szoveges neve (pl. "move_bus").
     * @param command A vegrehajto ICommand objektum.
     */
    public void registerCommand(String name, ICommand command) {
        commandRegistry.put(name, command);
    }

    /**
     * Vegtelen ciklusban olvassa a bemeneti folyamot, soronkent
     * tovabbadja parseLine-nak. EOF eseten visszater.
     *
     * @param in A bemeneti folyam (jellemzoen System.in vagy fajl).
     */
    public void parseStream(InputStream in) {
        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            parseLine(line);
        }
    }

    /**
     * Egyetlen bemeneti sor ertelmezese es a megfelelo parancs futtatasa.
     * Ures sort es '#'-tel kezdodo komment-sort kihagy.
     * Publikus, hogy a LoadCommand kozvetlenul tudja meghivni.
     *
     * @param line A feldolgozando sor.
     */
    public void parseLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }
        String[] args = trimmed.split("\\s+");
        String commandName = args[0];
        ICommand command = commandRegistry.get(commandName);
        if (command == null) {
            OutputFormatter.printError("Unknown command: " + commandName);
            return;
        }
        command.execute(args);
    }
}
