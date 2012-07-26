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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkConnection extends Connection implements Runnable
{
	// To be set to the socket's input stream
	DataInputStream in;

	// To be set to the socket's output stream
	DataOutputStream out;

	// The network socket
	Socket s;

	// Connection is constructed with a socket that's already been prepared
	public NetworkConnection( Socket s ) throws IOException
	{
		this.s = s;
		this.in = new DataInputStream( new BufferedInputStream( s.getInputStream() ) );
		this.out = new DataOutputStream( s.getOutputStream() );

		// Start the thread that traffics packets into/out of the socket
		new Thread( this ).start();
	}

	public void close()
	{
		try
		{
			s.close();
		}
		catch ( IOException e )
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}

	public boolean closed()
	{
		return s.isClosed();
	}

	public void run()
	{
		try
		{
			while( !s.isClosed() )
			{
				// If there's a packet id and packet size in the stream, start getting the packet
				if ( in.available() >= 8 )
				{
					// Mark the position in case the packet isn't done sending
					in.mark( Integer.MAX_VALUE );

					// Get the packet id
					int id = in.readInt();

					// Get the packet size
					int size = in.readInt();

					// Not enough data in the stream yet
					if ( in.available() < size )
					{
						// Reset back to the mark we just made
						in.reset();
					}
					else // The whole packet has arrived
					{
						byte[] data = new byte[size];
						in.read( data, 0, size );

						// Create the packet and put in the incoming queue
						incoming.put( PacketFactory.create( id, data ) );
					}
				}

				// Get and remove the next packet from the outgoing queue
				Packet p = outgoing.poll();

				if ( p != null )
				{
					// Write the serialized packet to the socket
					out.write( p.serialize().array() );
				}
			}
		}
		catch ( IOException e )
		{
			System.out.println( e );
			e.printStackTrace();
		}
		catch ( InterruptedException e )
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}
}
