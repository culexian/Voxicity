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

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import voxicity.scene.ChunkNode;

public class Chunk
{
	int x, y, z;

	ChunkNode node;

	public Block[] blocks = new Block[Constants.Chunk.block_number];

	public Chunk( int x, int y, int z )
	{
		this.x = x * Constants.Chunk.side_length;
		this.y = y * Constants.Chunk.side_length;
		this.z = z * Constants.Chunk.side_length;

		System.out.println( "Created chunk at " + x + " " + y + " " + z );

		node = new ChunkNode( this );
		node.set_pos( this.x, this.y, this.z );

		generate_blocks();
	}

	public int get_block_pos( int x, int y, int z )
	{
		int block_pos = 0;
		block_pos += Math.abs(x);
		block_pos += Math.abs(y) * Constants.Chunk.side_length * Constants.Chunk.side_length;
		block_pos += Math.abs(z) * Constants.Chunk.side_length;

		return block_pos;
	}

	public Block get_block( int x, int y, int z )
	{
		int[] offset = Coord.GlobalToChunkOffset( x, y, z );
		int block_pos = get_block_pos( offset[0], offset[1], offset[2] );

		if ( ( block_pos < 0 ) || ( block_pos > Constants.Chunk.block_number - 1 ) )
			return null;

		return blocks[block_pos];
	}

	public void set_block( int x, int y, int z, Block block )
	{
		int[] offset = Coord.GlobalToChunkOffset( x, y, z );
		int block_pos = get_block_pos( offset[0], offset[1], offset[2] );
		if ( ( block_pos < 0 ) || ( block_pos > Constants.Chunk.block_number - 1 ) )
		{
			return;
		}

		if ( block != null )
		{
			block.pos_x = offset[0];
			block.pos_y = offset[1];
			block.pos_z = offset[2];
		}

		blocks[block_pos] = block;
		node.mark();
	}

	public void render()
	{
		GL11.glPushMatrix();
		GL11.glTranslatef( x, y, z );

		for ( Block block : blocks )
		{
			if ( block != null )
				block.render();
		}

		GL11.glPopMatrix();
	}

	public void generate_blocks()
	{
		for ( int x = 0 ; x < Constants.Chunk.side_length ; x++ )
		{
			for ( int y = 0 ; y < Constants.Chunk.side_length ; y++ )
			{
				for ( int z = 0 ; z < Constants.Chunk.side_length ; z++ )
				{
					if ( (y + this.y) > Math.max( 0.001f * (x + this.x)*(x + this.x) , 0.001f * (z + this.z)*(z + this.z) ) )
						set_block( x, y, z, null );
					else
						set_block( x, y, z, new Block( x, y, z ) );
				}
			}
		}
	}
}
