package be.vub.swarmintelligence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class CSPProblem implements Problem {
	private final static Logger LOGGER = Logger.getLogger(CSPProblem.class);
	
	private List<String> strings;
	private List<Character> alphabet;
	private Integer alphabetLength;
	private Integer numStr;
	private Integer strLength;
	private Map<Character, Integer> inverseAlphabet;

	public CSPProblem(String fileLocation) throws IOException {
		this.initInternalStructures();
		this.loadInputData(fileLocation);
		this.computeInverseAlphabet();
		LOGGER.debug("Problem properly loaded from input file!");
	}

	private void computeInverseAlphabet() {
		this.inverseAlphabet = new HashMap<>();
		for (int i = 0; i < this.alphabetLength; i++) {
			this.inverseAlphabet.put(this.alphabet.get(i), i);
		}
	}

	/**
	 * Loads input data from provided instances following the specified format
	 */
	public void loadInputData(String fileLocation) throws IOException {
		FileReader fr = new FileReader(fileLocation);
		BufferedReader bufr = new BufferedReader(fr);

		int count = 0;
		String line = bufr.readLine();
		while (line != null) {
			if (count == 0) {
				this.alphabetLength = Integer.valueOf(line.trim());
			} else if (count == 1) {
				this.numStr = Integer.valueOf(line.trim());
			} else if (count == 2) {
				this.strLength = Integer.valueOf(line.trim());
			} else if (count <= this.alphabetLength + 2) {
				this.alphabet.add(line.trim().charAt(0));
			} else if (!line.trim().isEmpty()) {
				this.strings.add(line.trim());
			}
			line = bufr.readLine();
			count++;
		}
		bufr.close();

	}

	public void initInternalStructures() {
		strings = new ArrayList<>();
		alphabet = new ArrayList<>();

	}

	public List<String> getStrings() {
		return strings;
	}

	public List<Character> getAlphabet() {
		return alphabet;
	}

	public Integer getAlphabetLength() {
		return alphabetLength;
	}

	public Integer getNumStr() {
		return numStr;
	}

	public Integer getStrLength() {
		return strLength;
	}

	public Map<Character, Integer> getInverseAlphabet() {
		return inverseAlphabet;
	}

}
