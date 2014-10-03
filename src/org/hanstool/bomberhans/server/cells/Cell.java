package org.hanstool.bomberhans.server.cells;

import static org.hanstool.bomberhans.shared.Const.CellTypes.*;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const.CellTypes;

public abstract class Cell
{
	private final byte	x;
	private final byte	y;
	private byte		cellType;
	
	public static Cell createCell(byte cellType, byte x, byte y, SPlayer player, Cell followedBy)
	{
		switch(cellType)
		{
			case FIRE_CENTER:
			case FIRE_HORZ:
			case FIRE_VERT:
			case FIRE_END_N:
			case FIRE_END_E:
			case FIRE_END_S:
			case FIRE_END_W:
				return new CellFire(x, y, cellType, player, followedBy);
			case BOMB:
				return new CellBomb(x, y, player);
			case WALL:
				return new CellWall(x, y);
			case WOOD:
			case BURNING_WOOD:
				return new CellWood(x, y, cellType);
			case START_POINT:
				return new CellStartSlot(x, y, player);
			case CLEAR:
				return new CellClear(x, y);

			case SP_PORT:
				return new CellSpecialPort(x, y);
			case SP_SCHINKEN:
			case PU_BOMB:
			case PU_POWER:
			case PU_SPEED:
				return new CellSpecial(x, y, cellType);

			case HANS_BURNING:
			case HANS_DEAD:
			case HANS_GORE:
				return new CellDeadGuy(x, y, cellType, player);
			default:
				throw new UnsupportedOperationException("CreateCell not Implemented " + CellTypes.NAMES[cellType]);

		}
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
		ful.replaceCell(createCell(FIRE_CENTER, getX(), getY(), fireOwner, createCell(CLEAR, getX(), getY(), null, null)));

		for(byte i = 1; i <= power; i++ )
		{
			Cell c = ful.getCell((byte) (x + i), y);
			if( !c.setOnFire(i == power ? FIRE_END_E : FIRE_HORZ, ful, fireOwner))
			{
				break;
			}
		}
		for(byte i = 1; i <= power; i++ )
		{
			Cell c = ful.getCell((byte) (x - i), y);
			if( !c.setOnFire(i == power ? FIRE_END_W : FIRE_HORZ, ful, fireOwner))
			{
				break;
			}
		}
		for(byte i = 1; i <= power; i++ )
		{
			Cell c = ful.getCell(x, (byte) (y + i));
			if( !c.setOnFire(i == power ? FIRE_END_S : FIRE_VERT, ful, fireOwner))
			{
				break;
			}
		}
		for(byte i = 1; i <= power; i++ )
		{
			Cell c = ful.getCell(x, (byte) (y - i));
			if( !c.setOnFire(i == power ? FIRE_END_N : FIRE_VERT, ful, fireOwner))
			{
				break;
			}
		}
	}
	
	public byte getCellType()
	{
		return cellType;
	}
	
	public byte getX()
	{
		return x;
	}
	
	public byte getY()
	{
		return y;
	}
	
	public abstract void handleWalkOn(SPlayer paramSPlayer, UpdateListener paramUpdateListener);
	
	public void setCellType(byte cellType)
	{
		this.cellType = cellType;
	}
	
	public abstract boolean setOnFire(byte paramByte, UpdateListener paramUpdateListener, SPlayer paramSPlayer);
	
	public abstract void update(long paramLong, UpdateListener paramUpdateListener);
}
