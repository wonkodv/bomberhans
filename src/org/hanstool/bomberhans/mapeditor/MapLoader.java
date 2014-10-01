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
		DataInputStream in = new DataInputStream(new FileInputStream(file));

		byte version = in.readByte();

		this.mapName = in.readUTF();

		this.woodStayP = in.readByte();
		this.specialP = in.readByte();
		this.sizeX = in.readByte();
		this.sizeY = in.readByte();

		this.cellTypes = new byte[this.sizeX][this.sizeY];

		for(byte x = 0; x < this.sizeX; x = (byte) (x + 1))
		{
			for(byte y = 0; y < this.sizeY; y = (byte) (y + 1))
			{
				this.cellTypes[x][y] = in.readByte();
			}
		}
	}

	public byte[][] getCellTypes()
	{
		return this.cellTypes;
	}

	public byte[][] getCellTypesWoodReplaced()
	{
		byte[][] result = new byte[this.sizeX][this.sizeY];
		for(int y = 0; y < this.sizeY; y++ )
		{
			for(int x = 0; x < this.sizeX; x++ )
			{
				if(this.cellTypes[x][y] == 3 && Const.GameConsts.rand.nextInt(100) >= this.woodStayP)
				{
					result[x][y] = 1;
				}
				else
				{
					result[x][y] = this.cellTypes[x][y];
				}
			}
		}

		return result;
	}

	public String getMapName()
	{
		return this.mapName;
	}

	public byte getSizeX()
	{
		return this.sizeX;
	}

	public byte getSizeY()
	{
		return this.sizeY;
	}

	public byte getSpecialP()
	{
		return this.specialP;
	}

	public byte getWoodStayP()
	{
		return this.woodStayP;
	}
}
