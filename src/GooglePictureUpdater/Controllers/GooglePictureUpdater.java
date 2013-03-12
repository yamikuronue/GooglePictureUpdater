package GooglePictureUpdater.Controllers;

import chrriis.dj.nativeswing.NativeSwing;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import GooglePictureUpdater.Models.facebookFetcher;
import GooglePictureUpdater.Models.googleFetcher;
import GooglePictureUpdater.Views.MainView;

public class GooglePictureUpdater {
	
	

	public static void main(String[] args) {
		
		//Needed for web browser:
		NativeSwing.initialize();
		NativeInterface.open();


		//Inject dependencies:
		LoginController fbLoginController = new LoginController();
		facebookFetcher fbFetcher = new facebookFetcher();
		fbLoginController.setModel(fbFetcher);
		fbFetcher.setLoginController(fbLoginController);
		
		LoginController gLoginController = new LoginController();
		googleFetcher gFetcher = new googleFetcher();
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
