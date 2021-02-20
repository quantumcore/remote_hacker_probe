import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class test {

    /** 
     * 
     * Fixed this function :D
     * Thanks to, the test file!
     * 
     * **/
    public static String PortService(String Port)
	{
		String info = "";
		try {
            List<String> lines = Files.readAllLines(Paths.get("C://Users//saadm//Documents//eclipse-workspace//rhp//common_ports.rhp"));
                
            for (String line : lines) {
                if(line.contains(Port))
                {
                	String parse[] = line.split(" ");
                    info = parse[0];
                    break;
                }
                
                else {
                	info = " - ";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return info;
	}

    public static void main(String args[])
    {
        System.out.println(PortService("445"));
    }
}