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

				// Get packet id
				int id = p.get_id();

				// Serialize the packet data
				java.io.ByteArrayOutputStream out_buf_stream = new java.io.ByteArrayOutputStream();
				p.serialize( new java.io.DataOutputStream( out_buf_stream ) );
				byte[] buf = out_buf_stream.toByteArray();

				// Deserialize the same packet data with the same id
				java.io.ByteArrayInputStream in_buf_stream = new java.io.ByteArrayInputStream( buf );
				Packet i = PacketFactory.create( id, new java.io.DataInputStream( in_buf_stream ) );

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
}
