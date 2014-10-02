package org.hanstool.bomberhans.client;

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
import javax.swing.UIManager;

public abstract class GUI_graphics extends JFrame
{
	Properties					prop;
	private static final long	serialVersionUID	= 1L;
	private JButton				connectBTN;
	JCheckBox					readyCBX;
	JTextField					hostTFD;
	JTextField					nameTFD;
	final FieldPanel			field;
	JTextField					kl;
	private JTextArea			logTA;

	static
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception localException)
		{
		}
	}

	public GUI_graphics()
	{
		setLayout(null);

		propsLoad();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		nameTFD = new JTextField(prop.getProperty("name"));
		nameTFD.setBounds(20, 20, 200, 25);
		add(nameTFD);
		hostTFD = new JTextField(prop.getProperty("host"));
		hostTFD.setBounds(20, 70, 200, 25);
		add(hostTFD);

		connectBTN = new JButton("Connect");
		connectBTN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				GUI_graphics.this.connect(hostTFD.getText(), nameTFD.getText());
				prop.put("host", hostTFD.getText());
				prop.put("name", nameTFD.getText());
				GUI_graphics.this.propsSave();
				kl.requestFocus();
			}
		});
		connectBTN.setBounds(20, 120, 200, 25);
		add(connectBTN);

		readyCBX = new JCheckBox("Ready");
		readyCBX.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				GUI_graphics.this.setReady(readyCBX.isSelected());
			}
		});
		readyCBX.setBounds(20, 170, 200, 25);
		add(readyCBX);

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
					case 38:
					case 87:
						run_n = true;
						state = 2;
					break;
					case 40:
					case 83:
						run_s = true;
						state = 6;
					break;
					case 37:
					case 65:
						run_w = true;
						state = 8;
					break;
					case 39:
					case 68:
						run_e = true;
						state = 4;
					break;
					case 32:
						state = 13;
					break;
					default:
						return;
				}
				GUI_graphics.this.setPlayerState(state);
				e.setKeyCode(0);
				e.setKeyChar('\000');
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				switch(e.getKeyCode())
				{
					case 38:
					case 87:
						run_n = false;
					break;
					case 40:
					case 83:
						run_s = false;
					break;
					case 37:
					case 65:
						run_w = false;
					break;
					case 39:
					case 68:
						run_e = false;
					break;
					case 32:
					break;
					default:
						return;
				}
				byte state;
				if(run_n)
				{
					state = 2;
				}
				else
				{
					if(run_s)
					{
						state = 6;
					}
					else
					{
						if(run_w)
						{
							state = 8;
						}
						else
						{
							if(run_e)
							{
								state = 4;
							}
							else
							{
								state = 10;
							}
						}
					}
				}
				GUI_graphics.this.setPlayerState(state);

				e.setKeyCode(0);
				e.setKeyChar('\000');
			}

			@Override
			public void keyTyped(KeyEvent e)
			{
				e.setKeyCode(0);
				e.setKeyChar('\000');
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

		logTA = new JTextArea("log");
		logTA.setWrapStyleWord(true);
		logTA.setBounds(20, 220, 200, 400);
		add(logTA);

		pack();
		setVisible(true);
	}

	protected abstract void connect(String paramString1, String paramString2);

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

	private void propsLoad()
	{
		prop = new Properties();
		try
		{
			prop.load(new FileReader(new File("BomberHans.cfg")));
		}
		catch(Exception e1)
		{
			prop.put("host", "enter Host");
			prop.put("name", "enter UserName");
		}
	}

	void propsSave()
	{
		try
		{
			prop.store(new FileWriter(new File("BomberHans.cfg")), "saves BomberhansConfigs");
		}
		catch(IOException localIOException)
		{
		}
	}

	public void reDraw()
	{
		field.repaint();
	}

	protected abstract void setPlayerState(byte paramByte);

	protected abstract void setReady(boolean paramBoolean);

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

	public static class FieldPanel extends JPanel
	{
		private static final long	serialVersionUID	= -5459713617157096202L;
		public ImageBuffer			imgBuffer;
		private boolean				focused;
		private byte[][]			cellTypes;
		final List<GPlayer>			players;

		public FieldPanel()
		{
			players = new ArrayList<GPlayer>(8);
			for(byte i = 0; i < 8; i = (byte) (i + 1))
			{
				players.add(new GPlayer(i));
			}

			try
			{
				imgBuffer = new ImageBuffer();
			}
			catch(IOException e)
			{
				throw new Error(e);
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

		public synchronized byte getCellType(byte x, byte y)
		{
			return cellTypes[x][y];
		}

		public synchronized byte getSizeX()
		{
			return (byte) cellTypes.length;
		}

		public synchronized byte getSizeY()
		{
			return (byte) cellTypes[0].length;
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
			byte[] col;
			for(int x = 0; x < cellTypes.length; x++ )
			{
				col = cellTypes[x];
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

					g.drawImage(img, Math.round(5.0F + (x - 0.5F) * 25.0F), Math.round(5.0F + (y - 0.5F) * 25.0F), 25, 25, null);
				}
			}
		}

		synchronized void playerDrop(byte slot)
		{
			players.get(slot).setJoined(false);
		}

		public synchronized void playerJoined(int slot, String name)
		{
			players.get(slot).setName(name);
			players.get(slot).setJoined(true);
		}

		public synchronized void setCellTypes(byte[][] cellTypes)
		{
			this.cellTypes = cellTypes;
			if(cellTypes != null)
			{
				setBounds(getX(), getY(), 10 + 25 * cellTypes.length, 10 + 25 * cellTypes[0].length);
			}
		}

		public void setFocused(boolean focused)
		{
			this.focused = focused;
			repaint();
		}

		public synchronized void updateCell(byte x, byte y, byte cellType)
		{
			cellTypes[x][y] = cellType;
		}

		synchronized void updatePlayer(byte slot, byte state, float x, float y, float speed, byte power, byte score)
		{
			players.get(slot).updatePlayer(state, x, y, speed, power, score);
		}
	}
	
	public void setServerAddress(String string)
	{
		hostTFD.setText(string);
	}
}
