package org.hanstool.bomberhans.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println("server");
		try
		{
			Server server;
			if(args.length == 1)
			{
				server = new Server(args[0]);
			}
			else
			{
				server = new Server();
			}

			Thread serverThread = new Thread(server);

			ServerSocket serverSocket = new ServerSocket(5636);
			serverThread.start();
			while(true)
			{
				Socket s = serverSocket.accept();
				server.AddPlayer(s);
			}
		}
		catch(IOException e)
		{
			System.exit(5);
		}
	}
}
