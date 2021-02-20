package test;

import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


class Test{

    public static void changeProperty(String filename, String key, String value) throws IOException {
        Properties prop =new Properties();
        prop.load(new FileInputStream(filename));
        prop.setProperty(key, value);
        prop.store(new FileOutputStream(filename),null);
     }
    
    public static void main(String[] args){
        try{
            changeProperty("rhp.ini", "theme", "light");
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
}