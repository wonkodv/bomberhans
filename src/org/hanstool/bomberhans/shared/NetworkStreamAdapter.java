package org.hanstool.bomberhans.shared;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class NetworkStreamAdapter
{
	public static final byte		INVALID					= 0;
	public static final byte		CtS_PLAYER_HELO			= 1;
	public static final byte		CtS_PLAYER_SEND_STATE	= 2;
	public static final byte		CtS_PLAYER_NEWGAME		= 3;
	public static final byte		CtS_PLAYER_PLACE_BOMB	= 4;
	public static final byte		CtS_PLAYER_READY		= 5;
	public static final byte		FIN						= 6;
	public static final byte		StC_GAME_START			= 7;
	public static final byte		StC_PLAYER_JOIN			= 8;
	public static final byte		StC_PLAYER_SCORED		= 9;
	public static final byte		StC_SYNC_CELL			= 10;
	public static final byte		StC_SYNC_FIELD			= 11;
	public static final byte		StC_SYNC_PLAYER			= 12;
	public static final byte		StC_PLAYER_DROP			= 13;
	public static final byte		StC_UPDATE_COMPLETE		= 14;
	public static final String[]	NAMES					= {
			"invalid",
			"PLAYER_HELO",
			"PLAYER_MOVE",
			"CtS_PLAYER_NEWGAME",
			"CtS_PLAYER_PLACE_BOMB",
			"CtS_PLAYER_READY",
			"FIN",
			"StC_GAME_START",
			"StC_PLAYER_JOIN",
			"StC_PLAYER_SCORED",
			"StC_SYNC_CELL",
			"StC_SYNC_FIELD",
			"StC_SYNC_PLAYER",
			"StC_PLAYER_DROP",
			"StC_UPDATE_COMPLETE"							};
	DataOutputStream				dout;
	ByteArrayOutputStream			arrayOS;
	boolean							queueEmpty;
	
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
					d.writeByte(((Byte) o).byteValue());
				}
				else if(o instanceof Short)
				{
					d.writeShort(((Short) o).shortValue());
				}
				else if(o instanceof String)
				{
					d.write(((String) o).getBytes("UTF-8"));
				}
				else if(o instanceof Integer)
				{
					d.writeInt(((Integer) o).intValue());
				}
				else if(o instanceof Float)
				{
					d.writeFloat(((Float) o).floatValue());
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
				else if(Const.logging)
				{
					System.out.print(" ,  " + o.getClass().getSimpleName() + " [" + o + "]");
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
