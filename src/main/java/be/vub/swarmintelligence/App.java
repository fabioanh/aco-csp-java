package be.vub.swarmintelligence;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException {
		Map<String, Object> cfg = getCLIConfig(args);
		RandomUtils.getInstance((Integer)cfg.get("seed"));
		CSPSolver solver = new CSPSolver(cfg);
		solver.solve();

	}

	private static Map<String, Object> getCLIConfig(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Map<String, Object> response = new HashMap<>();
		try {
			// parse the command line arguments
			CommandLine cliArgs = parser.parse(getOptions(), args);
			if (cliArgs.hasOption("instance")) {
				response.put("instance", (String)cliArgs.getOptionValue("instance"));
			}
			if (cliArgs.hasOption("rho")) {
				response.put("rho", Double.valueOf(cliArgs.getOptionValue("rho")));
			}else{
				response.put("rho", 0.0003);
			}
			if (cliArgs.hasOption("seed")) {
				response.put("seed", Integer.valueOf(cliArgs.getOptionValue("seed")));
			}else{
				response.put("seed", 1234);
			}
		} catch (ParseException ex) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + ex.getMessage());
		}
		return response;
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
		options.addOption(rho);
		//@formatter:on

		return options;
	}
}
