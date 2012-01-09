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
import java.util.HashSet;

public class ChunkServer
{
	HashSet< ArrayList<Integer > > chunks_to_load = new HashSet< ArrayList<Integer> >();

	public void load_chunk( ArrayList<Integer> id )
	{
		chunks_to_load.add( id );
	}

	public Chunk get_next_chunk()
	{
		if ( chunks_to_load.isEmpty() )
			return null;

		ArrayList<Integer> id = chunks_to_load.iterator().next();

		chunks_to_load.remove( id );

		Chunk new_chunk = new Chunk( id.get(0), id.get(1), id.get(2) );

		return new_chunk;
	}
}
