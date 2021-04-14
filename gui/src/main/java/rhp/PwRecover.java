package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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

public class PwRecover extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public static int CLIENT_ID;
	public static String host;
	public static RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
	public static void AddText(String text)
	{
		if(text.contains("[PASSCAT]")) {
			textArea.append(text.replace("[PASSCAT]", ""));
		} else {
			textArea.append(text);
		}
		
		if(textArea.getText().contains("[PASSCAT]")) {
			textArea.setText(textArea.getText().replace("[PASSCAT]", ""));
		}
	}


	/**
	 * Create the dialog.
	 */
	public PwRecover() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe Pro | Password Recovery");
		setBounds(100, 100, 654, 430);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		  
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        textArea.setText("");
		    }
		});
		
	      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
	      textArea.setCodeFoldingEnabled(true);
	      RTextScrollPane sp = new RTextScrollPane(textArea);
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setText("Viewing Passwords of " + host );
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
				JButton okButton = new JButton("Save to File");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
			    		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			    		  File file = fileChooser.getSelectedFile();
			    		  FileWriter wt;
							try {
								wt = new FileWriter(file, true);
								wt.write(textArea.getText());
								wt.close();
							} catch (IOException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
			    		}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton btnNewButton = new JButton("OK");
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						textArea.setText("");
						dispose();
					}
				});
				buttonPane.add(btnNewButton);
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
