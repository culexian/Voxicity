/*
 * Copyright 2011, Erik Lund
 *
 * This file is part of Voxicity.
 *
 *  Voxicity is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Voxicity is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Voxicity.  If not, see <http://www.gnu.org/licenses/>.
 */

package voxicity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable
{
	BlockingQueue< Connection > connections = new LinkedBlockingQueue< Connection >();
	ServerSocket socket;

	public Listener( Config config ) throws Exception
	{
		socket = new ServerSocket( 11000 );

		new Thread( this ).start();
	}

	public void run()
	{
		try
		{
			while ( true )
			{
				Socket s = socket.accept();
				System.out.println( "Got new connection " + s );
				connections.put( new NetworkConnection( s ) );
				System.out.println( "Put new connection in queue" );
			}
		}
		catch ( Exception e )
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}

	public void quit()
	{
		try
		{
			socket.close();
		}
		catch ( Exception e )
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}

	public Connection get_new_connection()
	{
		return connections.poll();
	}
}
