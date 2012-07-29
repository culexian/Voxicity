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

import java.nio.ByteBuffer;

public class ConnectionGlue implements Runnable
{
	Connection a, b;
	boolean quitting = false;

	public ConnectionGlue( Connection a, Connection b )
	{
		this.a = a;
		this.b = b;
	}

	public void quit()
	{
		quitting = true;
	}

	public void run()
	{
		while( !quitting )
		{
			handle_traffic( a, b );
			handle_traffic( b, a );
		}
	}

	void handle_traffic( Connection out, Connection in )
	{
		try
		{
			// If packets coming from out, send one to in
			if ( out.outgoing.peek() != null )
			{
				// Get the packet from the outgoing queue
				Packet p = out.outgoing.take();

				// Serialize the packet
				ByteBuffer buf = p.serialize();

				// Get packet id
				int id = buf.getInt();

				// Skip length check, is a local connection
				buf.getInt();

				// Deserialize the same packet with the id and the buffer from this point
				Packet i = PacketFactory.create( id, buf.slice() );

				// Place the new packet on the incoming queue
				in.incoming.put( i );
			}
		}
		catch ( Exception e )
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}


	Packet deserialize( int id, ByteBuffer buf )
	{
		switch ( id )
		{
			case Constants.Packet.LoadChunk:
				return new LoadChunkPacket( buf );
			case Constants.Packet.RequestChunk:
				return new RequestChunkPacket( buf );
			default:
				return null;
		}
	}
}
