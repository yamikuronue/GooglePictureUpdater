package GooglePictureUpdater.Controllers;

import chrriis.dj.nativeswing.NativeSwing;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import GooglePictureUpdater.Models.facebookFetcher;

public class GooglePictureUpdater {
	
	

	public static void main(String[] args) {
		
		//Needed for web browser:
		NativeSwing.initialize();
		NativeInterface.open();

		
		facebookFetcher fetcher = new facebookFetcher();
		LoginController controller = new LoginController();
		controller.setModel(fetcher);
		fetcher.setLoginController(controller);
		
		
		NativeInterface.runEventPump();
	}
}
