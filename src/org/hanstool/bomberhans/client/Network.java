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
		this.nsa = new NetworkStreamAdapter();

		this.sock = new Socket(hostName, 5636);

		this.nsa.queue((byte) 1, new Object[] { userName });
		this.nsa.writeToStream(this.sock.getOutputStream());
		this.nsa.clear();

		this.dis = new DataInputStream(this.sock.getInputStream());

		new Thread(this).start();
	}

	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				int len = this.dis.readShort();

				byte command = this.dis.readByte();

				if(Const.logging)
				{
					System.out.print("rcv: (" + len + ") " + NetworkStreamAdapter.NAMES[command] + " ");
				}

				len-- ;

				switch(command)
				{
					case 11:
						int width = this.dis.readShort();
						int height = this.dis.readShort();
						len -= 4;

						byte[][] newField = new byte[width][height];

						for(int i = 0; i < width; i++ )
						{
							len -= height;
							this.dis.read(newField[i]);
						}
						this.guiInteractor.updateField(newField);
						break;
					case 8:
					{
						int slot = this.dis.read();
						len-- ;
						byte[] buffer = new byte[len];
						this.dis.read(buffer);
						len -= buffer.length;
						String name = new String(buffer, "UTF-8");

						this.guiInteractor.playerJoined(slot, name);
					}
					break;
					case 12:
					{
						byte slot = this.dis.readByte();
						byte state = this.dis.readByte();
						float x = this.dis.readFloat();
						float y = this.dis.readFloat();
						float speed = this.dis.readFloat();
						byte power = this.dis.readByte();
						byte score = this.dis.readByte();
						len -= 16;
						this.guiInteractor.updatePlayer(slot, state, x, y, speed, power, score);
					}
					break;
					case 10:
					{
						byte x = this.dis.readByte();
						byte y = this.dis.readByte();
						byte cellType = this.dis.readByte();
						len -= 3;
						this.guiInteractor.updateCell(x, y, cellType);
					}
					break;
					case 9:
					{
						byte p1 = this.dis.readByte();
						byte p2 = this.dis.readByte();
						len -= 2;
						this.guiInteractor.playerScored(p1, p2);
					}
					break;
					case 14:
						this.guiInteractor.reDraw();

						break;
					case 13:
						byte slot = this.dis.readByte();
						len-- ;
						this.guiInteractor.playerDrop(slot);

						break;
					default:
						throw new UnsupportedOperationException("not implemented: " + NetworkStreamAdapter.NAMES[command]);
				}

				if(len > 0)
				{
					byte[] buffer = new byte[len];
					this.dis.read(buffer);
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
			this.guiInteractor.disconnect();
		}
	}

	public void setPlayerState(byte state) throws IOException
	{
		if(state == this.lastStateKnownToServer)
		{
			return;
		}

		this.lastStateKnownToServer = state;

		this.nsa.queue((byte) 2, new Object[] { Byte.valueOf(state) });

		this.nsa.writeToStream(this.sock.getOutputStream());

		this.nsa.clear();
	}
}
