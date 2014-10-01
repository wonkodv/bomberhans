package org.hanstool.bomberhans.server.cells;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const;

public class CellDeadGuy extends Cell
{
	int		ttl	= 4250;
	SPlayer	corpseOf;

	protected CellDeadGuy(byte x, byte y, byte cellType, SPlayer corpseOf)
	{
		super(x, y, cellType);
		this.corpseOf = corpseOf;
	}

	@Override
	public boolean canWalkOn()
	{
		if(getCellType() == 15)
		{
			return true;
		}
		return Const.GameConsts.rand.nextInt(100) < 60;
	}

	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
	}

	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		if(getCellType() == 13)
		{
			setCellType((byte) 15);
		}

		ful.replaceCell(createCell(cellType, getX(), getY(), owner, this));

		return true;
	}

	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		if(getCellType() == 14)
		{
			this.ttl = (int) (this.ttl - timeElapsed);
			if(this.ttl <= 0)
			{
				setCellType((byte) 13);
				ful.replaceCell(this);
			}
		}
	}
}
