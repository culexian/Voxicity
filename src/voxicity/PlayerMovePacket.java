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

import org.lwjgl.util.vector.Vector3f;

public class PlayerMovePacket implements Packet
{
	Vector3f v = new Vector3f();

	// Create a chunk request packet.
	// Use global values for coordinates.
	public PlayerMovePacket( Vector3f v )
	{
		this.v.set( v.x, v.y, v.z );
	}

	public PlayerMovePacket( ByteBuffer buf )
	{
		v.x = buf.getFloat();
		v.y = buf.getFloat();
		v.z = buf.getFloat();
	}

	public int get_id()
	{
		return Constants.Packet.PlayerMove;
	}

	// Serialize this with id, length and coords
	public ByteBuffer serialize()
	{
		ByteBuffer buf = ByteBuffer.allocate( 3 * 4 );

		buf.putFloat( v.x ).putFloat( v.y ).putFloat( v.z );

		buf.rewind();

		return buf;
	}
}
