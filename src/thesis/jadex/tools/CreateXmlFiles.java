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

public class CreateXmlFiles {

	// The number of copies to be achieved.
	private int n;

	public CreateXmlFiles(int num){
		this.n = num;
	}
	
	public void xmlFiles() throws IOException {

		//deleteFiles();
		String fileName[] = new String[n];

		for (int i = 1; i < n; i++) {
			fileName[i] = "src/thesis/jadex/agents/PM" + (i + 1) + ".agent.xml";
			copyfile("src/thesis/jadex/agents/PM1.agent.xml", fileName[i]);
		}

		for (int i = 1; i < n-1; i++) {
			
			FileReader readFile = new FileReader("src/thesis/jadex/agents/PM" + (i + 2) + ".agent.xml");
			FileWriter destFile = new FileWriter("src/thesis/jadex/agents/PM" + (i + 1) + ".agent.xml");
			BufferedReader bf = new BufferedReader(readFile);
			BufferedWriter destbf = new BufferedWriter(destFile);

			String line;
			
			while ((line = bf.readLine()) != null) {

				if (line.contains("name=\"PM1\"")) {					
					String newline = line.replaceAll("PM1", "PM"+(i+1));
					destbf.write(newline);
					destbf.newLine();
				}
				else if (line.contains("VirtualMachineList(0).getList()")) {
					String newline = line.replaceAll("0", ""+(i));
					destbf.write(newline);
					destbf.newLine();
				}
				else if (line.contains("CalculateCpuUsage(0).getCpu()")) {
					String newline = line.replaceAll("0", ""+(i));
					destbf.write(newline);
					destbf.newLine();
				}
				else if (line.contains("class=\"PM1\"")) {
					String newline = line.replaceAll("PM1", "PM"+(i+1));
					destbf.write(newline);
					destbf.newLine();
				}
				else if (line.contains("TotalPMResources(0).getResources()")) {
					String newline = line.replaceAll("0", ""+(i));
					destbf.write(newline);
					destbf.newLine();
				}
				else if (line.contains("GetMaxVm(0).getVm()")){
					String newline = line.replaceAll("0", ""+(i));
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
				return pathname.getName().endsWith("xml");
			}
		});
		for (File f : files){
			f.delete();
		}
	}
}
