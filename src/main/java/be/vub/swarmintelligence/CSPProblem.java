package be.vub.swarmintelligence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSPProblem implements Problem {
	private List<String> strings;
	private List<String> alphabet;
	private Integer alphabetLength;
	private Integer numStr;
	private Integer strLength;

	public CSPProblem(String fileLocation) throws IOException {
		this.initInternalStructures();
		this.loadInputData(fileLocation);  
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
				this.alphabet.add(line.trim());
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

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

	public List<String> getAlphabet() {
		return alphabet;
	}

	public void setAlphabet(List<String> alphabet) {
		this.alphabet = alphabet;
	}

	public Integer getAlphabetLength() {
		return alphabetLength;
	}

	public void setAlphabetLength(Integer alphabetLength) {
		this.alphabetLength = alphabetLength;
	}

	public Integer getNumStr() {
		return numStr;
	}

	public void setNumStr(Integer numStr) {
		this.numStr = numStr;
	}

	public Integer getStrLength() {
		return strLength;
	}

	public void setStrLength(Integer strLength) {
		this.strLength = strLength;
	}
	
	
	
}
