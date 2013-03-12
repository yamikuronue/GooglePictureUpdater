package GooglePictureUpdater.Models;

import java.util.ArrayList;
import java.util.HashMap;

import GooglePictureUpdater.Controllers.LoginController;

public interface needsAuthentication {

	public void requestAuthentication();
	
	public void authenticate(HashMap<String, String> credentials);
	
	public ArrayList<String> getRequiredCredentials();
	
	public void setLoginController(LoginController controller);
	
	public String getAuthenticationURL();
	
	public boolean isAuthenticated();
}
