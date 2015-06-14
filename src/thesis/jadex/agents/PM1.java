package thesis.jadex.agents;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.SServiceProvider;

import java.io.IOException;
import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import thesis.jadex.main.CloudSimulator;
import thesis.jadex.tools.Log;

public class PM1 extends Plan {

	Object send_msg;
	List<Vm> vmlist;
	private int hostId = 0;
	protected IComponentIdentifier[] receivers;
	protected IDFComponentDescription[] result;

	// constructor
	public PM1() {
		send_msg = "migration needed";
	}

	public void body() {
 
		IParameterElement reason = (IParameterElement)getReason();
		
		if (reason instanceof IGoal) {
			IGoal maintain = createGoal("maintain_migration");
			dispatchSubgoal(maintain);

			//int usage = (int)this.getBeliefbase().getBelief("cpu_usage").getFact();

			try {
				/* print out the results of usage and VMs. */
				//Log.printLine(this.getComponentName()+" before migration: "+ usage);
				migrate();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (reason instanceof IMessageEvent){
			Object msg_received = reason.getParameter(SFipa.CONTENT).getValue();
			try {
				reply(msg_received);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Migrate a VM to another host.
	 * @throws IOException 
	 */
	protected void migrate() throws IOException {
		
		/**  =================== DF_SEARCH ====================
		 * Find the agents in the discovery service so as the communication to
		 * be eligible to be accomplished.
		 */
		do {
						
			IDF dfservice = (IDF) SServiceProvider.getServiceUpwards(
					getServiceContainer(), IDF.class).get(this);
			// Create a service description to search for.
			IDFServiceDescription sd = dfservice.createDFServiceDescription(
					null, "communication_service", null);
			IDFComponentDescription ad = dfservice
					.createDFComponentDescription(null, sd);

			// Use a subgoal to search for a translation agent
			IGoal ft = createGoal("df_search");
			ft.getParameter("description").setValue(ad);

			dispatchSubgoalAndWait(ft);
			
			result = (IDFComponentDescription[]) ft.getParameterSet("result")
					.getValues();
			receivers = getReceivers(result);
			
		} while (result.length == 0);

		
		/** =================== REQUEST LESS CPU ====================
		 * Talk with each of the agents available so as to figure out
		 * to which the VM will be migrated.
		 */
		int min = 2000;
		IComponentIdentifier compIdent = this.getComponentIdentifier();

		IGoal tw = null;
		for (int i = 0; i < receivers.length; i++) {
			// After founding the agent, sends the proper message.
			tw = createGoal("rp_initiate");
			tw.getParameter("action").setValue(send_msg);
			// Set the receiver(s).
			tw.getParameter("receiver").setValue(receivers[i]);
			// Send message and wait/store the reply msg.
			dispatchSubgoalAndWait(tw);

			int msg_received = (int) tw.getParameter("result").getValue();
			
			if (msg_received < min) { 
				min = msg_received;
				compIdent = receivers[i];
			}
		}
		Log.printLine("The minimum PM is: " + compIdent.getName()); // +" for host: "+this.getComponentName());

		/** =================== MIGRATION ========================
		 * Proceed with the actual migration after picking the One.
		 */
		
		Vm maxVm = CloudSimulator.getMaxVm(hostId);
//		Vm maxVm = (Vm) this.getBeliefbase().getBelief("maxVm").getFact();
		
		IGoal migrate= createGoal("rp_initiate");
		migrate.getParameter("receiver").setValue(compIdent);
		migrate.getParameter("action").setValue(maxVm.getId());
		dispatchSubgoalAndWait(migrate);
		
		String reply = (String) migrate.getParameter("result").getValue();
		
		if(reply.equals("done")){
			/* Remove the max CPU usage VM. */
			CloudSimulator.removeVmFromHost(hostId, maxVm);
			Log.printLine("=== "+this.getComponentName()+" usage after sending a migration: "+ this.getBeliefbase().getBelief("cpu_usage").getFact());
		}
		else if (reply.startsWith("not")){
			migrate.drop();
		}
 }

	/**
	 * Reply to an agent for a migration willing to take place.
	 * 
	 * @param msg_received
	 * @throws IOException 
	 */
	protected void reply(Object msg_received) throws IOException{
		
		if (msg_received.equals("migration needed")) {

			send_msg = this.getBeliefbase().getBelief("cpu_usage").getFact();
			String reply = "inform";

			IMessageEvent replymsg = getEventbase().createReply((IMessageEvent) getReason(), reply);
			replymsg.getParameter(SFipa.CONTENT).setValue(send_msg);
			sendMessage(replymsg);
		}
		else {
			// Retrieve the VM from the Id sent
			int maxVmId = Integer.parseInt(msg_received.toString());
			Vm maxVm = CloudSimulator.getVm(maxVmId);
			int vmMips = (int) maxVm.getMips();
		
			String reply = "inform";
			
			// If it fits in the destination host migrate and reply positively.
			if(vmMips <= (int) this.getBeliefbase().getBelief("cpu_availability").getFact()) {
				CloudSimulator.migrateVmToHost(maxVm, hostId);
				send_msg = "done";
				Log.printLine("=== "+this.getComponentName()+" usage after getting a migration: "+ this.getBeliefbase().getBelief("cpu_usage").getFact());
			}
			// else reply negatively.
			else {
				send_msg = "not enough space";
			}
			
			IMessageEvent replymsg = getEventbase().createReply((IMessageEvent)getReason(), reply);
			replymsg.getParameter(SFipa.CONTENT).setValue(send_msg);
			sendMessage(replymsg);
//			showBeliefs(this.getBeliefbase().getBeliefSet("vm_list").getFacts());
		}
	}
	
	//not used at the moment.
	public void showBeliefs(Object[] set) throws IOException {
		for (int i = 0; i < set.length; i++) {
			Log.printLine(this.getComponentName()+" has VMs: " + set[i]);
		}
	}

	protected boolean isAccomplished(String bool) {
		if (bool.equals("ok"))
			return true;
		else
			return false;
	}

	// Get all agents subscribed to a service.
	protected IComponentIdentifier[] getReceivers(
			IDFComponentDescription[] input) {

		IComponentIdentifier[] result = new IComponentIdentifier[input.length - 1];
		int j = 0;
		for (int i = 0; i < input.length; i++) {
			IComponentIdentifier x = input[i].getName();
			if (!x.equals(this.getComponentIdentifier())) {
				result[j] = x;
				j++;
			}
		}
		return result;
	}
}