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

public class Client
{
	boolean quitting = false;

	Config config;
	Connection connection;
	Renderer renderer;
	World world_cache = new World( config );

	public Client( Config config, Connection connection )
	{
		this.config = config;
		this.connection = connection;
		this.renderer = new Renderer( config );
	}

	public void init()
	{

	}

	public void run()
	{
		while( !quitting )
		{
			
		}
	}

	void update()
	{
		handle_packets();
	}

	void handle_packets()
	{
		Packet packet = connection.recieve();

		if ( packet == null )
			return;

		switch ( packet.get_id() )
		{
			case Constants.Packet.Chunk:
			{
				ChunkPacket p = (ChunkPacket)packet;
				world_cache.set_chunk( p.x, p.y, p.z, p.chunk );
				renderer.set_chunk( p.x, p.y, p.z, p.chunk );
				break;
			}
		}
		
	}
}
