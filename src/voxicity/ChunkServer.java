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

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.lwjgl.util.vector.Vector3f;

public class ChunkServer
{
	BlockingQueue<Loader> loading = new LinkedBlockingQueue<Loader>();

	BlockingQueue<Loader> queue = new PriorityBlockingQueue<Loader>( 1, new java.util.Comparator<Loader>()
	{
		public int compare( Loader o1, Loader o2 )
		{
			if ( o1.shortest_dist() < o2.shortest_dist() )
				return -1;
			else
				return 1;
		}
	});

	ExecutorService executor = Executors.newSingleThreadExecutor();

	private class Loader implements Runnable
	{
		public final ArrayList<Integer> id;
		List< Player > players = new LinkedList< Player >();
		Chunk result;

		public Loader( ArrayList<Integer> id, Player player )
		{
			this.id = id;
			add_player( player );
		}

		public void add_player( Player player )
		{
			if ( !players.contains( player ) )
				players.add( player );
		}

		public void run()
		{
			Chunk new_chunk = new Chunk( id.get(0), id.get(1), id.get(2) );
			result = new_chunk;
			Thread.currentThread().yield();
		}

		public float shortest_dist()
		{
			float dist = Float.POSITIVE_INFINITY;

			for ( Player p : players )
				dist = Math.min( dist, Vector3f.sub( new Vector3f( id.get(0), id.get(1), id.get(2) ), p.pos, null ).lengthSquared() );

			return dist;
		}

		public Chunk result()
		{
			return result;
		}
	}

	synchronized public void load_chunk( ArrayList<Integer> id, Player player )
	{
		Loader loader = chunk_queued( id );

		if ( chunk_queued( id ) != null )
			loader.add_player( player );
		else
			queue.offer( new Loader( id, player ) );
	}

	synchronized public Chunk get_next_chunk()
	{
		if( !queue.isEmpty() )
		{
			Loader loader = queue.poll();
			loading.add( loader );
			executor.execute( loader );
		}

		if ( loading.isEmpty() || loading.peek().result() == null )
		{
			return null;
		}
		else
			return loading.poll().result();
	}

	private Loader chunk_queued( ArrayList<Integer> id )
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
