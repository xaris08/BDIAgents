package thesis.jadex.beliefs;

import org.cloudbus.cloudsim.Vm;

import thesis.jadex.main.CloudSimulator;

	public class GetMaxVm {
		
		Vm maxVm;
	
		public GetMaxVm(int hostId){
			maxVm = CloudSimulator.getMaxVm(hostId);
		}

		public Vm getVm(){
			return maxVm;
		}
}
