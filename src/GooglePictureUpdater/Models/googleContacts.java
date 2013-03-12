package GooglePictureUpdater.Models;

import java.awt.Image;

public interface googleContacts extends needsAuthentication, needsPOSTAuthentication {

	/**
	 * Fetches the contact list for the authenticated user
	 * @return The user's contacts' names.
	 */
	public String[] fetchContactList();
	
	/**
	 * Fetches a contact's image. Please remember to fetch the contact list first!
	 * @param name The contact's name
	 * @return The contact's image, or a generic image if no contact image was found.
	 */
	public Image getContactImage(String name);
	
}
