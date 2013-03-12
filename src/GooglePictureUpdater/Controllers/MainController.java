package GooglePictureUpdater.Controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import GooglePictureUpdater.Models.facebookFetcher;
import GooglePictureUpdater.Models.googleContacts;
import GooglePictureUpdater.Models.googleFetcher;
import GooglePictureUpdater.Views.MainView;

public class MainController implements ActionListener, ListSelectionListener, Observer {
	MainView view;
	facebookFetcher fbFetcher;
	googleContacts gFetcher;


	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		
		if (command.startsWith("Login")) {
			if (command.contains("Google")) {
				//contact google model and log in.
				if (gFetcher == null)  return;
				gFetcher.requestAuthentication();
				
				//This continues when gFetcher notifies us,
				//the observer, that it has changed.
				
			} else {
				//contact facebook model and log in
				if (fbFetcher == null)  return;
				fbFetcher.requestAuthentication();
				
				//then update view
				view.updateFacebookContacts(new String[] { "Logged in!"});
			}
		}
		
		if (command.startsWith("Select")) {
			if (command.contains("Google")) {
				//as Google model to fetch pic
				
				//update view with pic
				
				
				//tell Facebook model to find results
				
				//pass results back to view
			}
			else {
				//ask Facebook to fetch pic
				
				//update View with pic
			}
		}
		
		if (command.startsWith("Update")) {
			//ask Facebook for pic
			
			//pass to Google
			
			//view reports success/failure
			
			//refresh view
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// This is always the facebook selection update
		
		//ask Facebook to fetch pic
		
		//update View with pic
		
	}

	public void setView(MainView view) {
		this.view = view;
	}

	public void setFbFetcher(facebookFetcher fbFetcher) {
		this.fbFetcher = fbFetcher;
	}

	public void setgFetcher(googleFetcher gFetcher) {
		this.gFetcher = gFetcher;
		gFetcher.addObserver(this);
	}

	@Override
	public void update(Observable source, Object arg1) {
		if (source == gFetcher) {
			//TODO: change strings to constants
			if (arg1.toString().contains("Authenticated")) {
				//fetch contacts
				String [] contacts = gFetcher.fetchContactList();
				view.updateGoogleContacts(contacts);
			}
		}
		
	}
	
}
