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

import de.matthiasmann.twl.*;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.SharedDrawable;
import org.lwjgl.util.glu.GLU;

public class Renderer
{
	public static int quads = 0;
	public static int draw_calls = 0;
	public static int batch_draw_calls = 0;

	private Config config;

	// Contains the full set of batches to render
	private Set< ChunkNode.Batch > batches = new HashSet< ChunkNode.Batch >();

	// The lock used for locking the set of batches
	private ReentrantLock lock = new ReentrantLock();

	Frustum camera = new Frustum();

	public Renderer( Config config )
	{
		this.config = config;
		setup_camera( 45.0f, 1200 / 720.0f, 1000f );
	}

	public void add_remove( ArrayList< ChunkNode.Batch > additions, ArrayList< ChunkNode.Batch > removals )
	{
		lock.lock();

		for ( ChunkNode.Batch batch : removals )
			batches.remove( batch );

		batches.addAll( additions );

		lock.unlock();
	}

	public void render()
	{
		lock.lock();

		quads = 0;
		draw_calls = 0;
		batch_draw_calls = 0;

		GL11.glLoadIdentity();
		GLU.gluLookAt( camera.pos.x, camera.pos.y, camera.pos.z,
		               camera.pos.x + camera.look.x, camera.pos.y + camera.look.y, camera.pos.z  + camera.look.z,
		               camera.up.x, camera.up.y, camera.up.z );

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		for ( ChunkNode.Batch batch : batches )
			render_batch( batch );

		GL11.glFlush();

		lock.unlock();
	}

	void setup_camera( float fov, float ratio, float view_distance )
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective( fov, ratio, 0.01f, view_distance );
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		camera.set_attribs( fov, ratio, 0.01f, view_distance );
	}

	private void render_batch( ChunkNode.Batch batch )
	{
		if ( !camera.collides( batch.box ) )
			return;

		Renderer.draw_calls++;

		// Use the shader the batch needs
		GL20.glUseProgram( batch.shader );

		// Bind VBO to vertex pointer
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, batch.vert_buf );
		GL11.glVertexPointer( 3, GL11.GL_FLOAT, 0, 0 );

		// Bind the texture coord VBO to texture pointer
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, batch.tex_buf );
		GL11.glTexCoordPointer( 2, GL11.GL_FLOAT, 0, 0 );

		// Bind the texture for this batch
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, batch.tex );

		// Bind index array
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, batch.indices );

		// Draw the block
		GL12.glDrawRangeElements( GL11.GL_QUADS, 0, Constants.Chunk.block_number * 24 -1, batch.num_elements, GL11.GL_UNSIGNED_INT, 0 );

		Renderer.batch_draw_calls++;
		Renderer.quads += batch.num_elements;

		// Unbind the texture
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, 0 );

		// Unbind all buffers
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );

		GL20.glUseProgram( 0 );
	}
}
