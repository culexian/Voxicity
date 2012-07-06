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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import voxicity.scene.ChunkNode;

public class Renderer
{
	Config config;
	Map< Collection< Integer >, ChunkNode > chunks = new HashMap< Collection< Integer >, ChunkNode >();

	public Renderer( Config config )
	{
		this.config = config;
	}

	public void set_chunk( int x, int y, int z, Chunk chunk )
	{
		if ( chunk == null )
			return;

		ChunkNode node = new ChunkNode( chunk );
		node.set_pos( x, y, z );

		chunks.put( World.get_chunk_id( x, y, z ), node );
	}

	public void render( Frustum camera )
	{
		Voxicity.quads = 0;
		Voxicity.draw_calls = 0;
		Voxicity.batch_draw_calls = 0;

		GL11.glLoadIdentity();
		GLU.gluLookAt( camera.pos.x, camera.pos.y, camera.pos.z, camera.pos.x + camera.look.x, camera.pos.y + camera.look.y, camera.pos.z  + camera.look.z, 0,1,0 );


		// Clear the screen and depth buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		
		System.out.println( "Before clean " + Time.get_time_ms() );
		for ( ChunkNode chunk : chunks.values() )
			chunk.clean();

		System.out.println( "Before render " + Time.get_time_ms() );
		for ( ChunkNode chunk : chunks.values() )
			chunk.render();

		System.out.println( "After render " + Time.get_time_ms() );

		TextRenderer.draw( "FPS: " + Integer.toString( Voxicity.fps), 5, 5 + TextRenderer.line_height() * 0 );
		TextRenderer.draw( "X: " + Float.toString(camera.pos.x), 5, 5 + TextRenderer.line_height() * 1 );
		TextRenderer.draw( "Y: " + Float.toString(camera.pos.y), 5, 5 + TextRenderer.line_height() * 2 );
		TextRenderer.draw( "Z: " + Float.toString(camera.pos.z), 5, 5 + TextRenderer.line_height() * 3 );
		TextRenderer.draw( "Verts: " + Integer.toString(Voxicity.quads * 4), 5, 5 + TextRenderer.line_height() * 4 );
		TextRenderer.draw( "Tris: " + Integer.toString(Voxicity.quads * 2), 5, 5 + TextRenderer.line_height() * 5 );
		TextRenderer.draw( "Render chunks: " + Integer.toString( Voxicity.draw_calls) + "/" + chunks.size(), 5, 5 + TextRenderer.line_height() * 6 );
		TextRenderer.draw( "Render batches: " + Integer.toString( Voxicity.batch_draw_calls), 5, 5 + TextRenderer.line_height() * 7 );

		Display.update();
	}
}
