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

import java.util.List;
import java.util.LinkedList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.lwjgl.util.vector.Vector3f;

public class ChunkServer
{
	// List of loaders being processed or done
	LinkedList<Loader> loading = new LinkedList<Loader>();

	// List of Loaders to be processed
	LinkedList<Loader> queue = new LinkedList<Loader>();

	// Comparator for sorting queue
	java.util.Comparator< Loader > comp = new java.util.Comparator<Loader>()
	{
		public int compare( Loader o1, Loader o2 )
		{
			if ( o1.shortest_dist() < o2.shortest_dist() )
				return -1;
			else
				return 1;
		}
	};

	// Service for async chunk loading
	ExecutorService executor = Executors.newSingleThreadExecutor();

	private class Loader implements Runnable
	{
		public final ChunkID id;
		List< Player > players = new LinkedList< Player >();
		Chunk result;

		public Loader( ChunkID id, Player p )
		{
			this.id = new ChunkID( id );
			add_player( p );
		}

		public void add_player( Player player )
		{
			if ( !players.contains( player ) )
				players.add( player );
		}

		public void run()
		{
			Chunk new_chunk = new Chunk( id );
			result = new_chunk;
			Thread.currentThread().yield();
		}

		public float shortest_dist()
		{
			float dist = Float.POSITIVE_INFINITY;

			for ( Player p : players )
				dist = Math.min( dist, Vector3f.sub( id.coords(), p.pos, null ).lengthSquared() );

			return dist;
		}

		public Chunk result()
		{
			return result;
		}
	}

	public void load_chunk( ChunkID id, Player player )
	{
		Loader loader = chunk_queued( id );

		if ( loader != null )
			loader.add_player( player );
		else
		{
			queue.add( new Loader( id, player ) );
			java.util.Collections.sort( queue, comp );
		}
	}

	public Chunk get_next_chunk()
	{
		if( !queue.isEmpty() )
		{
			java.util.Collections.sort( queue, comp );
			Loader loader = queue.poll();
			loading.add( loader );
			executor.execute( loader );
		}

		if ( loading.isEmpty() || loading.peek().result() == null )
			return null;
		else
			return loading.poll().result();
	}

	private Loader chunk_queued( ChunkID id )
	{
		for ( Loader loader : queue )
			if ( loader.id.equals( id ) )
				return loader;

		for ( Loader loader : loading )
			if ( loader.id.equals( id ) )
				return loader;

		return null;
	}

	public void shutdown()
	{
		executor.shutdownNow();
	}
}
