package org.hanstool.bomberhans;

import java.io.IOException;

import javax.swing.JOptionPane;

public class GUI extends GUI_graphics
{
	private static final long	serialVersionUID	= -2960180555176841087L;
	Network						net;
	
	@Override
	protected void connect(String host, String name)
	{
		try
		{
			this.net = new Network(new GuiInteractor(), host, name);
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
		this.net = null;
		super.disconnect();
	}
	
	@Override
	protected void setPlayerState(byte state)
	{
		try
		{
			this.net.setPlayerState(state);
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(this, e);
		}
	}
	
	@Override
	protected void setReady(boolean ready)
	{
		throw new UnsupportedOperationException("not Implemented");
	}
	
	class GuiInteractor
	{
		GuiInteractor()
		{
		}
		
		public void disconnect()
		{
			GUI.this.disconnect();
		}
		
		public void playerDrop(byte slot)
		{
			GUI.this.playerDrop(slot);
		}
		
		public void playerJoined(int slot, String name)
		{
			GUI.this.playerJoined(slot, name);
		}
		
		public void playerScored(byte p1, byte p2)
		{
			GUI.this.playerScored(p1, p2);
		}
		
		public void reDraw()
		{
			GUI.this.reDraw();
		}
		
		public void updateCell(byte x, byte y, byte cellType)
		{
			GUI.this.updateCell(x, y, cellType);
		}
		
		public void updateField(byte[][] newField)
		{
			GUI.this.updateField(newField);
		}
		
		public void updatePlayer(byte slot, byte state, float x, float y, float speed, byte power, byte score)
		{
			GUI.this.updatePlayer(slot, state, x, y, speed, power, score);
		}
	}
}
