package org.hanstool.bomberhans.server.cells;

import static org.hanstool.bomberhans.shared.Const.CellTypes.*;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.Const.GameConsts;

public class CellDeadGuy extends Cell
{
	int		ttl	= GameConsts.BURNING_HANS_TTL;
	SPlayer	corpseOf;
	
	protected CellDeadGuy(byte x, byte y, byte cellType, SPlayer corpseOf)
	{
		super(x, y, cellType);
		this.corpseOf = corpseOf;
	}
	
	@Override
	public boolean canWalkOn()
	{
		if(getCellType() == HANS_GORE)
		{
			return true;
		}
		return Const.GameConsts.rand.nextInt(100) < Const.GameConsts.DEAD_GUY_WALKOVER;
	}
	
	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
	}
	
	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		if(getCellType() == HANS_DEAD)
		{
			setCellType(HANS_GORE);
		}
		
		ful.replaceCell(createCell(cellType, getX(), getY(), owner, this));
		
		return true;
	}
	
	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		if(getCellType() == HANS_BURNING)
		{
			ttl -= timeElapsed;
			if(ttl <= 0)
			{
				setCellType(HANS_DEAD);
				ful.replaceCell(this);
			}
		}
	}
}
