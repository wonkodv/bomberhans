package org.hanstool.bomberhans.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.hanstool.bomberhans.shared.Const;
import org.hanstool.bomberhans.shared.NetworkStreamAdapter;

public class Network implements Runnable
{
	private final Socket				sock;
	private final NetworkStreamAdapter	nsa;
	DataInputStream						dis;
	private final GUI.GuiInteractor		guiInteractor;
	byte								lastStateKnownToServer	= 10;

	public Network(GUI.GuiInteractor guiInteractor, String hostName, String userName) throws UnknownHostException, IOException
	{
		this.guiInteractor = guiInteractor;
		nsa = new NetworkStreamAdapter();

		sock = new Socket(hostName, 5636);

		nsa.queue((byte) 1, new Object[] { userName });
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

				len-- ;

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
						len-- ;
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
						guiInteractor.reDraw();

						break;
					case NetworkStreamAdapter.StC_PLAYER_DROP:
						byte slot = dis.readByte();
						len-- ;
						guiInteractor.playerDrop(slot);

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

		nsa.queue((byte) 2, new Object[] { Byte.valueOf(state) });

		nsa.writeToStream(sock.getOutputStream());

		nsa.clear();
	}
}
