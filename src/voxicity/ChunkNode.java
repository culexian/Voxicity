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

public class ChunkNode
{
	Vector3f pos = new Vector3f();

	int tex_buf;
	int vert_buf;

	List<Batch> batches = new ArrayList<Batch>();

	static int shader_prog;

	Chunk chunk;
	long last_update;

	boolean dirty = true;
	boolean empty = true;

	static FloatBuffer verts = BufferUtils.createFloatBuffer( 3 * 24 * Constants.Chunk.block_number );
	static FloatBuffer tex_coords = BufferUtils.createFloatBuffer( 2 * 24 * Constants.Chunk.block_number );

	public class Batch
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
		this.chunk = chunk;
	}

	public void set_pos( float x, float y, float z )
	{
		pos.x = x;
		pos.y = y;
		pos.z = z;
	}

	public boolean clean()
	{
		if ( !dirty && ( last_update >= chunk.get_timestamp() ) )
			return false;
 
		last_update = Time.get_time_ms();

		if ( vert_buf == 0 )
		{
			IntBuffer buf = BufferUtils.createIntBuffer(1);
			GL15.glGenBuffers( buf );
			vert_buf = buf.get(0);
		}

		if ( tex_buf == 0 )
		{
			IntBuffer buf = BufferUtils.createIntBuffer(1);
			GL15.glGenBuffers( buf );
			tex_buf = buf.get(0);
		}

		if ( shader_prog == 0 )
			create_shader_prog();

		int offset = 0;

		clear_batches();

		verts.clear();
		tex_coords.clear();

		Map< Integer, IntBuffer> id_ind = new HashMap< Integer, IntBuffer >();

		BlockChunkLoc loc = new BlockChunkLoc( 0, 0, 0, chunk );

		// Iterate through all blocks in the chunk
		for ( int i = 0 ; i < Constants.Chunk.side_length ; i++ )
			for ( int j = 0 ; j < Constants.Chunk.side_length ; j++ )
				for ( int k = 0 ; k < Constants.Chunk.side_length ; k++ )
				{
					// Get block id
					int id = chunk.get_block( i, j, k );

					// If air, do nothing, next block
					if ( id != Constants.Blocks.air )
					{
						loc.x = i;
						loc.y = j;
						loc.z = k;

						Block b = Blocks.get( id );

						// If culled, do nothing, next block
						if ( !cull( loc ) && !chunk_edge_cull( loc ) )
						{
							// Get the vertices for this block and put them in the verts buffer
							verts.put( Coord.offset_coords( b.vertices(), new Vector3f( loc.x, loc.y, loc.z ) ) );

							// Get the texture coords for this block and put them in the tex_coords buffer
							tex_coords.put( b.texture_coords() );

							// Look up the texture for these vertices
							int tex_id = TextureManager.get_texture( b.texture_string() );

							// Look up the index buffer for this texture and create it if needed
							if ( !id_ind.containsKey( tex_id ) )
								id_ind.put( tex_id, BufferUtils.createIntBuffer( 24 * Constants.Chunk.block_number ) );

							// Get the index buffer for this texture
							IntBuffer ind_buf = id_ind.get( tex_id );

							// Get the indices for this block's vertices and put them in the ind_buf buffer after offsetting them
							IntBuffer block_indices = b.indices();
							while ( block_indices.hasRemaining() )
								ind_buf.put( block_indices.get() + offset );

							// Increase the offset by the number of indices
							offset += block_indices.position();
						}
					}
				}

		if ( verts.position() == 0 )
		{
			empty = true;
			dirty = false;
			return true;
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

			GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );
			GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );

		}

		empty = false;
		dirty = false;
		return true;
	}

	public void render( Frustum camera )
	{
		if ( empty )
			return;

		AABB chunk_box = new AABB( Constants.Chunk.side_length, Constants.Chunk.side_length, Constants.Chunk.side_length );

		chunk_box.center_on( chunk.get_x() + chunk_box.dimensions().x, chunk.get_y() + chunk_box.dimensions().y, chunk.get_z() + chunk_box.dimensions().z );

		//if ( !voxicity.Voxicity.cam_vol.collides_plane_check( chunk_box ) )
		if ( !camera.collides( chunk_box ) )
			return;

		// Push the world matrix
		GL11.glPushMatrix();

		// Translate to this chunk
		GL11.glTranslatef( pos.x, pos.y, pos.z );

		Renderer.draw_calls++;

		if ( shader_prog != 0 )
			GL20.glUseProgram( shader_prog );

		// Bind VBO to vertex pointer
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vert_buf );
		GL11.glVertexPointer( 3, GL11.GL_FLOAT, 0, 0 );

		// Bind the texture coord VBO to texture pointer
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, tex_buf );
		GL11.glTexCoordPointer( 2, GL11.GL_FLOAT, 0, 0 );

		int tex_bak = GL11.glGetInteger( GL11.GL_TEXTURE_BINDING_2D );

		for ( Batch batch : batches )
		{
			// Bind the texture for this batch
			GL11.glBindTexture( GL11.GL_TEXTURE_2D, batch.tex );

			// Bind index array
			GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, batch.indices );

			// Draw the block
			GL12.glDrawRangeElements( GL11.GL_QUADS, 0, Constants.Chunk.block_number * 24 -1, batch.num_elements, GL11.GL_UNSIGNED_INT, 0 );

			Renderer.batch_draw_calls++;
			Renderer.quads += batch.num_elements;
		}

		// Unbind the texture
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, tex_bak );

		// Unbind all buffers
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );

		// Disable the shader once more
		if ( shader_prog != 0 )
		GL20.glUseProgram( 0 );

		// Pop the world matrix
		GL11.glPopMatrix();
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
			GL20.glUniform1i( uniform, 0 );
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

	boolean cull( BlockChunkLoc loc )
	{
		for ( Direction dir : Direction.values() )
		{
			if ( loc.get( dir ).get() == Constants.Blocks.air )
				return false;
		}

		return true;
	}

	boolean chunk_edge_cull( BlockChunkLoc loc )
	{
		BlockLoc world_loc = new BlockLoc( loc );

		for ( Direction dir : Direction.values() )
		{
			BlockLoc dir_loc = world_loc.get( dir );

			if ( dir_loc.available() && dir_loc.get() == Constants.Blocks.air )
				return false;
		}

		return true;
	}

	void clear_batches()
	{
		for ( Batch batch : batches )
		{
			GL15.glDeleteBuffers( batch.indices );
		}

		batches.clear();
	}
}
