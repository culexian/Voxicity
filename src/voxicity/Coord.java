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

/*
 * Class with static methods used to convert coordinates
 * between different forms. All coordinate transformation
 * should go through here.
 */
public class Coord
{
	/*
	 * Converts global coords to chunk id coords
	 */
	static int[] GlobalToChunk( int x, int y, int z )
	{
		// Return coords divided by chunk size to get id
		// Avoid chunks at -0 coords by adding -1 to negative coords
		// Avoid adding -1 on negative chunk borders where
		// ( x|y|z mod chunk_size == 0 ). Avoids blocks being
		// a whole chunk away from actual location
		return new int[]{
		                 x / Constants.Chunk.side_length + ( ( ( x >= 0 ) || ( x % Constants.Chunk.side_length == 0 ) ) ? 0 : -1 ),
		                 y / Constants.Chunk.side_length + ( ( ( y >= 0 ) || ( y % Constants.Chunk.side_length == 0 ) ) ? 0 : -1 ),
		                 z / Constants.Chunk.side_length + ( ( ( z >= 0 ) || ( z % Constants.Chunk.side_length == 0 ) ) ? 0 : -1 )
		                };
	}

	/*
	 * Converts a chunk id to global coords for 0,0,0 offset in chunk
	 */
	static int[] ChunkToGlobal( int x, int y, int z )
	{
		return new int[]{
		                 x * Constants.Chunk.side_length,
		                 y * Constants.Chunk.side_length,
		                 z * Constants.Chunk.side_length
		                };
	}

	/*
	 * Converts global coords to chunk-local offset
	 */
	static int[] GlobalToChunkOffset( int x, int y, int z )
	{
		// First take the modulus of chunk size on all coordinates
		x = x % Constants.Chunk.side_length;
		y = y % Constants.Chunk.side_length;
		z = z % Constants.Chunk.side_length;

		// Short circuit return on 0 to avoid ( 32 - 0 ) on negative chunk borders
		// Return inverted modulus on negative coords to keep chunk offset
		// orientation uniform in all coords
		return new int[]{
		                 x >= 0 ? x : ( Constants.Chunk.side_length + x ),
		                 y >= 0 ? y : ( Constants.Chunk.side_length + y ),
		                 z >= 0 ? z : ( Constants.Chunk.side_length + z )
		                };
	}

	/*
	 * Converts a set of chunk ids and offsets to a global coord
	 */
	static int[] ChunkOffsetToGlobal( int[] chunk, int[] offset )
	{
		return new int[]{ chunk[0] * Constants.Chunk.side_length + offset[0], chunk[1] * Constants.Chunk.side_length + offset[1], chunk[2] * Constants.Chunk.side_length + offset[2] };
	}
}
