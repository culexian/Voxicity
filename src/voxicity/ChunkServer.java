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
				float o1_dist = Vector3f.sub( new Vector3f( o1.id.get(0), o1.id.get(1), o1.id.get(2) ), Voxicity.camera, null ).lengthSquared();
				float o2_dist = Vector3f.sub( new Vector3f( o2.id.get(0), o2.id.get(1), o2.id.get(2) ), Voxicity.camera, null ).lengthSquared();
				if ( o1_dist < o2_dist )
				{
					return -1;
				}
				else
				{
					return 1;
				}
			}
		}
	);
	ExecutorService executor = Executors.newSingleThreadExecutor();

	private class Loader implements Runnable
	{
		public final ArrayList<Integer> id;
		Chunk result;

		public Loader( ArrayList<Integer> id )
		{
			this.id = id;
		}

		public void run()
		{
			Chunk new_chunk = new Chunk( id.get(0), id.get(1), id.get(2) );
			float dist = Vector3f.sub( new Vector3f( id.get(0), id.get(1), id.get(2) ), Voxicity.camera, null ).lengthSquared();
			System.out.println( "New chunk is ready: x - " + id.get(0) + " y - " + id.get(1) + " z - " + id.get(2) + " at distance " + dist );
			result = new_chunk;
			Thread.currentThread().yield();
		}

		public Chunk result()
		{
			return result;
		}
	}

	public void load_chunk( ArrayList<Integer> id )
	{
		if ( chunk_queued( id ) )
			return;

		Loader loader = new Loader( id );
		queue.offer( loader );
	}

	public Chunk get_next_chunk()
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

	boolean chunk_queued( ArrayList<Integer> id )
	{
		for ( Loader loader : queue )
		{
			if ( loader.id.equals( id ) )
				return true;
		}

		for ( Loader loader : loading )
		{
			if ( loader.id.equals( id ) )
				return true;
		}

		return false;
	}

	public void shutdown()
	{
		executor.shutdownNow();
	}
}
