package org.hanstool.bomberhans.server.cells;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;

public class CellSpecial extends Cell
{
	protected CellSpecial(byte x, byte y, byte cellType)
	{
		super(x, y, cellType);
	}
	
	@Override
	public boolean canWalkOn()
	{
		return true;
	}
	
	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
		p.powerUp(getCellType());
		ful.replaceCell(createCell((byte) 1, getX(), getY(), null, null));
	}
	
	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		explode(ful, 1, owner);
		return true;
	}
	
	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
	}
}
