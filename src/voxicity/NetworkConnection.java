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
import java.nio.ByteBuffer;

public class NetworkConnection extends Connection
{
	// To be set to the socket's input stream
	DataInputStream in;

	// To be set to the socket's output stream
	DataOutputStream out;

	// The network socket
	Socket s;

	Thread sender;
	Thread receiver;

	// Connection is constructed with a socket that's already been prepared
	public NetworkConnection( Socket s ) throws IOException
	{
		this.s = s;
		this.in = new DataInputStream( new BufferedInputStream( s.getInputStream() ) );
		this.out = new DataOutputStream( s.getOutputStream() );

		// Start the threads that send/receive packets in/out of the socket
		start_receive_thread();
		start_send_thread();
	}

	void start_receive_thread()
	{
		Runnable receiver = new Runnable()
		{
			public void run()
			{
				try
				{
					while ( true )
					{
						int id = in.readInt();
						int length = in.readInt();
						byte[] data = new byte[length];

						in.readFully( data, 0, length );
						ByteBuffer buf = ByteBuffer.wrap( data );
						incoming.put( PacketFactory.create( id, buf ) );
					}
				}
				catch ( Exception e )
				{
					close();
				}
			}
		};

		this.receiver = new Thread( receiver, "Socket receiver thread - " + s.getInetAddress() );
		this.receiver.start();
	}

	void start_send_thread()
	{
		Runnable sender = new Runnable()
		{
			public void run()
			{
				try
				{
					while ( true )
					{
						Packet p = outgoing.take();
						ByteBuffer buf = p.serialize();

						out.writeInt( p.get_id() );
						out.writeInt( buf.limit() );
						out.write( buf.array() );
					}
				}
				catch ( Exception e )
				{
					close();
				}
			}
		};

		this.sender = new Thread( sender, "Socket sender thread - " + s.getInetAddress() );
		this.sender.start();
	}

	public void close()
	{
		try
		{
			s.close();
			sender.interrupt();
			receiver.interrupt();
		}
		catch ( IOException e )
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}

	public boolean is_closed()
	{
		return s.isClosed();
	}

	public void wait_send()
	{
		super.wait_send();

		// Flush the socket
		try {
			out.flush();
		}
		catch ( IOException e )
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}
}
