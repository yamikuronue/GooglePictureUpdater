package GooglePictureUpdater.Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserListener;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowOpeningEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;

import GooglePictureUpdater.Models.HTTPBridge;
import GooglePictureUpdater.Models.needsAuthentication;
import GooglePictureUpdater.Models.needsPOSTAuthentication;
import GooglePictureUpdater.Views.LoginView;

public class LoginController implements WebBrowserListener {

	/* The URL to listen for. This prevents misfiring on, say, "your account was locked" pages. */
	private String expectedURL;
	private needsAuthentication model;
	private LoginView view;
	
	//This is to flag the server to switch to using http POST requests instead of the more usual GET requests
	private boolean usePost = false;

	public void setModel(needsAuthentication model) {
		this.model = model;
	}

	public void setUsePost(boolean usePost) {
		this.usePost = usePost;
	}
	
	public void requestAuthentication() {
		if (usePost) {
			sendPOST();
		} else {
			//Fire up a View and let it do its thing
			//We'll continue processing this when we receive a locationChanged event
			//because the dominant paradigm for web authentication seems to be "user is redirected, client catches redirect event"
			//so continue on to locationChanged from here
			view = new LoginView(model.getAuthenticationURL(), this);
		}
	}
	
	private void sendPOST() {
		if (!(model instanceof needsPOSTAuthentication)) {
			return; //this is an invalid path
		}

		try {
			String response = HTTPBridge.sendPOSTRequest(((needsPOSTAuthentication) model).getPOSTURL(), ((needsPOSTAuthentication) model).getPOSTBody());

			//for some reason, Google uses JSON in this particular reply. 
			//so let's try to parse it as such.

			Pattern jsonPattern = Pattern.compile("\"(\\w+)\" : \"?([\\w\\d\\/\\-\\.]+)\"?");
			Matcher jsonMatcher = jsonPattern.matcher(response.toString());

			HashMap<String,String> credentials = new HashMap<String,String>();
			ArrayList<String> importantparts = model.getRequiredCredentials();

			while (jsonMatcher.find()) {
				String key = jsonMatcher.group(1);
				String value = jsonMatcher.group(2);
				
				if (importantparts.contains(key)) {
					credentials.put(key, value);
				}
			}

			model.authenticate(credentials);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


        
	}

	public void setExpectedURL(String expectedURL) {
		this.expectedURL = expectedURL;
	}

	@Override
	public void locationChanged(WebBrowserNavigationEvent e) {
		String currentURL = e.getNewResourceLocation();
		System.out.println("Received location URL: " + currentURL);
		if (currentURL == null || !(currentURL.startsWith(expectedURL))) {
				//We use "StartsWith" here because the get query will always be changing.
			System.out.println("Discarding irrelevant url");
			return;
		}

		//We have redirect!
		String query = "";
		try {
			query = new URL(currentURL).getQuery();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Also check if there's parameters in the response,
		//as some reply types put them there
		String response = e.getWebBrowser().getHTMLContent();
		
		//matcher for Graph API
		Pattern responsePattern = Pattern.compile("<pre>(([\\w\\d]+=[\\w\\d]+(&amp;)*)+)</pre>");
		Matcher responseMatcher = responsePattern.matcher(response);
		if (responseMatcher.find()) {
			query += "&" + responseMatcher.group(1).replaceAll("&amp;", "&"); //pretend it came in the query.
		}
		
		//matcher for Google API
		responsePattern = Pattern.compile("<title>Success (([\\w]+=[\\w\\d/\\-\\.\\~]+(&amp;)*)+)</title>");
		responseMatcher = responsePattern.matcher(response);
		if (responseMatcher.find()) {
			query += "&" + responseMatcher.group(1).replaceAll("&amp;", "&"); //pretend it came in the query.
		} 

		ArrayList<String> importantparts = model.getRequiredCredentials();
		HashMap<String,String> credentials = new HashMap<String,String>();

		String[] allparts = query.split("&");
		for(String part : allparts) {
			String key = part.split("=")[0];
			String value = part.split("=")[1];
			
			if (importantparts.contains(key)) {
				credentials.put(key, value);
			}
		}

		view.setVisible(false);
		model.authenticate(credentials);
	}



	/* Boilerplate methods to implement listener */

	@Override
	public void commandReceived(WebBrowserCommandEvent arg0) {
		// We don't care about this event
		return;

	}

	@Override
	public void loadingProgressChanged(WebBrowserEvent arg0) {
		// We don't care about this event
		return;
	}

	@Override
	public void locationChangeCanceled(WebBrowserNavigationEvent arg0) {
		// We don't care about this event
		return;

	}



	@Override
	public void locationChanging(WebBrowserNavigationEvent arg0) {
		// We don't care about this event
		return;		
	}

	@Override
	public void statusChanged(WebBrowserEvent arg0) {
		// We don't care about this event
		return;
	}

	@Override
	public void titleChanged(WebBrowserEvent arg0) {
		// We don't care about this event
		return;
	}

	@Override
	public void windowClosing(WebBrowserEvent arg0) {
		// We don't care about this event
		return;
	}

	@Override
	public void windowOpening(WebBrowserWindowOpeningEvent arg0) {
		// We don't care about this event
		return;
	}

	@Override
	public void windowWillOpen(WebBrowserWindowWillOpenEvent arg0) {
		// We don't care about this event
		return;
	}



}
