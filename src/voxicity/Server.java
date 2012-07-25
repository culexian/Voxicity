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

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread
{
	boolean quitting = false;

	Config config;
	ChunkServer chunk_server = new ChunkServer();
	World world;

	Map< Connection, Player > connection_to_player = new HashMap< Connection, Player >();
	Map< Player, Connection > player_to_connection = new HashMap< Player, Connection >();

	public Server( Config config )
	{
		this.config = config;
		this.world = new World( config );
	}

	boolean init()
	{
		return true;
	}

	public void run()
	{
		init();

		while ( !quitting )
		{
			update();
		}

		shutdown();
	}

	void update()
	{
		handle_packets();
		load_new_chunks();
	}

	public void quit()
	{
		quitting = true;
	}

	void shutdown()
	{
		chunk_server.shutdown();
	}

	void new_connection( Player player, Connection connection )
	{
		if ( connection_to_player.containsKey( connection ) )
			return;

		connection_to_player.put( connection, player );
		player_to_connection.put( player, connection );
	}

	public void load_new_chunks()
	{
		Chunk new_chunk = chunk_server.get_next_chunk();

		if ( new_chunk == null )
			return;

		new_chunk.world = world;
		world.set_chunk( new_chunk.x, new_chunk.y, new_chunk.z, new_chunk );

		for ( Connection c : connection_to_player.keySet() )
			c.send( new LoadChunkPacket( new_chunk ) );
	}

	public void load_chunk( int x, int y, int z, Player p )
	{
		if ( world.is_chunk_loaded( x, y, z ) ) return;

		chunk_server.load_chunk( World.get_chunk_id( x, y, z ), p );
	}

	public void load_chunk( float x, float y, float z, Player p )
	{
		load_chunk( Math.round( x ), Math.round( y ), Math.round( z ), p );
	}

	public void handle_packets()
	{
		for ( Connection c : connection_to_player.keySet() )
		{
			Packet p = c.recieve();

			while ( p != null )
			{
				switch( p.get_id() )
				{
					case Constants.Packet.RequestChunk:
					{
						RequestChunkPacket r = (RequestChunkPacket)p;
						load_chunk( r.x, r.y, r.z, connection_to_player.get( c ) );
						break;
					}
				}

				p = c.recieve();
			}
		}
	}
}
