package GooglePictureUpdater.Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPBridge {

	public static String sendGETRequest(String destination) throws Exception {
		try {
			URL url = new URL(destination);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
	        connection.connect();

	        
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        	//get content
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        	StringBuilder response = new StringBuilder();
	        	while (reader.ready()) {
	        		response.append(reader.readLine());
	        	}
	        	return response.toString();
	        } else {
	        	System.out.println("Server returned " + connection.getResponseCode());
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
	        	while (reader.ready()) {
	        		System.out.println(reader.readLine());
	        	}
	        	throw new Exception("Server replied with " + connection.getResponseCode());
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("IO error");
		}
	}
	
	public static String sendPOSTRequest(String destination, String message) throws Exception {
		try {
			URL url = new URL(destination);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
	
	        // instead of a GET, we're going to send using method="POST"
	        connection.setRequestMethod("POST");
	
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
	
	        writer.write(message);
	
	        // Closes this output stream and releases any system resources associated with this stream. At this point, we've sent all the data. Only the outputStream is closed at this point, not the actual connection
	        writer.close();
	        
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        	//get content
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        	StringBuilder response = new StringBuilder();
	        	while (reader.ready()) {
	        		response.append(reader.readLine());
	        	}
	        	return response.toString();
	        } else {
	        	throw new Exception("Server replied with " + connection.getResponseCode());
	        }
	        
		} catch (IOException e) {
			throw new Exception("IO error");
		}

	}
}
