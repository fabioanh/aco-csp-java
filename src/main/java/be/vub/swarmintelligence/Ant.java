package be.vub.swarmintelligence;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

public class Ant {

	// Total sum of all Humming Distances from the set of strings
	private Long score = 999999l;
	private Long maxHammingDistance;
	private Long minHammingDistance;

	private String solution;
	private List<Integer> path;
	
	/**
	 * Finds a solution using the probability values given by the pheromone
	 * traces.
	 * 
	 * @param pheromoneProbabilities
	 * @param alphabet
	 */
	public void findSolution(List<List<Double>> pheromoneProbabilities, List<Character> alphabet) {
		RandomUtils randUtils = RandomUtils.getInstance(null);
		List<Integer> positions = pheromoneProbabilities.stream().map(ls -> randUtils.getRandomFromSimpleList(ls))
				.collect(Collectors.toList());
		StringBuilder sb = new StringBuilder();
		this.path = positions;
		for (Integer p : positions) {
			sb.append(alphabet.get(p));
		}
		this.solution = sb.toString();
	}

	public void evaluateSolution(List<String> strings) {
		IntSummaryStatistics solutionStatistics = strings.stream().map(s -> this.hammingDistance(s, this.solution))
				.mapToInt(Integer::intValue).summaryStatistics();
		this.score = solutionStatistics.getSum();
		this.maxHammingDistance = (long) solutionStatistics.getMax();
		this.minHammingDistance = (long) solutionStatistics.getMin();
	}

	private Integer hammingDistance(String str1, String str2) {
		if (str1.length() != str2.length()) {
			throw new IllegalStateException("Strings should have the same length to compute the Hamming Distance");
		}
		Integer hd = 0;
		for (int i = 0; i < str1.length(); i++) {
			hd += str1.charAt(i) == str2.charAt(i) ? 0 : 1;
		}
		return hd;
	}

	public Long getScore() {
		return score;
	}

	public Long getMaxHammingDistance() {
		return maxHammingDistance;
	}

	public Long getMinHammingDistance() {
		return minHammingDistance;
	}

	public String getSolution() {
		return solution;
	}
	
	public List<Integer> getPath(){
		return this.path;
	}

}
