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

import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public class Block
{
	int id = Constants.Blocks.dirt;
	int data = 0;

	static ArrayList<Integer> block_texes = new ArrayList<Integer>();

	{
		register_block_tex( id, TextureManager.get_texture( "textures/dirt.png" ) );
	}

	protected static void register_block_tex( int id, int tex )
	{
		block_texes.ensureCapacity( id + 1 );

		if ( block_texes.size() < id + 1 )
			for ( int i = id + 1 - block_texes.size() ; i <= id + 1 ; i++ )
				block_texes.add( 0 );

		block_texes.set( id, tex );
	}

	public int get_id()
	{
		return id;
	}

	public int get_tex()
	{
		return block_texes.get( id );
	}

	static float[] gen_vert_data()
	{
		// Return coordinates for 6 faces of 4 vertices each
		// that make a cube
		return new float[]{
		                   // Left
		                   -0.5f,  0.5f,  0.5f,
		                   -0.5f,  0.5f, -0.5f,
		                   -0.5f, -0.5f,  0.5f,
		                   -0.5f, -0.5f, -0.5f,

		                   // Right
		                    0.5f,  0.5f,  0.5f,
		                    0.5f,  0.5f, -0.5f,
		                    0.5f, -0.5f,  0.5f,
		                    0.5f, -0.5f, -0.5f,

		                   // Top
		                    0.5f,  0.5f,  0.5f,
		                    0.5f,  0.5f, -0.5f,
		                   -0.5f,  0.5f,  0.5f,
		                   -0.5f,  0.5f, -0.5f,

		                   // Bottom
		                    0.5f, -0.5f,  0.5f,
		                    0.5f, -0.5f, -0.5f,
		                   -0.5f, -0.5f,  0.5f,
		                   -0.5f, -0.5f, -0.5f,

		                   // Front
		                    0.5f,  0.5f,  0.5f,
		                    0.5f, -0.5f,  0.5f,
		                   -0.5f,  0.5f,  0.5f,
		                   -0.5f, -0.5f,  0.5f,

		                   // Back
		                    0.5f,  0.5f, -0.5f,
		                    0.5f, -0.5f, -0.5f,
		                   -0.5f,  0.5f, -0.5f,
		                   -0.5f, -0.5f, -0.5f,
		                  };
	}

	static FloatBuffer gen_vert_nio()
	{
		// Store the vertices in a buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( 24 * 3 );
		buf.put( gen_vert_data() );
		buf.rewind();

		return buf;
	}

	static FloatBuffer gen_clean_vert_nio( Vector3f pos )
	{
		FloatBuffer verts = gen_vert_nio();

		for ( int i = 0 ; i < verts.limit() ; i++ )
		{
			if ( i % 3 == 0 ) verts.put( i, verts.get(i) + pos.x );
			if ( i % 3 == 1 ) verts.put( i, verts.get(i) + pos.y );
			if ( i % 3 == 2 ) verts.put( i, verts.get(i) + pos.z );
		}

		verts.rewind();

		return verts;
	}

	static int[] gen_index_data()
	{
		// Return array with indices for a cube, 6 sides, 1 quad each
		return new int[]{
		                  0, 1, 3, 2,     // Left
		                  6, 7, 5, 4,     // Right
		                  8, 9, 11, 10,   // Top
		                  14, 15, 13, 12, // Bottom
		                  17, 16, 18, 19, // Front
		                  23, 22, 20, 21, // Back
		                };
	}

	static IntBuffer gen_index_nio()
	{
		// Store the indices in a buffer
		IntBuffer buf = BufferUtils.createIntBuffer( 6 * 4 );
		buf.put( gen_index_data() );
		buf.rewind();

		return buf;
	}

	static float[] gen_tex_data()
	{
		// Return texture coords for 6 sides of 4 vertices that make a cube
		return new float[]{
		                   // Left
		                   1, 0,
		                   0, 0,
		                   1, 1,
		                   0, 1,

		                   // Right
		                   0, 0,
		                   1, 0,
		                   0, 1,
		                   1, 1,

		                   // Top
		                   1, 0,
		                   1, 1,
		                   0, 0,
		                   0, 1,

		                   // Bottom
		                   0, 0,
		                   0, 1,
		                   1, 0,
		                   1, 1,

		                   // Front
		                   0, 0,
		                   0, 1,
		                   1, 0,
		                   1, 1,

		                   // Back
		                   1, 0,
		                   1, 1,
		                   0, 0,
		                   0, 1,
		                  };
	}

	static FloatBuffer gen_tex_nio()
	{
		// Store the coords in a buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( 6 * 4 * 2 ); // Size 8 for now
		buf.put( gen_tex_data() );
		buf.rewind();

		return buf;
	}

	static AABB get_bounds()
	{
		return new AABB( 1, 1, 1 ); 
	}
}
