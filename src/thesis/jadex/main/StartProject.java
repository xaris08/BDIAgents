package thesis.jadex.main;

import java.io.IOException;

public class StartProject{
		
	public static void main(String[] args) {
		try {
			new GUI();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}