package org.hanstool.bomberhans.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.hanstool.bomberhans.server.cells.Cell;
import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.Const.CellTypes;
import org.hanstool.bomberhans.shared.Const.GameConsts;
import org.hanstool.bomberhans.shared.Const.PlayerState;
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
			dis = new DataInputStream(socket.getInputStream());
		}
		catch(IOException e)
		{
			throw new Error(e);
		}
		isNew = true;
		max_bombs = GameConsts.PLAYER_BASE_BOMBS;
		currentBombs = 0;
	}
	
	/**
	 * @return the currentBombs
	 */
	public int getCurrentBombs()
	{
		return currentBombs;
	}
	
	/**
	 * @return the didChange
	 */
	public boolean getDidChange()
	{
		return didChange;
	}
	
	public DataInputStream getDis()
	{
		return dis;
	}
	
	public boolean getIsNew()
	{
		return isNew;
	}
	
	/**
	 * @return the max_bombs
	 */
	public byte getMax_bombs()
	{
		return max_bombs;
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	/**
	 * @param ful
	 */
	public void incScore(UpdateListener ful)
	{
		setScore((byte) (getScore() + 1));
		didChange = true;
	}
	
	public void powerUp(byte cellType)
	{
		switch(cellType)
		{
			case CellTypes.PU_POWER:
				setPower((byte) (getPower() + Math.ceil(Const.GameConsts.PU_GAIN / getPower())));
			break;
			case CellTypes.PU_SPEED:
				setSpeed((getSpeed() + Const.GameConsts.PU_GAIN / getSpeed()));
			break;
			
			default:
				return;
		}
		didChange = true;
	}
	
	
	public void respawn(UpdateListener ul)
	{
		Cell c = ul.getSlotCell(getSlot());
		setX(c.getX() + .5f);
		setY(c.getY() + .5f);
		setState(PlayerState.RESPAWNED);
		didChange = true;
		
		setPower((byte) Math.max(GameConsts.PLAYER_BASE_POWER, getPower() * GameConsts.PU_RDUCE));
		
		setSpeed(Math.max(GameConsts.PLAYER_BASE_SPEED, getSpeed() * GameConsts.PU_RDUCE));
		setMax_bombs((byte) (Math.max(GameConsts.PLAYER_BASE_SPEED, getMax_bombs() * GameConsts.PU_RDUCE)));
	}
	
	/**
	 * @param currentBombs
	 *            the currentBombs to set
	 */
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
		/* getX() points to the Center of Hans so floor works */
		byte x = (byte) Math.floor(getX());
		byte y = (byte) Math.floor(getY());
		
		Cell currentlyOn = ful.getCell(x, y);
		
		byte dX = 0, dY = 0;
		
		switch(getState())
		{
			case PlayerState.RUNNING_N:
			case PlayerState.RUNNING_N2:
				dY = -1;
			break;
			case PlayerState.RUNNING_E:
			case PlayerState.RUNNING_E2:
				dX = +1;
			break;
			case PlayerState.RUNNING_S:
			case PlayerState.RUNNING_S2:
				dY = +1;
			break;
			case PlayerState.RUNNING_W:
			case PlayerState.RUNNING_W2:
				dX = -1;
			break;
			case PlayerState.IDLE:
			case PlayerState.IDLE2:

			break;
			case PlayerState.PLACING_BOMB:

				currentlyOn.handleWalkOn(this, ful);
				didChange = true;
				return;
			default:
				return;
		}
		
		if((getState() | 1) != PlayerState.IDLE2)
		{
			final float isle_width = 0.1f;
			final float proximity = 0.5f - isle_width;
			

			Cell nextCellA = ful.getCell((byte) Math.floor(getX() + dX * proximity + dY * isle_width), (byte) Math.floor(getY() + dY * proximity + dX * isle_width));
			Cell nextCellB = ful.getCell((byte) Math.floor(getX() + dX * proximity - dY * isle_width), (byte) Math.floor(getY() + dY * proximity - dX * isle_width));
			

			if(nextCellA.canWalkOn() && nextCellB.canWalkOn())
			{
				float movedist = getSpeed() * timeElapsed / 1000f;
				
				if(movedist > 0.5f)
				{
					movedist = 0.5f;
				}
				if(dX == 0)
				{
					setY(getY() + dY * movedist);
					
					didChange = true;
				}
				else
				{
					setX(getX() + dX * movedist);
					
					didChange = true;
				}
			}
		}
		
		animationTimer += timeElapsed;
		if(animationTimer > GameConsts.ANIMATION_TIME)
		{
			setState((byte) (getState() ^ 1));// hampeln
			animationTimer = 0;
			didChange = true;
		}
		
		currentlyOn.handleWalkOn(this, ful); // die in fire, pick up Specials
	}
}
