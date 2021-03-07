package rhp;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.ListModel;

public class NetworkScanner extends JDialog {
	private JTextField netrange;
	public int CLIENT_ID;
	JList NetworkScanListOutput;
	static DefaultListModel NsModel = new DefaultListModel();
	JList PList;
	static DefaultListModel PModel = new DefaultListModel();
	static DefaultListModel EModel = new DefaultListModel();
	static DefaultListModel HModel = new DefaultListModel();
	private JTextField PortScannerIP;
	JProgressBar progressBar = new JProgressBar();
	JProgressBar progressBar_1 = new JProgressBar();
	JList eternalscanoutput;
	private JTextField scaninfo;
	private JTextField textField;
	JList hsoutput;
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	Boolean isActionRunning = false;
	
	public static Boolean NetworkScanRunning = false;
	
	/**
	 * Create the dialog.
	 * @return 
	 */
	
	public static String PortService(String Port)
	{
		String info = "";
		try {
            List<String> lines = Files.readAllLines(Paths.get("common_ports.rhp"));
                
            for (String line : lines) {
                if(line.contains(Port))
                {
                	String parse[] = line.split(" ");
                    info = parse[0];
                    break;
                }
                
                else {
                	info = " - ";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return info;
	}
	
//	void DisableInactiveTabs() {
//		// Get Inactive JTabs and disable them.
//		int totalTabs = tabbedPane.getTabCount();
//		int selected = tabbedPane.getSelectedIndex();
//		for(int i = 0; i < totalTabs; i++)
//		{
//			if(selected == i)
//			{
//				// the selected tab
//				; // do nothing
//			} else {
//				tabbedPane.setEnabledAt(i, false); // disable the others
//			}
//		}
//	}
	
	
	
	public static String getRandomSubnet() {
		String array[] = {"192.168.0.1/24", "192.168.0.1/120", "192.168.1.1/26", "192.168.1.100/109"};
	    int rnd = new Random().nextInt(array.length);
	    return array[rnd];
	}
	
	// credit : https://stackoverflow.com/a/18591205
	public final boolean containsDigit(String s) {
	    boolean containsDigit = false;

	    if (s != null && !s.isEmpty()) {
	        for (char c : s.toCharArray()) {
	            if (containsDigit = Character.isDigit(c)) {
	                break;
	            }
	        }
	    }

	    return containsDigit;
	}
	
	void ScanPort(String TargetIP)
	{
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		MainWindow.HaltAllSystems();
		isActionRunning = true;
		NetworkScanRunning = true;
		progressBar_1.setIndeterminate(true);
		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get("os_ports.rhp"));
			for (String line : lines) {
            	//String port = line.split(" ")[1];
            	Server.SendData(Server.Clients.get(CLIENT_ID), "checkport");
            	Server.SendData(Server.Clients.get(CLIENT_ID), TargetIP + "," + line);
            	ServerThread.WaitForReplyMeta();
            }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PModel.addElement("Scan Finished.");
		progressBar_1.setIndeterminate(false);
		isActionRunning = false;
		MainWindow.EnableAllSystems();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		NetworkScanRunning = false;
	}
	
	void EScanIp(ArrayList<String> localArray)
	{
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		MainWindow.HaltAllSystems();
		isActionRunning = true;
		NetworkScanRunning = true;
		for (int counter = 0; counter < localArray.size(); counter++) { 		      
	         // System.out.println(localArray.get(counter));
			
			ServerThread.LOG.clear();
			Server.SendData(Server.Clients.get(CLIENT_ID), "eternal_scan");
			try {
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Server.SendData(Server.Clients.get(CLIENT_ID), localArray.get(counter));
			ServerThread.WaitForReplyMeta();
	    }

		EModel.addElement("MS17-010 Scan finished.");
		isActionRunning = false;
		NetworkScanRunning = false;
		MainWindow.EnableAllSystems();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	void ScanIp(ArrayList<String> localArray)
	{
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		MainWindow.HaltAllSystems();
		isActionRunning = true;
		NetworkScanRunning = true;
		progressBar.setIndeterminate(true);
		for (int counter = 0; counter < localArray.size(); counter++) { 		      
	         // System.out.println(localArray.get(counter));
			
			ServerThread.LOG.clear();
			Server.SendData(Server.Clients.get(CLIENT_ID), "checkhost");
			try {
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Server.SendData(Server.Clients.get(CLIENT_ID), localArray.get(counter));
			
			ServerThread.WaitForReplyMeta();
			// System.out.println("Moving over : " + localArray.get(counter));
	    }
		progressBar.setIndeterminate(false);
		NsModel.addElement("Scan finished.");
		isActionRunning = false;
		NetworkScanRunning = false;
		MainWindow.EnableAllSystems();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	public NetworkScanner() {
		MainWindow.HaltAllSystems();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setResizable(false);
		setTitle("Remote Hacker Probe | Network Scanner");
		setBounds(100, 100, 451, 476);
		getContentPane().setLayout(null);
		
		
		tabbedPane.setBounds(0, 0, 447, 437);
		getContentPane().add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Network Scanner", null, panel, null);
		panel.setLayout(null);
		
		netrange = new JTextField();
		netrange.setToolTipText("The network range to scan.\r\nFor example : 192.168.0.<START>/<END>\r\n- 192.168.0.102/108 will scan from 192.168.0.102 to 192.168.0.108.");
		netrange.setFont(new Font("Consolas", Font.PLAIN, 14));
		netrange.setBounds(55, 11, 229, 20);
		panel.add(netrange);
		netrange.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Range : ");
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 13));
		lblNewLabel.setBounds(10, 16, 46, 14);
		panel.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Scan");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(!isActionRunning)
					{
						
						ArrayList<String> localArray = new ArrayList<String>();

						String range = netrange.getText().strip();
						String[] scanrange = range.split("/");
						String[] get = scanrange[0].strip().split("\\.");
						if(range.length() > 0) {
							
							if(containsDigit(range)) {
								String xip = get[3]; 
								localArray.clear();
								String base = get[0] + "." + get[1] + "." + get[2] + ".";
								IntStream.range(Integer.valueOf(xip), Integer.valueOf(scanrange[1]) + 1).forEach(
								        n -> {
								            String IPAddr = base + String.valueOf(n);
								            localArray.add(IPAddr); // Make the Array of ip addresses to scan
								        }
								    );
								NsModel.addElement("Starting Network Scan : " + range);
								new Thread(new Runnable() {

									public void run() {
									     ScanIp(localArray);
									    }
									}).start();
								
							} else {
								JOptionPane.showMessageDialog(null, "Error : Invalid subnet range. Enter something like 192.168.0.<START>/<END>");
							}
							
						} else {
							JOptionPane.showMessageDialog(null, "Error : Invalid subnet range. Enter something like 192.168.0.<START>/<END>");
						}
						
					} else {
						JOptionPane.showMessageDialog(null, "A Scan is already in progress. Please wait while it is finished.");
					}
					
					
				} catch (Exception esp) {
					esp.printStackTrace();
				}
				
			}
		});
		btnNewButton.setToolTipText("Start Scanning the network");
		btnNewButton.setFont(new Font("Calibri", Font.PLAIN, 12));
		btnNewButton.setBounds(294, 10, 69, 22);
		panel.add(btnNewButton);
		
		JButton btnRandom = new JButton("(?)");
		btnRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String randomsubnet = getRandomSubnet();
				netrange.setText(randomsubnet);
			}
		});
		btnRandom.setToolTipText("Use Random Subnet Range");
		btnRandom.setFont(new Font("Calibri", Font.PLAIN, 12));
		btnRandom.setBounds(373, 10, 59, 22);
		panel.add(btnRandom);
		
		NetworkScanListOutput = new JList(NsModel);
		NetworkScanListOutput.setToolTipText("Network Scanner output appears here. Press right click on a discovered host for more options.");
		JScrollPane nsPane = new JScrollPane(NetworkScanListOutput);
		NetworkScanListOutput.setFont(new Font("Consolas", Font.PLAIN, 12));
		nsPane.setBounds(10, 41, 422, 346);
		panel.add(nsPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Port Scanner", null, panel_1, null);
		panel_1.setLayout(null);
		
		PortScannerIP = new JTextField();
		PortScannerIP.setToolTipText("The IP Address of the Host to do a Port scan on.\r\n");
		PortScannerIP.setFont(new Font("Consolas", Font.PLAIN, 14));
		PortScannerIP.setBounds(76, 9, 233, 23);
		panel_1.add(PortScannerIP);
		PortScannerIP.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("IP Address : ");
		lblNewLabel_1.setFont(new Font("Calibri", Font.PLAIN, 13));
		lblNewLabel_1.setBounds(10, 14, 67, 14);
		panel_1.add(lblNewLabel_1);
		
		PList = new JList(PModel);
		PList.setFont(new Font("Consolas", Font.PLAIN, 12));
		JScrollPane Ppane = new JScrollPane(PList);
		NetworkScanListOutput.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem sp = new JMenuItem("Send to Port Scanner");
		sp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(NetworkScanListOutput.isSelectionEmpty())
				{
					JOptionPane.showMessageDialog(null,"No Host selected.");
				} else {
					String host = (String) NetworkScanListOutput.getSelectedValue();
					if(host.startsWith("Error"))
					{
						JOptionPane.showMessageDialog(null,"The Selected Host does not exist in the target subnet.");
					} else if (host.startsWith("Discovered"))
					{
						String parse[] = host.strip().split(":");
						String ip = parse[1];
						
						PortScannerIP.setText(ip.split("-")[0].replaceAll("\\s","")); // remove hostname, mac and spaces
						tabbedPane.setSelectedIndex(1); 
					}
				}
			}
		});
		sp.setFont(new Font("Calibri", Font.PLAIN, 13));
		popupMenu.add(sp);
		
		progressBar.setBounds(10, 395, 422, 14);
		panel.add(progressBar);
		Ppane.setBounds(10, 41, 422, 346);
		panel_1.add(Ppane);
		
		JButton btnNewButton_1 = new JButton("Scan");
		btnNewButton_1.setToolTipText("Scan for common open ports");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isActionRunning)
				{
					String TargetIP = PortScannerIP.getText();
					PModel.addElement("Scanning for Open Ports on : " + TargetIP);
					new Thread(new Runnable() {

						public void run() {
						     ScanPort(TargetIP);
						    }
						}).start();
				} else {
					JOptionPane.showMessageDialog(null, "A Scan is already in progress. Please wait while it is finished.");
				}
				 
			}
		});
		btnNewButton_1.setFont(new Font("Calibri", Font.PLAIN, 12));
		btnNewButton_1.setBounds(311, 9, 89, 23);
		panel_1.add(btnNewButton_1);
		
		
		progressBar_1.setBounds(10, 395, 422, 14);
		panel_1.add(progressBar_1);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("MS17-10 Eternal Blue Scanner", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel modelbl = new JLabel("IP Address : ");
		modelbl.setFont(new Font("Calibri", Font.PLAIN, 12));
		modelbl.setBounds(10, 15, 66, 14);
		panel_2.add(modelbl);
		
        String opts[] = { "Direct Scan", "Range Scan"}; 
		JComboBox comboBox = new JComboBox(opts);
		comboBox.setToolTipText("Scan Options.\r\n Direct scan - Direct scan only 1 IP address.  \r\n Range Scan - Scan a range of ip addresses.");
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int mode = comboBox.getSelectedIndex();
				if(mode == 0)
				{
					modelbl.setText("IP Address : ");
				} else {
					modelbl.setText("   Range : ");
				}
			}
		});
		comboBox.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBox.setBounds(321, 11, 99, 22);
		panel_2.add(comboBox);
		
		
		
		scaninfo = new JTextField();
		scaninfo.setToolTipText("The IP Address of the Host to do a Eternal Blue scan on.");
		scaninfo.setFont(new Font("Consolas", Font.PLAIN, 12));
		scaninfo.setBounds(78, 11, 168, 22);
		panel_2.add(scaninfo);
		scaninfo.setColumns(10);
		
		JButton btnNewButton_2 = new JButton("Scan");
		btnNewButton_2.setToolTipText("Scan for MS17-010 Vulnerability.");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isActionRunning)
				{
					int scanMode = comboBox.getSelectedIndex();
					if(scaninfo.getText().length() > 0)
					{
						if(scanMode == 0)
						{
							// Direct one IP scan.
							String ip_addr = scaninfo.getText();
							if(containsDigit(ip_addr))
							{
								EModel.addElement("Checking " + ip_addr);
								Server.SendData(Server.Clients.get(CLIENT_ID), "eternal_scan");
								Server.SendData(Server.Clients.get(CLIENT_ID), ip_addr.strip());
								ServerThread.WaitForReply();
							}
							
						} else {
							ArrayList<String> localArray = new ArrayList<String>();

							String range = scaninfo.getText().strip();
							String[] scanrange = range.split("/");
							String[] get = scanrange[0].strip().split("\\.");
							if(range.length() > 0) {
								
								if(containsDigit(range)) {
									String xip = get[3]; 
									localArray.clear();
									String base = get[0] + "." + get[1] + "." + get[2] + ".";
									IntStream.range(Integer.valueOf(xip), Integer.valueOf(scanrange[1]) + 1).forEach(
									        n -> {
									            String IPAddr = base + String.valueOf(n);
									            localArray.add(IPAddr); // Make the Array of ip addresses to scan
									        }
									    );
									EModel.addElement("Starting Eternal Blue Network Scan : " + range);
									new Thread(new Runnable() {

										public void run() {
										     EScanIp(localArray);
										    }
										}).start();
								}
							}
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "A Scan is already in progress. Please wait while it is finished.");
				}
				
				
			}
		});
		btnNewButton_2.setFont(new Font("Calibri", Font.PLAIN, 12));
		btnNewButton_2.setBounds(250, 11, 61, 22);
		panel_2.add(btnNewButton_2);
		
		eternalscanoutput = new JList(EModel);
		JScrollPane EPane = new JScrollPane(eternalscanoutput);
		eternalscanoutput.setFont(new Font("Consolas", Font.PLAIN, 12));
		EPane.setBounds(10, 41, 422, 357);
		panel_2.add(EPane);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Host Sweep", null, panel_3, null);
		panel_3.setLayout(null);
		
		textField = new JTextField();
		textField.setToolTipText("The IP address of the host to get Hostname of.");
		textField.setFont(new Font("Consolas", Font.PLAIN, 12));
		textField.setBounds(77, 11, 182, 28);
		panel_3.add(textField);
		textField.setColumns(10);
		
		JLabel iplbl = new JLabel("IP Address : ");
		iplbl.setFont(new Font("Calibri", Font.PLAIN, 12));
		iplbl.setBounds(10, 19, 66, 17);
		panel_3.add(iplbl);
		
		JButton btnNewButton_3 = new JButton("Get Hostname");
		btnNewButton_3.setToolTipText("Get Hostname");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isActionRunning)
				{
					String ip = textField.getText();
					Server.SendData(Server.Clients.get(CLIENT_ID), "gethostname");
					Server.SendData(Server.Clients.get(CLIENT_ID), ip);
					HModel.addElement("Checking "+ip);
					ServerThread.WaitForReply();
				} else {
					JOptionPane.showMessageDialog(null, "A Scan is already in progress. Please wait while it is finished.");
				}
				
			}
		});
		btnNewButton_3.setFont(new Font("Calibri", Font.PLAIN, 12));
		btnNewButton_3.setBounds(264, 14, 116, 23);
		panel_3.add(btnNewButton_3);
		
		JMenuItem save_1 = new JMenuItem("Save Output");
		save_1.setFont(new Font("Calibri", Font.PLAIN, 13));
		
		save_1.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    		  File file = fileChooser.getSelectedFile();
	    		  FileWriter wt;
					try {
						wt = new FileWriter(file, true);
						for(int i = 0; i< NetworkScanListOutput.getModel().getSize();i++){
				            wt.write(String.valueOf(NetworkScanListOutput.getModel().getElementAt(i)) + "\n");
				        }
						wt.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
	    		}
	    	}
	    });
		
		JMenuItem save_2 = new JMenuItem("Save Output");
		save_2.setFont(new Font("Calibri", Font.PLAIN, 13));
		
		save_2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    		  File file = fileChooser.getSelectedFile();
	    		  FileWriter wt;
					try {
						wt = new FileWriter(file, true);
						for(int i = 0; i< PList.getModel().getSize();i++){
				            wt.write(String.valueOf(PList.getModel().getElementAt(i))+ "\n");
				        }
						wt.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
	    		}
	    	}
	    });
		
		JMenuItem save_3 = new JMenuItem("Save Output");
		save_3.setFont(new Font("Calibri", Font.PLAIN, 13));
		
		save_3.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    		  File file = fileChooser.getSelectedFile();
	    		  FileWriter wt;
					try {
						wt = new FileWriter(file, true);
						for(int i = 0; i< eternalscanoutput.getModel().getSize();i++){
				            wt.write(String.valueOf(eternalscanoutput.getModel().getElementAt(i))+ "\n");
				        }
						wt.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
	    		}
	    	}
	    });
		
		JMenuItem save_4 = new JMenuItem("Save Output");
		save_4.setFont(new Font("Calibri", Font.PLAIN, 13));
		
		save_4.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    		  File file = fileChooser.getSelectedFile();
	    		  FileWriter wt;
					try {
						wt = new FileWriter(file, true);
						for(int i = 0; i< hsoutput.getModel().getSize();i++){
				            wt.write(String.valueOf(hsoutput.getModel().getElementAt(i))+ "\n");
				        }
						wt.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
	    		}
	    	}
	    });
		
		popupMenu.add(save_1);
		
		JPopupMenu jp2 = new JPopupMenu();
		jp2.add(save_2);
		
		JPopupMenu jp3 = new JPopupMenu();
		jp3.add(save_3);
		
		JPopupMenu jp4 = new JPopupMenu();
		jp4.add(save_4);

		
		hsoutput = new JList(HModel);
		JScrollPane hpane = new JScrollPane(hsoutput);
		EPane.setBounds(10, 41, 422, 357);
		hsoutput.setFont(new Font("Consolas", Font.PLAIN, 12));
		hpane.setBounds(10, 43, 430, 355);
		panel_3.add(hpane);
		
		MainWindow.addPopup(NetworkScanListOutput, popupMenu);
		MainWindow.addPopup(PList, jp2);
		MainWindow.addPopup(eternalscanoutput, jp3);
		MainWindow.addPopup(hsoutput, jp4);
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent we) {
				MainWindow.EnableAllSystems();
			}
		});

	}
}
