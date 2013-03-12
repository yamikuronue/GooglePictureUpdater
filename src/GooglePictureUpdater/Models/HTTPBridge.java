package GooglePictureUpdater.Models;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Utility methods for sending http requests, since that's the backbone of this app.
 * @author Bayley
 *
 */
public class HTTPBridge {

	/**
	 * Send a request using GET.
	 * @param destination The complete URL string, including query parameters
	 * @return The contents of the http reply from the server
	 * @throws ServerRejectionException if the server sends any reply but 200-OK
	 * @throws IOException if there is any issue communicating with the host.
	 */
	public String sendGETRequest(String destination) throws ServerRejectionException, IOException {
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
	        	connection.disconnect();
	        	return response.toString();
	        } else {
	        	//get error
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
	        	StringBuilder response = new StringBuilder();
	        	while (reader.ready()) {
	        		response.append(reader.readLine());
	        	}
	        	
	        	connection.disconnect();
	        	throw new ServerRejectionException(connection.getResponseCode(), connection.getResponseMessage(), response.toString());
      }
	        
		} catch (IOException e) {
			e.printStackTrace();
			//TODO: Log this exception. 
			throw e;
		}
	}
	
	/**
	 * Send a request using POST
	 * @param destination The URL to send the message to
	 * @param message The contents of the POST message
	 * @return The contents of the http reply from the server
	 * @throws ServerRejectionException if the server sends any reply but 200-OK
	 * @throws IOException if there is any issue communicating with the host.
	 */
	public String sendPOSTRequest(String destination, String message) throws ServerRejectionException, Exception {
		try {
			URL url = new URL(destination);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
	
	        connection.setRequestMethod("POST");
	
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
	
	        writer.write(message);
	
	        // Closes this output stream but keeps the connection data alive
	        writer.close();
	        
	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        	//get content
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        	StringBuilder response = new StringBuilder();
	        	while (reader.ready()) {
	        		response.append(reader.readLine());
	        	}
	        	connection.disconnect();
	        	return response.toString();
	        } else {
	        	//get error
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
	        	StringBuilder response = new StringBuilder();
	        	while (reader.ready()) {
	        		response.append(reader.readLine());
	        	}
	        	
	        	connection.disconnect();
	        	throw new ServerRejectionException(connection.getResponseCode(), connection.getResponseMessage(), response.toString());
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
			//TODO: Log this exception. 
			throw e;
		}

	}


	/**
	 * Sends a GET request asking for an image resource, then packages the returned resource prettily.
	 * @param destination The complete URL string, including query parameters
	 * @return The image resource returned.
	 * @throws ServerRejectionException if the server sends any reply but 200-OK
	 * @throws IOException if there is any issue communicating with the host.
	 */
	public Image getImage(String destination) throws ServerRejectionException, IOException {
		URL url = new URL(destination);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.connect();

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = connection.getInputStream();
			
			//get content
			Image image = ImageIO.read(is);
			
			if (image == null) {
				//invalid response from host
				is.reset();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder response = new StringBuilder();
				while (reader.ready()) {
					response.append(reader.readLine());
				}

				connection.disconnect();
				throw new ServerRejectionException(connection.getResponseCode(), connection.getResponseMessage(), response.toString());
			} 
			
			return image;

		} else {
			//get error
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			StringBuilder response = new StringBuilder();
			while (reader.ready()) {
				response.append(reader.readLine());
			}

			connection.disconnect();
			throw new ServerRejectionException(connection.getResponseCode(), connection.getResponseMessage(), response.toString());
		}
	}


	/**
	 * Sends a PUT request uploading an image to a server.
	 * Ideally there'd be an override of this method without the final param, and overrides of the others
	 * with the last param, but YAGNI.
	 * @param destination The URL to send the message to
	 * @param image The image to put. Should be castable to RenderedImage
	 * @param headerValues Key-value pairs for special header values. This is because Google demands them.
	 * @return True if it succeeded, false if there was an error.
	 * @throws IOException If there was an error reading the image or sending the message
	 */
	public boolean putImage(String destination, Image image, HashMap<String,String> headerValues) throws IOException {
		try {
			URL url = new URL(destination);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true);
	
	        connection.setRequestMethod("PUT");
	        
	        if (headerValues != null) {
		        for (String headerKey : headerValues.keySet()) {
		        	connection.setRequestProperty(headerKey, headerValues.get(headerKey));
		        }
	        }
	
	       OutputStream out = connection.getOutputStream();
	       
	       ImageIO.write((RenderedImage) image, "png", out);
	       out.close();

	       if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	    	   connection.disconnect();
	    	   return true;
	       }

	       BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
	       StringBuilder response = new StringBuilder();
	       while (reader.ready()) {
	    	   response.append(reader.readLine());
	       }
	       System.err.println(connection.getResponseCode() + " " + connection.getResponseMessage());
	       System.err.println(response);
	       
	       connection.disconnect();
	       
	       //TODO: log error
	       return false;
	       
		} catch (IOException e) {
			e.printStackTrace();
			//TODO: Log this exception. 
			throw e;
		}
	}
	
}
