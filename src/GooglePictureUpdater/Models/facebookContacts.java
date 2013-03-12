package GooglePictureUpdater.Models;

import java.net.URL;

public interface facebookContacts extends needsAuthentication {

	/**
	 * Fetches the contact list into memory.
	 */
	public void fetchContacts();
	
	/**
	 * Searches for the contact whose name is passed in. If the name is an empty string, returns the entire list.
	 * @param name The name to search for
	 * @return A list of potential matches.
	 */
	public String[] findMatches(String name);
	
	/**
	 * Get the URL to the image resource for a given contact
	 * @param name The contact to retrieve an image for
	 * @return The URL, or the URL to a default image if the contact or image is not found.
	 */
	public URL getImageURL(String name);
}
