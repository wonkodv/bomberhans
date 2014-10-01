/**
 * 
 */
package org.hanstool.bomberhans.server.cells;

import static org.hanstool.bomberhans.shared.Const.CellTypes.*;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const.CellTypes;
import org.hanstool.bomberhans.shared.Const.GameConsts;

class CellWood extends Cell
{
	

	private int	timeToLive	= GameConsts.BURNING_WOOD_TTL;
	
	protected CellWood(byte x, byte y, byte cellType)
	{
		super(x, y, cellType);
	}
	
	@Override
	public boolean canWalkOn()
	{
		return false;
	}
	
	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
		throw new Error("walking on Wood");
	}
	
	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		setCellType(BURNING_WOOD);
		ful.replaceCell(this);
		return false;
	}
	
	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		if(getCellType() == BURNING_WOOD)
		{
			timeToLive -= timeElapsed;
			if(timeToLive <= 0)
			{
				byte cellType;
				if(GameConsts.rand.nextInt(100) < ful.getWoodToSpecial())
				{
					int i = GameConsts.rand.nextInt(GameConsts.WTS_SCHINKEN);
					
					if(i < GameConsts.WTS_WALL)
					{
						cellType = CellTypes.WALL;
					}
					else if(i < GameConsts.WTS_POWER)
					{
						cellType = CellTypes.PU_POWER;
					}
					else if(i < GameConsts.WTS_SPEED)
					{
						cellType = CellTypes.PU_SPEED;
					}
					
					else if(i < GameConsts.WTS_BOMBS)
					{
						cellType = CellTypes.PU_BOMB;
					}
					else if(i < GameConsts.WTS_PORT)
					{
						cellType = CellTypes.SP_PORT;
					}
					else if(i < GameConsts.WTS_SCHINKEN)
					{
						cellType = CellTypes.SP_SCHINKEN;
					}
					else
					{
						cellType = CLEAR;
					}
					
				}
				else
				{
					cellType = CLEAR;
				}
				
				ful.replaceCell(createCell(cellType, getX(), getY(), null, null));
			}
		}
	}
}