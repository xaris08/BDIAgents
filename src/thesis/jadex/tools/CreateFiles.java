package thesis.jadex.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class CreateFiles {

	private static int numberOfAgents;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		numberOfAgents = 5;
		CreatePlanFiles plan = new CreatePlanFiles(numberOfAgents);
		CreateXmlFiles xml = new CreateXmlFiles(numberOfAgents);
		
		plan.planFiles();
		xml.xmlFiles();
		System.out.println("Done.");
		deleteFiles();
	}

	public static void deleteFiles() {
		File dir = new File("src/thesis/jadex/agents");
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith("PM"+numberOfAgents);
			}
		});
		for (File f : files){
			f.delete();
		}
	}
}
