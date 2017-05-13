package de.uni_oldenburg.simulation.supermarket;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Writes given data as a string into an .arff file.
 */
public class ARFFWriter {


	private static StringBuilder sb;
	private static PrintWriter pw;

	/**
	 * Initializes an StringBuilder with the general arff header structure.
	 */
	public synchronized static void initializeARFF() {
		sb = new StringBuilder();
		sb.append("@RELATION waitingCustomers");
		sb.append('\n');
		sb.append("@ATTRIBUTE waitingCustomersAtCheckouts NUMERIC");
		sb.append('\n');
		sb.append("@ATTRIBUTE strategy STRING");
		sb.append('\n');
		sb.append("@ATTRIBUTE timestamp NUMERIC");
		sb.append('\n');
		sb.append("@Data");
		sb.append('\n');
	}

	/**
	 * Appends a arff data entry to the StringBuilder including a linebreak
	 *
	 * @param numberOfWaitingCustomers is the number of waiting customers at the checkouts
	 * @param customerStrategy         is the choosen customer strategy. Either random, onlyGoForward or shortestQueue
	 * @param timestamp                is the timestamp at when the data has been collected
	 */
	public synchronized static void appendArffEntry(int numberOfWaitingCustomers, int customerStrategy, long timestamp) {
		sb.append(numberOfWaitingCustomers).append(",").append(customerStrategy).append(",").append(timestamp);
		sb.append('\n');
	}

	/**
	 * Finally writes the data appended at the StringBuilder to an .arff file using the 'user.dir' + /arffOutput/output_x.arff where x ist the counter for the already written files. The StringBuilder is reset for new entries after writing.
	 */
	public synchronized static void writeToARFF() {
		File file;
		int counter = 0;
		do {
			file = new File(System.getProperty("user.dir") + File.separator + "arffOutput" + File.separator + "output_" + counter + ".arff");
			counter++;
		} while (file.exists());
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (pw != null) {
			pw.write(sb.toString());
			pw.close();
			initializeARFF(); // reset the stringbuilder
			System.out.println("Data exported successfully to " + file.getAbsolutePath() + "!");
		}
	}


}
