package org.hanstool.bomberhans.client;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.hanstool.bomberhans.shared.Const;

public class ImageBuffer
{
	BufferedImage[][]				hansBuffer;
	BufferedImage[]					cellBuffer;
	ColorChanger					changer					= new ColorChanger();
	
	private static final Color[]	hansMainColors			= { new Color(16769303), new Color(1244928), new Color(1966335), new Color(16711680), new Color(16711872), new Color(14306851), new Color(31578), new Color(7452883) };
	private static final Color[]	hansSubColors			= { new Color(13565952), new Color(37119), new Color(16187136), new Color(43784), new Color(60159), new Color(5710810), new Color(16755514), new Color(16103567) };
	
	private static final Color		hansMainReplaceColor	= new Color(231, 242, 220);
	private static final Color		hansSubReplaceColor		= new Color(164, 252, 252);
	
	public ImageBuffer() throws IOException
	{
		this.hansBuffer = new BufferedImage[Const.PlayerState.Names.length][8];
		
		for(int playerState = 0; playerState < this.hansBuffer.length; playerState++ )
		{
			String s = Const.PlayerState.Names[playerState] + ".bmp";
			URL u = getClass().getClassLoader().getResource(s);
			BufferedImage img;
			try
			{
				img = ImageIO.read(u);
			}
			catch(IllegalArgumentException e)
			{
				throw new IOException("Resource '" + s + "' not Found", e);
			}
			for(int slot = 0; slot < this.hansBuffer[playerState].length; slot++ )
			{
				BufferedImage newImg = this.changer.changeColor(img, hansMainReplaceColor, hansMainColors[slot]);
				newImg = this.changer.changeColor(newImg, hansSubReplaceColor, hansSubColors[slot]);
				
				this.hansBuffer[playerState][slot] = newImg;
			}
			
		}
		
		this.cellBuffer = new BufferedImage[Const.CellTypes.NAMES.length];
		
		for(int cellState = 0; cellState < this.cellBuffer.length; cellState++ )
		{
			String s = Const.CellTypes.NAMES[cellState] + ".bmp";
			URL u = getClass().getClassLoader().getResource(s);
			BufferedImage img;
			try
			{
				img = ImageIO.read(u);
			}
			catch(IllegalArgumentException e)
			{
				throw new IOException("Resource '" + s + "' not Found", e);
			}
			this.cellBuffer[cellState] = img;
		}
	}
	
	public BufferedImage getcellImage(byte cellType)
	{
		return this.cellBuffer[cellType];
	}
	
	public BufferedImage getHansImage(byte playerState, byte slot)
	{
		return this.hansBuffer[playerState][slot];
	}
}