package be.vub.swarmintelligence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class CSPElitistSolver extends CSPSolver {

	private final static Logger LOGGER = Logger.getLogger(CSPElitistSolver.class);

	private Ant bestAnt;

	private Double epsilon;

	public CSPElitistSolver(Map<String, Object> cfg) throws IOException {
		super(cfg);
	}

	/**
	 * Extracted method containing the logic to run the colony over one
	 * iteration of the solution process.
	 */
	@Override
	protected void solveColony() {

		// ants.parallelStream().

		for (Ant ant : this.ants) {
			ant.findSolution(this.probability, this.problem.getAlphabet());
			ant.evaluateSolution(this.problem.getStrings());
			if (this.bestAnt.getMaxHammingDistance() > ant.getMaxHammingDistance()) {
				this.bestAnt = ant;
			}
		}
		this.updatePheromone();
		this.updateProbability();
		this.normalizeProbability();
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

	@Deprecated
	public void evaporatePheromone() {
		this.pheromone = this.pheromone.stream()
				.map(p -> p.stream().map(v -> v - this.rho).collect(Collectors.toCollection(ArrayList::new)))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Deprecated
	public void depositPheromone() {
		for (int i = 0; i < this.problem.getStrLength(); i++) {
			int j = this.problem.getInverseAlphabet().get(this.bestAnt.getSolution().charAt(i));
			Double val = this.pheromone.get(i).get(j)
					+ (1.0 - ((double) this.bestAnt.getMaxHammingDistance() / this.problem.getStrLength()));
			this.pheromone.get(i).set(j, val);
		}

	}

	@Override
	protected Long getCurrentScore() {
		return this.bestAnt.getScore();
	}

	@Override
	protected Long getCurrentMinHammingDistance() {
		return this.bestAnt.getMinHammingDistance();
	}

	@Override
	protected Long getCurrentMaxHammingDistance() {
		return this.bestAnt.getMaxHammingDistance();
	}

	/**
	 * Update the pheromone value using an additional increased based on the
	 * solution given by the best ant
	 */
	@Override
	public void updatePheromone() {
		this.heuristicPheromone = this.heuristicPheromone.stream()
				.map(p -> p.stream()
						.map(v -> new HeuristicPheromone(v.getHeuristicInformationValue(),
								(1.0 - this.rho) * v.getPheromoneValue()
										+ this.epsilon * 1.0 / (double) this.getCurrentMaxHammingDistance()))
						.collect(Collectors.toCollection(ArrayList::new)))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	@Override
	protected void initAdditionalParameters() {
		this.epsilon = (Double) this.cfg.get("epsilon");
	}
}
