package org.hanstool.bomberhans.shared;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Every Command has a fixed width of FRAMEWIDTH bytes, the first 2byte always
 * declares the number of used bytes, the second is the network code as listed
 * below, the remaining ones are the parameters
 **/
public class NetworkStreamAdapter
{
	public static final byte		INVALID					= 0;
	/**
	 * Direction: Client to Server <br>
	 * Parameters: utf-8 String name
	 **/
	public static final byte		CtS_PLAYER_HELO			= 1;
	/**
	 * Direction: Client to Server <br>
	 * Parameters: byte playerstate
	 **/
	public static final byte		CtS_PLAYER_SEND_STATE	= 2;
	/**
	 * Direction: Client to Server <br>
	 * requesting new Game
	 **/
	
	public static final byte		CtS_PLAYER_NEWGAME		= 3;
	/**
	 * Direction: Client to Server <br>
	 * order place bomb DEPRICATED (in CtS_PLAYER_SEND_STATE )
	 **/
	public static final byte		CtS_PLAYER_PLACE_BOMB	= 4;
	/**
	 * Direction: Client to Server <br>
	 * Parameters: byte ready
	 **/
	public static final byte		CtS_PLAYER_READY		= 5;
	/**
	 * Direction: Server to Client <br>
	 * Connection closing
	 **/
	public static final byte		FIN						= 6;
	/**
	 * Direction: Server to Client <br>
	 * Game started
	 **/
	public static final byte		StC_GAME_START			= 7;
	/**
	 * Direction: Server to Client <br>
	 * Parameters: byte slot, utf-8 String name
	 **/
	public static final byte		StC_PLAYER_JOIN			= 8;
	/**
	 * Direction: Server to Client<br>
	 * Fred killed Noob<br>
	 * Parameters: byte slotFred byte slotNoob
	 **/
	public static final byte		StC_PLAYER_SCORED		= 9;
	/**
	 * Direction: Server to Client <br>
	 * Parameters: byte x byte y byte CellType
	 **/
	public static final byte		StC_SYNC_CELL			= 10;
	
	/**
	 * Direction: Server to Client <br>
	 * Parameters: short width short height byte[][] CellTypes
	 **/
	public static final byte		StC_SYNC_FIELD			= 11;
	

	/**
	 * Direction: Server to Client <br>
	 * Parameters: byte slot, byte state, float x, float y, byte speed, byte
	 * power, byte score...
	 **/
	public static final byte		StC_SYNC_PLAYER			= 12;
	
	public static final byte		StC_PLAYER_DROP			= 13;
	/**
	 * it now makes sence to repaint;
	 */
	public static final byte		StC_UPDATE_COMPLETE		= 14;
	

	public static final String[]	NAMES					= { "invalid", "PLAYER_HELO", "PLAYER_MOVE", "CtS_PLAYER_NEWGAME", "CtS_PLAYER_PLACE_BOMB", "CtS_PLAYER_READY", "FIN", "StC_GAME_START", "StC_PLAYER_JOIN", "StC_PLAYER_SCORED", "StC_SYNC_CELL", "StC_SYNC_FIELD", "StC_SYNC_PLAYER", "StC_PLAYER_DROP", "StC_UPDATE_COMPLETE", };
	

	DataOutputStream				dout;
	ByteArrayOutputStream			arrayOS;
	boolean							queueEmpty;
	
	/**
	 * 
	 */
	public NetworkStreamAdapter()
	{
		clear();
	}
	
	public void clear()
	{
		
		arrayOS = new ByteArrayOutputStream(1024);
		dout = new DataOutputStream(arrayOS);
		queueEmpty = true;
	}
	
	public boolean isQueueEmpty()
	{
		return queueEmpty;
	}
	
	public void queue(byte networkCMD, Object... params)
	{
		


		ByteArrayOutputStream temp = new ByteArrayOutputStream(255);
		
		DataOutputStream d = new DataOutputStream(temp);
		


		try
		{
			d.writeByte(networkCMD);
			for(Object o : params)
			{
				if(o instanceof Byte)
				{
					d.writeByte((Byte) o);
				}
				else if(o instanceof Short)
				{
					d.writeShort((Short) o);
				}
				else if(o instanceof String)
				{
					d.write(((String) o).getBytes("UTF-8"));
				}
				else if(o instanceof Integer)
				{
					d.writeInt((Integer) o);
				}
				else if(o instanceof Float)
				{
					d.writeFloat((Float) o);
				}
				else if(o instanceof byte[])
				{
					
					d.write((byte[]) o);
					

				}
				else
				{
					throw new UnsupportedOperationException("unsupported DataType " + o.getClass());
				}
			}
			
			byte[] buff = temp.toByteArray();
			dout.writeShort(buff.length);
			dout.write(buff);
			

			queueEmpty = false;
			
			if(Const.logging)
			{
				System.out.print("QUE: (" + buff.length + ")  " + NAMES[networkCMD]);
			}
			
			for(Object o : params)
			{
				if(o instanceof byte[])
				{
					byte[] b = (byte[]) o;
					
					if(Const.logging)
					{
						System.out.print(" ,  byte(" + b.length + ")" + Arrays.toString(b));
					}
				}
				else
				{
					
					if(Const.logging)
					{
						System.out.print(" ,  " + o.getClass().getSimpleName() + " [" + o + "]");
					}
				}
			}
			
			if(Const.logging)
			{
				System.out.println();
			}
			


		}
		catch(IOException e)
		{
			throw new Error(e);
		}
		

	}
	
	public void writeToStream(OutputStream os) throws IOException
	{
		

		byte[] updatePack = arrayOS.toByteArray();
		

		os.write(updatePack);
		os.flush();
	}
}
