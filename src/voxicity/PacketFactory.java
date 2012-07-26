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

public class PacketFactory
{
	public static Packet create( int id, byte[] data )
	{
		return create( id, ByteBuffer.wrap( data ) );
	}

	public static Packet create( int id, ByteBuffer data )
	{
		switch ( id )
		{
			case Constants.Packet.LoadChunk:
				return new LoadChunkPacket( data );
			case Constants.Packet.RequestChunk:
				return new RequestChunkPacket( data );
			case Constants.Packet.UseAction:
				return new UseActionPacket( data );
			case Constants.Packet.HitAction:
				return new HitActionPacket( data );
			case Constants.Packet.MoveAction:
				return null;
			case Constants.Packet.BlockUpdate:
				return new BlockUpdatePacket( data );
			case Constants.Packet.PlayerMove:
				return new PlayerMovePacket( data );
			default:
				return null;
		}
	}
}
