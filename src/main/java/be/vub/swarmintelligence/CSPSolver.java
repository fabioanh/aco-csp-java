package be.vub.swarmintelligence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSPSolver implements Solver {
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

	private ArrayList<ArrayList<Double>> pheromoneProbabilities;

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
		this.initAnts();
	}

	@Override
	public void solve() {
		// System.out.println(this.problem.getAlphabet());
		// System.out.println(this.pheromoneProbabilities);
		this.evaporatePheromone();
		System.out.println(this.pheromoneProbabilities);
		this.normalizePheromone();
		System.out.println(this.pheromoneProbabilities);
		
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
		this.pheromoneProbabilities = new ArrayList<>();
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			this.pheromoneProbabilities.add(auxInitPheromoneProbabilities(this.problem.getAlphabetLength(),
					1.0 / this.problem.getAlphabetLength()));
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
	private ArrayList<Double> auxInitPheromoneProbabilities(int length, Double value) {
		ArrayList<Double> result = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			result.add(value);
		}
		return result;
	}

	@Override
	public void initAnts() {
		this.ants = new ArrayList<>();
		for (int i = 0; i < this.numAnts; i++) {
			this.ants.add(new Ant());
		}
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
		this.pheromoneProbabilities = this.pheromoneProbabilities.stream()
				.map(p -> p.stream().map(v -> v - this.rho).collect(Collectors.toCollection(ArrayList::new)))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void depositPheromone() {

	}

	/**
	 * Makes the pheromone probability values match a probability making them
	 * all positive and sum 1.0
	 */
	private void normalizePheromone() {
		// Make all negative values equals to zero
		this.pheromoneProbabilities = this.pheromoneProbabilities.stream()
				.map(p -> p.stream().map(v -> v.compareTo(0.0) < 0 ? 0.0 : v)
						.collect(Collectors.toCollection(ArrayList::new)))
				.collect(Collectors.toCollection(ArrayList::new));

		// Make all values in probability arrays sum exactly 1.0
		this.pheromoneProbabilities = this.pheromoneProbabilities.stream().map(p -> {
			Double sum = p.stream().mapToDouble(a -> a).sum();
			return p.stream().map(v -> v / sum).collect(Collectors.toCollection(ArrayList::new));
		}).collect(Collectors.toCollection(ArrayList::new));

	}

}
