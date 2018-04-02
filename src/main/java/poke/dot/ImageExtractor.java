package poke.dot;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ImageExtractor {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 1) {
			throw new Exception("Input image path");
		}
		String imagePath = args[0];
		System.out.println(imagePath);
		
		ImageExtractor extractor = new ImageExtractor();
		BufferedImage image = extractor.loadImage(imagePath);
		extractor.split(image);
//		extractor.display(image);
	}
	
	private void split(BufferedImage image) {
		for (int i = 0 ; i < 100 ; i++) {
			int rgb = image.getRGB(i, i);
			System.out.println(String.format("%d : %d - %d", i, i, rgb));
		}
	}
	
	private BufferedImage loadImage(String imagePath) throws IOException {
		File f = new File(imagePath);
		BufferedImage bi = ImageIO.read(f);
		
		return bi;
	}
	
	private void display(BufferedImage image) {
		JLabel picLabel = new JLabel(new ImageIcon(image));

		JPanel jPanel = new JPanel();
		jPanel.add(picLabel);
		
		JFrame f = new JFrame();
		f.setSize(new Dimension(image.getWidth(), image.getHeight()));
		f.add(jPanel);
		f.setVisible(true);
	}
}
