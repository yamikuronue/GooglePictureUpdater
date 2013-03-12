package GooglePictureUpdater.Views;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import GooglePictureUpdater.Controllers.LoginController;

/**
 * A view for HTTP authentication. Because the dominant paradigm is to open a browser window
 * and point the user at an authentication URL, which is then redirected to a success URL, 
 * this involves an embedded browser and event handling for redirects.
 * @author Bayley
 *
 */
@SuppressWarnings("serial")
public class LoginView extends JFrame {

	
	public String url;
	final LoginController controller;
	JWebBrowser browser;
	
	/**
	 * Construct and show the login view
	 * @param loginUrl The URL to point the browser at for authentication
	 * @param controller The controller to notify when the URL changes
	 */
	public LoginView(String loginUrl, LoginController controller) {
		url = loginUrl;		
		
		this.controller = controller;
		browser = new JWebBrowser();


		browser.addWebBrowserListener(controller);
		browser.setBarsVisible(false);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				browser.navigate(url);				
			}
		});
		
		this.setMinimumSize(new Dimension(500,500));
		this.add(browser);
		this.setVisible(true);
		
	}
	

}
