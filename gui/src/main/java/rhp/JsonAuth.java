package rhp;

import java.io.IOException;
import java.util.*;

import javax.swing.JOptionPane;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.*;


/*
 * 
 * 
 * "https://quantumcored.com/api/auth/generate_auth_cookie/?username=" + USERNAME + " &password=" + PASSWORD + "&seconds=60";
 * 
 * {
    "status":"ok",
    "cookie":"USERNAME|COOKIE",
    "cookie_name":"wordpress_logged_in_",
    "user":{
       "id":USER ID,
       "username":"USERNAME",
       "nicename":"USERNAME",
       "email":"EMAIL",
       "url":"",
       "registered":"date",
       "displayname":"DISPLAY NAME",
       "firstname":"",
       "lastname":"",
       "nickname":"NICK NAME",
       "description":"",
       "capabilities":{
          "user":true
       },
       "avatar":null
    }
 }
 * */

class JsonAuth {

    public static String GetInfo(String URL) throws Exception {
    	
    	String output = "";
    	final CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(URL);

        

        
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            // System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            //System.out.println(headers);

            // return it as a String
			String result = EntityUtils.toString(entity);
			System.out.println(result);
			
			String jsonString = result ; //assign your JSON String here
			JSONObject obj = new JSONObject(jsonString);
			
			// We only need username, email and user id.
			
			
			String status = obj.getString("status");
			// System.out.println(status);
			
			if(status.equals("error"))
			{
				String errormsg = obj.getString("error");
				JOptionPane.showMessageDialog(null, errormsg);
				
			} else if(status.equals("ok")) {
				String username = obj.getJSONObject("user").getString("username");
				String id = obj.getJSONObject("user").getString("id");
				String email = obj.getJSONObject("user").getString("email");
				
				output = username + "," + id +"," + email;
			}
			
			

        }
        
        return output;

    }
    
    
    public boolean isUserValid(String URL) throws ClientProtocolException, IOException, JSONException
    {
    	final CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(URL);
        boolean res = false;
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            // System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            //System.out.println(headers);

            // return it as a String
			String result = EntityUtils.toString(entity);
			System.out.println(result);
			
			String jsonString = result ; //assign your JSON String here
			JSONObject obj = new JSONObject(jsonString);
			
			// We only need username, email and user id.
			
			
			String status = obj.getString("status");
			// System.out.println(status);
			
			if(status.equals("error"))
			{
				
				res =  false;
			} else if(status.equals("ok")) {
				res =  true;
			}

        }
        
        return res;
    }

    /*
    public static void main(String[] args)
    {
        try {
			System.out.println(GetInfo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }*/

}