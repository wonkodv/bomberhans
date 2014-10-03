package org.hanstool.bomberhans.server.cells;

import static org.hanstool.bomberhans.shared.Const.CellTypes.*;

import java.util.Random;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const;

class CellBomb extends Cell
{
	private static Random	rand		= new Random();
	int						timeToLive	= Const.GameConsts.BOMB_TTL();
	SPlayer					owner;
	int						power;

	public CellBomb(byte x, byte y, SPlayer owner)
	{
		super(x, y, BOMB);
		this.owner = owner;
		power = owner.getPower();
	}

	@Override
	public boolean canWalkOn()
	{
		return rand.nextInt(100) < Const.GameConsts.BOMB_WALKOVER;
	}

	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
	}

	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		// TODO: Change Owner?
		if(timeToLive > Const.GameConsts.BOMB_TTL_AFTER_BURNING)
		{
			timeToLive = Const.GameConsts.BOMB_TTL_AFTER_BURNING;
		}

		return true;
	}

	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		timeToLive -= timeElapsed;
		if(timeToLive <= 0)
		{
			explode(ful, power, owner);
			owner.setCurrentBombs(owner.getCurrentBombs() - 1);
		}
	}
}
