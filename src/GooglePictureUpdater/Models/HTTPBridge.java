package GooglePictureUpdater.Models;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

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
	public static String sendGETRequest(String destination) throws ServerRejectionException, IOException {
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
	public static String sendPOSTRequest(String destination, String message) throws ServerRejectionException, Exception {
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
	public static Image getImage(String destination) throws ServerRejectionException, IOException {
		URL url = new URL(destination);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.connect();

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			InputStream is = connection.getInputStream();
			
			//get content
			//TODO: Why is this only returning half an image?
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
}
