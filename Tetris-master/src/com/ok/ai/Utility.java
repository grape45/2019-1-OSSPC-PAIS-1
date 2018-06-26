package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.awt.*;
import java.awt.image.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;


public class Utility
{
	private Utility() {}
	
	public static Image iconToImage(Icon icon)
	{
		if (icon instanceof ImageIcon)
		{
			return ((ImageIcon)icon).getImage();
		}
		else
		{
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();
			GraphicsEnvironment ge =
					GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			BufferedImage image = gc.createCompatibleImage(w, h);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}
	
	public static BufferedImage deepCopy(BufferedImage bi)
	{
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static BufferedImage rotateRight (BufferedImage img)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage ans = new BufferedImage(height, width , img.getType() );
	
		for( int x = 0; x < width; x++ )
		{
			for( int y = 0; y < height; y++ )
				ans.setRGB(height - y - 1, x, img.getRGB(x, y));
		}
		return ans;
	}
}
