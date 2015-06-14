package thesis.jadex.main;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;



public class GUI {

	private JFrame frame = new JFrame("Simulator Controller"); // create Frame
	private JFrame popup;  // popup window
	
	private JPanel pnlWest = new JPanel(); // panel with buttons
	private JPanel pnlEast = new JPanel(); // panel with output
	private JPanel pnlWest2 = new JPanel(); // gridLayout on top of pnlWest
	private JPanel ppPanel = new JPanel(new GridLayout(5, 0)); // Panel for popup
	
	private JPanel pnlStart = new JPanel();
	private JPanel pnlStop = new JPanel();
	//private JPanel pnlResume = new JPanel();
	private JPanel pnlAdd = new JPanel();
	private JPanel pnlRemove = new JPanel();
	private JPanel pnlExit = new JPanel();
	private JPanel pnlLogo = new JPanel();
	
	private JButton btnStart = new JButton("Start");
	private JButton btnStop = new JButton("Stop");
	private JButton btnExit = new JButton("Exit");
	private JButton btnAdd = new JButton("Add VM");
	private JButton btnRemove = new JButton("Remove VM");
	private static JTextArea textArea;
//	private JButton btnResume = new JButton("Resume");
	
	// Features for popup menu.
	private JButton subMitButton = new JButton("OK"); 
	private JLabel vmNumberLabel = new JLabel();
	private JLabel hostIdLabel = new JLabel();
	private JTextField vmNumberField = new JTextField();
	//private JTextField hostIdField = new JTextField();
	private JComboBox<Object> hostIdComboBox;
	
	private Dimension screenSize;
	public static CloudSimulator sim;
	
	public GUI() throws IOException {
				
		sim = new CloudSimulator();
		/* Swing Worker */
		final CounterTask task = new CounterTask();
		
		pnlStart.add(btnStart);
		pnlStop.add(btnStop);
		pnlAdd.add(btnAdd);
		pnlRemove.add(btnRemove);
		pnlExit.add(btnExit);
//		pnlResume.add(btnResume);
//		btnResume.setVisible(false);
		
		pnlWest.setLayout(new GridLayout(5, 1));
		pnlWest.add(pnlStart);
		pnlWest.add(pnlStop);
		pnlWest.add(pnlAdd);
		//pnlWest.add(pnlRemove);
		pnlWest.add(pnlExit);
		//pnlWest.add(pnlResume);
		
		BufferedImage myPicture = ImageIO.read(new File("src/thesis/jadex/logo/jadex_logo.png"));
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		pnlLogo.add(picLabel);
		
		pnlWest2.setLayout(new GridLayout(2, 0));
		pnlWest2.add(pnlWest);
		pnlWest2.add(pnlLogo);
		
		textArea = new JTextArea(30,70);
		textArea.setText("");
		textArea.setEditable(false);
		textArea.setForeground(Color.black);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        
		pnlEast.add(scrollPane);

		/* Get the original Screen Size*/
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		/* Main frame */
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(pnlWest2, BorderLayout.WEST);
		frame.getContentPane().add(pnlEast, BorderLayout.EAST);
		frame.setAlwaysOnTop(true);
		frame.setLocation(screenSize.width/5, screenSize.height/5);
		frame.setResizable(false);
				
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); // Adjusts panel to components for display
		frame.setVisible(true);
		
		
		/* ====  BUTTON LISTENERS  ==== */
		
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				task.execute();		
			}
		});
		
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					task.cancel(true);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}				
			}
		});
		
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPopUpMenu();			
			}
		});
		
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
								
			}
		});
		
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
	}

	public static void addVms(int VmNumber, int hostId) throws IOException {
		sim.addVMs(VmNumber, hostId);
	}
	
	public static void startSimulator() throws Throwable {
		sim.startSimulator();
	}
	
	public static void resumeSimulator() {
//		sim.resumeSimulator();
	}
	
	public static void stopSimulator() throws Throwable {
		sim.exitSimulator();
	}
	
	public static void println(String text) {
        textArea.append(text + "\n");
        textArea.setCaretPosition(textArea.getCaretPosition()+text.length());
    }	


	private void createPopUpMenu(){
		
		popup= new JFrame("Add Virtual Machine");
		
		/* Popup window */
		vmNumberLabel.setText("Insert the number of VMs:");
		vmNumberField.setText("");
		hostIdLabel.setText("Insert the Host Id to be inserted to:");
		//hostIdField.setText("");
		hostIdComboBox = new JComboBox<Object>(CloudSimulator.getHosts());
		hostIdComboBox.setSelectedIndex(0);
		
		ppPanel.add(vmNumberLabel);
		ppPanel.add(vmNumberField);
		ppPanel.add(hostIdLabel);
		//ppPanel.add(hostIdField);
		ppPanel.add(hostIdComboBox);
		ppPanel.add(subMitButton);

		popup.setDefaultCloseOperation(popup.DO_NOTHING_ON_CLOSE);
		popup.setContentPane(ppPanel);
		popup.setResizable(false);
		popup.setAlwaysOnTop(true);
		popup.setAutoRequestFocus(true);
		popup.setLocation(screenSize.width/5, screenSize.height/5);
		popup.pack();
		popup.setVisible(true);

		subMitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!vmNumberField.getText().equals("")){
					int number = Integer.parseInt(vmNumberField.getText());
					//int hostId = Integer.parseInt(hostIdField.getText());	
					int hostId = hostIdComboBox.getSelectedIndex();
					try {
						sim.addVMs(number, hostId);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					popup.dispose();
				} 
				else {
					JOptionPane.showMessageDialog(popup, "Inputs cannot be empty.");
				}
			}
		});
	}
}

class CounterTask extends SwingWorker<Integer, Integer> {

	  @Override
	  protected Integer doInBackground() throws Exception {	  
		  try {
			GUI.startSimulator();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		  return 1;
	  } 
	  
	  protected void process(List<Integer> chunks) {
	  }
	  
	  @Override
	  protected void done() {
		  if (isCancelled()) {
			  try {
				  GUI.stopSimulator();
			  } catch (Throwable e) {
				  e.printStackTrace();
			  }			  
		  }
	  }
}