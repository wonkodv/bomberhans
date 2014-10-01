/**
 * 
 */
package org.hanstool.bomberhans.server.cells;

import static org.hanstool.bomberhans.shared.Const.CellTypes.*;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.NetworkStreamAdapter;

class CellFire extends Cell
{
	int		timeToLive	= Const.GameConsts.FIRE_TTL;
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
		if(followedBy instanceof CellStartSlot)
		{
			CellStartSlot css = (CellStartSlot) followedBy;
			if(css.owner == p)
			{
				return;// save on own startSlot
			}
		}
		else
		{
			followedBy = createCell(HANS_BURNING, getX(), getY(), p, null);
		}
		p.respawn(ful);
		owner.incScore(ful);
		
		ful.sendToClient(NetworkStreamAdapter.StC_PLAYER_SCORED, owner.getSlot(), p.getSlot());
		

	}
	
	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		this.owner = owner;
		setCellType(cellType);
		timeToLive = Const.GameConsts.FIRE_TTL;
		return true;
	}
	
	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		timeToLive -= timeElapsed;
		if(timeToLive <= 0)
		{
			ful.replaceCell(followedBy);
		}
	}
}