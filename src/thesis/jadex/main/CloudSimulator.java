/*
 * Title:        CloudSimulator Toolkit
 * Description:  CloudSimulator (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

package thesis.jadex.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import thesis.jadex.tools.JadexConnector;
import thesis.jadex.tools.Log;

/**
 * A simple example showing how to create
 * a datacenter with two hosts and run two
 * cloudlets on it. The cloudlets run in
 * VMs with different MIPS requirements.
 * The cloudlets will take different time
 * to complete the execution depending on
 * the requested VM performance.
 */
public class CloudSimulator {
	
	/** The number of the VMs and Hosts to be created the first time. */
	private static int numberOfVms = 40;
	private static int numberOfHosts = 4;
	private static int minVm = 130, maxVm = 180;
	private static int hostMips = 2000;
	
	/** The list of hosts available. */
	private static List<Host> hostList;
	
	/** The allocation policy provided by the CloudSim. */
	private static VmAllocationPolicySimple vmAllocationPolicySimple;
	
	/** The id of the first is set to 0. */
	private static int vmid = 0;
	
	/** The Jadex platform. */
	private static JadexPlatform platform;

	/** The vmlist. */
	public static List<Vm> vmlist = new ArrayList<Vm>();
	private static double initialTime;
	static int brokerId;

	public CloudSimulator(){
	}
	
	/**
	 * Creates main() to run this example
	 * @throws Throwable 
	 */
	public static void startSimulator() throws Throwable {	
		Log.emptyFile();
		Log.printLine("Starting CloudSimulator...");
		
		try {
			// First step: Initialize the CloudSimulator package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSimulator library
			CloudSim.init(num_user, calendar, trace_flag);
			
			// Second step: Create Datacenters Datacenters are the resource 
			// providers in CloudSimulator. We will use only one for our simulation.
			// num is the number of hosts or physical machines in the datacenter
			createDatacenter("Datacenter_0", numberOfHosts);
			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			brokerId = broker.getId();

			//Fourth step: Create the virtual machines.
			createVms(numberOfVms, brokerId);
			//submit VM list to the broker
			broker.submitVmList(vmlist);
			// Allocate VMs to hosts.
			vmAllocation(hostList, vmlist);
			/* Create the agents on each host (physical machine) 
			 * after initializing the host with its virtual machines. 
			 */
			createAgents(hostList);
			
			/** Sixth step: Start the simulation */
			CloudSim.startSimulation();
			
			getInitialTimeForAll();
			setInitialTime();
			/** Seventh step: Terminate the simulation after n sec.*/
			waiting(250);
			// Get the time after ending the simulation.
			getTime(numberOfHosts);
			Log.printLine("Simulation has been successfully terminated.");
			exitSimulator();
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	} //end of main.

	public static void setInitialTime() {
		initialTime = System.currentTimeMillis(); 
	}
	
	/**
	 * Add VMs at runtime.
	 * 
	 * @param number
	 * @param hostId
	 * @throws IOException
	 */
	public static void addVMs(int number, int hostId) throws IOException{
		getTime(numberOfHosts);
		createVms(number, brokerId);
		vmlist.get(vmlist.size()-1).setHost(hostList.get(hostId));
		hostList.get(hostId).addMigratingInVm(vmlist.get(vmlist.size()-1));
	}
	
	public static void exitSimulator() throws Throwable{
		getTime(numberOfHosts);
		CloudSim.terminateSimulation();
		platform.quit();
		Log.printLine("Exit Simulation.");
		Log.closeFile();
	}
	
	/**
	 * Create VMs by providing a random number of CPU resources needed.
	 * 
	 * @param count
	 * @param brokerId
	 * @throws IOException
	 */
	private static void createVms(int count, int brokerId) throws IOException{
		
		// VM description.
		int mips;
		long size = 10000; //image size (MB)
		int ram = 2048; //vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; //number of cpus
		String vmm = "Xen"; //VMM name
		
		for (int i=0; i<count; i++){	
			// Get a random number of mips for each VM description.
			mips = minVm + (int)(Math.random() * ((maxVm - minVm) + 1));
			//Log.printLine("adding vm"+vmid+" "+mips);
			//add the VMs to the vmList
			vmlist.add(new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared()));
			vmid++;
		}
	}
	
