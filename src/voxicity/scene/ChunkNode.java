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

package voxicity.scene;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.util.vector.Vector3f;

import voxicity.Block;
import voxicity.Chunk;
import voxicity.Constants;
import voxicity.TextureManager;

public class ChunkNode extends Node
{
	int index_buf;
	int tex_buf;
	int vert_buf;
	int num_elements;

	int block_tex;

	Chunk chunk;

	public ChunkNode( Chunk chunk )
	{
		dirty = true;

		this.chunk = chunk;
		block_tex = TextureManager.get_texture( "textures/dirt.png" );

		IntBuffer int_buf = BufferUtils.createIntBuffer(3);
		GL15.glGenBuffers( int_buf );

		this.vert_buf = int_buf.get(0);
		this.index_buf = int_buf.get(1);
		this.tex_buf = int_buf.get(2);

		this.num_elements = 0;
	}

	void clean_self()
	{
		System.out.println( "Got here!" );
		int offset = 0;
		FloatBuffer verts = BufferUtils.createFloatBuffer( 3 * 24 * Constants.Chunk.block_number );
		FloatBuffer tex_coords = BufferUtils.createFloatBuffer( 2 * 24 * Constants.Chunk.block_number );
		IntBuffer indices = BufferUtils.createIntBuffer( 24 * Constants.Chunk.block_number );

		for ( Block block : chunk.blocks )
		{
			if ( block != null )
			{
				FloatBuffer block_verts = block.gen_clean_vert_nio();
				while ( block_verts.hasRemaining() )
					verts.put( block_verts.get() );

				FloatBuffer block_tex = block.gen_tex_nio();
				while ( block_tex.hasRemaining() )
					tex_coords.put( block_tex.get() );

				IntBuffer block_indices = block.gen_index_nio();
				while ( block_indices.hasRemaining() )
					indices.put( block_indices.get() + offset );

				offset += block_indices.limit();
			}
		}

		verts.limit( verts.position() ).rewind();
		tex_coords.limit( tex_coords.position() ).rewind();
		indices.limit( indices.position() ).rewind();
		num_elements = offset;
		System.out.println( verts.limit() + " " + tex_coords.limit() + " " + indices.limit() );

		// Pass the buffer to a VBO
		System.out.println( "Binding vertex buffer" );
		System.out.flush();
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		gl_error_test();
		System.out.println( "Setting buffer data" );
		System.out.flush();
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW );
		gl_error_test();
		System.out.println( "Vertex buffer data set" );
		System.out.flush();


		System.out.println( "Size of vertex buffer is: " + GL15.glGetBufferParameter( GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE ) );

		// Pass the buffer to a VBO
		System.out.println( "Binding tex coord buffer" );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, tex_buf );
		gl_error_test();
		System.out.println( "Setting buffer data" );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, tex_coords, GL15.GL_STATIC_DRAW );
		gl_error_test();

		System.out.println( "Size of tex coord buffer is: " + GL15.glGetBufferParameter( GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE ) );

		// Pass the buffer to an IBO
		System.out.println( "Binding index buffer" );
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, index_buf );
		gl_error_test();
		System.out.println( "Setting buffer data" );
		GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW );
		gl_error_test();

		System.out.println( "Size of index buffer is: " + GL15.glGetBufferParameter( GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE ) );
	}

	void gl_error_test()
	{
		int err_code = GL11.glGetError();
		if ( err_code != GL11.GL_NO_ERROR )
		{
			throw new OpenGLException( err_code );
		}
	}

	void render_self()
	{
		// Bind VBO to vertex pointer
		GL11.glEnableClientState( GL11.GL_VERTEX_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL11.glVertexPointer( 3, GL11.GL_FLOAT, 0, 0 );

		// Bind the texture coord VBO to texture pointer
		GL11.glEnableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, tex_buf );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, block_tex );
		GL11.glTexCoordPointer( 2, GL11.GL_FLOAT, 0, 0 );

		// Bind index array
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, index_buf );

		// Draw the block
		GL12.glDrawRangeElements( GL11.GL_QUADS, 0, num_elements -1, num_elements, GL11.GL_UNSIGNED_INT, 0 );

		// Unbind both buffers
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );

		// Disable Texture pointer
		GL11.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );

		// Disable Vertex pointer
		GL11.glDisableClientState( GL11.GL_VERTEX_ARRAY );
	}
}

