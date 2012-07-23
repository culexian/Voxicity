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

public class PlayerMovePacket extends Packet
{
	float x, y, z;

	// Create a chunk request packet.
	// Use global values for coordinates.
	public PlayerMovePacket( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PlayerMovePacket( ByteBuffer buf )
	{
		x = buf.getFloat();
		y = buf.getFloat();
		z = buf.getFloat();
	}

	public int get_id()
	{
		return Constants.Packet.PlayerMove;
	}

	// Serialize this with id, length and coords
	public ByteBuffer serialize()
	{
		ByteBuffer buf = ByteBuffer.allocate( 4 + 4 + 3 * 4 );

		buf.putInt( get_id() ).putInt( 3 * 4 ).putFloat( x ).putFloat( y ).putFloat( z );

		buf.rewind();

		return buf;
	}
}