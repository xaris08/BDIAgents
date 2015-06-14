package thesis.jadex.beliefs;

import java.util.List;

import org.cloudbus.cloudsim.Vm;

import thesis.jadex.main.CloudSimulator;

public class VirtualMachineList {

	List<Vm> vmlist;
	
	public VirtualMachineList(int host_id){
		vmlist = CloudSimulator.getVmsInHost(host_id);
	}
	
	public int[] getList(){
	
		int[] vms = new int[vmlist.size()];
		
		for(int i=0; i<vmlist.size(); i++) {
			vms[i] = vmlist.get(i).getId();
		}
		return vms;
	}

}
