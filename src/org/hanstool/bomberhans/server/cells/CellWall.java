package org.hanstool.bomberhans.server.cells;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;

class CellWall extends Cell
{
	protected CellWall(byte x, byte y)
	{
		super(x, y, (byte) 2);
	}

	@Override
	public boolean canWalkOn()
	{
		return false;
	}

	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
		throw new Error("walking on wood");
	}

	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		return false;
	}

	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
	}
}
