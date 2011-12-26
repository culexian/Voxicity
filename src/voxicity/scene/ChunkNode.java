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

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.vector.Vector3f;

import voxicity.Block;
import voxicity.Chunk;
import voxicity.Constants;
import voxicity.TextureManager;

public class ChunkNode extends Node
{
	int tex_buf;
	int vert_buf;

	int block_tex;

	Chunk chunk;

	List<Batch> batches = new ArrayList<Batch>();

	static int shader_prog;

	private class Batch
	{
		public final int tex;
		public final int indices;
		public final int num_elements;

		public Batch( int tex, int indices, int num_elements )
		{
			this.tex = tex;
			this.indices = indices;
			this.num_elements = num_elements;
		}
	}

	public ChunkNode( Chunk chunk )
	{
		dirty = true;

		this.chunk = chunk;
		block_tex = TextureManager.get_texture( "textures/stone.png" );

		IntBuffer int_buf = BufferUtils.createIntBuffer(2);
		GL15.glGenBuffers( int_buf );

		this.vert_buf = int_buf.get(0);
		this.tex_buf = int_buf.get(1);

		if ( shader_prog == 0 )
			create_shader_prog();
	}

	void clean_self()
	{
		int offset = 0;
		batches.clear();
		FloatBuffer verts = BufferUtils.createFloatBuffer( 3 * 24 * Constants.Chunk.block_number );
		FloatBuffer tex_coords = BufferUtils.createFloatBuffer( 2 * 24 * Constants.Chunk.block_number );

		Map< Integer, IntBuffer> id_ind = new HashMap< Integer, IntBuffer >();

		for ( Block block : chunk.blocks )
		{
			if ( block != null )
			{
				verts.put( block.gen_clean_vert_nio() );
				tex_coords.put( block.gen_tex_nio() );

				if ( !id_ind.containsKey( block.get_tex() ) )
					id_ind.put( block.get_tex(), BufferUtils.createIntBuffer( 24 * Constants.Chunk.block_number ) );

				IntBuffer ind_buf = id_ind.get( block.get_tex() );

				IntBuffer block_indices = block.gen_index_nio();
				while ( block_indices.hasRemaining() )
					ind_buf.put( block_indices.get() + offset );

				offset += block_indices.position();
			}
		}

		verts.limit( verts.position() ).rewind();
		tex_coords.limit( tex_coords.position() ).rewind();
		System.out.println( verts.limit() + " " + tex_coords.limit() );

		// Pass the buffer to a VBO
		System.out.println( "Binding vertex buffer" );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		Util.checkGLError();
		System.out.println( "Setting buffer data" );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW );
		Util.checkGLError();
		System.out.println( "Vertex buffer data set" );

		System.out.println( "Size of vertex buffer is: " + GL15.glGetBufferParameter( GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE ) );

