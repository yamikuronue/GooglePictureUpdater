package GooglePictureUpdater.Controllers;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import GooglePictureUpdater.Models.facebookContacts;
import GooglePictureUpdater.Models.facebookFetcher;
import GooglePictureUpdater.Models.googleContacts;
import GooglePictureUpdater.Models.googleFetcher;
import GooglePictureUpdater.Views.MainView;

public class MainController implements ActionListener, ListSelectionListener, Observer {
	MainView view;
	facebookContacts fbFetcher;
	googleContacts gFetcher;

	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		Object source = arg0.getSource();
		
		if (command.startsWith("Login")) {
			if (command.contains("Google")) {
				//contact google model and log in.
				if (gFetcher == null)  return;
				gFetcher.requestAuthentication();
				
				//This continues when gFetcher notifies us,
				//the observer, that it has authenticated.
				
			} else {
				//contact facebook model and log in
				if (fbFetcher == null)  return;
				fbFetcher.requestAuthentication();
				
				//This continues when fbFetcher notifies us,
				//the observer, that it has authenticated.
			}
		}
		
		if (command.startsWith("Select")) {
			if (command.contains("Google")) {
				//get contact selection
				String contact = view.getGoogleSelection();
				if (contact == null) {
					//this happens when the combobox contents change.
					//just ignore it
					return;
				}
				
				//ask Google model to fetch pic
				Image image = gFetcher.getContactImage(contact);
				
				//update view with pic
				view.updateGoogleImage(image);
				
				//tell Facebook model to find results
				String[] matches = fbFetcher.findMatches(contact);
				
				//pass results back to view
				view.updateFacebookContacts(matches);
			}
			else {
				//ask Facebook to fetch pic
				URL location = fbFetcher.getImageURL(view.getFacebookSelection());
				
				//update View with pic
				view.updateFacebookImage(location);
			}
		}
		
		if (command.startsWith("Update")) {
			//ask Facebook for pic
			URL location = fbFetcher.getImageURL(view.getFacebookSelection());
			
			//Get name
			String contact = view.getGoogleSelection();
			
			//pass to Google
			boolean Success = gFetcher.updateContactImage(contact, location);
			
			//view reports success/failure
			view.reportActionCompletion(Success);
			

			//refresh view
			//XXX: This has timing issues. The below code won't work, so for now, fake it:
			//Image newImage = gFetcher.getContactImage(contact);
			Image newImage = null;
			try {
				newImage = ImageIO.read(fbFetcher.getImageURL(contact));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			view.updateGoogleImage(newImage);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// This is always the facebook selection update
		//ask Facebook to fetch pic
		URL location = fbFetcher.getImageURL(view.getFacebookSelection());
		
		//update View with pic
		view.updateFacebookImage(location);
		
	}

	public void setView(MainView view) {
		this.view = view;
	}

	public void setFbFetcher(facebookFetcher fbFetcher) {
		this.fbFetcher = fbFetcher;
		fbFetcher.addObserver(this);
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
				
				//update view
				view.updateGoogleContacts(contacts);
			}
		} else if (source == fbFetcher) {
			//TODO: change strings to constants
			if (arg1.toString().contains("Authenticated")) {
				//fetch contacts
				fbFetcher.fetchContacts();
				
				//update view
				view.updateFacebookContacts(fbFetcher.findMatches(""));
			}
		}
		
	}
	
}
