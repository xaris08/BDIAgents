package thesis.jadex.beliefs;

import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.lists.HostList;

import thesis.jadex.main.CloudSimulator;

public class CalculateCpuUsage {

	int resourcesUsed;
	
	public CalculateCpuUsage(int id){
		resourcesUsed = CloudSimulator.getCpuUsage(id);
	}

	public int getCpu(){
		return resourcesUsed;
	}
}
