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
//		LOGGER.debug(this.printPheromone());
		this.normalizeProbability();
	}

	private String printPheromone() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		this.heuristicPheromone.stream().forEach(ls -> {
			sb.append('[');
			ls.stream().forEach(v -> sb.append(v.getPheromoneValue() + ", "));
			sb.append("]");
		});
		sb.append("]\n");
		return sb.toString();
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
		for (int j = 0; j < this.problem.getStrLength(); j++) {
			for (int i = 0; i < this.problem.getAlphabetLength(); i++) {
				Double pherVal = (1.0 - this.rho) * this.heuristicPheromone.get(j).get(i).getPheromoneValue();
				if (this.bestAnt.getPath().get(j) == i) {
					pherVal += this.epsilon * 1.0 / (double) this.getCurrentMaxHammingDistance();
				}
				this.heuristicPheromone.get(j).get(i).setPheromoneValue(pherVal);
			}
		}

	}

	/**
	 * Updates the probability list using the values for pheromone and heuristic
	 * information
	 */
	public void updateProbability() {
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
	protected void loadAdditionalParameters() {
		this.epsilon = (Double) this.cfg.get("epsilon");
	}
}
