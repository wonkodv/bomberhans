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
		return field.cells[x][y];
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
		field.cells[cell.getX()][cell.getY()] = cell;
		sendToClient((byte) 10, Byte.valueOf(cell.getX()), Byte.valueOf(cell.getY()), Byte.valueOf(cell.getCellType()));
	}

	public void sendToClient(byte networkCMD, Object... params)
	{
		server.sendToClient(networkCMD, params);
	}
}
