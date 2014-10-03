package org.hanstool.bomberhans.server.cells;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const.CellTypes;

public class CellClear extends Cell
{
	protected CellClear(byte x, byte y)
	{
		super(x, y, CellTypes.CLEAR);
	}

	@Override
	public boolean canWalkOn()
	{
		return true;
	}

	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
	}

	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		ful.replaceCell(createCell(cellType, getX(), getY(), owner, this));
		return true;
	}

	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
	}
}
