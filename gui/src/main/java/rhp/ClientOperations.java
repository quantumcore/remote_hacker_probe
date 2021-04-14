package rhp;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ClientOperations {
	// TODO : Add more functionality here
	public static void UploadFile(Socket CLIENTSOCKET, File file, String FileName)
	{
		int filesize = (int) file.length();
		if(FileName.length() > 0) {
			byte[] file_buffer = new byte[filesize];
			Arrays.fill(file_buffer, (byte)0);
			int count = 0;
			DataOutputStream out = null;
			try {
				out = new DataOutputStream(CLIENTSOCKET.getOutputStream());
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
			Server.SendData(CLIENTSOCKET, "frecv");
			Server.SendData(CLIENTSOCKET, FileName + ":" + String.valueOf(filesize));
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
			FileManager.Refresh();
		}
	}
}
