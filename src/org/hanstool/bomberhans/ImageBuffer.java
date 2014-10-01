/**
 * 
 */
package org.hanstool.bomberhans;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.Const.CellTypes;
import org.hanstool.bomberhans.shared.Const.PlayerState;

/**
 * @author Wonko
 */
public class ImageBuffer
{
	

	BufferedImage[][]				hansBuffer;
	BufferedImage[]					cellBuffer;
	ColorChanger					changer					= new ColorChanger();
	
	private static final Color[]	hansMainColors			= { new Color(0xffe117), new Color(0x12ff00), new Color(0x1e00ff), new Color(0xff0000), new Color(0xff00c0), new Color(0xda4e23), new Color(0x007b5a), new Color(0x71b8d3) };
	private static final Color[]	hansSubColors			= { new Color(0xcf0000), new Color(0x0090ff), new Color(0xf6ff00), new Color(0x00ab08), new Color(0x00eaff), new Color(0x5723da), new Color(0xffab3a), new Color(0xf5b88f) };
	
	private static final Color		hansMainReplaceColor	= new Color(231, 242, 220);
	private static final Color		hansSubReplaceColor		= new Color(164, 252, 252);
	
	

	public ImageBuffer() throws IOException
	{
		hansBuffer = new BufferedImage[PlayerState.Names.length][Const.GameConsts.MAX_PLAYERS];
		
		for(int playerState = 0; playerState < hansBuffer.length; playerState++ )
		{
			
			String s = PlayerState.Names[playerState] + ".bmp";
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
			for(int slot = 0; slot < hansBuffer[playerState].length; slot++ )
			{
				BufferedImage newImg;
				newImg = changer.changeColor(img, hansMainReplaceColor, hansMainColors[slot]);
				newImg = changer.changeColor(newImg, hansSubReplaceColor, hansSubColors[slot]);
				
				hansBuffer[playerState][slot] = newImg;
			}
		}
		

		cellBuffer = new BufferedImage[CellTypes.NAMES.length];
		
		for(int cellState = 0; cellState < cellBuffer.length; cellState++ )
		{
			
			String s = CellTypes.NAMES[cellState] + ".bmp";
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
			
			cellBuffer[cellState] = img;
		}
	}
	
	public BufferedImage getcellImage(byte cellType)
	{
		
		return cellBuffer[cellType];
	}
	
	public BufferedImage getHansImage(byte playerState, byte slot)
	{
		
		return hansBuffer[playerState][slot];
	}
	

}
