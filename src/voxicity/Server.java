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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server extends Thread
{
	boolean quitting = false;

	Config config;
	ChunkServer chunk_server = new ChunkServer();
	Listener listener;
	World world;

	Map< Connection, Player > connection_to_player = new HashMap< Connection, Player >();
	Map< Player, Connection > player_to_connection = new HashMap< Player, Connection >();
	Map< Player, Set< ChunkID > > chunk_requests = new HashMap< Player, Set< ChunkID > >();

	public Server( Config config )
	{
		this.config = config;
		this.world = new World( config );
	}

	void init()
	{
		try
		{
			listener = new Listener( config );
		}
		catch ( Exception e )
		{
			System.out.println( e );
			e.printStackTrace();
			quit();
		}
	}

	public void run()
	{
		init();

		while ( !quitting )
			update();

		shutdown();
	}

	void update()
	{
		handle_new_connections();
		handle_packets();
		load_new_chunks();
		handle_chunk_requests();
	}

	public void quit()
	{
		System.out.println( "Server is quitting" );
		quitting = true;
	}

	void shutdown()
	{
		listener.quit();
		chunk_server.shutdown();

		for ( Connection c : player_to_connection.values() )
			c.close();
	}

	void handle_new_connections()
	{
		Connection c = listener.get_new_connection();

		System.out.println( "Handling new connection " + c );
		while ( c != null )
		{
			new_connection( new Player(), c );
			c = listener.get_new_connection();
		}
	}

	void new_connection( Player player, Connection connection )
	{
		if ( connection_to_player.containsKey( connection ) )
			return;

		connection_to_player.put( connection, player );
		player_to_connection.put( player, connection );
		chunk_requests.put( player, new HashSet< ChunkID >() );
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

		chunk_server.load_chunk( new ChunkID( x, y, z ), p );
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
						request_chunk( r.x, r.y, r.z, c );
						break;
					}
				}

				p = c.recieve();
			}
		}
	}

	// Adds the chunk request to the list of requests for this player
	void request_chunk( int x, int y, int z, Connection c )
	{
		Player p = connection_to_player.get( c );
		Set< ChunkID > id_set = chunk_requests.get( p );

		id_set.add( new ChunkID( x, y, z ) );
		load_chunk( x, y, z, p );
	}

	void handle_chunk_requests()
	{
		java.util.Iterator< Map.Entry< Player, Set< ChunkID > > > map_iter = chunk_requests.entrySet().iterator();

		while ( map_iter.hasNext() )
		{
			Map.Entry< Player, Set< ChunkID > > e = map_iter.next();

			java.util.Iterator< ChunkID > id_iter = e.getValue().iterator();

			while ( id_iter.hasNext() )
			{
				ChunkID id = id_iter.next();

				if ( world.is_chunk_loaded( id ) )
				{
					id_iter.remove();
					player_to_connection.get( e.getKey() ).send( new LoadChunkPacket( world.get_chunk( id ) ) );
				}
			}
		}
	}
}
