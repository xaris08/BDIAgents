package thesis.jadex.tools;

import java.io.FileWriter;
import java.io.IOException;

import thesis.jadex.main.GUI;

public class Log {

	static String filename = "src/Log";
	static FileWriter fw;

	public static void printLine(String message) throws IOException {
		// print to the UI (textArea)
		GUI.println(message);
		
		// print to log file
		fw = new FileWriter(filename, true);
		fw.write(message + "\n");
		fw.flush();
//		System.out.println(message);
	}

	public static void closeFile() throws IOException {
		fw.close();
	}
	
	public static void emptyFile() throws IOException{
		fw = new FileWriter(filename, false);
		fw.write("");
		fw.flush();
	}
}
