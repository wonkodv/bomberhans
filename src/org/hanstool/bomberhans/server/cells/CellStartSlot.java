/**
 * 
 */
package org.hanstool.bomberhans.server.cells;

import org.hanstool.bomberhans.server.SPlayer;
import org.hanstool.bomberhans.server.UpdateListener;
import org.hanstool.bomberhans.shared.Const.CellTypes;

/**
 * @author Wonko
 */
public class CellStartSlot extends Cell
{
	SPlayer	owner;
	
	protected CellStartSlot(byte x, byte y, SPlayer owner)
	{
		super(x, y, CellTypes.START_POINT);
		this.owner = owner;
	}
	
	@Override
	public boolean canWalkOn()
	{
		return true;
	}
	
	/**
	 * @return the owner
	 */
	public SPlayer getOwner()
	{
		return owner;
	}
	
	@Override
	public void handleWalkOn(SPlayer p, UpdateListener ful)
	{
		// game Mode capture others circle
	}
	
	
	@Override
	public boolean setOnFire(byte cellType, UpdateListener ful, SPlayer owner)
	{
		ful.replaceCell(createCell(cellType, getX(), getY(), owner, this));
		return true;
	}
	
	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(SPlayer owner)
	{
		this.owner = owner;
	}
	
	@Override
	public void update(long timeElapsed, UpdateListener ful)
	{
		//
	}
	

}
