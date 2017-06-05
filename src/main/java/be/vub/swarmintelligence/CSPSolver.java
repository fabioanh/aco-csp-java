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

	protected List<List<Double>> pheromone;
	protected List<List<Integer>> heuristicInformation;

	public CSPSolver(Map<String, Object> cfg) throws IOException {
		this.cfg = cfg;
		this.initProblem();
		this.alpha = (Double) this.cfg.get("alpha");
		this.beta = (Double) this.cfg.get("beta");
		this.rho = (Double) this.cfg.get("rho");
		this.numAnts = (Integer) this.cfg.get("numants");
		this.maxIter = (Integer) this.cfg.get("maxiter");
		this.currentIter = 0;
		this.initPheromone();
		this.initHeursiticInformation();
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
	public void initPheromone() {
		this.pheromone = new ArrayList<>();
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			this.pheromone.add(auxInitPheromoneProbabilities());
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

	public abstract void solveColony();

	public abstract Long getCurrentScore();

	public abstract Long getCurrentMinHammingDistance();

	public abstract Long getCurrentMaxHammingDistance();

	/**
	 * Auxiliary function to set the probability values into the arrays that
	 * will compose the big array of probabilities (pheromone)
	 * 
	 * @param length
	 * @param value
	 * @return
	 */
	private ArrayList<Double> auxInitPheromoneProbabilities() {
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
	private void initHeursiticInformation() {
		this.heuristicInformation = new ArrayList<>();
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			this.heuristicInformation.add(auxInitHeuristicInformation(i));
		}
	}

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

}
