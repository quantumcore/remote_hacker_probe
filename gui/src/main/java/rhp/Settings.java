package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;

public class Settings extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField Host;
	private JTextField Port;

	/**
	 * Create the dialog.
	 */
	
	
	
	public static String userSetting(String key) {
		Properties prop = new Properties();
		String fileName = "user.ini";
		InputStream is = null;
		String ret = "";
		try {
		    is = new FileInputStream(fileName);
		    try {
			    prop.load(is);
			    ret = prop.getProperty(key);
			} catch (IOException ex) {
			    ex.printStackTrace();
			}
		} catch (FileNotFoundException ex) {
		    ret = "FILE_NOT_FOUND";
		}
		
		return ret;
	}
	
	public static String returnSetting(String key) {
		Properties prop = new Properties();
		String fileName = "rhp.ini";
		InputStream is = null;
		try {
		    is = new FileInputStream(fileName);
		} catch (FileNotFoundException ex) {
//		    JOptionPane.showMessageDialog(null, "File 'rhp.ini' was not found. Will create default Settings file.");
		    StartupWindow.CreateSettingsFile();
		    try {
				is = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
		    prop.load(is);
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
		
		return prop.getProperty(key);
	}
	
	public static void changeProperty(String filename, String key, String value) throws IOException {
        Properties prop =new Properties();
        prop.load(new FileInputStream(filename));
        prop.setProperty(key, value);
        prop.store(new FileOutputStream(filename),null);
     }
	
	public static void UpdateThemeGlobally(String ThemeName)
	{
		ArrayList<String> themes = new ArrayList<String>();
		themes.add("Dark");
		themes.add("Light");
		themes.add("Solarized Dark");
		themes.add("Solarized Light");
		
		String theme = themes.get(themes.indexOf(ThemeName));
		MainWindow.SaveTheme(theme);
		MainWindow.UpdateTheme();
	}
	
	public Settings() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe | Settings");
		setBounds(100, 100, 361, 203);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel heading = new JLabel("Server Settings");
		heading.setFont(new Font("Calibri", Font.PLAIN, 15));
		
		JLabel servhost = new JLabel("Server Host : ");
		servhost.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		JLabel lblTcpServerPort = new JLabel("Server Port : ");
		lblTcpServerPort.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		Host = new JTextField();
		Host.setEditable(false);
		Host.setFont(new Font("Consolas", Font.PLAIN, 11));
		Host.setColumns(10);
		Host.setText(Settings.returnSetting("host"));
		
		Port = new JTextField();
		Port.setEditable(false);
		Port.setFont(new Font("Consolas", Font.PLAIN, 11));
		Port.setColumns(10);
		Port.setText(Settings.returnSetting("port"));
		JLabel lblNewLabel = new JLabel("Theme : ");
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		String[] items = {"Dark", "Light", "Solarized Dark", "Solarized Light"};
		JComboBox comboBox = new JComboBox(items);
		comboBox.getModel().setSelectedItem(Settings.returnSetting("theme"));
		
		comboBox.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String themeName = items[comboBox.getSelectedIndex()];
				UpdateThemeGlobally(themeName);
				JOptionPane.showMessageDialog(null, "Theme Updated.");
			}
		});
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(heading, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
							.addGap(207))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(servhost, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
							.addGap(1)
							.addComponent(Host, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(lblTcpServerPort, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
									.addGap(1))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(10)
									.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 19, Short.MAX_VALUE)
									.addGap(35)))
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(comboBox, 0, 128, Short.MAX_VALUE)
									.addGap(97))
								.addComponent(Port, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
							.addContainerGap())))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(6)
					.addComponent(heading, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(11)
							.addComponent(servhost, GroupLayout.PREFERRED_SIZE, 14, Short.MAX_VALUE)
							.addGap(1))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(6)
							.addComponent(Host)))
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(13)
							.addComponent(lblTcpServerPort, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(10)
							.addComponent(Port)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(comboBox)
							.addGap(1))
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addGap(12))
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
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			
			JButton btnNewButton = new JButton("Change");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Host.setEditable(true);
					Port.setEditable(true);
				}
			});
			buttonPane.add(btnNewButton);
			
			JButton savebtn = new JButton("Save");
			savebtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(Host.isEditable() && Port.isEditable())
					{
						String newhost = Host.getText();
						String newport = Port.getText();
						try {
							if(newhost != null) {
								changeProperty("rhp.ini", "host", newhost);
								changeProperty("rhp.ini", "port", newport);
							    JOptionPane.showMessageDialog(null, "The Host or Port has been changed. Restart for Changes to take Effect.");
							    Host.setEditable(false);
							    Port.setEditable(false);
							    MainWindow.Log("Host and Port Changed to " + Settings.returnSetting("host")+":"+Settings.returnSetting("port"));
							}
						} catch (IOException e1) {
						    e1.printStackTrace();
						}   
					} else {
						JOptionPane.showMessageDialog(null, "You must change the values first.");
					}
				}
			});
			buttonPane.add(savebtn);
		}
	}
}