	/**
	 * A simple allocation policy of VMs to available hosts.
	 * 
	 * @param host
	 * @param vm
	 * @throws IOException
	 */
	private static void vmAllocationSimple(List<Host> host, List<Vm> vm) throws IOException{
		
		int vmForEach = numberOfVms / numberOfHosts;
		
		int i, j=0;
		int[] usage = new int[numberOfHosts];
		
		for (i=0; i<numberOfHosts; i++){
			usage[i] = 0;
			for (j=i; j<numberOfVms; j+=vmForEach) {
				vm.get(j).setHost(host.get(i));
				hostList.get(i).addMigratingInVm(vm.get(j));
				//Log.printLine("host"+hostList.get(i).getId()+" has vm"+j);
				usage[i] += vm.get(j).getMips();
			}
		}
		for (int k = j; k<numberOfVms; k++){
			vm.get(k).setHost(host.get(i));
			hostList.get(i).addMigratingInVm(vm.get(k));
			Log.printLine("host"+hostList.get(i).getId()+" has vm"+k);
			usage[i] += vm.get(k).getMips();
		}
		for (int l=0; l<numberOfHosts; l++){
			Log.printLine("host"+hostList.get(l).getId()+" has usage: "+ usage[l]);
		}
	}
	
	/**
	 * A random allocation policy of VMs to PMs (hosts).
	 * 
	 * @param host
	 * @param vm
	 * @throws IOException
	 */
	private static void vmAllocation(List<Host> host, List<Vm> vm) throws IOException{
		
		int randomHost;
		int[] usage = new int[100];
		Arrays.fill(usage, 0);
		
		for (int i=0; i<numberOfVms; i++) {
			
			randomHost = (int)(Math.random() * ((2)));
			
			vm.get(i).setHost(host.get(randomHost));
			hostList.get(randomHost).addMigratingInVm(vm.get(i));
			//Log.printLine("host"+hostList.get(randomHost).getId()+" has vm"+i);
			usage[randomHost]+=vm.get(i).getMips();
		}
		/* Print out the usage of each host. */
		for (int l=0; l<numberOfHosts; l++){
			Log.printLine("PM"+(hostList.get(l).getId()+1)+" has usage: "+ usage[l]);
		}
	}
	
	/**
	 * Returns the resources currently a host uses.
	 * 
	 * @param host
	 * @return
	 */
	public static int getCpuUsage(int hostId){
		int usage = 0;
		Host host = hostList.get(hostId);
		List<Vm> vms = host.getVmsMigratingIn();
		
		for(int i=0; i<vms.size(); i++){
			usage += (int)vms.get(i).getMips();
		}
		return usage;
	}
	
	/**
	 * Get the total resources in MIPS of a PM (host).
	 * @param hostId
	 * @return
	 */
	public static int getTotalPMResources(int hostId){
		return (int)hostList.get(hostId).getMaxAvailableMips();
	}
	
	/**
	 * Migrate a VM to a given host.
	 * 
	 * @param hostId
	 * @param vmId
	 * @throws IOException 
	 */
	public static void migrateVmToHost(Vm vm, int hostId) throws IOException{
		Host host = hostList.get(hostId);
		
		vm.setHost(host);
		host.addMigratingInVm(vm);
		
		getTime(hostId);
	}
	
	/**
	 * Remove the recenntly migrated VM.
	 * @param hostId
	 * @param vmId
	 * @throws IOException
	 */
	public static void removeVmFromHost(int hostId, Vm vm) throws IOException{
		Host host = hostList.get(hostId);
		
		host.removeMigratingInVm(vm);
		
		getTime(hostId);
	}
	
	/**
	 * Get all the hosts available.
	 * @return
	 */
	public static Object[] getHosts(){
		Object[] list = new Object[hostList.size()];
		for (int i=0; i< hostList.size(); i++){
			list[i] = (int)hostList.get(i).getId();
		}
		return list;
	}
	
	
	/**
	 * Return a list with all the VMs of a host.
	 * @param hostId
	 * @return
	 */
	public static List<Vm> getVmsInHost(int hostId){
		List<Vm> list = hostList.get(hostId).getVmsMigratingIn();
		return list;
	}
	
	/**
	 * Create Jadex agents. One for each Physical Machine (host).
	 * 
	 * @param hostList
	 */
	private static void createAgents(List<Host> hostList) {
		
		platform = new JadexPlatform();
		JadexConnector conn = platform.getConn();
		
		// Create the agents according to the hosts.
		for (int i=0; i<hostList.size(); i++){
			conn.createAgent("PM"+(i+1), "src/thesis.jadex.agents/PM"+(i+1)+".agent.xml");
		}
	}
	
