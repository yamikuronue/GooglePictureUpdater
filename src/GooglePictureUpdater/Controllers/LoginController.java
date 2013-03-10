package GooglePictureUpdater.Controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserListener;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowOpeningEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;

import GooglePictureUpdater.Models.needsAuthentication;
import GooglePictureUpdater.Views.LoginView;

public class LoginController implements WebBrowserListener {

	/* The URL to listen for. This prevents misfiring on, say, "your account was locked" pages. */
	private String expectedURL;
	private needsAuthentication model;
	private LoginView view;

	public void setModel(needsAuthentication model) {
		this.model = model;
	}

	public void requestAuthentication() {
		//Fire up a View and let it do its thing.
		view = new LoginView(model.getAuthenticationURL(), this);
	}

	public void setExpectedURL(String expectedURL) {
		this.expectedURL = expectedURL;
	}

	@Override
	public void locationChanged(WebBrowserNavigationEvent e) {
		String currentURL = e.getNewResourceLocation();
		if (!currentURL.equalsIgnoreCase(expectedURL)) {
			return;
		}

		//We have redirect!
		String query;
		try {
			query = new URL(currentURL).getQuery();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		if (query == null) {
			return; //nothing to process.
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
