package punchline.vision;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class ImageLoader extends JFrame {

	/**
	 * Abhi was here
	 */
	private static final long serialVersionUID = 1L;
	
	private JFrame window;
	
	public ImageLoader(Mat image, String dest) {
		
		// set up jframe
		window = new JFrame("Vision");
		
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		window.setResizable(false);
		if (image != null) {
			Highgui.imwrite(dest, image); // write image to destination
		}
		ImageIcon icon = new ImageIcon(dest); // convert image to icon
		window.setSize(icon.getIconWidth() + 10, icon.getIconHeight() + 35); //set display to fit icon
		JLabel imageLabel = new JLabel(" ",icon, JLabel.CENTER); // create component from icon
		window.getContentPane().add(imageLabel); // add component to frame
		
		window.pack();

		window.setVisible(true); // display
	}
}
