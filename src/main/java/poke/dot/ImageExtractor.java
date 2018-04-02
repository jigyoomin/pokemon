package poke.dot;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
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
		BufferedImage split = extractor.split(image);
		BufferedImage splitHorizontal = extractor.splitHorizontal(split);
		BufferedImage scaled = extractor.scale(splitHorizontal, 10);
		extractor.save(scaled);
//		extractor.display(scaled);
//		extractor.display(image);
	}
	
	private void save(BufferedImage image) throws IOException {
		File file = new File("C:\\Users\\earth\\Downloads\\00.png");
		ImageIO.write(image, "png", file);
	}
	
	private BufferedImage scale(BufferedImage image, int multiple) {
		int newW = image.getWidth() * multiple;
		int newH = image.getHeight() * multiple;
		Image tmp = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
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
	private BufferedImage splitHorizontal(BufferedImage image) {
		
		
		int[][] split = new int[100][];
		int hIndex = 0;
		// 0 - prepare
		// 1 - start
		int status = 0;
		int subWidth = 0;
		int minEmptyHeader = image.getHeight(); 
		int minEmptyTail = minEmptyHeader; 
				
		for (int w = 0 ; w < image.getWidth() ; w++) {
			int[] line = new int[image.getHeight()];
			boolean isNull = true;
			int emptyHeaderCounter = 0;
			int emptyTailCounter = 0;
			for (int h = 0 ; h < image.getHeight() ; h++) {
				line[h] = image.getRGB(w, h);
				if (line[h] != 0) {
					isNull = false;
					emptyTailCounter = 0;
				} else {
					if (isNull) {
						emptyHeaderCounter++;
					} else {
						emptyTailCounter++;
					}
				}
			}
			
			if (!isNull) {
				split[hIndex++] = line;
				subWidth++;
				status = 1;
				
				if (minEmptyHeader > emptyHeaderCounter) {
					minEmptyHeader = emptyHeaderCounter;
				}
				if (minEmptyTail > emptyTailCounter) {
					minEmptyTail = emptyTailCounter;
				}
			} else {
				if (status == 1) {
					status = 0;
					// 한무더기 끝
					// subHeight = 0;
					break;
				}
			}
		}
		
		BufferedImage resultImage = new BufferedImage(subWidth, image.getHeight() - minEmptyHeader - minEmptyTail, image.getType());
		for (int i = 0 ; i < split.length ; i++) {
			if (split[i] == null) {
				break;
			}
			for (int j = 0 ; j < image.getHeight() - minEmptyHeader - minEmptyTail ; j++) {
				resultImage.setRGB(i, j, split[i][j + minEmptyHeader]);
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
		
		final JFrame f = new JFrame();
		f.setSize(new Dimension(image.getWidth() + 50, image.getHeight() + 80));
		f.add(jPanel);
		f.setVisible(true);
		f.addWindowStateListener(new WindowStateListener() {
			
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == WindowEvent.WINDOW_CLOSED) {
					f.dispose();
				}
			}
		});
	}
}
