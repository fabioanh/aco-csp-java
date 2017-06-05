package be.vub.swarmintelligence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class CSPElitistSolver extends CSPSolver {

	private final static Logger LOGGER = Logger.getLogger(CSPElitistSolver.class);

	private Ant bestAnt;

	public CSPElitistSolver(Map<String, Object> cfg) throws IOException {
		super(cfg);
	}

	/**
	 * Extracted method containing the logic to run the colony over one
	 * iteration of the solution process.
	 */
	public void solveColony() {

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

	@Override
	public void initAnts() {
		this.ants = new ArrayList<>();
		for (int i = 0; i < this.numAnts; i++) {
			this.ants.add(new Ant());
		}
		this.bestAnt = this.ants.get(0);
		this.solveColony();
	}

	@Override
	public void evaporatePheromone() {
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

	@Override
	public Long getCurrentScore() {
		return this.bestAnt.getScore();
	}

	@Override
	public Long getCurrentMinHammingDistance() {
		return this.bestAnt.getMinHammingDistance();
	}

	@Override
	public Long getCurrentMaxHammingDistance() {
		return this.bestAnt.getMaxHammingDistance();
	}
}
