package org.hanstool.bomberhans.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.hanstool.bomberhans.server.cells.Cell;
import org.hanstool.bomberhans.shared.Player;

public class SPlayer extends Player
{
	private Socket			socket;
	int						lenghtOfNextPartialFrame	= 0;
	private DataInputStream	dis;
	private boolean			isNew;
	private boolean			didChange;
	private int				currentBombs;
	private byte			max_bombs;
	private int				animationTimer				= 0;
	
	SPlayer(byte slot, String name, Socket socket, byte x, byte y)
	{
		super(slot, name, x, y);
		this.socket = socket;
		try
		{
			this.dis = new DataInputStream(socket.getInputStream());
		}
		catch(IOException e)
		{
			throw new Error(e);
		}
		this.isNew = true;
		this.max_bombs = 1;
		this.currentBombs = 0;
	}
	
	public int getCurrentBombs()
	{
		return this.currentBombs;
	}
	
	public boolean getDidChange()
	{
		return this.didChange;
	}
	
	public DataInputStream getDis()
	{
		return this.dis;
	}
	
	public boolean getIsNew()
	{
		return this.isNew;
	}
	
	public byte getMax_bombs()
	{
		return this.max_bombs;
	}
	
	public Socket getSocket()
	{
		return this.socket;
	}
	
	public void incScore(UpdateListener ful)
	{
		setScore((byte) (getScore() + 1));
		this.didChange = true;
	}
	
	public void powerUp(byte cellType)
	{
		switch(cellType)
		{
			case 17:
				setPower((byte) (int) (getPower() + Math.ceil(0.5F / getPower())));
			break;
			case 18:
				setSpeed(getSpeed() + 0.5F / getSpeed());
			break;
			case 16:
				this.max_bombs = (byte) (this.max_bombs + 1);
			break;
			default:
				return;
		}
		this.didChange = true;
	}
	
	public void respawn(UpdateListener ul)
	{
		Cell c = ul.getSlotCell(getSlot());
		setX(c.getX() + 0.5F);
		setY(c.getY() + 0.5F);
		setState((byte) 1);
		this.didChange = true;
		
		setPower((byte) (int) Math.max(2.0F, getPower() * 0.8F));
		
		setSpeed(Math.max(1.3F, getSpeed() * 0.8F));
		setMax_bombs((byte) (int) Math.max(1.0F, getMax_bombs() * 0.8F));
	}
	
	public void setCurrentBombs(int currentBombs)
	{
		this.currentBombs = currentBombs;
	}
	
	public void setDidChange(boolean didChange)
	{
		this.didChange = didChange;
	}
	
	public void setIsNew(boolean isNew)
	{
		this.isNew = isNew;
	}
	
	public void setMax_bombs(byte max_bombs)
	{
		this.max_bombs = max_bombs;
	}
	
	public void update(long timeElapsed, UpdateListener ful)
	{
		byte x = (byte) (int) Math.floor(getX());
		byte y = (byte) (int) Math.floor(getY());
		
		Cell currentlyOn = ful.getCell(x, y);
		
		byte dX = 0;
		byte dY = 0;
		
		switch(getState())
		{
			case 2:
			case 3:
				dY = -1;
			break;
			case 4:
			case 5:
				dX = 1;
			break;
			case 6:
			case 7:
				dY = 1;
			break;
			case 8:
			case 9:
				dX = -1;
			break;
			case 10:
			case 11:
			break;
			case 13:
				currentlyOn.handleWalkOn(this, ful);
				this.didChange = true;
				return;
			case 12:
			default:
				return;
		}
		
		if((getState() | 0x1) != 11)
		{
			float isle_width = 0.1F;
			float proximity = 0.4F;
			
			Cell nextCellA = ful.getCell((byte) (int) Math.floor(getX() + dX * 0.4F + dY * 0.1F), (byte) (int) Math.floor(getY() + dY * 0.4F + dX * 0.1F));
			Cell nextCellB = ful.getCell((byte) (int) Math.floor(getX() + dX * 0.4F - dY * 0.1F), (byte) (int) Math.floor(getY() + dY * 0.4F - dX * 0.1F));
			
			if(nextCellA.canWalkOn() && nextCellB.canWalkOn())
			{
				float movedist = getSpeed() * timeElapsed / 1000.0F;
				
				if(movedist > 0.5F)
				{
					movedist = 0.5F;
				}
				if(dX == 0)
				{
					setY(getY() + dY * movedist);
					
					this.didChange = true;
				}
				else
				{
					setX(getX() + dX * movedist);
					
					this.didChange = true;
				}
			}
		}
		
		this.animationTimer = (int) (this.animationTimer + timeElapsed);
		if(this.animationTimer > 200)
		{
			setState((byte) (getState() ^ 0x1));
			this.animationTimer = 0;
			this.didChange = true;
		}
		
		currentlyOn.handleWalkOn(this, ful);
	}
}
