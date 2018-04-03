package poke.dot;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ImageExtractor {
	protected static int index = 0;

	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 1) {
			throw new Exception("Input image path");
		}
		String imagePath = args[0];
		System.out.println(imagePath);
		
		ImageExtractor extractor = new ImageExtractor();
		BufferedImage image = extractor.loadImage(imagePath);
		BufferedImage[] split = extractor.splitHorizontal(image);
		BufferedImage[][] all = new BufferedImage[split.length][];
		for (int i = 0 ; i < split.length ; i++) {
			BufferedImage[] splitVertical = extractor.splitVertical(split[i]);
			all[i] = new BufferedImage[splitVertical.length];
//			BufferedImage splitHorizontal = extractor.splitHorizontal(split[i]);
			for (int j = 0 ; j < splitVertical.length ; j++) {
				extractor.save(extractor.scale(splitVertical[j], 10), j * all.length + i);
			}
		}
//		extractor.display(scaled);
//		extractor.display(image);
	}
	
	private void save(BufferedImage image, int index) throws IOException {
		File file = new File(String.format("C:\\Users\\earth\\Downloads\\a\\%03d.png", index));
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
	
	private BufferedImage[] splitVertical(BufferedImage image) {
		
//		for (int i = 0 ; i < 100 ; i++) {
//			int rgb = image.getRGB(i, i);
//			int a = rgb >> 32 & 0xff;
//			int r = rgb >> 16 & 0xff;
//			int g = rgb >> 8 & 0xff;
//			int b = rgb  & 0xff;
//			
//			System.out.println(String.format("%d : %d - %d %d %d", i, i, r, g, b));
//		}
		
//		int[][] split = new int[100][];
		int hIndex = 0;
		// 0 - prepare
		// 1 - start
		int status = 0;
		int startHeight = 0;
		
		int subHeight = 0;
		int minEmptyHeader = image.getHeight(); 
		int minEmptyTail = minEmptyHeader; 
		
		List<BufferedImage> result = new ArrayList<BufferedImage>();
		
		for (int h = 0 ; h < image.getHeight() ; h++) {
			boolean isNull = true;
			int emptyHeaderCounter = 0;
			int emptyTailCounter = 0;
			
			for (int w = 0 ; w < image.getWidth() ; w++) {
				int rgb = image.getRGB(w, h);
				if (rgb != 0) {
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
//				split[hIndex++] = line;
				subHeight++;
				status = 1;
				
				if (minEmptyHeader > emptyHeaderCounter) {
					minEmptyHeader = emptyHeaderCounter;
				}
				if (minEmptyTail > emptyTailCounter) {
					minEmptyTail = emptyTailCounter;
				}
				
			} else {
				if (status == 1) {
					// 한무더기 끝
					System.out.println(String.format("index %d : left - %d , right - %d", index++, minEmptyHeader, minEmptyTail));
					result.add(image.getSubimage(0 + minEmptyHeader, startHeight, image.getWidth() - minEmptyHeader - minEmptyTail, subHeight));
					status = 0;
					subHeight = 0;
					
					minEmptyHeader = image.getHeight(); 
					minEmptyTail = minEmptyHeader;
//					break;
				}
				startHeight = h + 1;
			}
		}
		result.add(image.getSubimage(0 + minEmptyHeader, startHeight, image.getWidth() - minEmptyHeader - minEmptyTail, subHeight));
//		BufferedImage resultImage = new BufferedImage(image.getWidth(), subHeight, image.getType());
//		for (int i = 0 ; i < split.length ; i++) {
//			if (split[i] == null) {
//				break;
//			}
//			for (int j = 0 ; j < image.getWidth() ; j++) {
//				resultImage.setRGB(j, i, split[i][j]);
//			}
//		}
		return result.toArray(new BufferedImage[result.size()]);
	}
	private BufferedImage[] splitHorizontal(BufferedImage image) {
		
		
		int[][] split = new int[100][];
		
		// 0 - prepare
		// 1 - start
		int status = 0;
		int startWidth = 0;
		int subWidth = 0;
		int minEmptyHeader = image.getHeight(); 
		int minEmptyTail = minEmptyHeader; 
				
		List<BufferedImage> result = new ArrayList<BufferedImage>();
		
		for (int w = 0 ; w < image.getWidth() ; w++) {
			boolean isNull = true;
			int emptyHeaderCounter = 0;
			int emptyTailCounter = 0;
			for (int h = 0 ; h < image.getHeight() ; h++) {
				int rgb = image.getRGB(w, h);
				if (rgb != 0) {
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
					// 한무더기 끝
					result.add(image.getSubimage(startWidth, minEmptyHeader, subWidth, image.getHeight() - minEmptyHeader - minEmptyTail));
					status = 0;
					subWidth = 0;
					minEmptyHeader = image.getHeight(); 
					minEmptyTail = minEmptyHeader;
//					break;
				}
				startWidth = w + 1;
			}
		}
		
//		BufferedImage resultImage = new BufferedImage(subWidth, image.getHeight() - minEmptyHeader - minEmptyTail, image.getType());
//		for (int i = 0 ; i < split.length ; i++) {
//			if (split[i] == null) {
//				break;
//			}
//			for (int j = 0 ; j < image.getHeight() - minEmptyHeader - minEmptyTail ; j++) {
//				resultImage.setRGB(i, j, split[i][j + minEmptyHeader]);
//			}
//		}
		return result.toArray(new BufferedImage[result.size()]);
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
