/**
 * 
 */
package org.hanstool.bomberhans;

import org.hanstool.bomberhans.shared.Player;

/**
 * @author Wonko
 */
public class GPlayer extends Player
{
	
	boolean	joined;
	
	public GPlayer(byte slot)
	{
		super(slot, "", (byte) 0, (byte) 0);
		joined = false;
	}
	
	/**
	 * @return the joined
	 */
	public boolean getJoined()
	{
		return joined;
	}
	
	/**
	 * @param joined
	 *            the joined to set
	 */
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
