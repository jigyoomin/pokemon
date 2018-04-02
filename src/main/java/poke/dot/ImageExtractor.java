package poke.dot;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
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
		BufferedImage split = extractor.split(image);
		extractor.display(split);
	}
	
	private BufferedImage split(BufferedImage image) {
		
//		for (int i = 0 ; i < 100 ; i++) {
//			int rgb = image.getRGB(i, i);
//			int a = rgb >> 32 & 0xff;
//			int r = rgb >> 16 & 0xff;
//			int g = rgb >> 8 & 0xff;
//			int b = rgb  & 0xff;
//			
//			System.out.println(String.format("%d : %d - %d %d %d", i, i, r, g, b));
//		}
		
		int[][] split = new int[100][];
		int hIndex = 0;
		// 0 - prepare
		// 1 - start
		int status = 0;
		int subHeight = 0;
		for (int h = 0 ; h < image.getHeight() ; h++) {
			int[] line = new int[image.getWidth()];
			boolean isNull = true;
			for (int w = 0 ; w < image.getWidth() ; w++) {
				line[w] = image.getRGB(w, h);
				if (line[w] != 0) isNull = false;
				
			}
			if (!isNull) {
				split[hIndex++] = line;
				subHeight++;
				status = 1;
			} else {
				if (status == 1) {
					status = 0;
					// 한무더기 끝
					// subHeight = 0;
					break;
				}
			}
		}
		
		BufferedImage resultImage = new BufferedImage(image.getWidth(), subHeight, image.getType());
		for (int i = 0 ; i < split.length ; i++) {
			if (split[i] == null) {
				break;
			}
			for (int j = 0 ; j < image.getWidth() ; j++) {
				resultImage.setRGB(j, i, split[i][j]);
			}
		}
		return resultImage;
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
