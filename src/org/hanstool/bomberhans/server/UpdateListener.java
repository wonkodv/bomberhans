package org.hanstool.bomberhans.server;

import org.hanstool.bomberhans.server.cells.Cell;
import org.hanstool.bomberhans.server.cells.CellStartSlot;

public class UpdateListener
{
	private final Field		field;
	private final Server	server;
	
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
		return this.field.getHeight();
	}
	
	public CellStartSlot getSlotCell(byte slot)
	{
		return this.field.getSlotCell(slot);
	}
	
	public int getWidth()
	{
		return this.field.getWidth();
	}
	
	public int getWoodToSpecial()
	{
		return this.field.getWoodToSpecial();
	}
	
	public void replaceCell(Cell cell)
	{
		this.field.cells[cell.getX()][cell.getY()] = cell;
		sendToClient((byte) 10, new Object[] { Byte.valueOf(cell.getX()), Byte.valueOf(cell.getY()), Byte.valueOf(cell.getCellType()) });
	}
	
	public void sendToClient(byte networkCMD, Object[] params)
	{
		this.server.sendToClient(networkCMD, params);
	}
}
