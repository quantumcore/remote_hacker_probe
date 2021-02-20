package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class MsgBox extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField title;
	private JTextField msg;
	public static int CLIENT_ID;


	/**
	 * Create the dialog.
	 */
	public MsgBox() {
		setResizable(false);
		setTitle("Remote Hacker Probe | Message Box");
		setBounds(100, 100, 421, 217);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Message Box Title : ");
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblNewLabel.setBounds(10, 20, 114, 14);
		contentPanel.add(lblNewLabel);
		
		title = new JTextField();
		title.setToolTipText("The Message Box title.");
		title.setFont(new Font("Consolas", Font.PLAIN, 11));
		title.setBounds(134, 11, 255, 28);
		contentPanel.add(title);
		title.setColumns(10);
		
		JLabel lblMessageBoxMessage = new JLabel("Message : ");
		lblMessageBoxMessage.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblMessageBoxMessage.setBounds(10, 86, 58, 14);
		contentPanel.add(lblMessageBoxMessage);
		
		msg = new JTextField();
		msg.setToolTipText("The Message box Message.");
		msg.setHorizontalAlignment(SwingConstants.CENTER);
		msg.setFont(new Font("Consolas", Font.PLAIN, 11));
		msg.setColumns(10);
		msg.setBounds(76, 50, 313, 86);
		contentPanel.add(msg);
		
		String opts[] = {"Information Message", "Warning Message", "Error Message"}; 
		JComboBox mode = new JComboBox(opts);
		mode.setToolTipText("Message box modes. Messagebox to be displayed as Information Message, Warning message or Error message.\r\n");
		mode.setFont(new Font("Calibri", Font.PLAIN, 12));
		mode.setBounds(137, 147, 153, 22);
		contentPanel.add(mode);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String titletxt = title.getText();
				String message = msg.getText();
				String md = String.valueOf(mode.getSelectedIndex());
				
				if(titletxt.length() > 0)
				{
					if(message.length() > 0)
					{
						try {
							Server.SendData(Server.Clients.get(CLIENT_ID), "msgbox:"+md);
							TimeUnit.MILLISECONDS.sleep(500);
							Server.SendData(Server.Clients.get(CLIENT_ID), titletxt);
							TimeUnit.MILLISECONDS.sleep(500);
							Server.SendData(Server.Clients.get(CLIENT_ID), message);
							ServerThread.WaitForReply();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				
			}
		});
		btnNewButton.setFont(new Font("Calibri", Font.PLAIN, 12));
		btnNewButton.setBounds(300, 147, 89, 23);
		contentPanel.add(btnNewButton);
	}
}
