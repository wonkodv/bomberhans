package org.hanstool.bomberhans.server.cells;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const;
class CellWood extends Cell
{
	private int	timeToLive	= 1800;
	
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
		setCellType((byte) 4);
		ful.replaceCell(this);
		return false;
	}
	
	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		if(getCellType() == 4)
		{
			this.timeToLive = (int) (this.timeToLive - timeElapsed);
			if(this.timeToLive <= 0)
			{
				byte cellType;
				if(Const.GameConsts.rand.nextInt(100) < ful.getWoodToSpecial())
				{
					int i = Const.GameConsts.rand.nextInt(416);

					if(i < 1)
					{
						cellType = 2;
					}
					else
					{
						if(i < 101)
						{
							cellType = 17;
						}
						else
						{
							if(i < 201)
							{
								cellType = 18;
							}
							else
							{
								if(i < 301)
								{
									cellType = 16;
								}
								else
								{
									if(i < 401)
									{
										cellType = 19;
									}
									else
									{
										if(i < 416)
										{
											cellType = 20;
										}
										else
										{
											cellType = 1;
										}
									}
								}
							}
						}
					}
				}
				else
				{
					cellType = 1;
				}
				
				ful.replaceCell(createCell(cellType, getX(), getY(), null, null));
			}
		}
	}
}