		// Pass the buffer to a VBO
		System.out.println( "Binding tex coord buffer" );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, tex_buf );
		Util.checkGLError();
		System.out.println( "Setting buffer data" );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, tex_coords, GL15.GL_STATIC_DRAW );
		Util.checkGLError();

		System.out.println( "Size of tex coord buffer is: " + GL15.glGetBufferParameter( GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE ) );

		for ( Map.Entry< Integer, IntBuffer > entry : id_ind.entrySet() )
		{
			entry.getValue().limit( entry.getValue().position() ).rewind();

			IntBuffer ibo = BufferUtils.createIntBuffer(1);
			GL15.glGenBuffers( ibo );

			if ( ibo.get(0) == 0 )
				System.out.println( "Could not generate buffer object!" );

			GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ibo.get(0) );
			Util.checkGLError();
			GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, entry.getValue(), GL15.GL_STATIC_DRAW );
			Util.checkGLError();
			System.out.println( "Size of index buffer is: " + GL15.glGetBufferParameter( GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE )  + " with " + entry.getValue().limit() + " indices" );

			System.out.println( "Creating batch: " + entry.getKey() + " " + ibo.get(0) + " " + entry.getValue().limit() );
			batches.add( new Batch( entry.getKey(), ibo.get(0), entry.getValue().limit() ) );
		}
	}

	void render_self()
	{
		if ( shader_prog != 0 )
			GL20.glUseProgram( shader_prog );

		// Bind VBO to vertex pointer
		GL11.glEnableClientState( GL11.GL_VERTEX_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL11.glVertexPointer( 3, GL11.GL_FLOAT, 0, 0 );

		// Bind the texture coord VBO to texture pointer
		GL11.glEnableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, tex_buf );
		GL11.glTexCoordPointer( 2, GL11.GL_FLOAT, 0, 0 );

		for ( Batch batch : batches )
		{
			// Bind the texture for this batch
			GL11.glBindTexture( GL11.GL_TEXTURE_2D, batch.tex );

			// Bind index array
			GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, batch.indices );

			// Draw the block
			GL12.glDrawRangeElements( GL11.GL_QUADS, 0, Constants.Chunk.block_number * 24 -1, batch.num_elements, GL11.GL_UNSIGNED_INT, 0 );
		}

		// Unbind all buffers
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );

		// Disable Texture pointer
		GL11.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );

		// Disable Vertex pointer
		GL11.glDisableClientState( GL11.GL_VERTEX_ARRAY );

		// Disable the shader once more
		if ( shader_prog != 0 )
		GL20.glUseProgram( 0 );
	}

	void create_shader_prog()
	{
		shader_prog = GL20.glCreateProgram();

		int vert_shader = create_vert_shader( "shader/block.vert" );
		int frag_shader = create_frag_shader( "shader/block.frag" );

		GL20.glAttachShader( shader_prog, vert_shader );
		GL20.glAttachShader( shader_prog, frag_shader );
		GL20.glLinkProgram( shader_prog );

		if ( check_shader_error( shader_prog ) )
		{
			GL20.glDeleteProgram( shader_prog );
			shader_prog = 0;
		}

		GL20.glUseProgram( shader_prog );

		int uniform;
		if ( ( uniform = GL20.glGetUniformLocation( shader_prog, "textures" ) ) != -1 )
		{
			
			GL11.glBindTexture( GL11.GL_TEXTURE_2D, block_tex );
			GL20.glUniform1i( uniform, 0 );
			GL11.glBindTexture( GL11.GL_TEXTURE_2D, 0 );
		}

		System.out.println( "Textures at: " + GL20.glGetUniformLocation( shader_prog, "textures" ) );

		GL20.glUseProgram( 0 );
	}

	int create_vert_shader( String filename )
	{
		int shader = GL20.glCreateShader( GL20.GL_VERTEX_SHADER );

		String code_text = "";
		String line;
		try
		{
			BufferedReader reader=new BufferedReader(new FileReader(filename));
			while ( ( line = reader.readLine() ) != null )
			{
				code_text += line + "\n";
			}
		}
		catch ( Exception e )
		{
			System.out.println( "Reading vertex shader code failed" );
			e.printStackTrace();
		}

		GL20.glShaderSource( shader, code_text );

		GL20.glCompileShader( shader );

		System.out.println( "Compiling vertex shader: " + filename );
		if ( GL20.glGetShader( shader, GL20.GL_COMPILE_STATUS ) == GL11.GL_FALSE )
		{
			print_shader_log( shader );
		}

		if ( check_shader_error( shader ) )
		{
			GL20.glDeleteShader( shader );
			shader = 0;
		}

		return shader;
	}

	int create_frag_shader( String filename )
	{
		int shader = GL20.glCreateShader( GL20.GL_FRAGMENT_SHADER );

		String code_text = "";
		String line;
		try
		{
			BufferedReader reader=new BufferedReader(new FileReader(filename));
			while ( ( line = reader.readLine() ) != null )
			{
				code_text += line + "\n";
			}
		}
		catch ( Exception e )
		{
			System.out.println( "Reading vertex shader code failed" );
			e.printStackTrace();
		}

		GL20.glShaderSource( shader, code_text );

		System.out.println( "Compiling fragment shader: " + filename );
		GL20.glCompileShader( shader );

		if ( GL20.glGetShader( shader, GL20.GL_COMPILE_STATUS ) == GL11.GL_FALSE )
		{
			print_shader_log( shader );
		}

		if ( check_shader_error( shader ) )
		{
			GL20.glDeleteShader( shader );
			shader = 0;
		}

		return shader;
	}

	boolean check_shader_error( int shader )
	{
		return false;
	}

	static void print_shader_log( int shader )
	{
		IntBuffer log_length = BufferUtils.createIntBuffer(1);
		GL20.glGetShader( shader, GL20.GL_INFO_LOG_LENGTH, log_length );

		if ( log_length.get(0) > 1 )
		{
			System.out.println( "Shader log:\n" + GL20.glGetShaderInfoLog( shader, log_length.get(0) ) );
			
		}
	}
}
