package GooglePictureUpdater.Views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import GooglePictureUpdater.Controllers.MainController;

public class MainView extends JFrame {

	JButton google_login;
	JButton facebook_login;
	
	JComboBox google_contacts;
	JList facebook_results;
	
	JLabel google_imglabel;
	JLabel facebook_imglabel;
	ImageIcon google_image;
	ImageIcon facebook_image;
	
	JButton update;
	
	public MainView(MainController controller) {
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints c;
		//Buttons first
		
		ImageIcon googleLogin = new ImageIcon();
		
		try {
			Image img = ImageIO.read(getClass().getResource("/img/google.JPG")).getScaledInstance(175, 25, java.awt.Image.SCALE_SMOOTH);
			googleLogin.setImage(img);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		google_login = new JButton(googleLogin);
		google_login.setActionCommand("Login Google");
		google_login.setBorder(BorderFactory.createEmptyBorder());
		google_login.setContentAreaFilled(false);
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridx = 1;
		c.gridy = 1;
		this.add(google_login, c);
		
		ImageIcon facebookLogin = new ImageIcon();
		try {
			Image img = ImageIO.read(getClass().getResource("/img/facebook.jpg")).getScaledInstance(175, 25, java.awt.Image.SCALE_SMOOTH);
			facebookLogin.setImage(img);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		facebook_login = new JButton(facebookLogin);
		facebook_login.setActionCommand("Login Facebook");
		facebook_login.setBorder(BorderFactory.createEmptyBorder());
		facebook_login.setContentAreaFilled(false);
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridx = 3;
		c.gridy = 1;
		this.add(facebook_login, c);
		
		google_contacts = new JComboBox();
		google_contacts.addItem("Contact List:");
		google_contacts.addItem("Please log in to fetch contacts.");
		google_contacts.setActionCommand("Select Google");
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 1;
		c.gridy = 2;
		this.add(google_contacts, c);
		
		facebook_results = new JList(new String[] {"Please log in ", "to view Facebook contacts", "", "", ""});
		facebook_results.setVisibleRowCount(5);
		facebook_results.setFixedCellHeight(15);
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 10;
		c.gridx = 3;
		c.gridy = 2;
		JScrollPane facebookScroller = new JScrollPane(facebook_results,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		facebookScroller.setBorder(BorderFactory.createLineBorder(Color.black));
		facebookScroller.setVisible(true);
		this.add(facebookScroller, c);
		
	    
		google_image = new ImageIcon();
		try {
			Image img = ImageIO.read(getClass().getResource("/img/nopic.jpg"));
			google_image.setImage(img);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		google_imglabel = new JLabel();
		google_imglabel.setIcon(google_image);
		google_imglabel.setBorder(BorderFactory.createLineBorder(Color.black));
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridx = 1;
		c.gridy = 3;
		this.add(google_imglabel, c);
		
		facebook_image = new ImageIcon();
		try {
			Image img = ImageIO.read(getClass().getResource("/img/nopic.jpg"));
			facebook_image.setImage(img);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		facebook_imglabel = new JLabel();
		facebook_imglabel.setIcon(facebook_image);
		facebook_imglabel.setMinimumSize(new Dimension(128,128));
		facebook_imglabel.setBorder(BorderFactory.createLineBorder(Color.black));
		c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridx = 3;
		c.gridy = 3;
		this.add(facebook_imglabel, c);
		
		update = new JButton();
		update.setText("Update!");
		update.setActionCommand("Update");
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 3;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(10,10,10,10);
		this.add(update, c);
		
		this.pack();
		this.setVisible(true);
		
		//Add listeners
		google_login.addActionListener(controller);
		facebook_login.addActionListener(controller);
		google_contacts.addActionListener(controller);
		facebook_results.addListSelectionListener(controller);
		update.addActionListener(controller);
	}
	
	public void updateFacebookImage(Image newImage) {
		facebook_image.setImage(newImage);
		facebook_imglabel.invalidate();
	}
	
	public void updateFacebookImage(URL source) {
		facebook_image = new ImageIcon(source);
		facebook_imglabel.setIcon(facebook_image);
		facebook_imglabel.invalidate();
	}
	
	public void updateGoogleImage(Image newImage) {
		google_image.setImage(newImage);
		google_imglabel.invalidate();
	}
	
	public void updateGoogleContacts(String[] contacts) {
		google_contacts.removeAllItems();
		for (String item : contacts) {
			google_contacts.addItem(item);
		}
		google_contacts.invalidate();
	}
	
	public void updateFacebookContacts(String[] contacts) {
		facebook_results.setListData(contacts);
		facebook_results.setVisibleRowCount(5);
		facebook_results.invalidate();
	}
	
	public String getGoogleSelection() {
		int index = google_contacts.getSelectedIndex();
		if (index == -1) {
			return null;
		}
		
		return google_contacts.getItemAt(index).toString();
	}
	
	public String getFacebookSelection() {
		return (String) facebook_results.getSelectedValue();
	}

}
