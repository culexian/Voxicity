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

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.SharedDrawable;

public class RenderUpdater implements Runnable
{
	private boolean quitting = false;
	private Client client;
	private Map< ChunkID, ChunkNode > chunks = new HashMap< ChunkID, ChunkNode >();
	private ReentrantLock lock = new ReentrantLock();
	private SharedDrawable drawable;
	private Thread thread;

	public RenderUpdater( Client client )
	{
		this.client = client;

		try
		{
			this.drawable = new SharedDrawable( Display.getDrawable() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( e );
			System.exit( 1 );
		}

		thread = new Thread( this, "Render Updater" );
		thread.start();
	}

	public void set_chunk( int x, int y, int z, Chunk chunk )
	{
		if ( chunk == null )
			return;

		lock.lock();
		ChunkNode node = new ChunkNode( chunk );
		node.set_pos( x, y, z );

		chunks.put( new ChunkID( x, y, z ), node );
		lock.unlock();
	}

	public void run()
	{
		boolean cleaned_one = false;

		try
		{
			drawable.makeCurrent();

			while ( !quitting )
			{
				ArrayList< ChunkNode.Batch > additions = new ArrayList< ChunkNode.Batch >();
				ArrayList< ChunkNode.Batch > removals = new ArrayList< ChunkNode.Batch >();

				lock.lock();
				for ( ChunkNode chunk : chunks.values() )
				{
					ArrayList< ChunkNode.Batch > old_batches = new ArrayList< ChunkNode.Batch >( chunk.batches );
					if ( chunk.clean() )
					{
						additions.addAll( new ArrayList< ChunkNode.Batch >( chunk.batches ) );
						removals.addAll( old_batches );
					}
				}
				lock.unlock();

				GL11.glFlush();

				if ( additions.size() > 0 || removals.size() > 0 )
					client.renderer.add_remove( additions, removals );

				for ( ChunkNode.Batch batch : removals )
				{
					GL15.glDeleteBuffers( batch.vert_buf );
					GL15.glDeleteBuffers( batch.tex_buf );
					GL15.glDeleteBuffers( batch.indices );
				}
			}
		}
		catch ( Exception e )
		{
			System.out.println( "Renderer exception" );
			System.out.println( e );
			e.printStackTrace();
		}
	}

	public void quit()
	{
		quitting = true;

		while ( thread.isAlive() )
			;
	}
}
