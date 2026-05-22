package commands;

import cli.ICommand;

/**
 * Az 'exit' parancs implementacioja. Leallitja a prototipus
 * futasat es bezarja az I/O csatornakat.
 */
public class ExitCommand implements ICommand {

    /**
     * Vegrehajtja az exit parancsot.
     *
     * @param args A parancs es parameterei (nem hasznalt).
     */
    @Override
    public void execute(String[] args) {
        System.exit(0);
    }
}
