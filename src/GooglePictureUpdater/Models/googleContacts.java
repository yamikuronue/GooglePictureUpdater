package GooglePictureUpdater.Models;

public interface googleContacts extends needsAuthentication, needsPOSTAuthentication {

	public String[] fetchContactList();
	
}
