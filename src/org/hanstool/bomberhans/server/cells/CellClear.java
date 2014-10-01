/**
 * 
 */
package org.hanstool.bomberhans.server.cells;

import static org.hanstool.bomberhans.shared.Const.CellTypes.*;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;

/**
 * @author Wonko
 */
public class CellClear extends Cell
{
	
	/**
	 * @param x
	 * @param y
	 * @param cellType
	 */
	protected CellClear(byte x, byte y)
	{
		super(x, y, CLEAR);
	}
	
	@Override
	public boolean canWalkOn()
	{
		return true;
	}
	
	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
		//
	}
	
	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		
		ful.replaceCell(createCell(cellType, getX(), getY(), owner, this));
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hanstool.bomberhans.server.cells.Cell#update(long,
	 * org.hanstool.bomberhans.server.Field.FieldUpdateListener)
	 */
	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		//
	}
	
}
