package org.hanstool.bomberhans.shared;

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
		setName(name);
		this.x = x + 0.5F;
		this.y = y + 0.5F;
		setState((byte) 0);
		this.speed = 1.3F;
		this.power = 2;
		this.state = 10;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public byte getPower()
	{
		return this.power;
	}
	
	public byte getScore()
	{
		return this.score;
	}
	
	public byte getSlot()
	{
		return this.slot;
	}
	
	public float getSpeed()
	{
		return this.speed;
	}
	
	public byte getState()
	{
		return this.state;
	}
	
	public float getX()
	{
		return this.x;
	}
	
	public float getY()
	{
		return this.y;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
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
