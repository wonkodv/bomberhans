package org.hanstool.bomberhans.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.hanstool.bomberhans.shared.Const;

public class Main
{
	
	public static void main(String args[])
	{
		System.out.println("server");
		try
		{
			Server server = new Server();
			Thread serverThread = new Thread(server);
			ServerSocket serverSocket;
			serverSocket = new ServerSocket(Const.NetworkConsts.SERVER_PORT);
			serverThread.start();
			while(true)
			{
				Socket s = serverSocket.accept();
				server.AddPlayer(s);
			}
		}
		catch(IOException e)
		{
			System.exit(5); //
		}
	}
}
