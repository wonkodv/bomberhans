package org.hanstool.bomberhans.server.cells;

import java.util.LinkedList;
import java.util.List;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const;

public class CellSpecialPort extends CellSpecial
{
	protected CellSpecialPort(byte x, byte y)
	{
		super(x, y, (byte) 19);
	}
	
	private CellSpecialPort findOtherPort(UpdateListener ful)
	{
		List<Cell> list = new LinkedList<Cell>();
		
		for(byte x = 0; x < ful.getWidth(); x = (byte) (x + 1))
		{
			for(byte y = 0; y < ful.getHeight(); y = (byte) (y + 1))
			{
				if(x != getX() || y != getY())
				{
					Cell c;
					if((c = ful.getCell(x, y)) instanceof CellSpecialPort)
					{
						list.add(c);
					}
				}
			}
		}
		
		if(list.size() > 0)
		{
			return (CellSpecialPort) list.get(Const.GameConsts.rand.nextInt(list.size()));
		}
		return null;
	}
	
	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
		CellSpecialPort c = findOtherPort(ful);
		if(c != null)
		{
			ful.replaceCell(createCell((byte) 1, c.getX(), c.getY(), null, null));
			p.setX(c.getX() + 0.5F);
			p.setY(c.getY() + 0.5F);
			super.handleWalkOn(p, ful);
		}
	}
	
	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		CellSpecialPort c = findOtherPort(ful);
		if(c != null)
		{
			c.explode(ful, 1, owner);
		}
		return super.setOnFire(cellType, ful, owner);
	}
}
