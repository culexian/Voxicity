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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import voxicity.scene.Node;

public class World
{
	Config config;

	// Chunk lookup map
	Map< Collection< Integer >, Chunk > chunks = new HashMap< Collection< Integer >, Chunk >();

	Node node;

	public World( Config config )
	{
		this.config = config;
		node = new Node();
	}

	public boolean is_chunk_loaded( int x, int y, int z )
	{
		return chunks.containsKey( get_chunk_id( x, y, z ) );
	}

	public Chunk get_chunk( int x, int y, int z )
	{
		ArrayList<Integer> id = get_chunk_id( x, y, z );

		if ( chunks.containsKey( get_chunk_id( x, y, z ) ) )
		{
			return chunks.get( id );
		}

		return null;
	}

	public void set_chunk( int x, int y, int z, Chunk chunk )
	{
		node.add_child( chunk.node );
		chunks.put( get_chunk_id( x, y, z ), chunk );
		mark_neighbors( x, y, z );
	}

	void mark_neighbors( int x, int y, int z )
	{
		if ( chunks.containsKey( get_chunk_id( x - Constants.Chunk.side_length, y, z ) ) )
			chunks.get( get_chunk_id( x - Constants.Chunk.side_length, y, z ) ).node.mark();

		if ( chunks.containsKey( get_chunk_id( x + Constants.Chunk.side_length, y, z ) ) )
			chunks.get( get_chunk_id( x + Constants.Chunk.side_length, y, z ) ).node.mark();

		if ( chunks.containsKey( get_chunk_id( x, y - Constants.Chunk.side_length, z ) ) )
			chunks.get( get_chunk_id( x, y - Constants.Chunk.side_length, z ) ).node.mark();

		if ( chunks.containsKey( get_chunk_id( x, y + Constants.Chunk.side_length, z ) ) )
			chunks.get( get_chunk_id( x, y + Constants.Chunk.side_length, z ) ).node.mark();

		if ( chunks.containsKey( get_chunk_id( x, y, z - Constants.Chunk.side_length ) ) )
			chunks.get( get_chunk_id( x, y, z - Constants.Chunk.side_length ) ).node.mark();

		if ( chunks.containsKey( get_chunk_id( x, y, z + Constants.Chunk.side_length ) ) )
			chunks.get( get_chunk_id( x, y, z + Constants.Chunk.side_length ) ).node.mark();
	}

	public static ArrayList<Integer> get_chunk_id( int x, int y, int z )
	{
		ArrayList<Integer> id = new ArrayList<Integer>();

		int[] coords = Coord.GlobalToChunk( x, y, z );

		id.add( coords[0] );
		id.add( coords[1] );
		id.add( coords[2] );

		return id;
	}

	public Block get_block( int x, int y, int z )
	{
		BlockLoc loc = new BlockLoc( x, y, z, this );

		return loc.get_block();
	}

	public Block get_block( float x, float y, float z )
	{
		return get_block( Math.round( x ), Math.round( y ), Math.round( z ) );
	}

	public void set_block( int x, int y, int z, Block block )
	{
		BlockLoc loc = new BlockLoc( x, y, z, this );

		if ( loc.get_chunk() == null )
			return;

		loc.get_chunk().set_block( x, y, z, block );
	}

	public void set_block( float x, float y, float z, Block block )
	{
		set_block( Math.round( x ), Math.round( y ), Math.round( z ), block );
	}

	public AABB get_hit_box( int x, int y, int z )
	{
		Block block = get_block( x, y, z );

		if ( block == null )
			return null;

		AABB box = block.get_bounds();
		int[] chunk = Coord.GlobalToChunkBase( x, y, z );
		Vector3f.add( box.pos, new Vector3f( chunk[0], chunk[1], chunk[2] ), box.pos );
		return box;
	}
}
