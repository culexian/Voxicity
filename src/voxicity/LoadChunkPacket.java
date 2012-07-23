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

public class LoadChunkPacket extends Packet
{
	Chunk chunk;

	public LoadChunkPacket( Chunk chunk )
	{
		this.chunk = chunk;
	}

	public LoadChunkPacket( ByteBuffer buf )
	{
		chunk = new Chunk( buf );
	}

	public int get_id()
	{
		return Constants.Packet.LoadChunk;
	}

	public ByteBuffer serialize()
	{
		ByteBuffer chunk_ser = chunk.serialize();
		ByteBuffer buf = ByteBuffer.allocate( 4 + 4 + chunk_ser.limit() );

		buf.putInt( get_id() ).putInt( chunk_ser.limit() ).put( chunk_ser );

		buf.rewind();

		return buf;
	}
}