	/**
	 * Creates the datacenter. 
	 * In our case only one datacenter is going to be used.
	 * 
	 * @param name
	 * @param num
	 * @return
	 * @throws IOException
	 */
	private static Datacenter createDatacenter(String name, int num) throws IOException{

		/* Create the hosts. */
		createHosts(num);

		/*   Create a DatacenterCharacteristics object that stores the
		*    properties of a data center: architecture, OS, list of
		*    Machines, allocation policy: time- or space-shared, time zone
		*    and its price (G$/Pe time unit).
		*/    
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";			// hypervisor
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		/*  Finally, we need to create a PowerDatacenter object.*/
		Datacenter datacenter = null;
		try {
			vmAllocationPolicySimple = new VmAllocationPolicySimple(hostList);
			datacenter = new Datacenter(name, characteristics, vmAllocationPolicySimple, storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	/**
	 * Give the number of the desired number of PMs within a datacentre 
	 * to be created.
	 * @param num
	 * @throws IOException
	 */
	private static void createHosts(int num) throws IOException {
		
		/* 1. We need to create a list to store our machine. */   
		hostList = new ArrayList<Host>();
		int hostId=0;
		
		for (int i=0; i<num; i++){
			
		/* 2. Create num hosts and a machine in each of them by stating
		 * the amount of different PEs or CPUs/Cores that are being contained.
		 */
		// Create the list of PEs.
		List<Pe> peList = new ArrayList<Pe>();
		
		/* 3. Create PEs of each Vm and put these into a list. */
		peList.add(new Pe(0, new PeProvisionerSimple(hostMips)));
		
		//4. Create Hosts with its id and list of PEs and add them to the list of machines
	
		int ram = 2048; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;

		// 1st machine.
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerTimeShared(peList)
    			)
    		); 

		// 2nd machine
		hostId++;
		}
		Log.printLine("Done with creating hosts...");
		
	}

	/**
	 * Create the Datacenter Broker.
	 * @return
	 */
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 *  Wait for n seconds passed as argument.
	 * @throws IOException 
	 */ 
	public static void waiting(int n) throws IOException {
	        
	      long t0 = System.currentTimeMillis(), t1;
//	      boolean bool =true;
//    	  // For the 5% of the VMs at random we will have scaling up.
//	      int iter = (int) (0.05*numberOfVms);
//	      int[] id = new int[iter];
//	      double[] cpuOld = new double[iter];
	      
	      do {
	          t1 = System.currentTimeMillis();
	          
//	          if (t1-initialTime == 100*1000 && bool){
//	        	  for (int i=0; i<iter; i++){
//	        		  id[i] = (int)Math.random()*(numberOfVms); 
//	        		  getTime(numberOfHosts);
//	        		  cpuOld[i] = vmlist.get(id[i]).getMips();
//
//	        		  // Scale-up the VM.
//	        		  vmlist.get(id[i]).setMips(cpuOld[i]+150);
//
//	        		  Log.printLine("Scale-up vm"+id+" new usage: "+(cpuOld[i]+150)+ " " +
//	        				  "on host: "+vmlist.get(id[i]).getHost().getId());
//	        	  }
//        		  getTime(numberOfVms);
//	        	  //So as to enter the if-statement only once - do not know why.
//	        	  bool = false;
//	          }
//	          
//	          if (t1-initialTime == 150*1000 && !bool){
//	        	  
//	        	  Log.printLine("End of scaling up");
//	        	  for (int i=0; i<iter; i++){
//	        		  vmlist.get(id[i]).setMips(cpuOld[i]);
//	        	  }
//	        	  getTime(numberOfHosts);
//	          }
	      }
	      while ((t1 - t0) < (n * 1000));
	}
	
	/**
	 * Writting to output times so as to hepl us on 
	 * the creation of the figures.
	 *
	 */
	
	public static void getTime(int hostId) throws IOException {
		for (int i=0; i<numberOfHosts; i++){
			Log.printLine("- host "+i+" in time: "+ ((System.currentTimeMillis()-initialTime)/1000)+
					" " + getCpuUsage(i));
		}
	}
	
	public static void getInitialTimeForAll() throws IOException {
		for (int i=0; i<numberOfHosts; i++){
				Log.printLine("- host "+i+" in time: "+ 0.000+
						" " + getCpuUsage(i));
		}
	}

	public static Vm getMaxVm(int hostId) {
		
		List<Vm> list = getVmsInHost(hostId);
		
		double mips = 0;
		Iterator<Vm> iterator = list.iterator();
		Vm vm, desiredVm = null;
		while (iterator.hasNext()) {
			vm = iterator.next();
			if (vm.getMips()>mips) {
				mips = vm.getMips();
				desiredVm = vm;
			}
		}
		return desiredVm;
	}
	
	public static int getVmMips(int vmId){

		double mips = 0;
		Iterator<Vm> iterator = vmlist.iterator();
		Vm vm;
		while (iterator.hasNext()) {
			vm = iterator.next();
			if (vm.getId()==vmId) {
				mips = vm.getMips();
				break;
			}
		}
		return (int) mips;
	}
	
	public static Vm getVm(int vmId){

		Iterator<Vm> iterator = vmlist.iterator();
		Vm vm, desiredVm = null;
		while (iterator.hasNext()) {
			vm = iterator.next();
			if (vm.getId()==vmId) {
				desiredVm = vm;
				break;
			}
		}
		return desiredVm;
	}	
}