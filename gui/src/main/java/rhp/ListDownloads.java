package rhp;

import java.io.File;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ListDownloads {
	
	static DefaultTableModel model = (DefaultTableModel) MainWindow.dlTable.getModel();
	
	public static void DownloadsList()
	{
		model.setRowCount(0);
		File folder = new File("downloads/");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile()) {
			    //System.out.println("File " + listOfFiles[i].getName());
				  if(!listOfFiles[i].toString().contains("DO_NOT_DELETE")) {
					  Object[] obj = new Object[] { listOfFiles[i].getName(), listOfFiles[i].getAbsoluteFile() };
					  MainWindow.dlModel.addRow(obj);
					
				  }
			}
		}
	}
}
