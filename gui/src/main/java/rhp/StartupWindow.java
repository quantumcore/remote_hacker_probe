package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class StartupWindow extends JDialog {
	
	public static void CreateSettingsFile()
	{
		try {
			FileWriter rewrite = new FileWriter("rhp.ini", false);
		    rewrite.write("host=0.0.0.0"); 
		    rewrite.write("\nport=1234"); 
		    rewrite.write("\ntheme=Light"); 
		    rewrite.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}   
	}

	private final JPanel contentPanel = new JPanel();
	JButton starbtn = new JButton("Star");
	JButton donatebtn = new JButton("Donate");
	
	String firstMessage = "<html><div style='text-align: center;'>"
			+ "Welcome, " + Settings.userSetting("username")
			+ ". Thank you for purchasing The Remote Hacker Probe Pro. The Hacking Software for normal people. Created to be EASY and Stable to use.<br>"
			+ "The Remote Hacker Probe Pro will now Start with Default Settings.<br>"
			+ "Server Host : 0.0.0.0<br>Server Port : 1234<br>Theme : Light."
			+ "</div></html>";
	
	String secondMessage = "<html>Before you start, I (QuantumCore) would like to ask for your support.<br>"
			+ "Help me with my future projects / Leave a Star on the Remote Hacker Probe Pro Repository.<br>"
			+ "<br>OR Consider donating, When you Donate, You save the developer from starvation.<br>"
			+ "</html>";

	/**
	 * Create the dialog.
	 */
	public StartupWindow() {
		setTitle("Remote Hacker Probe Pro | Welcome");
		setBounds(100, 100, 499, 475);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel introlbl = new JLabel();
		introlbl.setFont(new Font("Calibri", Font.PLAIN, 12));
		introlbl.setText(firstMessage);
		
		JLabel heading = new JLabel("The Remote Hacker Probe Pro");
		heading.setFont(new Font("Calibri", Font.BOLD, 14));
		ImageIcon cat = new ImageIcon("img/hacker-cat.gif");
		JLabel icon = new JLabel(cat);
		icon.setToolTipText("Letsss Gett Started!!");
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
					.addGap(138)
					.addComponent(heading, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(159))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(introlbl, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
						.addComponent(icon, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(heading, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(introlbl, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(icon, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			starbtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String url = "https://www.github.com/quantumcored/remote_hacker_probe";
	    	        try {
						java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			starbtn.setVisible(false);
			buttonPane.add(starbtn);
			
			
			donatebtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String url = "https://commerce.coinbase.com/checkout/cebcb394-f73e-4990-98b9-b3fdd852358f";
	    	        try {
						java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			donatebtn.setVisible(false);
			buttonPane.add(donatebtn);
			
			
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Next");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(okButton.getText().equals("Next")) {
							introlbl.setText(secondMessage);
							icon.setIcon(new ImageIcon("img/hamster.gif"));
							okButton.setText("OK");
							starbtn.setVisible(true);
							donatebtn.setVisible(true);
//							CreateSettingsFile();
						} else {
							MainWindow mw = new MainWindow();
							mw.frmRemoteHackerProbe.setVisible(true);
							dispose();
						}
						
					}
				});
				
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				
			}
		}
	}
}