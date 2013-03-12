package GooglePictureUpdater.Models;

import java.util.ArrayList;
import java.util.HashMap;

import GooglePictureUpdater.Controllers.LoginController;

public interface needsAuthentication {

	/**
	 * Request authentication. This kicks off the authentication process, whether initial or 
	 * re-authenticating. 
	 */
	public void requestAuthentication();
	
	/**
	 * Stores the given authentication credentials.
	 * In a multi-phase authentication procedure, if these are not the
	 * final credentials, the next phase will be initiated.
	 * @param credentials A list of key-value pairs, typically sent from the host.
	 */
	public void authenticate(HashMap<String, String> credentials);
	
	/**
	 * Gets a list of the credentials the model is expecting at this point in time.
	 * This will vary in a multi-phase authentication procedure depending on what step we're on.
	 * These keys should be used to populate the input for {@link authenticate}
	 * @return The list of credential keys.
	 */
	public ArrayList<String> getRequiredCredentials();
	
	/**
	 * Set the login controller used for authentication.
	 * @param controller The login controller used to authenticate.
	 */
	public void setLoginController(LoginController controller);
	
	/**
	 * Gets the URL of the host to send authentication requests to.
	 * @return the URL of the host to send authentication requests to.
	 */
	public String getAuthenticationURL();
	
	/**
	 * Checks if the model is already authenticated
	 * @return True if it is authentication, false if it is not.
	 */
	public boolean isAuthenticated();
}
