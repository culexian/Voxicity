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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChunkServer implements Runnable
{
	// The thread running this object
	Thread thread;

	// The world to supply finished chunks to
	World world;

	// The queue of incoming requests for chunk serving
	LinkedBlockingQueue<ChunkID> incoming_requests = new LinkedBlockingQueue<ChunkID>();

	public ChunkServer( World world )
	{
		if ( world == null )
			throw new NullPointerException( "Argument world is null" );

		this.world = world;

		// Start this chunk serving thread
		thread = new Thread( this, "Chunk Server" );
		thread.start();
	}

	public void quit()
	{
		thread.interrupt();
	}

	public void run()
	{
		try
		{
			while ( true )
			{
				// Try to get a new request
				ChunkID id = incoming_requests.take();

				// Don't get a chunk that's already loaded
				if ( world.is_chunk_loaded( id ) )
					continue;

				Chunk c = new Chunk( id );

				world.set_chunk( id.x, id.y, id.z, c );
			}
		}
		catch ( InterruptedException e )
		{

		}
	}

	public void request_chunk( ChunkID id )
	{
		try
		{
			incoming_requests.put( id );
		}
		catch ( InterruptedException e )
		{

		}
	}
}
