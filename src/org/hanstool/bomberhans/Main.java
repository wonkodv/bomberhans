package org.hanstool.bomberhans;

import org.hanstool.bomberhans.client.GUI;
import org.hanstool.bomberhans.mapeditor.MapEditor;
import org.hanstool.bomberhans.server.Server;

public class Main
{
	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			args = new String[] { "client" };
		}
		
		switch(args[0])
		{
			case "client":
				GUI c = new GUI();
				if(args.length == 2)
				{
					c.setServerAddress(args[1]);
				}
			break;
			case "server":
				
				new Server();
			break;
			case "mapeditor":
				new MapEditor();
			break;
			default:
				throw new IllegalArgumentException("unknown command " + args[0]);
		}
		
	}
}
