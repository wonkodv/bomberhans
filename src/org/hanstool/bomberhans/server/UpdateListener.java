/**
 * 
 */
package org.hanstool.bomberhans.server;

import org.hanstool.bomberhans.server.cells.Cell;
import org.hanstool.bomberhans.server.cells.CellStartSlot;
import org.hanstool.bomberhans.shared.NetworkStreamAdapter;

public class UpdateListener
{
	/**
	 * 
	 */
	private final Field		field;
	private final Server	server;
	
	/**
	 * @param field
	 */
	UpdateListener(Server server, Field field)
	{
		this.field = field;
		this.server = server;
	}
	
	public Cell getCell(byte x, byte y)
	{
		return this.field.cells[x][y];
	}
	
	public int getHeight()
	{
		return field.getHeight();
	}
	
	public CellStartSlot getSlotCell(byte slot)
	{
		return field.getSlotCell(slot);
	}
	
	public int getWidth()
	{
		return field.getWidth();
	}
	
	public int getWoodToSpecial()
	{
		return field.getWoodToSpecial();
	}
	
	public void replaceCell(Cell cell)
	{
		this.field.cells[cell.getX()][cell.getY()] = cell;
		sendToClient(NetworkStreamAdapter.StC_SYNC_CELL, cell.getX(), cell.getY(), cell.getCellType());
	}
	
	public void sendToClient(byte networkCMD, Object... params)
	{
		server.sendToClient(networkCMD, params);
	}
}