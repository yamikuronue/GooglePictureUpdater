package GooglePictureUpdater.Models;

import java.net.URL;

public interface facebookContacts extends needsAuthentication {

	public void fetchContacts();
	
	public String[] findMatches(String name);
	
	public URL getImageURL(String name);
}
