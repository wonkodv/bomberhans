package org.hanstool.bomberhans.client;

import org.hanstool.bomberhans.shared.Player;

public class GPlayer extends Player
{
	boolean	joined;
	
	public GPlayer(byte slot)
	{
		super(slot, "", (byte) 0, (byte) 0);
		this.joined = false;
	}
	
	public boolean getJoined()
	{
		return this.joined;
	}
	
	public void setJoined(boolean joined)
	{
		this.joined = joined;
	}
	
	void updatePlayer(byte state, float x, float y, float speed, byte power, byte score)
	{
		setState(state);
		setX(x);
		setY(y);
		setSpeed(speed);
		setPower(power);
		setScore(score);
	}
}
