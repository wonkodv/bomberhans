/**
 * 
 */
package org.hanstool.bomberhans;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * @author Wonko
 */
public class GUI extends GUI_graphics
{
	class GuiInteractor
	{
		
		/**
		 * 
		 */
		public void disconnect()
		{
			GUI.this.disconnect();
			
		}
		
		/**
		 * @param slot
		 */
		public void playerDrop(byte slot)
		{
			GUI.this.playerDrop(slot);
			
		}
		
		/**
		 * @param slot
		 * @param name
		 */
		public void playerJoined(int slot, String name)
		{
			GUI.this.playerJoined(slot, name);
			
		}
		
		public void playerScored(byte p1, byte p2)
		{
			GUI.this.playerScored(p1, p2);
		}
		
		/**
		 * 
		 */
		public void reDraw()
		{
			GUI.this.reDraw();
			
		}
		
		public void updateCell(byte x, byte y, byte cellType)
		{
			
			GUI.this.updateCell(x, y, cellType);
			
		}
		
		/**
		 * @param newField
		 */
		public void updateField(byte[][] newField)
		{
			GUI.this.updateField(newField);
		}
		
		/**
		 * @param slot
		 * @param state
		 * @param x
		 * @param y
		 * @param speed
		 * @param power
		 * @param score
		 */
		public void updatePlayer(byte slot, byte state, float x, float y, float speed, byte power, byte score)
		{
			GUI.this.updatePlayer(slot, state, x, y, speed, power, score);
			
		}
	}
	
	static
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex)
		{
			;
		}
	}
	
	Network	net;
	
	@Override
	protected void connect(String host, String name)
	{
		
		try
		{
			net = new Network(new GuiInteractor(), host, name);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e);
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void disconnect()
	{
		net = null;
		super.disconnect();
	}
	
	@Override
	protected void setPlayerState(byte state)
	{
		try
		{
			net.setPlayerState(state);
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(this, e);
		}
	}
	
	

	@Override
	protected void setReady(boolean ready)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not Implemented");
		
	}
	


}
