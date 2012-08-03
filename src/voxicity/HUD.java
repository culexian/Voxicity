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
import de.matthiasmann.twl.theme.ThemeManager;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;

import org.lwjgl.util.vector.Vector3f;

public class HUD extends Widget
{
	GUI gui;
	Widget crosshair;
	Label debug_info;

	int fps;
	Vector3f loc = new Vector3f();
	int quads;
	int chunks_rendered;
	int max_chunks;
	int draw_calls;

	public HUD()
	{
		try
		{
			LWJGLRenderer renderer = new LWJGLRenderer();
			ThemeManager theme = ThemeManager.createThemeManager( HUD.class.getResource( "/hud.xml" ), renderer );
			gui = new GUI( this, renderer );
			gui.applyTheme( theme );
		}
		catch ( org.lwjgl.LWJGLException e )
		{
			System.out.println( e );
			e.printStackTrace();
			throw new NullPointerException( "Could not initialize LWJGLRenderer for HUD" );
		}
		catch ( java.io.IOException e )
		{
			System.out.println( e );
			e.printStackTrace();
			throw new NullPointerException( "Could not load theme file for HUD" );
		}

		crosshair = new Widget();
		crosshair.setTheme( "crosshair" );

		add( crosshair );

		debug_info = new Label();
		debug_info.setTheme( "debug_info" );
		add( debug_info );
	}
	public void render()
	{
		gui.update();
	}

	protected void layout()
	{
		crosshair.adjustSize();
		crosshair.setPosition( getInnerX() + ( getInnerWidth() - crosshair.getWidth() ) / 2,
		                       getInnerY() + ( getInnerHeight() - crosshair.getHeight() ) / 2 );

		debug_info.adjustSize();
		debug_info.setPosition( getInnerX(), getInnerY() );
	}

	public void set_fps( int fps )
	{
		this.fps = fps;

		update_debug_text();
	}

	public void set_loc( Vector3f v )
	{
		if ( v == null )
			return;

		loc.set( v.x, v.y, v.z );
	}

	public void set_quads( int quads )
	{
		this.quads = quads;
	}

	public void set_chunks( int chunks_rendered, int max_chunks, int draw_calls )
	{
		this.chunks_rendered = chunks_rendered;
		this.max_chunks = max_chunks;
		this.draw_calls = draw_calls;
	}

	void update_debug_text()
	{
		debug_info.setText( "FPS: " + Integer.toString( fps ) + "\n" +
		                    "X: " + Float.toString( loc.x ) + "\n" +
		                    "Y: " + Float.toString( loc.y ) + "\n" +
		                    "Z: " + Float.toString( loc.z ) + "\n" +
		                    "Triangles: " + Integer.toString( quads * 2 ) + "\n" +
		                    "Vertices: " + Integer.toString( quads * 4 ) + "\n" +
		                    "Render chunks: " + Integer.toString( chunks_rendered ) + "/" + Integer.toString( max_chunks ) + "\n" +
		                    "Draw calls: " + Integer.toString( draw_calls )
		                   );
	}
}
