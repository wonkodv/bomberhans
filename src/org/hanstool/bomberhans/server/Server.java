package org.hanstool.bomberhans.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.hanstool.bomberhans.server.cells.Cell;
import org.hanstool.bomberhans.server.cells.CellStartSlot;
import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.Const.CellTypes;
import org.hanstool.bomberhans.shared.Const.PlayerState;
import org.hanstool.bomberhans.shared.NetworkStreamAdapter;

public class Server implements Runnable
{
	
	private LinkedList<SPlayer>		players;
	
	private Field					field;
	
	private NetworkStreamAdapter	nsa;
	
	Server()
	{
		players = new LinkedList<SPlayer>();
		field = Field.generateNewField(this);
		nsa = new NetworkStreamAdapter();
	}
	
	

	void AddPlayer(Socket socket)
	{
		if(players.size() >= field.getMaxPlayers())
		{
			try
			{
				socket.getOutputStream().write(NetworkStreamAdapter.FIN);
				socket.close();
			}
			catch(IOException e)
			{
				; //
			}
		}
		CellStartSlot cSlot = null;
		byte slot;
		for(slot = 0; slot <= field.getMaxPlayers() - 1; slot++ )
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
			int len;
			DataInputStream dis = p.getDis();
			if(dis.available() < 2)
			{
				return;
			}
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
				case NetworkStreamAdapter.INVALID:
					throw new Error("What the fuck? Network Command Invalid O_O");
				case NetworkStreamAdapter.CtS_PLAYER_HELO:
				{
					byte[] buffer;
					buffer = new byte[len];
					dis.read(buffer, 0, len);
					len -= buffer.length;
					p.setName(new String(buffer, "utf-8"));
					
					for(SPlayer pl : players)
					{
						sendToClient(NetworkStreamAdapter.StC_PLAYER_JOIN, pl.getSlot(), pl.getName());
					}
				}
				break;
				case NetworkStreamAdapter.CtS_PLAYER_SEND_STATE:
					byte state = dis.readByte();
					len -= 1;
					
					if(state == PlayerState.PLACING_BOMB)
					{
						if(p.getCurrentBombs() < p.getMax_bombs())
						{
							
							if(p.getState() != state)
							{
								float dX = 0, dY = 0;
								switch(p.getState() | 1)
								{
									case PlayerState.RUNNING_N2:
										dY = 0.4f;
									break;
									case PlayerState.RUNNING_W2:
										dX = 0.4f;
									break;
									case PlayerState.RUNNING_E2:
										dX = -0.4f;
									break;
									case PlayerState.RUNNING_S2:
										dY = -0.4f;
									break;
								}
								

								byte x = (byte) Math.floor(p.getX() + dX);
								byte y = (byte) Math.floor(p.getY() + dY);
								if(ful.getCell(x, y).getCellType() == CellTypes.CLEAR || ful.getCell(x, y).getCellType() == CellTypes.HANS_GORE)
								{
									ful.replaceCell(Cell.createCell(CellTypes.BOMB, x, y, p, null));
									p.setCurrentBombs(p.getCurrentBombs() + 1);
								}
							}
						}
					}
					p.setState(state);
					
				break;
				case NetworkStreamAdapter.CtS_PLAYER_PLACE_BOMB:
					// TODO if not StartSlot
					throw new UnsupportedOperationException("NetworkCommand not implemented");
				case NetworkStreamAdapter.CtS_PLAYER_NEWGAME:
					throw new UnsupportedOperationException("NetworkCommand not implemented");
			}
			


			if(len > 0)
			{
				byte[] buffer = new byte[len];
				dis.read(buffer);
				throw new Error(len + "unused byte " + Arrays.toString(buffer));
				
			}
			else if(len < 0)
			{
				throw new Error( -len + " bytes too much ");
			}
			else
			{
				
				if(Const.logging)
				{
					System.out.println();
				}
			}
		}
	}
	
	/**
	 * @param p
	 */
	private void netHandlePlayerDrop(SPlayer p)
	{
		nsa.queue(NetworkStreamAdapter.StC_PLAYER_DROP, p.getSlot());
		
	}
	
	
	/**
	 * @param p
	 */
	private void netSendPlayerToClient(SPlayer p)
	{
		/*
		 * Parameters: byte slot, byte state, float x, float y, byte speed, byte
		 * power, byte score
		 */
		sendToClient(NetworkStreamAdapter.StC_SYNC_PLAYER, p.getSlot(), p.getState(), p.getX(), p.getY(), p.getSpeed(), p.getPower(), p.getScore());
		
	}
	
	

	/**
	 * @param p
	 */
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
		nsa.queue(NetworkStreamAdapter.StC_SYNC_FIELD, w, h, buff);
		
	}
	
	

	@Override
	public void run()
	{
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 2);
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
					nsa.queue(NetworkStreamAdapter.StC_UPDATE_COMPLETE);
					
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
				Thread.sleep(Const.NetworkConsts.UPDATE_INTERVAL);
			}
			catch(InterruptedException e)
			{
				; // cannot happen
			}
		}
	}
	
	/**
	 * @param stcPlayerJoin
	 * @param slot
	 * @param name
	 */
	void sendToClient(byte networkCMD, Object... params)
	{
		nsa.queue(networkCMD, params);
		
	}
	

}
