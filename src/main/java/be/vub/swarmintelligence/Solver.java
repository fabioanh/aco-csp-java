package be.vub.swarmintelligence;

import java.io.IOException;

public interface Solver {
	void solve();

	void initProblem() throws IOException;

	void initHeuristicPheromone();

	void initAnts();
	
	void updatePheromone();
	
	void updateProbability();

	Boolean terminate();

}
