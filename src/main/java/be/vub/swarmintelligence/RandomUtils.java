package be.vub.swarmintelligence;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomUtils {

	private static RandomUtils instance = null;
	private final Random random;

	private RandomUtils(Integer seed) {
		random = new Random(seed);
	}

	public static RandomUtils getInstance(Integer seed) {
		if (instance == null && seed != null) {
			instance = new RandomUtils(seed);
		}
		return instance;
	}

	/**
	 * Returns an unbounded random integer
	 * 
	 * @return
	 */
	public Integer getRandomInt() {
		return random.nextInt();
	}

	/**
	 * Returns a bounded random integer
	 * 
	 * @param upperBound
	 * @return
	 */
	public Integer getRandomInt(Integer upperBound) {
		Integer aux = random.nextInt(upperBound);
		return aux;
	}

	/**
	 * Returns a random double between 0.0 (inclusive) and 1.0 (exclusive)
	 * 
	 * @return
	 */
	public Double getRandomDouble() {
		return random.nextDouble();
	}

	public Random getRandom() {
		return random;
	}

	/**
	 * Returns a random element from a list given the probability items in an
	 * {@link Map.Entry<Integer, Double>} format, where the key of the entry is
	 * the element and the value is its probability.
	 * 
	 * Asumes that the list of elements total 1.0 for the sum of their
	 * probabilities.
	 * 
	 * @param list
	 * @return
	 */
	public Integer getRandomFromList(List<Map.Entry<Integer, Double>> list) {
		Double prob = getRandomDouble();
		Double sum = 0.0;
		for (Map.Entry<Integer, Double> elem : list) {
			sum += elem.getValue();
			if (prob.compareTo(sum) <= 0) {
				return elem.getKey();
			}
		}
		throw new IllegalStateException("The probabilities of the given elements don't sum 1.0");
	}

	/**
	 * Returns the index of a random element picked from a list given as
	 * probability values {@link List<Double>}.
	 * 
	 * Asumes that the list of elements total 1.0 for the sum of their
	 * probabilities.
	 * 
	 * @param list
	 * @return
	 */
	public Integer getRandomFromSimpleList(List<Double> list) {
		double p = Math.random();
		double cumulativeProbability = 0.0;
		Integer idx = 0;
		for (Double item : list) {
			cumulativeProbability += item;
			if (p <= cumulativeProbability) {
				return idx;
			}
			idx++;
		}
		throw new IllegalStateException("The probabilities of the given elements don't sum 1.0");
	}

}