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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkServer
{
	HashMap< ArrayList<Integer>, Loader > chunk_loaders = new HashMap< ArrayList<Integer>, Loader >();
	ExecutorService executor = Executors.newSingleThreadExecutor();

	private class Loader implements Runnable
	{
		final ArrayList<Integer> id;
		Chunk result;

		public Loader( ArrayList<Integer> id )
		{
			this.id = id;
		}

		public void run()
		{
			Chunk new_chunk = new Chunk( id.get(0), id.get(1), id.get(2) );
			result = new_chunk;
		}

		public Chunk result()
		{
			return result;
		}
	}

	public void load_chunk( ArrayList<Integer> id )
	{
		if ( chunk_loaders.containsKey( id ) )
			return;

		Loader new_loader = new Loader( id );
		chunk_loaders.put( id, new_loader );
		executor.execute( new_loader );
	}

	public Chunk get_next_chunk()
	{
		if ( chunk_loaders.isEmpty() )
			return null;

		Map.Entry< ArrayList<Integer>, Loader > entry = chunk_loaders.entrySet().iterator().next();

		Loader loader = entry.getValue();

		if ( entry.getValue() == null )
		{
			chunk_loaders.remove( entry.getKey() );
			return null;
		}
		else
		{
			if ( loader.result() == null )
				return null;
			else
			{
				chunk_loaders.remove( entry.getKey() );
				return loader.result();
			}
		}
	}

	public void shutdown()
	{
		executor.shutdownNow();
	}
}
