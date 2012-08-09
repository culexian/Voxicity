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

import java.util.Map;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

public class World
{
	Config config;

	// Chunk lookup map
	Map< ChunkID, Chunk > chunks = new HashMap< ChunkID, Chunk >();

	public World( Config config )
	{
		this.config = config;
	}

	// Checks if a chunk is loaded and returns true if so
	public synchronized boolean is_chunk_loaded( ChunkID id )
	{
		return ( chunks.get( id ) != null );
	}

	public boolean is_chunk_loaded( int x, int y, int z )
	{
		return is_chunk_loaded( new ChunkID( x, y, z ) );
	}

	public synchronized Chunk get_chunk( ChunkID id )
	{
		return chunks.get( id );
	}

	public Chunk get_chunk( int x, int y, int z )
	{
		return get_chunk( new ChunkID( x, y, z ) );
	}

	// Sets a chunk in the world to the given chunk.
	// Doesn't care about overwriting existing chunks
	// Marks neighbors for update after.
	public synchronized void set_chunk( int x, int y, int z, Chunk chunk )
	{
		if ( chunk == null ) return;

		ChunkID id = new ChunkID( x, y, z );
		chunk.world = this;
		chunks.put( id, chunk );
		mark_neighbors( id );
	}

	synchronized void mark_neighbors( ChunkID id )
	{
		if ( is_chunk_loaded( id.get( Direction.West ) ) )
			get_chunk( id.get( Direction.West ) ).update_timestamp();

		if ( is_chunk_loaded( id.get( Direction.East ) ) )
			get_chunk( id.get( Direction.East ) ).update_timestamp();

		if ( is_chunk_loaded( id.get( Direction.North ) ) )
			get_chunk( id.get( Direction.North ) ).update_timestamp();

		if ( is_chunk_loaded( id.get( Direction.South ) ) )
			get_chunk( id.get( Direction.South ) ).update_timestamp();

		if ( is_chunk_loaded( id.get( Direction.Up ) ) )
			get_chunk( id.get( Direction.Up ) ).update_timestamp();

		if ( is_chunk_loaded( id.get( Direction.Down ) ) )
			get_chunk( id.get( Direction.Down ) ).update_timestamp();
	}

	public int get_block( int x, int y, int z )
	{
		Chunk c = chunks.get( new ChunkID( x, y, z ) );

		// If chunk is not loaded, block is air
		if ( c == null )
			return Constants.Blocks.air;
		else // Otherwise, return the loaded block
			return c.get_block( x, y, z );
	}

	public int get_block( float x, float y, float z )
	{
		return get_block( Math.round( x ), Math.round( y ), Math.round( z ) );
	}

	// Sets the block id of a block in the world if that chunk is
	// loaded
	public void set_block( int x, int y, int z, int id )
	{
		Chunk c = chunks.get( new ChunkID( x, y, z ) );

		// Don't operate on null, otherwise, set the block
		if ( c != null )
			c.set_block( x, y, z, id );
	}

	public void set_block( float x, float y, float z, int id )
	{
		set_block( Math.round( x ), Math.round( y ), Math.round( z ), id );
	}

	public AABB get_hit_box( int x, int y, int z )
	{
		int id = get_block( x, y, z );

		if ( id == Constants.Blocks.air )
			return null;

		AABB box = Cube.bounds();
		box.center_on( x, y, z );
		return box;
	}
}
