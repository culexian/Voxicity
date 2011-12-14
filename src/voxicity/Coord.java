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

public class Coord
{
	static int[] GlobalToChunk( int x, int y, int z )
	{
		return new int[]{
		                 x / Constants.Chunk.side_length + ( ( ( x >= 0 ) || ( x % Constants.Chunk.side_length == 0 ) ) ? 0 : -1 ),
		                 y / Constants.Chunk.side_length + ( ( ( y >= 0 ) || ( y % Constants.Chunk.side_length == 0 ) ) ? 0 : -1 ),
		                 z / Constants.Chunk.side_length + ( ( ( z >= 0 ) || ( z % Constants.Chunk.side_length == 0 ) ) ? 0 : -1 )
		                };
	}

	static int[] ChunkToGlobal( int x, int y, int z )
	{
		return new int[]{ x, y, z };
	}

	static int[] GlobalToChunkOffset( int x, int y, int z )
	{
		x = x % Constants.Chunk.side_length;
		y = y % Constants.Chunk.side_length;
		z = z % Constants.Chunk.side_length;

		return new int[]{
		                 x >= 0 ? x : ( Constants.Chunk.side_length + x ),
		                 y >= 0 ? y : ( Constants.Chunk.side_length + y ),
		                 z >= 0 ? z : ( Constants.Chunk.side_length + z )
		                };
	}

	static int[] ChunkOffsetToGlobal( int[] chunk, int[] offset )
	{
		return new int[]{ chunk[0] * Constants.Chunk.side_length + offset[0], chunk[1] * Constants.Chunk.side_length + offset[1], chunk[2] * Constants.Chunk.side_length + offset[2] };
	}
}
