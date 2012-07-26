
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

public class HitActionPacket implements Packet
{
	int x;
	int y;
	int z;

	public HitActionPacket( int x, int y, int z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public HitActionPacket( ByteBuffer data )
	{
		this.x = data.getInt();
		this.y = data.getInt();
		this.z = data.getInt();
	}

	public int get_id()
	{
		return Constants.Packet.HitAction;
	}

	public ByteBuffer serialize()
	{
		ByteBuffer buf = ByteBuffer.allocate( 4 + 4 + 3 * 4 );

		buf.putInt( get_id() ).putInt( 3 * 4 ).putInt( x ).putInt( y ).putInt( z );

		buf.rewind();

		return buf;
	}
}
