package org.hanstool.bomberhans.server.cells;

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
		super(x, y, (byte) 5);
		this.owner = owner;
		this.power = owner.getPower();
	}

	@Override
	public boolean canWalkOn()
	{
		return rand.nextInt(100) < 60;
	}

	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
	}

	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		if(this.timeToLive > 400)
		{
			this.timeToLive = 400;
		}

		return true;
	}

	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		this.timeToLive = (int) (this.timeToLive - timeElapsed);
		if(this.timeToLive < 0)
		{
			explode(ful, this.power, this.owner);
			this.owner.setCurrentBombs(this.owner.getCurrentBombs() - 1);
		}
	}
}
