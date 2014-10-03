package org.hanstool.bomberhans.server.cells;

import java.util.LinkedList;
import java.util.List;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.Const.CellTypes;

public class CellSpecialPort extends CellSpecial
{
	protected CellSpecialPort(byte x, byte y)
	{
		super(x, y, CellTypes.SP_PORT);
	}

	private CellSpecialPort findOtherPort(UpdateListener ful)
	{
		List<CellSpecialPort> list = new LinkedList<>();

		for(byte x = 0; x < ful.getWidth(); x++ )
		{
			for(byte y = 0; y < ful.getHeight(); y++ )
			{
				if(x != getX() || y != getY())
				{
					Cell c;
					if((c = ful.getCell(x, y)) instanceof CellSpecialPort)
					{
						list.add((CellSpecialPort) c);
					}
				}
			}
		}

		if(list.size() > 0)
		{
			return list.get(Const.GameConsts.rand.nextInt(list.size()));
		}
		return null;
	}

	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
		CellSpecialPort c = findOtherPort(ful);
		if(c != null)
		{
			ful.replaceCell(createCell(CellTypes.CLEAR, c.getX(), c.getY(), null, null));
			p.setX(c.getX() + .5f);
			p.setY(c.getY() + .5f);
			super.handleWalkOn(p, ful);
		}
	}

	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		CellSpecialPort c = findOtherPort(ful);
		if(c != null)
		{
			c.explode(ful, Const.GameConsts.SPECIAL_EXPLODE_POWER, owner);
		}
		return super.setOnFire(cellType, ful, owner);
	}
}
