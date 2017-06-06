package be.vub.swarmintelligence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public abstract class CSPSolver implements Solver {
	private final static Logger LOGGER = Logger.getLogger(CSPSolver.class);

	protected Map<String, Object> cfg;
	protected Double alpha;
	protected Double beta;
	protected Double rho;
	protected Integer numAnts;
	protected Integer maxIter;
	protected Integer currentIter;
	protected List<Ant> ants;

	protected CSPProblem problem;

	@Deprecated
	protected List<List<Double>> pheromone;
	@Deprecated
	protected List<List<Integer>> heuristicInformation;
	protected List<List<Double>> probability;
	protected List<List<HeuristicPheromone>> heuristicPheromone;

	public CSPSolver(Map<String, Object> cfg) throws IOException {
		this.cfg = cfg;
		this.initProblem();
		this.alpha = (Double) this.cfg.get("alpha");
		this.beta = (Double) this.cfg.get("beta");
		this.rho = (Double) this.cfg.get("rho");
		this.numAnts = (Integer) this.cfg.get("numants");
		this.maxIter = (Integer) this.cfg.get("maxiter");
		this.currentIter = 0;
		this.initAdditionalParameters();
		this.initHeuristicPheromone();
		this.initProbability();
		this.initAnts();

		LOGGER.debug("Solver instance initalised. Ready to go!!!");
	}

	@Override
	public void initProblem() throws IOException {
		this.problem = new CSPProblem((String) this.cfg.get("instance"));
	}

	/**
	 * Initialize the pheromone components. The main component is the array of
	 * probabilities which is an array of arrays. Each sub-array shows the
	 * probability of each item to be picked in the shape of a pheromone matrix.
	 */
	@Override
	@Deprecated
	public void initPheromone() {
		this.pheromone = new ArrayList<>();
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			this.pheromone.add(auxInitPheromone());
		}
	}

	/**
	 * Auxiliary function to set the probability values into the arrays that
	 * will compose the big array of probabilities (pheromone)
	 * 
	 * @param length
	 * @param value
	 * @return
	 */
	@Deprecated
	private ArrayList<Double> auxInitPheromone() {
		ArrayList<Double> result = new ArrayList<>();
		for (int i = 0; i < this.problem.getAlphabetLength(); i++) {
			result.add(1.0 / this.problem.getAlphabetLength());
		}
		return result;
	}

	/**
	 * Initializes the Heuristic information with a count of each character that
	 * occurs at the given position taken from the list of input strings of the
	 * problem
	 */
	@Deprecated
	private void initHeursiticInformation() {
		this.heuristicInformation = new ArrayList<>();
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			this.heuristicInformation.add(auxInitHeuristicInformation(i));
		}
	}

	/**
	 * Auxiliary function to set the heuristic information values into the list
	 * of lists containing the required values
	 * 
	 * @param alphIdx
	 * @return
	 */
	@Deprecated
	private List<Integer> auxInitHeuristicInformation(Integer alphIdx) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < this.problem.getAlphabetLength(); i++) {
			Integer count = 0;
			for (String str : this.problem.getStrings()) {
				if (this.problem.getAlphabet().get(i).equals(str.charAt(alphIdx))) {
					count++;
				}
			}
			result.add(count);
		}
		return result;
	}

	/**
	 * Initialises the structure that contains both the pheromone and heuristic
	 * information together for faster computations
	 */
	private void initHeuristicPheromone() {
		this.heuristicPheromone = new ArrayList<>();
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			this.heuristicPheromone.add(auxInitHeuristicPheromone(i));
		}
	}

	/**
	 * Auxiliary function to set the values of heuristic information and
	 * probability for the required positions for the strings' length
	 * corresponding to the different values of the alphabet
	 * 
	 * @param alphIdx
	 * @return
	 */
	private List<HeuristicPheromone> auxInitHeuristicPheromone(Integer alphIdx) {
		List<HeuristicPheromone> result = new ArrayList<>();
		for (int i = 0; i < this.problem.getAlphabetLength(); i++) {
			// Initialise Heuristic Information
			Long count = 0l;
			for (String str : this.problem.getStrings()) {
				if (this.problem.getAlphabet().get(i).equals(str.charAt(alphIdx))) {
					count++;
				}
			}
			result.add(new HeuristicPheromone(count, 1.0 / this.problem.getAlphabetLength()));
		}
		return result;
	}

	/**
	 * Initialises the probability information based on the pheromone and
	 * heuristic information
	 */
	private void initProbability() {
		this.probability = new ArrayList<>();
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			this.probability.add(auxInitProbability());
		}

		this.updateProbability();
	}

	private List<Double> auxInitProbability() {
		List<Double> result = new ArrayList<>();
		for (int i = 0; i < this.problem.getAlphabetLength(); i++) {
			result.add(0.0);
		}
		return result;
	}

	// private void initProbability() {
	// this.probability = new ArrayList<>();
	// for (int j = 0; j < this.problem.getStrLength(); j++) {
	// List<Double> probList = new ArrayList<>();
	// for (int i = 0; i < this.problem.getStrLength(); i++) {
	// LOGGER.info(j);
	// probList.add(0.0);
	// }
	// this.probability.add(probList);
	// }
	// this.updateProbability();
	//
	// }

	/**
	 * Updates the probability list using the values for pheromone and heuristic
	 * information
	 */
	protected void updateProbability() {
		for (int j = 0; j < this.problem.getStrLength(); j++) {
			Double denominator = this.heuristicPheromone.get(j).stream()
					.mapToDouble(hp -> hp.getPheromoneValue() * Math.pow(hp.getHeuristicInformationValue(), this.alpha))
					.sum();
			for (int i = 0; i < this.problem.getAlphabetLength(); i++) {
				Double value = heuristicPheromone.get(j).get(i).getPheromoneValue()
						* Math.pow(heuristicPheromone.get(j).get(i).getHeuristicInformationValue(), this.alpha)
						/ denominator;
				this.probability.get(j).set(i, value);
			}
		}
	}

	@Override
	public void solve() {
		while (!this.terminate()) {
			this.solveColony();
			this.currentIter++;
			LOGGER.info("Iteration: " + this.currentIter + " Score: " + this.getCurrentScore() + " Min: "
					+ this.getCurrentMinHammingDistance() + " Max: " + this.getCurrentMaxHammingDistance());
		}

	}

	protected abstract void solveColony();

	protected abstract Long getCurrentScore();

	protected abstract Long getCurrentMinHammingDistance();

	protected abstract Long getCurrentMaxHammingDistance();

	protected abstract void initAdditionalParameters();

	/**
	 * Defines the termination condition
	 */
	@Override
	public Boolean terminate() {
		return this.maxIter < this.currentIter;
	}

	/**
	 * Makes the pheromone probability values match a probability making them
	 * all positive and sum 1.0
	 */
	protected void normalizePheromone() {
		// Make all negative values equals to zero
		this.pheromone = this.pheromone.stream()
				.map(p -> p.stream().map(v -> v.compareTo(0.0) < 0 ? 0.0 : v)
						.collect(Collectors.toCollection(ArrayList::new)))
				.collect(Collectors.toCollection(ArrayList::new));

		// Make all values in probability arrays sum exactly 1.0
		this.pheromone = this.pheromone.stream().map(p -> {
			Double sum = p.stream().mapToDouble(a -> a).sum();
			return p.stream().map(v -> v / sum).collect(Collectors.toCollection(ArrayList::new));
		}).collect(Collectors.toCollection(ArrayList::new));

	}

	protected void normalizeProbability() {
		// Make all negative values equals to zero
		this.probability = this.probability.stream()
				.map(p -> p.stream().map(v -> v.compareTo(0.0) < 0 ? 0.0 : v)
						.collect(Collectors.toCollection(ArrayList::new)))
				.collect(Collectors.toCollection(ArrayList::new));

		// Make all values in probability arrays sum exactly 1.0
		this.probability = this.probability.stream().map(p -> {
			Double sum = p.stream().mapToDouble(a -> a).sum();
			return p.stream().map(v -> v / sum).collect(Collectors.toCollection(ArrayList::new));
		}).collect(Collectors.toCollection(ArrayList::new));
	}

}
