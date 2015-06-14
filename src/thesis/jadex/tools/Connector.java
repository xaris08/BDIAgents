package thesis.jadex.tools;

/**
 * Abstract Class that defines method
 * 
 * @author Jorge Solitario
 */
public interface Connector {

	public void launchPlatform();

	public void createAgent(String agent_name, String path);
	
	public Object getAgentID(String agent_name);

	public Object getMessageService();

	public Object getAgentsExternalAccess(String agent_name);

}
