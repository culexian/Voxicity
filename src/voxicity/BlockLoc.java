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

public class BlockLoc
{
	public World world;
	public int x, y, z;

	public BlockLoc( int x, int y, int z, World world )
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Chunk get_chunk()
	{
		if ( world == null )
			return null;

		return world.get_chunk( x, y, z );
	}

	public Block get_block()
	{
		if ( get_chunk() == null )
			return null;

		int chunk_x = x % Constants.Chunk.side_length;
		int chunk_y = y % Constants.Chunk.side_length;
		int chunk_z = z % Constants.Chunk.side_length;

		return get_chunk().get_block( chunk_x, chunk_y, chunk_z );
	}
}
