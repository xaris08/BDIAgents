package thesis.jadex.main;

import thesis.jadex.tools.JadexConnector;
import jadex.bridge.IComponentIdentifier;

public class JadexPlatform {

	private JadexConnector conn;

	public JadexPlatform(){

		conn = new JadexConnector();
		conn.launchPlatform();
	}

	public JadexConnector getConn(){
		return this.conn;
	}
	
	public IComponentIdentifier getAgentId(String name) {
		return conn.getAgentID(name);
	}
	
	public void quit() throws Throwable {
		conn.killAllAgents();
	}
}
