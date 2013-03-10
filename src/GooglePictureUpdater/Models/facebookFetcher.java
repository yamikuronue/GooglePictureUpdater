package GooglePictureUpdater.Models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import GooglePictureUpdater.Controllers.LoginController;

public class facebookFetcher implements needsAuthentication {

	private String authenticationCode;
	private String accessToken;
	private LoginController controller;
	private String appId = ""; //Facebook API credentials moved to a text file
	private String appSecret = ""; //for security reasons.
	private final String state = "f345ddsfe"; //This is passed to facebook and defined as an "arbitrary but unique string"
											//TODO: Make it more, um, unique.
	private final String redirectURL = "https://www.facebook.com/connect/login_success.html"; //As documented at  https://developers.facebook.com/docs/howtos/login/login-for-desktop/
	private final String accessTokenURL = "https://graph.facebook.com/oauth/access_token";
	
	public facebookFetcher() {
		File secretFile = new File("C:\\FacebookSecrets.txt"); //You'll want to replace this with your own app keys
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
			appId = reader.readLine();
			appSecret = reader.readLine();
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
	public void authenticate(HashMap<String, String> credentials) {
		if (credentials.containsKey("code")) {
			authenticationCode = credentials.get("code");
			//Now ask for the token
			controller.requestAuthentication();
		}
		
		if (credentials.containsKey("access_token")) {
			accessToken = credentials.get("access_token");
		}
		
	}

	@Override
	public ArrayList<String> getRequiredCredentials() {
		
		ArrayList<String> requiredCredentials = new ArrayList<String>();
		
		/*Step 1: Determine what phase we're on. */
		
		if (authenticationCode == null) {
			//Request authentication code.
			requiredCredentials.add("code");
		}
		
		else if (accessToken == null) {
			//Request access token
			requiredCredentials.add("access_token");
		}
		
		//Intentional missing else:
		//If we have a token and a code
		//we don't need anything
		//so leave the arraylist empty
		
		return requiredCredentials;
	}

	@Override
	public void setLoginController(LoginController controller) {
		this.controller = controller;
		controller.setExpectedURL(redirectURL);		
	}

	@Override
	public void requestAuthentication() {
		if (controller == null) {
			//cannot request
			return;
		}
		
		if (authenticationCode == null) {
			controller.setExpectedURL(redirectURL);
		} else {
			controller.setExpectedURL(accessTokenURL);
		}
		
		//Are we re-authenticating?
		if (accessToken != null) {
			accessToken = null;
		}
		
		controller.requestAuthentication();
	}
	
	@Override
	public String getAuthenticationURL() {
		/*Step 1: Determine what phase we're on. */
		StringBuilder urlBuilder = new StringBuilder();

		if (authenticationCode == null) {
			//Request authentication code.
			urlBuilder.append("https://www.facebook.com/dialog/oauth?");
			urlBuilder.append("client_id=" + appId);
			urlBuilder.append("&redirect_uri=" + redirectURL);
			urlBuilder.append("&state=" + state);
		}
		
		else if (accessToken == null) {
			//Request access token'
			controller.setExpectedURL(accessTokenURL);
			urlBuilder.append(accessTokenURL + "?");
			urlBuilder.append("client_id=" + appId);
			urlBuilder.append("&redirect_uri=" + redirectURL);
			urlBuilder.append("&client_secret=" + appSecret);
			urlBuilder.append("&code=" + authenticationCode);
		}
		
		return urlBuilder.toString();
	}



}
