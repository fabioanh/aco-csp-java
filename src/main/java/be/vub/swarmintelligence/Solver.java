package be.vub.swarmintelligence;

import java.io.IOException;

public interface Solver {
	void solve();

	void initProblem() throws IOException;

	void initPheromone();

	void initAnts();
	
	void evaporatePheromone();
	
	void depositPheromone();

	Boolean terminate();

}