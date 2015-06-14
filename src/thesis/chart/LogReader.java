package thesis.chart;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jfree.ui.RefineryUtilities;

public class LogReader {
	
	private String path;
	private Chart chart;

	public LogReader() throws IOException {
		path = "src/Log";
		// Initialize the Chart window.
		chart = new Chart("PM resources");
		readLines();
	}

	private void readLines() throws IOException {
		
		FileReader readFile = new FileReader(path);
		BufferedReader bf = new BufferedReader(readFile);

		String line;

		while ((line = bf.readLine()) != null) {
			if (line.startsWith("-")){
				String[] tokens = line.split(" ");
				chart.addToDataset(Integer.parseInt(tokens[2]), Float.parseFloat(tokens[5]), Integer.parseInt(tokens[6]));
				System.out.println(Integer.parseInt(tokens[2]) +" "+ Float.parseFloat(tokens[5]) +" "+ Integer.parseInt(tokens[6]));
			}
		}
		chart.initialise();
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);
		bf.close();
	}
	
	public static void main(String[] args) throws IOException{
		new LogReader();
	}
	
}
