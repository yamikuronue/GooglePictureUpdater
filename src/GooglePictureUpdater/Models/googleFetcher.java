package GooglePictureUpdater.Models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import GooglePictureUpdater.Controllers.LoginController;

public class googleFetcher implements needsAuthentication, needsPOSTAuthentication {

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
}
