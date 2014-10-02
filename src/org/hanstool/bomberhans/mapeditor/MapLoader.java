package org.hanstool.bomberhans.mapeditor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.hanstool.bomberhans.shared.Const;

public class MapLoader
{
	private final String	mapName;
	private final byte		sizeX;
	private final byte		sizeY;
	private final byte		woodStayP;
	private final byte		specialP;
	private final byte[][]	cellTypes;
	
	public MapLoader(File file) throws IOException, FileNotFoundException
	{
		try (DataInputStream in = new DataInputStream(new FileInputStream(file)))
		{
			byte version = in.readByte();
			
			mapName = in.readUTF();
			
			woodStayP = in.readByte();
			specialP = in.readByte();
			sizeX = in.readByte();
			sizeY = in.readByte();
			
			cellTypes = new byte[sizeX][sizeY];
			
			for(byte x = 0; x < sizeX; x = (byte) (x + 1))
			{
				for(byte y = 0; y < sizeY; y = (byte) (y + 1))
				{
					cellTypes[x][y] = in.readByte();
				}
			}
		}
	}
	
	public byte[][] getCellTypes()
	{
		return cellTypes;
	}
	
	public byte[][] getCellTypesWoodReplaced()
	{
		byte[][] result = new byte[sizeX][sizeY];
		for(int y = 0; y < sizeY; y++ )
		{
			for(int x = 0; x < sizeX; x++ )
			{
				if(cellTypes[x][y] == 3 && Const.GameConsts.rand.nextInt(100) >= woodStayP)
				{
					result[x][y] = 1;
				}
				else
				{
					result[x][y] = cellTypes[x][y];
				}
			}
		}
		
		return result;
	}
	
	public String getMapName()
	{
		return mapName;
	}
	
	public byte getSizeX()
	{
		return sizeX;
	}
	
	public byte getSizeY()
	{
		return sizeY;
	}
	
	public byte getSpecialP()
	{
		return specialP;
	}
	
	public byte getWoodStayP()
	{
		return woodStayP;
	}
}
