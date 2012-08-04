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

import org.lwjgl.util.vector.Vector3f;

public class ChunkID
{
	final int x;
	final int y;
	final int z;

	// Takes global coords and creates chunk coords
	public ChunkID( int x, int y, int z )
	{
		int[] c = Coord.GlobalToChunkBase( x, y, z );
		this.x = c[0];
		this.y = c[1];
		this.z = c[2];
	}

	public ChunkID( ChunkID id )
	{
		this( id.x, id.y, id.z );
	}

	public Vector3f coords()
	{
		return new Vector3f( x, y, z );
	}

	public boolean equals( Object o )
	{
		if ( o == null )
			return false;

		if ( !(o instanceof ChunkID ) )
			return false;

		ChunkID c = (ChunkID)o;

		if ( this.x != c.x || this.y != c.y || this.z != c.z )
			return false;

		return true;
	}

	public int hashCode()
	{
		return x ^ y ^ z;
	}

	public ChunkID get( Direction d )
	{
		switch ( d )
		{
			case East:
				return new ChunkID( x + Constants.Chunk.side_length, y, z );
			case West:
				return new ChunkID( x - Constants.Chunk.side_length, y, z );
			case North:
				return new ChunkID( x, y, z + Constants.Chunk.side_length );
			case South:
				return new ChunkID( x, y, z - Constants.Chunk.side_length );
			case Up:
				return new ChunkID( x, y + Constants.Chunk.side_length, z );
			case Down:
				return new ChunkID( x, y - Constants.Chunk.side_length, z );
			default:
				return new ChunkID( this );
		}
	}
}
