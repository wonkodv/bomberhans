/**
 * 
 */
package org.hanstool.bomberhans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.hanstool.bomberhans.shared.Const.GameConsts;
import org.hanstool.bomberhans.shared.Const.PlayerState;

/**
 * @author Wonko
 */
public abstract class GUI_graphics extends JFrame
{
	private class FieldPanel extends JPanel
	{
		private boolean		focused;
		
		private byte[][]	cellTypes;
		
		final List<GPlayer>	players;
		
		public FieldPanel()
		{
			this.players = new ArrayList<GPlayer>(GameConsts.MAX_PLAYERS);
			for(byte i = 0; i < GameConsts.MAX_PLAYERS; i++ )
			{
				players.add(new GPlayer(i));
			}
			

		}
		
		public void disconnect()
		{
			for(GPlayer p : players)
			{
				p.setJoined(false);
			}
			setCellTypes(null);
		}
		
		@Override
		public synchronized void paint(Graphics g)
		{
			
			super.paint(g);
			if(focused)
			{
				g.setColor(Color.DARK_GRAY);
			}
			else
			{
				g.setColor(Color.LIGHT_GRAY);
			}
			g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
			
			if(cellTypes == null)
			{
				return;
			}
			
			for(int x = 0; x < cellTypes.length; x++ )
			{
				byte[] col = cellTypes[x];
				int left = 5 + x * 25;
				for(int y = 0; y < col.length; y++ )
				{
					int top = 5 + y * 25;
					
					BufferedImage img = imgBuffer.getcellImage(col[y]);
					g.drawImage(img, left, top, 25, 25, null);
				}
			}
			
			for(GPlayer p : players)
			{
				if(p.getJoined())
				{
					float x = p.getX();
					float y = p.getY();
					byte slot = p.getSlot();
					byte state = p.getState();
					
					BufferedImage img = imgBuffer.getHansImage(state, slot);
					
					g.drawImage(img, Math.round(5 + (x - .5f) * 25), Math.round(5 + (y - .5f) * 25), 25, 25, null);
					
				}
			}
		}
		
		synchronized void playerDrop(byte slot)
		{
			players.get(slot).setJoined(false);
		}
		
		
		/**
		 * @param slot
		 * @param name
		 */
		synchronized public void playerJoined(int slot, String name)
		{
			players.get(slot).setName(name);
			players.get(slot).setJoined(true);
		}
		
		synchronized void setCellTypes(byte[][] cellTypes)
		{
			this.cellTypes = cellTypes;
			setBounds(getX(), getY(), 10 + 25 * cellTypes.length, 10 + 25 * cellTypes[0].length);
		}
		
		
		public void setFocused(boolean focused)
		{
			this.focused = focused;
			repaint();
		}
		
		synchronized void updateCell(byte x, byte y, byte cellType)
		{
			cellTypes[x][y] = cellType;
			
		}
		
		synchronized void updatePlayer(byte slot, byte state, float x, float y, float speed, byte power, byte score)
		{
			players.get(slot).updatePlayer(state, x, y, speed, power, score);
		}
		
	}
	
	Properties					prop;
	
	private static final long	serialVersionUID	= 1L;
	
	ImageBuffer					imgBuffer;
	
	private JButton				connectBTN;
	
	JCheckBox					readyCBX;
	
	JTextField					hostTFD;
	
	JTextField					nameTFD;
	
	private final FieldPanel	field;
	
	JTextField					kl;
	
	private JTextArea			logTA;
	
