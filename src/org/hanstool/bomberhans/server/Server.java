package org.hanstool.bomberhans.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.hanstool.bomberhans.mapeditor.MapLoader;
import org.hanstool.bomberhans.server.cells.Cell;
import org.hanstool.bomberhans.server.cells.CellStartSlot;
import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.NetworkStreamAdapter;

public class Server implements Runnable
{
	private LinkedList<SPlayer>		players;
	private Field					field;
	private NetworkStreamAdapter	nsa;

	class ClientAccepter implements Runnable
	{
		@Override
		public void run()
		{
			try (ServerSocket serverSocket = new ServerSocket(5636))
			{
				while(true)
				{
					Socket s = serverSocket.accept();
					AddPlayer(s);
				}
			}
			catch(IOException e)
			{
				// TODO queue Sever Stop
				System.exit(5);
			}
		}
	}
	
	public Server()
	{
		this(null);
	}
	
	public Server(String mapFile)
	{
		nsa = new NetworkStreamAdapter();
		players = new LinkedList<SPlayer>();

		field = null;
		if(mapFile != null)
		{
			MapLoader ml;
			try
			{
				ml = new MapLoader(new File(mapFile));
				field = new Field(this, ml);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

		}
		if(field == null)
		{
			field = new Field(this, Field.generateNewField(), 70);
		}
		
		Thread serverThread = new Thread(this, "Server Thread");
		
		Thread acceptClientsThread = new Thread(new ClientAccepter(), "ClientAcceptThread");
		
		serverThread.start();
		acceptClientsThread.start();
	}

	void AddPlayer(Socket socket)
	{
		if(players.size() >= field.getMaxPlayers())
		{
			try
			{
				socket.getOutputStream().write(6);
				socket.close();
			}
			catch(IOException localIOException)
			{
			}
		}
		byte slot;
		CellStartSlot cSlot = null;
		for(slot = 0; slot <= field.getMaxPlayers() - 1; slot = (byte) (slot + 1))
		{
			cSlot = field.ful.getSlotCell(slot);
			if(cSlot.getOwner() == null)
			{
				break;
			}
		}
		if(cSlot == null)
		{
			throw new Error("players.size = " + players.size() + " field.getMaxPlayers() = " + field.getMaxPlayers() + " no free slot was found :(");
		}

		SPlayer p = new SPlayer(slot, "player_" + slot, socket, cSlot.getX(), cSlot.getY());
		cSlot.setOwner(p);
		synchronized(players)
		{
			players.add(p);
		}
	}

	void netHandleInput(SPlayer p, UpdateListener ful) throws IOException
	{
		while(true)
		{
			DataInputStream dis = p.getDis();
			if(dis.available() < 2)
			{
				return;
			}
			int len;
			if(p.lenghtOfNextPartialFrame != 0)
			{
				if(dis.available() < p.lenghtOfNextPartialFrame)
				{
					return;
				}
				len = p.lenghtOfNextPartialFrame;
				p.lenghtOfNextPartialFrame = 0;
			}
			else
			{
				len = dis.readShort();
				if(dis.available() < len)
				{
					p.lenghtOfNextPartialFrame = len;
					return;
				}

			}

			int command = dis.read();
			len-- ;

			if(Const.logging)
			{
				System.out.print("rcv: " + NetworkStreamAdapter.NAMES[command]);
			}

			switch(command)
			{
				case 0:
					throw new Error("What the fuck? Network Command Invalid O_O");
				case 1:
					byte[] buffer = new byte[len];
					dis.read(buffer, 0, len);
					len -= buffer.length;
					p.setName(new String(buffer, "utf-8"));

					for(SPlayer pl : players)
					{
						sendToClient((byte) 8, new Object[] { Byte.valueOf(pl.getSlot()), pl.getName() });
					}
				
				break;
				case 2:
					byte state = dis.readByte();
					len-- ;

					if(state == 13)
					{
						if(p.getCurrentBombs() < p.getMax_bombs())
						{
							if(p.getState() != state)
							{
								float dX = 0.0F;
								float dY = 0.0F;
								switch(p.getState() | 0x1)
								{
									case 3:
										dY = 0.4F;
									break;
									case 9:
										dX = 0.4F;
									break;
									case 5:
										dX = -0.4F;
									break;
									case 7:
										dY = -0.4F;
									case 4:
									case 6:
									case 8:
								}
								byte x = (byte) (int) Math.floor(p.getX() + dX);
								byte y = (byte) (int) Math.floor(p.getY() + dY);
								if(ful.getCell(x, y).getCellType() == 1 || ful.getCell(x, y).getCellType() == 15)
								{
									ful.replaceCell(Cell.createCell((byte) 5, x, y, p, null));
									p.setCurrentBombs(p.getCurrentBombs() + 1);
								}
							}
						}
					}
					p.setState(state);
				
				break;
				case 4:
					throw new UnsupportedOperationException("NetworkCommand not implemented");
				case 3:
					throw new UnsupportedOperationException("NetworkCommand not implemented");
			}

			if(len > 0)
			{
				byte[] buffer = new byte[len];
				dis.read(buffer);
				throw new Error(len + "unused byte " + Arrays.toString(buffer));
			}

			if(len < 0)
			{
				throw new Error( -len + " bytes too much ");
			}

			if(Const.logging)
			{
				System.out.println();
			}
		}
	}

	private void netHandlePlayerDrop(SPlayer p)
	{
		nsa.queue((byte) 13, new Object[] { Byte.valueOf(p.getSlot()) });
	}

	private void netSendPlayerToClient(SPlayer p)
	{
		sendToClient((byte) 12, new Object[] { Byte.valueOf(p.getSlot()), Byte.valueOf(p.getState()), Float.valueOf(p.getX()), Float.valueOf(p.getY()), Float.valueOf(p.getSpeed()), Byte.valueOf(p.getPower()), Byte.valueOf(p.getScore()) });
	}

	private void netSendWholeFieldToClient()
	{
		short w = (short) field.getWidth();
		short h = (short) field.getHeight();
		byte[] buff = new byte[w * h];

		for(int x = 0; x < w; x++ )
		{
			for(int y = 0; y < h; y++ )
			{
				buff[x * h + y] = field.getCellType(x, y);
			}
		}
		nsa.queue((byte) 11, new Object[] { Short.valueOf(w), Short.valueOf(h), buff });
	}

	@Override
	public void run()
	{
		Thread.currentThread().setPriority(7);
		long lastTime = System.currentTimeMillis();
		while(true)
		{
			long timePassed = System.currentTimeMillis() - lastTime;
			lastTime = System.currentTimeMillis();

			synchronized(players)
			{
				for(Iterator<SPlayer> it = players.iterator(); it.hasNext();)
				{
					SPlayer p = it.next();
					try
					{
						netHandleInput(p, field.ful);
						if(p.getIsNew())
						{
							netSendWholeFieldToClient();
							p.setIsNew(false);
						}
					}
					catch(IOException e)
					{
						field.ful.getSlotCell(p.getSlot()).setOwner(null);

						it.remove();
						netHandlePlayerDrop(p);
					}
				}

				for(SPlayer p : players)
				{
					p.update(timePassed, field.ful);
				}

				for(SPlayer p : players)
				{
					if(p.getDidChange())
					{
						netSendPlayerToClient(p);
						p.setDidChange(false);
					}

				}

				field.update(timePassed);

				if( !nsa.isQueueEmpty())
				{
					nsa.queue((byte) 14, new Object[0]);

					for(Iterator<SPlayer> it = players.iterator(); it.hasNext();)
					{
						SPlayer p = it.next();
						try
						{
							nsa.writeToStream(p.getSocket().getOutputStream());
						}
						catch(IOException e)
						{
							it.remove();
							netHandlePlayerDrop(p);
						}
					}
				}

			}

			nsa.clear();
			try
			{
				Thread.sleep(50L);
			}
			catch(InterruptedException localInterruptedException)
			{
			}
		}
	}

	void sendToClient(byte networkCMD, Object[] params)
	{
		nsa.queue(networkCMD, params);
	}
}
