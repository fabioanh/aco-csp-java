package be.vub.swarmintelligence;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * 
 * @author fabio
 *
 */
public class App {
	private final static Logger LOGGER = Logger.getLogger(App.class);

	public static void main(String[] args) throws IOException, ParseException {
		CommandLineParser parser = new DefaultParser();
		CommandLine line = parser.parse(helpOptions(), args, true); // true so it
		// does not throw on unrecognized options
		if (line.hasOption("help")) {
		    help(); // calls exit
		}
		Map<String, Object> cfg = getCLIConfig(args);
		RandomUtils.getInstance((Integer) cfg.get("seed"));
		CSPSolver solver = new CSPSolver(cfg);
		solver.solve();

	}

	private static Map<String, Object> getCLIConfig(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Map<String, Object> response = new HashMap<>();
		try {
			// parse the command line arguments
			CommandLine cliArgs = parser.parse(getOptions(), args);
			if (cliArgs.hasOption("help")) {
				help();
			}
			if (cliArgs.hasOption("instance")) {
				response.put("instance", (String) cliArgs.getOptionValue("instance"));
			}
			if (cliArgs.hasOption("numants")) {
				response.put("numants", Integer.valueOf(cliArgs.getOptionValue("numants")));
			} else {
				response.put("numants", 20);
			}
			if (cliArgs.hasOption("rho")) {
				response.put("rho", Double.valueOf(cliArgs.getOptionValue("rho")));
			} else {
				response.put("rho", 0.0003);
			}
			if (cliArgs.hasOption("seed")) {
				response.put("seed", Integer.valueOf(cliArgs.getOptionValue("seed")));
			} else {
				response.put("seed", 1234);
			}
			if (cliArgs.hasOption("maxiter")) {
				response.put("maxiter", Integer.valueOf(cliArgs.getOptionValue("maxiter")));
			} else {
				response.put("maxiter", 1000);
			}
			if (cliArgs.hasOption("algorithm")) {
				response.put("algorithm", Algorithm.valueOf(cliArgs.getOptionValue("algorithm").toUpperCase()));
			} else {
				response.put("algorithm", Algorithm.ELITIST);
			}
			if (cliArgs.hasOption("localsearch")) {
				response.put("localsearch", true);
			} else {
				response.put("localsearch", false);
			}
			
		} catch (ParseException ex) {
			// oops, something went wrong
			LOGGER.error("Parsing failed.  Reason: " + ex.getMessage());
		}

		LOGGER.debug("\n****************************\n****************************\n" + "Configuration to be used:\n"
				+ prettyPrintMap(response) + "****************************\n****************************\n");

		return response;
	}

	private static String prettyPrintMap(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Object> entry = iter.next();
			sb.append(entry.getKey());
			sb.append(" = ");
			sb.append(entry.getValue());
			// sb.append('"');
			// if (iter.hasNext()) {
			sb.append('\n');
			// }
		}
		return sb.toString();

	}

	private static Options getOptions() {
		Options options = new Options();

		//@formatter:off
		Option instance = Option.builder("i").
				argName("instance").
				hasArg().
				longOpt("instance").
				desc("File location of the instance to use in the problem").
				required().
				build();
		options.addOption(instance);
		//@formatter:on

		//@formatter:off
		Option numAnts = Option.builder("n").
				argName("numants").
				hasArg().
				longOpt("numants").
				desc("Number of ants to be used in the colony").
				build();
		options.addOption(numAnts);
		//@formatter:on

		//@formatter:off
		Option rho = Option.builder("r").
				argName("rho").
				hasArg().
				longOpt("rho").
				desc("Rho value used in pheromone evaporation").
				build();
		options.addOption(rho);
		//@formatter:on

		//@formatter:off
		Option seed = Option.builder("s").
				argName("seed").
				hasArg().
				longOpt("seed").
				desc("Seed value used in the random numbers generation").
				build();
		options.addOption(seed);
		//@formatter:on

		//@formatter:off
		Option maxIter = Option.builder("m").
				argName("maxiter").
				hasArg().
				longOpt("maxiter").
				desc("Maximum number of iterations to be run for the solution").
				build();
		options.addOption(maxIter);
		//@formatter:on

		//@formatter:off
		Option algo = Option.builder("a").
				argName("algorithm").
				hasArg().
				longOpt("algorithm").
				desc("ACO Algorithm to run along with the solution. MINMAX and ELITIST (default) are the possible values").
				build();
		options.addOption(algo);
		//@formatter:on

		//@formatter:off
		Option localSearch = Option.builder("l").
				argName("localsearch").
				hasArg(false).
				longOpt("localsearch").
				desc("Whether or not to use local search for the MinMax solution").
				build();
		options.addOption(localSearch);
		//@formatter:on

		//@formatter:off
		Option help = Option.builder("h").
				argName("help").
				hasArg(false).
				longOpt("help").
				desc("Shows these instructions").
				build();
		options.addOption(help);
		//@formatter:on

		return options;
	}
	
	private static Options helpOptions(){
		//@formatter:off
		Options options = new Options();
		Option help = Option.builder("h").
			argName("help").
			hasArg(false).
			longOpt("help").
			desc("Shows these instructions").
			build();
		//@formatter:on
		options.addOption(help);
		return options;
	}

	private static void help() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ACO - CSP", getOptions());
		System.exit(0);
	}
}
