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

public class ChunkRequester extends BackgroundTask
{
	Player player;
	World world;
	Connection connection;

	public ChunkRequester( Player player, World world, Connection connection )
	{
		this.player = player;
		this.world = world;
		this.connection = connection;
	}

	public void task()
	{
		int view = 4 * Constants.Chunk.side_length;

		for ( int x = -view ; x <= view ; x += Constants.Chunk.side_length )
			for ( int y = -view ; y <= view ; y += Constants.Chunk.side_length )
				for ( int z = -view ; z <= view ; z += Constants.Chunk.side_length )
				{
					if ( quitting() )
						return;

					if ( ( x*x + y*y + z*z ) <= view*view )
						connection.send( new RequestChunkPacket( player.pos.x + x, player.pos.y + y, player.pos.z + z ) ); 
				}

		try
		{
			Thread.currentThread().sleep( 500 );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
