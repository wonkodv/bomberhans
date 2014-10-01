package org.hanstool.bomberhans.server.cells;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;

public abstract class Cell
{
	private final byte	x;
	private final byte	y;
	private byte		cellType;
	
	public static Cell createCell(byte cellType, byte x, byte y, SPlayer player, Cell followedBy)
	{
		switch(cellType)
		{
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				return new CellFire(x, y, cellType, player, followedBy);
			case 5:
				return new CellBomb(x, y, player);
			case 2:
				return new CellWall(x, y);
			case 3:
			case 4:
				return new CellWood(x, y, cellType);
			case 21:
				return new CellStartSlot(x, y, player);
			case 1:
				return new CellClear(x, y);
			case 19:
				return new CellSpecialPort(x, y);
			case 16:
			case 17:
			case 18:
			case 20:
				return new CellSpecial(x, y, cellType);
			case 13:
			case 14:
			case 15:
				return new CellDeadGuy(x, y, cellType, player);
		}
		throw new UnsupportedOperationException("CreateCell not Implemented " + org.hanstool.bomberhans.shared.Const.CellTypes.NAMES[cellType]);
	}
	
	protected Cell(byte x, byte y, byte cellType)
	{
		this.x = x;
		this.y = y;
		this.cellType = cellType;
	}
	
	public abstract boolean canWalkOn();
	
	protected void explode(UpdateListener ful, int power, SPlayer fireOwner)
	{
		ful.replaceCell(createCell((byte) 6, getX(), getY(), fireOwner, createCell((byte) 1, getX(), getY(), null, null)));
		
		for(byte i = 1; i <= power; i = (byte) (i + 1))
		{
			Cell c = ful.getCell((byte) (this.x + i), this.y);
			if( !c.setOnFire((byte) (i == power ? 10 : 7), ful, fireOwner))
			{
				break;
			}
		}
		for(byte i = 1; i <= power; i = (byte) (i + 1))
		{
			Cell c = ful.getCell((byte) (this.x - i), this.y);
			if( !c.setOnFire((byte) (i == power ? 12 : 7), ful, fireOwner))
			{
				break;
			}
		}
		for(byte i = 1; i <= power; i = (byte) (i + 1))
		{
			Cell c = ful.getCell(this.x, (byte) (this.y + i));
			if( !c.setOnFire((byte) (i == power ? 11 : 8), ful, fireOwner))
			{
				break;
			}
		}
		for(byte i = 1; i <= power; i = (byte) (i + 1))
		{
			Cell c = ful.getCell(this.x, (byte) (this.y - i));
			if( !c.setOnFire((byte) (i == power ? 9 : 8), ful, fireOwner))
			{
				break;
			}
		}
	}
	
	public byte getCellType()
	{
		return this.cellType;
	}
	
	public byte getX()
	{
		return this.x;
	}
	
	public byte getY()
	{
		return this.y;
	}
	
	public abstract void handleWalkOn(SPlayer paramSPlayer, UpdateListener paramUpdateListener);
	
	public void setCellType(byte cellType)
	{
		this.cellType = cellType;
	}
	
	public abstract boolean setOnFire(byte paramByte, UpdateListener paramUpdateListener, SPlayer paramSPlayer);
	
	public abstract void update(long paramLong, UpdateListener paramUpdateListener);
}
