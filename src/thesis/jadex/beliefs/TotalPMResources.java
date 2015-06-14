package thesis.jadex.beliefs;

import thesis.jadex.main.CloudSimulator;

public class TotalPMResources {

	int resources;
	
	public TotalPMResources(int id){
		resources = CloudSimulator.getTotalPMResources(id);
	}
	
	public int getResources(){
		return resources;
	}
}
