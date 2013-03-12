package GooglePictureUpdater.Models;

public interface needsPOSTAuthentication {

	/**
	 * Get the URL of the host to send POST requests for authorization to.
	 * @return the URL of the host to send POST requests for authorization
	 */
	public String getPOSTURL();
	
	/**
	 * Get the authentication parameters to pass to the host
	 * @return The body of the POST request
	 */
	public String getPOSTBody();
}
