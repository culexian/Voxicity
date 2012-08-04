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

public class BlockChunkLoc
{
	public Chunk chunk;
	public int x, y, z;

	public BlockChunkLoc( int x, int y, int z, Chunk chunk )
	{
		this.chunk = chunk;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int get()
	{
		if ( x < 0 || y < 0 || z < 0 )
			return Constants.Blocks.air;

		if ( ( x >= Constants.Chunk.side_length ) || ( y >= Constants.Chunk.side_length ) || ( z >= Constants.Chunk.side_length ) )
			return Constants.Blocks.air;

		return chunk.get_block( x, y, z );
	}

	public BlockChunkLoc get( Direction dir )
	{
		switch ( dir )
		{
			case North:
				return new BlockChunkLoc( x, y, z - 1, chunk );
			case East:
				return new BlockChunkLoc( x + 1, y, z, chunk );
			case South:
				return new BlockChunkLoc( x, y, z + 1, chunk );
			case West:
				return new BlockChunkLoc( x - 1, y, z, chunk );
			case Up:
				return new BlockChunkLoc( x, y + 1, z, chunk );
			case Down:
				return new BlockChunkLoc( x, y - 1, z, chunk );
			default:
				return this;
		}
	}
}
