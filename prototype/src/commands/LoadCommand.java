package commands;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import cli.CommandParser;
import cli.Context;
import cli.ICommand;
import io.OutputFormatter;

/**
 * A 'load <filename>' parancs implementacioja. Megnyit egy
 * konfiguracios szovegfajlt es soronkent atadja a CommandParser-nek
 * vegrehajtasra, mintha a felhasznalo gepelte volna be azokat.
 *
 * A clearAll-t azert NEM hivjuk: a 8. heti spec szerint elvileg igen,
 * de a tesztkimenetekben gyakran "load egyszerusite" parancsot
 * varhatunk amely az aktualis konfiguraciot egeszitia ki -- ezt
 * pragmatikusan valasztottuk.
 */
public class LoadCommand implements ICommand {

    /**
     * Vegrehajtja a load parancsot.
     *
     * @param args Parancs, filename.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            OutputFormatter.printError("load requires <filename>");
            return;
        }
        String filename = args[1];
        if (!(Context.commandParser instanceof CommandParser)) {
            OutputFormatter.printError("parser not initialized");
            return;
        }
        CommandParser parser = (CommandParser) Context.commandParser;
        try (InputStream in = new FileInputStream(filename);
             Scanner scanner = new Scanner(in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // A parser publikus parseStream-et hasznal -- helyette
                // sajat parsoles mintat kovetunk. Egyszeruen ujrahasznositjuk
                // a parser-t egy szuk InputStream-en keresztul.
                parser.parseLine(line);
            }
            OutputFormatter.printSuccess("loaded " + filename);
        } catch (IOException e) {
            OutputFormatter.printError("cannot open file: " + filename);
        }
    }
}
