package org.hanstool.bomberhans;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class ColorChanger
{
	public static final int	ALPHA		= 0;
	public static final int	RED			= 1;
	public static final int	GREEN		= 2;
	public static final int	BLUE		= 3;
	public static final int	HUE			= 0;
	public static final int	SATURATION	= 1;
	public static final int	BRIGHTNESS	= 2;
	public static final int	TRANSPARENT	= 0;
	
	private static BufferedImage imageToBufferedImage(Image image)
	{
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		
		return bufferedImage;
	}
	
	public static BufferedImage makeColorTransparent(BufferedImage im, Color color)
	{
		ImageFilter filter = new RGBImageFilter() {
			public int	markerRGB;
			
			@Override
			public final int filterRGB(int x, int y, int rgb)
			{
				if((rgb | 0xFF000000) == this.markerRGB)
				{
					return 0xFFFFFF & rgb;
				}
				
				return rgb;
			}
		};
		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return imageToBufferedImage(Toolkit.getDefaultToolkit().createImage(ip));
	}
	
	public BufferedImage changeColor(BufferedImage image, Color mask, Color replacement)
	{
		BufferedImage destImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
		
		Graphics2D g = destImage.createGraphics();
		g.drawImage(image, null, 0, 0);
		g.dispose();
		
		for(int i = 0; i < destImage.getWidth(); i++ )
		{
			for(int j = 0; j < destImage.getHeight(); j++ )
			{
				int destRGB = destImage.getRGB(i, j);
				
				if(matches(mask.getRGB(), destRGB))
				{
					int rgbnew = getNewPixelRGB(replacement.getRGB(), destRGB);
					destImage.setRGB(i, j, rgbnew);
				}
			}
		}
		
		return makeColorTransparent(destImage, new Color(192, 192, 192));
	}
	
	private float[] getHSBArray(int rgb)
	{
		int[] rgbArr = getRGBArray(rgb);
		return Color.RGBtoHSB(rgbArr[1], rgbArr[2], rgbArr[3], null);
	}
	
	private int getNewPixelRGB(int replacement, int destRGB)
	{
		float[] destHSB = getHSBArray(destRGB);
		float[] replHSB = getHSBArray(replacement);
		
		int rgbnew = Color.HSBtoRGB(replHSB[0], replHSB[1], destHSB[2]);
		return rgbnew;
	}
	
	private int[] getRGBArray(int rgb)
	{
		return new int[] { rgb >> 24 & 0xFF, rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF };
	}
	
	private boolean matches(int maskRGB, int destRGB)
	{
		float[] hsbMask = getHSBArray(maskRGB);
		float[] hsbDest = getHSBArray(destRGB);
		
		if(hsbMask[0] == hsbDest[0])
		{
			return true;
		}
		return false;
	}
}
