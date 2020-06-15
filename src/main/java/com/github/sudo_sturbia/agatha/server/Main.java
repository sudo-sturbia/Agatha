package com.github.sudo_sturbia.agatha.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main handles command line flags and runs an instance of the
 * Agatha server.
 * <p>
 * Available command line flags are<br/>
 * <pre>
 *     -p  --port <number>              Port number to listen to, default: 54321
 *     -d  --database <name>            Name of database to user, default: Agatha
 *     -su --db-server-username <name>  Username for database server (MySQL), default: root
 *     -sp --db-server-password <pass>  Password for database server (MySQL), default: ""
 * </pre>
 */
public class Main
{
    /**
     * Create a server instance and run it.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args)
    {
        CommandLineParser parser = new DefaultParser();

        try
        {
            CommandLine cmd = parser.parse(Main.options(), args);

            Server server = ServerBuilder.newServer()
                                         .dbName(cmd.getOptionValue("d", "Agatha"))
                                         .port(cmd.hasOption("p") ? (Integer) cmd.getParsedOptionValue("p") : 54321)
                                         .dbServerUsername(cmd.getOptionValue("su", "root"))
                                         .dbServerPass(cmd.getOptionValue("sp", ""))
                                         .build();

            server.run();
        }
        catch (ServerSetupException | ParseException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Create command line options.
     *
     * @return command line options.
     */
    private static Options options()
    {
        Options options = new Options();
        options.addOption(Option.builder("p")
                                .longOpt("port")
                                .argName("number")
                                .hasArg()
                                .type(Integer.class)
                                .desc("Port number to listen to, default: 54321")
                                .build());

        options.addOption(Option.builder("d")
                                .longOpt("database")
                                .argName("name")
                                .hasArg()
                                .type(String.class)
                                .desc("Name of database to user, default: Agatha")
                                .build());

        options.addOption(Option.builder("su")
                                .longOpt("db-server-username")
                                .argName("name")
                                .hasArg()
                                .type(String.class)
                                .desc("Username for database server (MySQL), default: root")
                                .build());

        options.addOption(Option.builder("sp")
                                .longOpt("db-server-password")
                                .argName("pass")
                                .hasArg()
                                .type(String.class)
                                .desc("")
                                .build());

        return options;
    }
}
