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

public class RequestChunkPacket implements Packet
{
	int x, y, z;

	// Create a chunk request packet.
	// Use global values for coordinates.
	public RequestChunkPacket( int x, int y, int z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public RequestChunkPacket( float x, float y, float z )
	{
		this( Math.round( x ), Math.round( y ), Math.round( z ) );
	}

	public RequestChunkPacket( ByteBuffer buf )
	{
		x = buf.getInt();
		y = buf.getInt();
		z = buf.getInt();
	}

	public int get_id()
	{
		return Constants.Packet.RequestChunk;
	}

	// Serialize this with id, length and coords
	public ByteBuffer serialize()
	{
		ByteBuffer buf = ByteBuffer.allocate( 4 + 4 + 3 * 4 );

		buf.putInt( get_id() ).putInt( 3 * 4 ).putInt( x ).putInt( y ).putInt( z );

		buf.rewind();

		return buf;
	}
}
