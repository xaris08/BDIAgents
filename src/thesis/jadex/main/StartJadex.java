package thesis.jadex.main;

import thesis.jadex.tools.JadexConnector;
import jadex.bridge.IComponentIdentifier;


public class StartJadex {

	private JadexConnector conn;

	public StartJadex(){

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
