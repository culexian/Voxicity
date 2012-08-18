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

import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.opengl.Display;

public class Client
{
	boolean quitting = false;

	long last_packet_time = Time.get_time_ms();

	BackgroundTask chunk_requester;
	Config config;
	Connection connection;
	FPSCounter fps_counter = new FPSCounter();
	HUD hud;
	InputHandler input_handler;
	Player player = new Player();
	Renderer renderer;
	RenderUpdater render_updater;
	World world;

	public Client( Config config, Connection connection )
	{
		this.config = config;
		this.connection = connection;
		this.world = new World( config );
		this.renderer = new Renderer( config );
		this.render_updater = new RenderUpdater( this );
		this.chunk_requester = new ChunkRequester( player, world, connection );
		this.hud = new HUD();
		this.input_handler = new InputHandler( this );
	}

	public void init()
	{
		chunk_requester.start();
		fps_counter.reset();
		player.pos.set( 0, 3, 0 );
		input_handler.init();
	}

	public void run()
	{
		init();

		while( !quitting )
		{
			update();
		}

		shutdown();
	}

	void quit()
	{
		quitting = true;
	}

	void update()
	{
		handle_packets();
		check_connection();
		input_handler.update( world );
		input_handler.place_loc.set( input_handler.calc_place_loc() );
		input_handler.update_camera();
		hud.set_loc( player.pos );
		hud.set_chunks( renderer.draw_calls, world.chunks.size(), renderer.batch_draw_calls );
		hud.set_quads( renderer.quads );
		renderer.render();
		hud.render();
		Display.update();
		update_fps();
		tell_player_position();
	}

	void shutdown()
	{
		chunk_requester.quit();
		disconnect();
		render_updater.quit();
	}

	void update_fps()
	{
		if ( fps_counter.time_delta() > 200 )
		{
			hud.set_fps( fps_counter.fps() );
			fps_counter.reset();
		}

		fps_counter.update();
	}

	void handle_packets()
	{
		Packet packet = connection.recieve();

		while ( packet != null )
		{
			// Update the last time a packet was recieved from the server
			last_packet_time = Time.get_time_ms();

			switch ( packet.get_id() )
			{
				case Constants.Packet.LoadChunk:
				{
					LoadChunkPacket p = (LoadChunkPacket)packet;
					Chunk c = p.chunk;
					world.set_chunk( c.x, c.y, c.z, c );
					render_updater.set_chunk( c.x, c.y, c.z, c );
					break;
				}
				case Constants.Packet.BlockUpdate:
				{
					BlockUpdatePacket p = (BlockUpdatePacket)packet;
					System.out.println( "Server told client to update block " + p.x + " " + p.y + " " + p.z + " " + p.id );
					world.set_block( p.x, p.y, p.z, p.id );
					break;
				}
				case Constants.Packet.KeepAlive:
				{
					// Accept the KeepAlivePacket and
					// return the same one to the server
					connection.send( packet );
					break;
				}
				case Constants.Packet.Disconnect:
				{
					disconnect();
					break;
				}
			}

			packet = connection.recieve();
		}
	}

	void tell_player_position()
	{
		connection.send( new PlayerMovePacket( player.pos ) );
	}

	void tell_use_action( BlockLoc loc, Direction dir )
	{
		connection.send( new UseActionPacket( loc, dir ) );
	}

	void tell_hit_action( int x, int y, int z )
	{
		connection.send( new HitActionPacket( x, y, z ) );
	}

	void check_connection()
	{
		long delta = Time.get_time_ms() - last_packet_time;

		// If more than 10 seconds have passed with no packets, disconnect
		if ( delta > 10000 )
			disconnect();
	}

	void disconnect()
	{
		connection.send( new DisconnectPacket() );
		connection.wait_send();
		connection.close();
		quit();
	}
}