	public GUI_graphics()
	{
		setLayout(null);
		
		propsLoad();
		
		{
			try
			{
				imgBuffer = new ImageBuffer();
			}
			catch(IOException e)
			{
				throw new Error(e);
			}
		}
		
		{
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e)
				{
					System.exit(0);
				}
			});
		}
		
		{
			nameTFD = new JTextField(prop.getProperty("name"));
			nameTFD.setBounds(20, 20, 200, 25);
			add(nameTFD);
			hostTFD = new JTextField(prop.getProperty("host"));
			hostTFD.setBounds(20, 70, 200, 25);
			add(hostTFD);
		}
		{
			connectBTN = new JButton("Connect");
			connectBTN.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					connect(hostTFD.getText(), nameTFD.getText());
					prop.put("host", hostTFD.getText());
					prop.put("name", nameTFD.getText());
					propsSave();
					kl.requestFocus();
				}
			});
			
			connectBTN.setBounds(20, 120, 200, 25);
			add(connectBTN);
		}
		
		{
			readyCBX = new JCheckBox("Ready");
			readyCBX.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					setReady(readyCBX.isSelected());
				}
			});
			
			readyCBX.setBounds(20, 170, 200, 25);
			add(readyCBX);
		}
		
		{
			field = new FieldPanel();
			field.setBounds(250, 30, 300, 300);
			field.setFocusable(false);
			field.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e)
				{
					kl.requestFocus();
					
				}
			});
			
			kl = new JTextField("input here");
			kl.setBounds( -20, 210, 10, 25);
			add(kl);
			kl.addKeyListener(new KeyListener() {
				private boolean	run_n	= false;
				private boolean	run_e	= false;
				private boolean	run_s	= false;
				private boolean	run_w	= false;
				
				@Override
				public void keyPressed(KeyEvent e)
				{
					byte state;
					

					switch(e.getKeyCode())
					{
						case KeyEvent.VK_UP:
						case KeyEvent.VK_W:
							run_n = true;
							state = PlayerState.RUNNING_N;
						break;
						case KeyEvent.VK_DOWN:
						case KeyEvent.VK_S:
							run_s = true;
							state = PlayerState.RUNNING_S;
						break;
						case KeyEvent.VK_LEFT:
						case KeyEvent.VK_A:
							run_w = true;
							state = PlayerState.RUNNING_W;
						break;
						case KeyEvent.VK_RIGHT:
						case KeyEvent.VK_D:
							run_e = true;
							state = PlayerState.RUNNING_E;
						break;
						case KeyEvent.VK_SPACE:
							state = PlayerState.PLACING_BOMB;
						break;
						default:
							return;
					}
					setPlayerState(state);
					e.setKeyCode(0);
					e.setKeyChar('\u0000');
				}
				
				@Override
				public void keyReleased(KeyEvent e)
				{
					switch(e.getKeyCode())
					{
						case KeyEvent.VK_UP:
						case KeyEvent.VK_W:
							run_n = false;
						break;
						case KeyEvent.VK_DOWN:
						case KeyEvent.VK_S:
							run_s = false;
						break;
						case KeyEvent.VK_LEFT:
						case KeyEvent.VK_A:
							run_w = false;
						break;
						case KeyEvent.VK_RIGHT:
						case KeyEvent.VK_D:
							run_e = false;
						break;
						case KeyEvent.VK_SPACE:

						break;
						default:
							return;
					}
					byte state;
					if(run_n)
					{
						state = PlayerState.RUNNING_N;
					}
					else if(run_s)
					{
						state = PlayerState.RUNNING_S;
					}
					else if(run_w)
					{
						state = PlayerState.RUNNING_W;
					}
					else if(run_e)
					{
						state = PlayerState.RUNNING_E;
					}
					else
					{
						state = PlayerState.IDLE;
					}
					setPlayerState(state);
					
					e.setKeyCode(0);
					e.setKeyChar('\u0000');
				}
				
				@Override
				public void keyTyped(KeyEvent e)
				{
					e.setKeyCode(0);
					e.setKeyChar('\u0000');
				}
			});
			kl.addFocusListener(new FocusListener() {
				
				@Override
				public void focusGained(FocusEvent e)
				{
					
					field.setFocused(true);
				}
				
				@Override
				public void focusLost(FocusEvent e)
				{
					field.setFocused(false);
				}
			});
			
			add(field);
		}
		
		{
			logTA = new JTextArea("log");
			logTA.setWrapStyleWord(true);
			logTA.setBounds(20, 220, 200, 400);
			add(logTA);
			
		}
		
		pack();
		setVisible(true);
	}
	
	protected abstract void connect(String host, String name);
	
	public void disconnect()
	{
		field.disconnect();
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(1400, 800);
	}
	
	public void playerDrop(byte slot)
	{
		field.playerDrop(slot);
		
	}
	
	public void playerJoined(int slot, String name)
	{
		field.playerJoined(slot, name);
		logTA.insert(name + " joined\n", 0);
		
	}
	
	public void playerScored(byte p1, byte p2)
	{
		
		if(p1 == p2)
		{
			logTA.insert(field.players.get(p1).getName() + " noob!\n", 0);
		}
		else
		{
			logTA.insert(field.players.get(p1).getName() + " > " + field.players.get(p2).getName() + "\n", 0);
		}
		
	}
	
	//
	// @Override
	// public void paint(Graphics g)
	// {
	// super.paint(g);
	// /*
	// * for(byte playerState = 0; playerState < PlayerState.Names.length;
	// * playerState++ ) { for(byte slot = 0; slot < GameConsts.MAX_PLAYERS;
	// * slot++ ) { BufferedImage img = imgBuffer.getHansImage(playerState,
	// * slot); g.drawImage(img, 30 + 30 * playerState, 50 + 30 * slot, 25,
	// * 25, null); } }
	// */
	//
	//
	// }
	

	private void propsLoad()
	{
		prop = new Properties();
		try
		{
			prop.load(new FileReader(new File("config.cfg")));
		}
		catch(Exception e1)
		{
			prop.put("host", "enter Host");
			prop.put("name", "enter UserName");
		}
	}
	
	private void propsSave()
	{
		try
		{
			prop.store(new FileWriter(new File("config.cfg")), "saves BomberhansConfigs");
		}
		catch(IOException e)
		{
		}
	}
	
	public void reDraw()
	{
		field.repaint();
		
	}
	
	/**
	 * @param state
	 */
	protected abstract void setPlayerState(byte state);
	
	protected abstract void setReady(boolean ready);
	
	public void updateCell(byte x, byte y, byte cellType)
	{
		
		field.updateCell(x, y, cellType);
		
	}
	
	public void updateField(byte[][] newField)
	{
		field.setCellTypes(newField);
		
	}
	
	public void updatePlayer(byte slot, byte state, float x, float y, float speed, byte power, byte score)
	{
		field.updatePlayer(slot, state, x, y, speed, power, score);
		
	}
}
