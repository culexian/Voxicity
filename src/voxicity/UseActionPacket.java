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

public class UseActionPacket implements Packet
{
	int x;
	int y;
	int z;

	Constants.Direction dir;

	public UseActionPacket( BlockLoc loc, Constants.Direction dir )
	{
		this.x = loc.x;
		this.y = loc.y;
		this.z = loc.z;
		this.dir = dir;
	}

	public UseActionPacket( ByteBuffer buf )
	{
		x = buf.getInt();
		y = buf.getInt();
		z = buf.getInt();
		dir = Constants.Direction.values()[buf.getInt()];
	}

	public int get_id()
	{
		return Constants.Packet.UseAction;
	}

	public ByteBuffer serialize()
	{
		ByteBuffer buf = ByteBuffer.allocate( 4 * 4 );
		buf.putInt( x ).putInt( y ).putInt( z ).putInt( dir.ordinal() );

		buf.rewind();

		return buf;
	}
}
