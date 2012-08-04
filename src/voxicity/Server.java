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

public class Server implements Runnable
{
	boolean quitting = false;

	long last_tick_start;

	Config config;
	ChunkServer chunk_server;
	Listener listener;
	World world;

	Map< Connection, Player > connection_to_player = new HashMap< Connection, Player >();
	Map< Connection, ConnectionInfo > connection_info = new HashMap< Connection, ConnectionInfo >();
	Map< Player, Connection > player_to_connection = new HashMap< Player, Connection >();
	Map< Player, Set< ChunkID > > chunk_requests = new HashMap< Player, Set< ChunkID > >();
	Map< Player, Set< ChunkID > > served_chunks = new HashMap< Player, Set< ChunkID > >();
	Set< Connection > closing_queue = new HashSet< Connection >();

	public Server( Config config )
	{
		this.config = config;
		this.world = new World( config );
		this.chunk_server = new ChunkServer( world );
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
		tick_delay();
		handle_new_connections();
		handle_packets();
		handle_connection_keepalive();
		remove_closed_connections();
//		load_new_chunks();
		handle_chunk_requests();
	}

	void tick_delay()
	{
		
		long delta = last_tick_start + 50 - Time.get_time_ms();

		if ( delta > 0 )
		try
		{
			Thread.currentThread().sleep( delta );
		}
		catch ( InterruptedException e )
		{

		}

		last_tick_start = Time.get_time_ms();
	}

	public void quit()
	{
		System.out.println( "Server is shutting down" );
		quitting = true;
	}

	void shutdown()
	{
		if ( listener != null )
			listener.quit();

		if ( chunk_server != null )
			chunk_server.quit();

		for ( Connection c : player_to_connection.values() )
		{
			c.send( new DisconnectPacket() );
			c.wait_send();
			c.close();
		}
	}

	void handle_new_connections()
	{
		Connection c = listener.get_new_connection();

		while ( c != null )
		{
			System.out.println( "Handling new connection " + c );
			new_connection( new Player(), c );
			c = listener.get_new_connection();
		}
	}

	void new_connection( Player player, Connection connection )
	{
		if ( connection_to_player.containsKey( connection ) )
			return;

		connection_info.put( connection, new ConnectionInfo() );
		connection_to_player.put( connection, player );
		player_to_connection.put( player, connection );
		chunk_requests.put( player, new HashSet< ChunkID >() );
		served_chunks.put( player, new HashSet< ChunkID >() );
	}

	void handle_connection_keepalive()
	{
		Set< Connection > connections = new HashSet< Connection >( connection_to_player.keySet() );

		for ( Connection c : connections )
		{
			ConnectionInfo info = connection_info.get( c );
			long update_delta = Time.get_time_ms() - info.get_last_update();

			// Time to send out a new KeepAlivePacket to this connection
			if ( !info.awaiting_update && update_delta < 5000 )
			{
				// We're awaiting a packet back from now on
				info.awaiting_update = true;
				c.send( new KeepAlivePacket( info.get_update_id() ) );
			} // Connection has timed out, close it
			else if ( info.awaiting_update && update_delta > 10000 || c.is_closed() )
			{
				closing_queue.add( c );
			}
		}
	}

	// Disconnect, close and remove Connections
	// that have been queued for closing
	void remove_closed_connections()
	{
		for ( Connection c : closing_queue )
		{
			if ( !c.is_closed() )
			{
				c.send( new DisconnectPacket() );
				c.wait_send();
			}
			c.close();
			remove_connection( c );
		}

		closing_queue.clear();
	}

	void remove_connection( Connection c )
	{
		System.out.println( "Removing connection " + c );
		if ( !connection_to_player.containsKey( c ) )
			return;

		Player p = connection_to_player.get( c );

		connection_info.remove( c );
		connection_to_player.remove( c );
		player_to_connection.remove( p );
		chunk_requests.remove( p );
		served_chunks.remove( p );
	}

	public void load_chunk( int x, int y, int z, Player p )
	{
		if ( world.is_chunk_loaded( x, y, z ) ) return;

		chunk_server.request_chunk( new ChunkID( x, y, z ) );
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
					case Constants.Packet.PlayerMove:
					{
						PlayerMovePacket r = (PlayerMovePacket)p;
						connection_to_player.get( c ).pos.set( r.v.x, r.v.y, r.v.z );
						break;
					}
					case Constants.Packet.UseAction:
					{
						UseActionPacket r = (UseActionPacket)p;
						player_use_action( r.x, r.y, r.z, r.dir, connection_to_player.get( c ) );
						break;
					}
					case Constants.Packet.HitAction:
					{
						HitActionPacket r = (HitActionPacket)p;
						player_hit_action( r.x, r.y, r.z, connection_to_player.get( c ) );
						break;
					}
					case Constants.Packet.KeepAlive:
					{
						// Accept the KeepAlivePacket
						KeepAlivePacket r = (KeepAlivePacket)p;
						ConnectionInfo i = connection_info.get( c );

						// If the id matches, update the keep-alive status
						if ( i.get_update_id() == r.id )
							i.update();

						break;
					}
					case Constants.Packet.Disconnect:
					{
						closing_queue.add( c );
						break;
					}
				}

				p = c.recieve();
			}
		}
	}

	void player_use_action( int x, int y, int z, Constants.Direction dir, Player player )
	{
		System.out.println( "Processing player use action for " + x + " " + y + " " + z + " " + dir + " " + player );
		BlockLoc loc = new BlockLoc( x, y, z, world ).get( dir );
		loc.set( Constants.Blocks.dirt );

		for ( Player p : connection_to_player.values() )
			tell_block_update( p, loc );
	}

	void player_hit_action( int x, int y, int z, Player player )
	{
		System.out.println( "Processing player hit action for " + x + " " + y + " " + z + " " + player );
		BlockLoc loc = new BlockLoc( x, y, z, world );
		loc.set( Constants.Blocks.air );

		for ( Player p : connection_to_player.values() )
			tell_block_update( p, loc );
	}

	void tell_block_update( Player p, BlockLoc loc )
	{
		if ( is_chunk_served( p, new ChunkID( loc.x, loc.y, loc.z ) ) )
			player_to_connection.get( p ).send( new BlockUpdatePacket( loc.x, loc.y, loc.z, loc.get() ) );
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

				if ( world.is_chunk_loaded( id ) && !is_chunk_served( e.getKey(), id ) )
				{
					serve_chunk( e.getKey(), id );
					id_iter.remove();
				}
			}
		}
	}

	// Serve a chunk to a player and mark it as served
	void serve_chunk( Player player, ChunkID id )
	{
		served_chunks.get( player ).add( id );
		player_to_connection.get( player ).send( new LoadChunkPacket( world.get_chunk( id ) ) );
	}

	// Check if a chunk is served for this player
	boolean is_chunk_served( Player player, ChunkID id )
	{
		return served_chunks.get( player ).contains( id );
	}
}
