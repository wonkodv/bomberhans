package org.hanstool.bomberhans.shared;

import org.hanstool.bomberhans.shared.Const.GameConsts;
import org.hanstool.bomberhans.shared.Const.PlayerState;


public class Player
{
	

	private final byte	slot;
	private String		name;
	private float		x;
	private float		y;
	private float		speed;
	private byte		power;
	private byte		state;
	private byte		score;
	
	public Player(byte slot, String name, byte x, byte y)
	{
		this.slot = slot;
		this.setName(name);
		this.x = x + 0.5f;
		this.y = y + 0.5f;
		setState(PlayerState.INVALID);
		speed = GameConsts.PLAYER_BASE_SPEED;
		power = GameConsts.PLAYER_BASE_POWER;
		state = PlayerState.IDLE;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return the power
	 */
	public byte getPower()
	{
		return power;
	}
	
	public byte getScore()
	{
		return score;
	}
	
	public byte getSlot()
	{
		return slot;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public byte getState()
	{
		return state;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * @param power
	 *            the power to set
	 */
	public void setPower(byte power)
	{
		this.power = power;
	}
	
	public void setScore(byte score)
	{
		this.score = score;
	}
	
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
	
	public void setState(byte state)
	{
		this.state = state;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
}
