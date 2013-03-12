package GooglePictureUpdater.Controllers;

import chrriis.dj.nativeswing.NativeSwing;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import GooglePictureUpdater.Models.HTTPBridge;
import GooglePictureUpdater.Models.facebookFetcher;
import GooglePictureUpdater.Models.googleFetcher;
import GooglePictureUpdater.Views.MainView;

public class GooglePictureUpdater {
	
	

	/**
	 * Start the program.
	 * @param args unused
	 */
	public static void main(String[] args) {
		
		//Needed for web browser:
		NativeSwing.initialize();
		NativeInterface.open();


		//Inject dependencies:
		HTTPBridge httpHandler = new HTTPBridge();
		LoginController fbLoginController = new LoginController();
		fbLoginController.setHttpHandler(httpHandler);
		facebookFetcher fbFetcher = new facebookFetcher();
		fbFetcher.setHTTPHandler(httpHandler);
		fbLoginController.setModel(fbFetcher);
		fbFetcher.setLoginController(fbLoginController);
		
		LoginController gLoginController = new LoginController();
		gLoginController.setHttpHandler(httpHandler);
		googleFetcher gFetcher = new googleFetcher();
		gFetcher.setHTTPHandler(httpHandler);
		gLoginController.setModel(gFetcher);
		gFetcher.setLoginController(gLoginController);
		
		
		MainController mController = new MainController();
		mController.setFbFetcher(fbFetcher);
		mController.setgFetcher(gFetcher);
		MainView view = new MainView(mController);
		mController.setView(view);
		
		NativeInterface.runEventPump();
	}
}
