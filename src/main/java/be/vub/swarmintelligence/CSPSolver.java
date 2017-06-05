package be.vub.swarmintelligence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class CSPSolver implements Solver {
	private final static Logger LOGGER = Logger.getLogger(CSPSolver.class);

	private Map<String, Object> cfg;
	private Double alpha;
	private Double beta;
	private Double rho;
	private Integer numAnts;
	private Integer maxIter;
	private Integer currentIter;
	private Ant bestAnt;
	private List<Ant> ants;

	private CSPProblem problem;

	private List<List<Double>> pheromone;
	private List<List<Integer>> heuristicInformation;

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
	}

	@Override
	public void solve() {
		while (!this.terminate()) {
			this.solveColony();
			this.currentIter++;
			LOGGER.info("Iteration: " + this.currentIter + " Score: " + this.bestAnt.getScore() + " Min: "
					+ this.bestAnt.getMinHammingDistance() + " Max: " + this.bestAnt.getMaxHammingDistance());
		}

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
		for(int i= 0; i<this.problem.getAlphabetLength(); i++){
			Integer count = 0;
			for(String str : this.problem.getStrings()){
				if(this.problem.getAlphabet().get(i).equals(str.charAt(alphIdx))){
					count ++;
				}
			}
			result.add(count);
		}
		return result;
	}

	@Override
	public void initAnts() {
		this.ants = new ArrayList<>();
		for (int i = 0; i < this.numAnts; i++) {
			this.ants.add(new Ant());
		}
		this.bestAnt = this.ants.get(0);
		this.solveColony();
	}

	/**
	 * Extracted method containing the logic to run the colony over one
	 * iteration of the solution process.
	 */
	private void solveColony() {

		// ants.parallelStream().

		for (Ant ant : this.ants) {
			ant.findSolution(this.pheromone, this.problem.getAlphabet());
			ant.evaluateSolution(this.problem.getStrings());
			if (this.bestAnt.getMaxHammingDistance() > ant.getMaxHammingDistance()) {
				this.bestAnt = ant;
			}
		}
		this.evaporatePheromone();
		this.depositPheromone();
		this.normalizePheromone();
	}

	/**
	 * Defines the termination condition
	 */
	@Override
	public Boolean terminate() {
		return this.maxIter < this.currentIter;
	}

	@Override
	public void evaporatePheromone() {
		// this.pheromoneProbabilities =
		// this.pheromoneProbabilities.parallelStream()
		this.pheromone = this.pheromone.stream()
				.map(p -> p.stream().map(v -> v - this.rho).collect(Collectors.toCollection(ArrayList::new)))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void depositPheromone() {
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			int j = this.problem.getInverseAlphabet().get(this.bestAnt.getSolution().charAt(i));
			Double val = this.pheromone.get(i).get(j)
					+ (1.0 - ((double) this.bestAnt.getMaxHammingDistance() / this.problem.getStrLength()));
			this.pheromone.get(i).set(j, val);
		}

	}

	/**
	 * Makes the pheromone probability values match a probability making them
	 * all positive and sum 1.0
	 */
	private void normalizePheromone() {
		// Make all negative values equals to zero
		// this.pheromoneProbabilities =
		// this.pheromoneProbabilities.parallelStream()
		this.pheromone = this.pheromone.stream()
				.map(p -> p.stream().map(v -> v.compareTo(0.0) < 0 ? 0.0 : v)
						.collect(Collectors.toCollection(ArrayList::new)))
				.collect(Collectors.toCollection(ArrayList::new));

		// Make all values in probability arrays sum exactly 1.0
		// this.pheromoneProbabilities =
		// this.pheromoneProbabilities.parallelStream().map(p -> {
		this.pheromone = this.pheromone.stream().map(p -> {
			Double sum = p.stream().mapToDouble(a -> a).sum();
			return p.stream().map(v -> v / sum).collect(Collectors.toCollection(ArrayList::new));
		}).collect(Collectors.toCollection(ArrayList::new));

	}

}
