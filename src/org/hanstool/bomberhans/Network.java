package org.hanstool.bomberhans;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.hanstool.bomberhans.GUI.GuiInteractor;
import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.Const.NetworkConsts;
import org.hanstool.bomberhans.shared.Const.PlayerState;
import org.hanstool.bomberhans.shared.NetworkStreamAdapter;

public class Network implements Runnable
{
	private final Socket				sock;
	private final NetworkStreamAdapter	nsa;
	
	DataInputStream						dis;
	
	private final GuiInteractor			guiInteractor;
	byte								lastStateKnownToServer	= PlayerState.IDLE;
	
	public Network(GuiInteractor guiInteractor, String hostName, String userName) throws UnknownHostException, IOException
	{
		this.guiInteractor = guiInteractor;
		this.nsa = new NetworkStreamAdapter();
		
		this.sock = new Socket(hostName, NetworkConsts.SERVER_PORT);
		
		nsa.queue(NetworkStreamAdapter.CtS_PLAYER_HELO, userName);
		nsa.writeToStream(sock.getOutputStream());
		nsa.clear();
		
		dis = new DataInputStream(sock.getInputStream());
		
		new Thread(this).start();
	}
	
	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				
				int len = dis.readShort();
				


				byte command = dis.readByte();
				
				if(Const.logging)
				{
					System.out.print("rcv: (" + len + ") " + NetworkStreamAdapter.NAMES[command] + " ");
				}
				
				len -= 1;
				
				switch(command)
				{
					case NetworkStreamAdapter.StC_SYNC_FIELD:
						int width = dis.readShort();
						int height = dis.readShort();
						len -= 4;
						
						byte[][] newField = new byte[width][height];
						
						for(int i = 0; i < width; i++ )
						{
							len -= height;
							dis.read(newField[i]);
						}
						guiInteractor.updateField(newField);
					break;
					case NetworkStreamAdapter.StC_PLAYER_JOIN:
					{
						int slot = dis.read();
						len -= 1;
						byte[] buffer = new byte[len];
						dis.read(buffer);
						len -= buffer.length;
						String name = new String(buffer, "UTF-8");
						
						guiInteractor.playerJoined(slot, name);
					}
					break;
					
					case NetworkStreamAdapter.StC_SYNC_PLAYER:
					{
						byte slot = dis.readByte();
						byte state = dis.readByte();
						float x = dis.readFloat();
						float y = dis.readFloat();
						float speed = dis.readFloat();
						byte power = dis.readByte();
						byte score = dis.readByte();
						len -= 16;
						guiInteractor.updatePlayer(slot, state, x, y, speed, power, score);
					}
					break;
					
					case NetworkStreamAdapter.StC_SYNC_CELL:
					{
						byte x = dis.readByte();
						byte y = dis.readByte();
						byte cellType = dis.readByte();
						len -= 3;
						guiInteractor.updateCell(x, y, cellType);
					}
					break;
					
					case NetworkStreamAdapter.StC_PLAYER_SCORED:
					{
						byte p1 = dis.readByte();
						byte p2 = dis.readByte();
						len -= 2;
						guiInteractor.playerScored(p1, p2);
					}
					break;
					
					case NetworkStreamAdapter.StC_UPDATE_COMPLETE:
					{
						guiInteractor.reDraw();
					}
					break;
					
					case NetworkStreamAdapter.StC_PLAYER_DROP:
					{
						byte slot = dis.readByte();
						len -= 1;
						guiInteractor.playerDrop(slot);
					}
					break;
					default:
						throw new UnsupportedOperationException("not implemented: " + NetworkStreamAdapter.NAMES[command]);
						
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
		catch(IOException e)
		{
			guiInteractor.disconnect();
		}
	}
	
	public void setPlayerState(byte state) throws IOException
	{
		if(state == lastStateKnownToServer)
		{
			return;
		}
		
		lastStateKnownToServer = state;
		
		nsa.queue(NetworkStreamAdapter.CtS_PLAYER_SEND_STATE, state);
		
		nsa.writeToStream(sock.getOutputStream());
		
		nsa.clear();
		
	}
}
