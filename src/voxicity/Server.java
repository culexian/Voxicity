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

public class Server extends Thread
{
	boolean quitting = false;

	Config config;
	ChunkServer chunk_server = new ChunkServer();
	List< Connection > connections = new LinkedList< Connection >();
	List< Player > players = new LinkedList< Player >();
	World world;

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

	void new_connection( Connection connection )
	{
		if ( connections.contains( connection ) )
			return;

		connections.add( connection );
	}

	public void load_new_chunks()
	{
		Chunk new_chunk = chunk_server.get_next_chunk();

		if ( new_chunk == null )
			return;

		new_chunk.world = world;
		world.set_chunk( new_chunk.x, new_chunk.y, new_chunk.z, new_chunk );

		for ( Connection conn : connections )
			conn.send( new LoadChunkPacket( new_chunk ) );
	}

	public void load_chunk( int x, int y, int z )
	{
		if ( world.is_chunk_loaded( x, y, z ) ) return;

		chunk_server.load_chunk( World.get_chunk_id( x, y, z ) );
	}

	public void load_chunk( float x, float y, float z )
	{
		load_chunk( Math.round( x ), Math.round( y ), Math.round( z ) );
	}

	public void handle_packets()
	{
		for ( Connection c : connections )
		{
			Packet p = c.recieve();

			while ( p != null )
			{
				switch( p.get_id() )
				{
					case Constants.Packet.RequestChunk:
					{
						RequestChunkPacket r = (RequestChunkPacket)p;
						load_chunk( r.x, r.y, r.z );
						break;
					}
				}

				p = c.recieve();
			}
		}
	}
}
