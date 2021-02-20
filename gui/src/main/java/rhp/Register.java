package rhp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Register extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	public void RegisterUserOnDisk(String user)
	{
		try {
			if(user.length() > 0) {
				FileWriter rewrite = new FileWriter("user.ini", false);
			    rewrite.write("username="+user); 
			    rewrite.close();
			    JOptionPane.showMessageDialog(null, 
						"Success!"+
						"\nLet's go, " + user + "!");
			    MainWindow.userlbl.setText("User : " + Settings.userSetting("username"));
			    
			    this.dispose();
			    
			    StartupWindow sw = new StartupWindow();
			    sw.setVisible(true);
			}
		} catch (IOException e) {
		    e.printStackTrace();
		}   
	}
	

	/**
	 * Create the dialog.
	 */
	public Register() {
		setTitle("Remote Hacker Probe | Authentication");
		setResizable(false);
		setBounds(100, 100, 323, 198);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lblNewLabel = new JLabel("The Remote Hacker Probe");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Calibri", Font.BOLD, 13));
		JLabel lblNewLabel_1 = new JLabel("<html>Welcome to the Remote Hacker Probe!<br> To start, Enter a Username.</html>");
		lblNewLabel_1.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		
		textField = new JTextField();
		textField.setFont(new Font("Consolas", Font.PLAIN, 12));
		textField.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Username : ");
		lblNewLabel_2.setFont(new Font("Calibri", Font.BOLD, 12));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField, GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(66)
							.addComponent(lblNewLabel)))
					.addGap(27))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addGap(1)
					.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(5)
							.addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
					.addGap(10))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont(new Font("Calibri", Font.PLAIN, 13));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				
				JButton btnNewButton = new JButton("Lets Start");
				buttonPane.add(btnNewButton);
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String key = textField.getText();
						RegisterUserOnDisk(key);
					}
				});
				btnNewButton.setFont(new Font("Calibri", Font.PLAIN, 12));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
