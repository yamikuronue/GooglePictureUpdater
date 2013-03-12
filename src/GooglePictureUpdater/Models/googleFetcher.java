package GooglePictureUpdater.Models;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import GooglePictureUpdater.Controllers.LoginController;

public class googleFetcher extends Observable implements googleContacts {

	private String authenticationCode;
	private String accessToken;
	private String refreshToken;
	private LoginController controller;
	
	private final String response_type = "code";
	private String client_id;
	private String client_secret;
	private final String redirect_url = "urn:ietf:wg:oauth:2.0:oob";
	private final String scope = "https://www.google.com/m8/feeds";
	private final String state = "f345ddsfe"; //This is passed to google
	
	private final String authenticationURL = "https://accounts.google.com/o/oauth2/auth";
	private final String postURL = "https://accounts.google.com/o/oauth2/token";
	
	private HashMap<String, String> contacts = null;
	private Image defaultImage;
	
	public googleFetcher() {
		File secretFile = new File("C:\\GoogleSecrets.txt"); //You'll want to replace this with your own app key
		FileReader freader = null;
		try {
			freader = new FileReader(secretFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		BufferedReader reader = new BufferedReader(freader);
		try {
			client_id = reader.readLine();
			client_secret = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			freader.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			defaultImage = ImageIO.read(this.getClass().getResource("/img/nopic.jpg"));
		} catch (IOException ex) {
			// XXX: Handle this error better
			ex.printStackTrace();
			defaultImage = null;
		}
	}
	
	@Override
	public boolean isAuthenticated() {
		return accessToken != null;
	}
	
	@Override
	public void requestAuthentication() {
		if (controller == null) {
			//cannot request
			return;
		}
		
		if (authenticationCode == null) {
			controller.setUsePost(false);
			controller.setExpectedURL("https://accounts.google.com/o/oauth2/approval");
		} else {
			controller.setUsePost(true);
		}
		
		//Are we re-authenticating?
		if (accessToken != null) {
			accessToken = null;
			//TODO: use refresh token
			//
		}
		
		controller.requestAuthentication();
	}

	@Override
	public void authenticate(HashMap<String, String> credentials) {
		if (credentials.containsKey("code")) {
			authenticationCode = credentials.get("code");
			controller.setUsePost(true);
			controller.requestAuthentication();		
		} else {
			accessToken = credentials.get("access_token");
			refreshToken = credentials.get("refresh_token");
			setChanged();
			notifyObservers("Authenticated");
		}
	}

	@Override
	public ArrayList<String> getRequiredCredentials() {
		ArrayList<String> credentials = new ArrayList<String>();

		if (authenticationCode == null) {
			credentials.add("code");
		}
		else if (accessToken == null) {
			credentials.add("access_token");
			credentials.add("refresh_token");
		}
		
		return credentials;
	}

	@Override
	public void setLoginController(LoginController controller) {
		this.controller = controller;		
	}

	@Override
	public String getAuthenticationURL() {
		/*Step 1: Determine what phase we're on. */
		StringBuilder urlBuilder = new StringBuilder();

		if (authenticationCode == null) {
			//Request authentication code.
			urlBuilder.append(authenticationURL + "?");
			urlBuilder.append("response_type=" + response_type);
			urlBuilder.append("&client_id=" + client_id);
			urlBuilder.append("&redirect_uri=" + redirect_url);
			urlBuilder.append("&scope=" + scope);
			urlBuilder.append("&state=" + state);
		}
		
		//This method is only called when fetching the code,
		//as the token itself doesn't use the GEt method
		
		return urlBuilder.toString();
	}

	@Override
	public String getPOSTURL() {
		return postURL;
	}

	@Override
	public String getPOSTBody() {
		StringBuilder POSTBuilder = new StringBuilder();
		POSTBuilder.append("code=" + authenticationCode);
		POSTBuilder.append("&client_id=" + client_id);
		POSTBuilder.append("&client_secret=" + client_secret);
		POSTBuilder.append("&redirect_uri=" + redirect_url);
		POSTBuilder.append("&grant_type=authorization_code"); //as per the spec, this is static
		
		return POSTBuilder.toString();
	}

	
	@Override
	public String[] fetchContactList() {
		contacts = new HashMap<String,String>();
		
		//To retrieve all of a user's contacts, send an authorized GET request to the following URL:
		String destination = "https://www.google.com/m8/feeds/contacts/default/full?access_token=" + accessToken;
		//Note: The special userEmail value default can be used to refer to the authenticated user.
		
		try {
			String response = HTTPBridge.sendGETRequest(destination);
			
			//now parse the atom feed
			//there's about eighty ways to do this
			//but good old xpath is simple and effective
			//if horribly inefficient
			
			//For some blasted reason, you can't hand a string to DocumentBuilder.
			//It will, however, take an input stream.
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse((new ByteArrayInputStream(response.getBytes())));
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			//A Feed has many Entrys, each one is a contact
			XPathExpression expr = xpath.compile("/feed/entry");
			
			NodeList contactList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

			int numContacts =  contactList.getLength();
			for (int i = 0; i < numContacts; i++) {
				Node node = contactList.item(i);
				
				NodeList elements = node.getChildNodes();
				int numElements = elements.getLength();
				String id = null;
				String name = "unnamed";
				for (int j = 0; j < numElements; j++) {
					if (elements.item(j).getNodeName().equalsIgnoreCase("id")) {
						id = elements.item(j).getTextContent();
						id = id.substring(id.lastIndexOf("/")+1);
					}
					if (elements.item(j).getNodeName().equalsIgnoreCase("title")) {
						//Sometimes contacts have no name.
						//TODO: decide if I want to just exclude contacts with no name, or put email, or what.
						if ( elements.item(j).getTextContent().isEmpty()) {
							name = "(untitled)";
						} else {
							name = elements.item(j).getTextContent();
						}
						
					}
				}
				
				if (id != null) {
					contacts.put(name, id);
				}
			}
			
			
			//FIXME: Contacts only returns 25 results at a time, so there needs to be a loop.
			//However, this is only a demo app.
			String[] ret = (String[]) contacts.keySet().toArray(new String[] {});
			Arrays.sort(ret);
			
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			
			if (e.getMessage().contains("401")) {
				//401 unauthorized. Try to authenticate:
				//(this will make more sense when the refresh token is used)
				requestAuthentication();
			}
			
			//XXX: figure out a better way to report errors back
			return new String[] {"ERROR: " + e.getMessage()};
		}
	}

	
	@Override
	public Image getContactImage(String name) {
		
		String contactID = contacts.get(name);
		if (contactID == null) {
			return defaultImage;
		}
		
		String destination = "https://www.google.com/m8/feeds/photos/media/default/"+ contactID + "?access_token=" + accessToken;
		Image image;
		try {
			image = HTTPBridge.getImage(destination);
		} catch (ServerRejectionException e) {
			if (e.getServerResponseCode() != 404) {
				//404's are perfectly normal, just return the no-image image
				// TODO: Log error
				e.printStackTrace();
			}

			return defaultImage;
		} catch (IOException e) {
			// TODO: Log error
			e.printStackTrace();
			return defaultImage;
		}
		
		return image;
	}

	@Override
	public boolean updateContactImage(String name, URL imageURL) {
		try {
			//First, we have to fetch the image.
			Image newImage = HTTPBridge.getImage(imageURL.toString());
			
			//Now, we update
			String contactID = contacts.get(name);
			if (contactID == null) {
				System.err.println("null contact");
				//TODO: log error
				return false;
			}
			
			String destination = "https://www.google.com/m8/feeds/photos/media/default/"+ contactID + "?access_token=" + accessToken;
			HashMap<String,String> requiredHeaders = new HashMap<String,String>();
			requiredHeaders.put("If-match", "*");
			requiredHeaders.put("Content-Type", "image/*");
			
			return HTTPBridge.putImage(destination, newImage, requiredHeaders);
			
		} catch (ServerRejectionException e) {
			// TODO Log error
			System.err.println("Server rejection: " + e.getServerResponse());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Log error
			System.err.println("IO error: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	

	

}
