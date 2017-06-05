package be.vub.swarmintelligence;

import java.io.IOException;

public interface Problem {

	void loadInputData(String fileLocation) throws IOException;

	void initInternalStructures();
}
