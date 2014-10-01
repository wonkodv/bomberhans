package org.hanstool.bomberhans.server;

import static org.hanstool.bomberhans.shared.Const.CellTypes.*;

import java.util.LinkedList;
import java.util.List;

import org.hanstool.bomberhans.server.cells.Cell;
import org.hanstool.bomberhans.server.cells.CellStartSlot;
import org.hanstool.bomberhans.shared.Const.GameConsts;
import org.hanstool.bomberhans.shared.Const.GameConsts.StandardField;

public class Field
{
	public static Field generateNewField(Server server)
	{
		// UNCHECKED
		int size = GameConsts.StandardField.SIZE;
		byte[][] bytes = new byte[size][size];
		for(int y = 0; y < size; y++ )
		{
			for(int x = 0; x < size; x++ )
			{
				byte b;
				
				if(x == 0 || x == size - 1 || y == 0 || y == size - 1)
				{
					b = WALL;
				}
				else if((x == 1 || x == (size - 2)) && (y == 1 || y == (size - 2)))
				{
					b = START_POINT;
				}
				else if((x == 2 || x == size - 3) && (y == 1 || y == size - 2))
				{
					b = CLEAR;
				}
				else if((x == 1 || x == size - 2) && (y == 2 || y == size - 3))
				{
					b = CLEAR;
				}
				// else if(x > (size / 2 - 5) && x < (size / 2 + 5) && y > (size
				// / 2 - 5) && y < (size / 2 + 5))
				// {
				//
				// b = (byte) (PU_BOMB + Const.GameConsts.rand.nextInt(5));
				// }
				else if(x % 2 == 0 && y % 2 == 0)
				{
					b = WALL;
				}
				else if(GameConsts.rand.nextInt(100) < GameConsts.StandardField.WOOD)
				{
					b = WOOD;
				}
				else
				{
					b = CLEAR;
				}
				

				bytes[x][y] = b;
			}
		}
		return new Field(server, bytes, StandardField.WOOD_TO_SPECIAL);
	}
	
	private List<CellStartSlot>	slots;
	
	UpdateListener				ful;
	Cell[][]					cells;
	
	private final int			width, height, woodToSpecial;
	
	
	public Field(Server server, byte[][] field, int woodToSpecial)
	{
		ful = new UpdateListener(server, this);
		width = field.length;
		height = field[0].length;
		cells = new Cell[width][height];
		slots = new LinkedList<CellStartSlot>();
		for(byte x = 0; x < width; x++ )
		{
			for(byte y = 0; y < height; y++ )
			{
				Cell c = Cell.createCell(field[x][y], x, y, null, null);
				cells[x][y] = c;
				if(c instanceof CellStartSlot)
				{
					slots.add((CellStartSlot) c);
				}
			}
		}
		
		this.woodToSpecial = woodToSpecial;
		

	}
	
	public byte[][] getBytes()
	{
		throw new UnsupportedOperationException();
		// return cells;
	}
	
	public Cell getCell(byte x, byte y)
	{
		return cells[x][y];
	}
	
	public byte getCellType(int x, int y)
	{
		return cells[x][y].getCellType();
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getMaxPlayers()
	{
		return slots.size();
	}
	
	public CellStartSlot getSlotCell(byte slot)
	{
		return slots.get(slot);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getWoodToSpecial()
	{
		return woodToSpecial;
	}
	
	/**
	 * @param timePassed
	 */
	public void update(long timePassed)
	{
		// if(false)// DEBUG
		{
			for(Cell[] collumn : cells)
			{
				for(Cell c : collumn)
				{
					c.update(timePassed, ful);
				}
			}
		}
		
	}
}
