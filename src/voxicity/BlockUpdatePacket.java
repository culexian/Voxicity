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

public class BlockUpdatePacket implements Packet
{
	int x;
	int y;
	int z;
	int id;

	public BlockUpdatePacket( int x, int y, int z, int id )
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	public BlockUpdatePacket( ByteBuffer buf )
	{
		this.x = buf.getInt();
		this.y = buf.getInt();
		this.z = buf.getInt();
		this.id = buf.getInt();
	}

	public int get_id()
	{
		return Constants.Packet.BlockUpdate;
	}

	public ByteBuffer serialize()
	{
		ByteBuffer buf = ByteBuffer.allocate( 4 * 4 );

		buf.putInt( x ).putInt( y ).putInt( z ).putInt( id );

		buf.rewind();

		return buf;
	};
}
