package GooglePictureUpdater.Views;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import GooglePictureUpdater.Controllers.LoginController;

@SuppressWarnings("serial")
public class LoginView extends JFrame {

	
	public String url;
	final LoginController controller;
	JWebBrowser browser;
	
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
	
	 /**
	  * Instantiate the JavaFX Components in
	  * the JavaFX Application Thread.
	  * This approach doesn't give an easy way to detect redirects,
	  * so for now, commenting it out.
	  */
	//WebView browser;
	//WebEngine engine;
	//JFXPanel panel;
	
	/*  private void loadJavaFXScene(){
	    Platform.runLater(new Runnable() {
	      @Override
	      public void run() {
	 
	        BorderPane borderPane = new BorderPane();
	        browser = new WebView();
	        engine = browser.getEngine();
	        
	        engine.load(url);
	 
	        borderPane.setCenter(browser);
	        Scene scene = new Scene(borderPane,450,450);
	        panel.setScene(scene);
	        panel.setVisible(true);
	        System.out.print(engine.getDocument());
	        
	      }
	    });
	  } */

}
