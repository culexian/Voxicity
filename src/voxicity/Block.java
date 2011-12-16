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
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.BufferUtils;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

public class Block
{
	int block_id = 0;
	int block_data = 0;
	int block_tex = TextureManager.get_texture( "textures/dirt.png" );

	int pos_x,pos_y,pos_z;

	int vert_buf = 0;
	int index_buf = 0;
	int tex_buffer = 0;

	public Block()
	{
		this( 0, 0, 0 );
	}

	public Block( int pos_x, int pos_y, int pos_z )
	{
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.pos_z = pos_z;

		IntBuffer int_buf = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers( int_buf );

		this.vert_buf = int_buf.get(0);
		this.index_buf = int_buf.get(1);
		this.tex_buffer = int_buf.get(2);

		//gen_vert_buffer();
		//gen_index_buffer();
		//gen_tex_buffer();
	}

	public void render()
	{
		GL11.glPushMatrix();

		// Translate to block location offset
		GL11.glTranslatef( pos_x, pos_y, pos_z );

		// Bind VBO to vertex pointer
		GL11.glEnableClientState( GL11.GL_VERTEX_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL11.glVertexPointer( 3, GL11.GL_FLOAT, 0, 0 );

		// Bind the texture coord VBO to texture pointer
		GL11.glEnableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, tex_buffer );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, block_tex );
		GL11.glTexCoordPointer( 2, GL11.GL_FLOAT, 0, 0 );

		// Bind index array
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, index_buf );

		// Draw the block
		GL12.glDrawRangeElements( GL11.GL_QUADS, 0, 23, 24, GL11.GL_UNSIGNED_INT, 0 );

		// Unbind both buffers
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );

		// Disable Texture pointer
		GL11.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );

		// Disable Vertex pointer
		GL11.glDisableClientState( GL11.GL_VERTEX_ARRAY );

		GL11.glPopMatrix();
	}

	float[] gen_vert_data()
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
		                  };
	}

	public FloatBuffer gen_vert_nio()
	{
		// Store the vertices in a buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( 24 * 3 );
		buf.put( gen_vert_data() );
		buf.rewind();

		return buf;
	}

	public FloatBuffer gen_clean_vert_nio()
	{
		FloatBuffer verts = gen_vert_nio();

		for ( int i = 0 ; i < verts.limit() ; i++ )
		{
			if ( i % 3 == 0 ) verts.put( i, verts.get(i) + pos_x );
			if ( i % 3 == 1 ) verts.put( i, verts.get(i) + pos_y );
			if ( i % 3 == 2 ) verts.put( i, verts.get(i) + pos_z );
		}

		verts.rewind();

		return verts;
	}

	void gen_vert_buffer()
	{
		// 8 vertices make a block
		float[] verts = {
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
		                };

		// Store the vertices in a buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( 24 * 3 );
		buf.put( verts );
		buf.rewind();

		// Pass the buffer to a VBO
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
		
	}

	public int[] gen_index_data()
	{
		// Return array with indices for a cube, 6 sides, 1 quad each
		return new int[]{
		                  0, 1, 3, 2,     // right
		                  4, 5, 7, 6,     // left
		                  8, 9, 11, 10,   // top
		                  12, 13, 15, 14, // bottom
		                  16, 17, 19, 18, // back
		                  20, 21, 23, 22, // front
		                };
	}

	public IntBuffer gen_index_nio()
	{
		// Store the indices in a buffer
		IntBuffer buf = BufferUtils.createIntBuffer( 6 * 4 );
		buf.put( gen_index_data() );
		buf.rewind();

		return buf;
	}

	void gen_index_buffer()
	{
		// Create indices for a cube, 6 sides, 1 quad each
		int indices[] = {
		                  0, 1, 3, 2,     // right
		                  4, 5, 7, 6,     // left
		                  8, 9, 11, 10,   // top
		                  12, 13, 15, 14, // bottom
		                  16, 17, 19, 18, // back
		                  20, 21, 23, 22, // front
		                };

		// Store the indices in a buffer
		IntBuffer buf = BufferUtils.createIntBuffer( 6 * 4 );
		buf.put( indices );
		buf.rewind();

		// Pass the buffer to an IBO
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, index_buf );
		GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
	}

	float[] gen_tex_data()
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

		                   // Front
		                   1, 0,
		                   1, 1,
		                   0, 0,
		                   0, 1,

		                   // Back
		                   0, 0,
		                   0, 1,
		                   1, 0,
		                   1, 1,

		                   // Top
		                   0, 0,
		                   0, 1,
		                   1, 0,
		                   1, 1,

		                   // Bottom
		                   1, 0,
		                   1, 1,
		                   0, 0,
		                   0, 1,
		                  };
	}

	public FloatBuffer gen_tex_nio()
	{
		// Store the coords in a buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( 6 * 4 * 2 ); // Size 8 for now
		buf.put( gen_tex_data() );
		buf.rewind();

		return buf;
	}

	void gen_tex_buffer()
	{
		// Create texture coords for a cube, not ready
		float[] coords = {
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

		                // Front
		                1, 0,
		                1, 1,
		                0, 0,
		                0, 1,

		                // Back
		                0, 0,
		                0, 1,
		                1, 0,
		                1, 1,

		                // Top
		                0, 0,
		                0, 1,
		                1, 0,
		                1, 1,

		                // Bottom
		                1, 0,
		                1, 1,
		                0, 0,
		                0, 1,
		               };

		// Store the coords in a buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( 6 * 4 * 2 ); // Size 8 for now
		buf.put( coords );
		buf.rewind();

		// Pass the buffer to a VBO
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, tex_buffer );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
	}

	public AABB get_bounds()
	{
		AABB box = new AABB( 1, 1, 1 );
		box.pos.x = pos_x;
		box.pos.y = pos_y;
		box.pos.z = pos_z;
		return box; 
	}
}
