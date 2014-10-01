package org.hanstool.bomberhans.server.cells;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;

class CellFire extends Cell
{
	int		timeToLive	= 1250;
	SPlayer	owner;
	Cell	followedBy;
	
	public CellFire(byte x, byte y, byte cellType, SPlayer owner, Cell followedBy)
	{
		super(x, y, cellType);
		this.owner = owner;
		this.followedBy = followedBy;
	}
	
	@Override
	public boolean canWalkOn()
	{
		return true;
	}
	
	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
		if(this.followedBy instanceof CellStartSlot)
		{
			CellStartSlot css = (CellStartSlot) this.followedBy;
			if(css.owner != p)
			{
				;
			}
		}
		else
		{
			this.followedBy = createCell((byte) 14, getX(), getY(), p, null);
		}
		p.respawn(ful);
		this.owner.incScore(ful);
		
		ful.sendToClient((byte) 9, new Object[] { Byte.valueOf(this.owner.getSlot()), Byte.valueOf(p.getSlot()) });
	}
	
	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		this.owner = owner;
		setCellType(cellType);
		this.timeToLive = 1250;
		return true;
	}
	
	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		this.timeToLive = (int) (this.timeToLive - timeElapsed);
		if(this.timeToLive <= 0)
		{
			ful.replaceCell(this.followedBy);
		}
	}
}
