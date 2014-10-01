package org.hanstool.bomberhans.server;

import java.util.LinkedList;
import java.util.List;

import org.hanstool.bomberhans.mapeditor.MapLoader;
import org.hanstool.bomberhans.server.cells.Cell;
import org.hanstool.bomberhans.server.cells.CellStartSlot;
import org.hanstool.bomberhans.shared.Const;

public class Field
{
	private List<CellStartSlot>	slots;
	UpdateListener				ful;
	Cell[][]					cells;
	private final int			width;
	private final int			height;
	private final int			woodToSpecial;
	
	public static byte[][] generateNewField()
	{
		int size = 11;
		byte[][] bytes = new byte[size][size];
		for(int y = 0; y < size; y++ )
		{
			for(int x = 0; x < size; x++ )
			{
				byte b;
				if(x == 0 || x == size - 1 || y == 0 || y == size - 1)
				{
					b = 2;
				}
				else if((x == 1 || x == size - 2) && (y == 1 || y == size - 2))
				{
					b = 21;
				}
				else if((x == 2 || x == size - 3) && (y == 1 || y == size - 2))
				{
					b = 1;
				}
				else if((x == 1 || x == size - 2) && (y == 2 || y == size - 3))
				{
					b = 1;
				}
				else
				{
					if(x % 2 == 0 && y % 2 == 0)
					{
						b = 2;
					}
					else if(Const.GameConsts.rand.nextInt(100) < 60)
					{
						b = 3;
					}
					else
					{
						b = 1;
					}

				}

				bytes[x][y] = b;
			}
		}
		return bytes;
	}
	
	public Field(Server server, byte[][] field, int woodToSpecial)
	{
		this.ful = new UpdateListener(server, this);
		this.width = field.length;
		this.height = field[0].length;
		this.cells = new Cell[this.width][this.height];
		this.slots = new LinkedList();
		for(byte x = 0; x < this.width; x = (byte) (x + 1))
		{
			for(byte y = 0; y < this.height; y = (byte) (y + 1))
			{
				Cell c = Cell.createCell(field[x][y], x, y, null, null);
				this.cells[x][y] = c;
				if(c instanceof CellStartSlot)
				{
					this.slots.add((CellStartSlot) c);
				}
			}
		}
		
		this.woodToSpecial = woodToSpecial;
	}
	
	public Field(Server server, MapLoader ml)
	{
		this(server, ml.getCellTypesWoodReplaced(), ml.getSpecialP());
	}
	
	public byte[][] getBytes()
	{
		throw new UnsupportedOperationException();
	}
	
	public Cell getCell(byte x, byte y)
	{
		return this.cells[x][y];
	}
	
	public byte getCellType(int x, int y)
	{
		return this.cells[x][y].getCellType();
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public int getMaxPlayers()
	{
		return this.slots.size();
	}
	
	public CellStartSlot getSlotCell(byte slot)
	{
		return this.slots.get(slot);
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	public int getWoodToSpecial()
	{
		return this.woodToSpecial;
	}
	
	public void update(long timePassed)
	{
		for(Cell[] collumn : this.cells)
		{
			for(Cell c : collumn)
			{
				c.update(timePassed, this.ful);
			}
		}
	}
}
