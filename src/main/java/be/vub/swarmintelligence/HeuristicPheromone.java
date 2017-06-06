package be.vub.swarmintelligence;

/**
 * Auxiliary structure holding both values for heuristic information and
 * pheromone simultaneously. Structure intended to reduce the number of
 * iterations by two, given that every time that the pheromone needs to be
 * walked through the pheromone does as well.
 * 
 * @author fabio
 *
 */
public class HeuristicPheromone {
	private Double pheromoneValue;
	private Long heuristicInformationValue;

	public HeuristicPheromone(Long heuristicInformationValue, Double pheromoneValue) {
		super();
		this.pheromoneValue = pheromoneValue;
		this.heuristicInformationValue = heuristicInformationValue;
	}

	public Double getPheromoneValue() {
		return pheromoneValue;
	}

	public void setPheromoneValue(Double pheromoneValue) {
		this.pheromoneValue = pheromoneValue;
	}

	public Long getHeuristicInformationValue() {
		return heuristicInformationValue;
	}

	public void setHeuristicInformationValue(Long heuristicInformationValue) {
		this.heuristicInformationValue = heuristicInformationValue;
	}

}
