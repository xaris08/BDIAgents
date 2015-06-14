package thesis.jadex.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CreatePlanFiles {

	// The number of copies to be achieved.
	// put required + 1.
	private int n;

	public CreatePlanFiles(int num){
		this.n= num;
	}
	
	public void planFiles() throws IOException {

		//deleteFiles();
		String fileName[] = new String[n];

		for (int i = 1; i < n; i++) {
			fileName[i] = "src/thesis/jadex/agents/PM" + (i + 1) + ".java";
			copyfile("src/thesis/jadex/agents/PM1.java", fileName[i]);
			//System.out.println("File " + (i + 1) + " copied.");
		}

		for (int i = 1; i < n-1; i++) {
			
			FileReader readFile = new FileReader("src/thesis/jadex/agents/PM" + (i + 2) + ".java");
			FileWriter destFile = new FileWriter("src/thesis/jadex/agents/PM" + (i + 1) + ".java");
			BufferedReader bf = new BufferedReader(readFile);
			BufferedWriter destbf = new BufferedWriter(destFile);

			String line;
			
			while ((line = bf.readLine()) != null) {

				if (line.contains("int hostId")) {					
					String newline = line.replaceAll(""+0, ""+i);
					destbf.write(newline);
					destbf.newLine();
				}
				else if (line.contains("=====")) {
					String newline = line.replaceAll("1", ""+(i+1));
					destbf.write(newline);
					destbf.newLine();
				}
				else if (line.contains("class")) {
					String newline = line.replaceAll("PM1", "PM"+(i+1));
					destbf.write(newline);
					destbf.newLine();
				}
				else if (line.contains("PM1()")) {
					String newline = line.replaceAll("PM1", "PM"+(i+1));
					destbf.write(newline);
					destbf.newLine();
				}
				else { 
					destbf.write(line);
					destbf.newLine();
				}
			}
			destbf.close();
		}
	}

	public static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);
			// For Append the file.
			OutputStream out = new FileOutputStream(f2, true);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void deleteFiles() {
		File dir = new File("src/thesis/jadex/agents");
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith("PM");
			}
		});
		for (File f : files){
			f.delete();
		}
	}
}
