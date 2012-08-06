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

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import de.matthiasmann.twl.*;

import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

public class Voxicity
{
	long last_frame = 0;

	static Vector3f camera;

	boolean is_close_requested = false;

	Server server;
	Client client;
	Config config;
	Arguments args;
	LWJGLRenderer gui_renderer;

	public Voxicity( Arguments args )
	{
		this.args = args;
	}

	public void init()
	{
		// Load the specified config file
		config = new Config( args.get_value( "config", "voxicity.properties" ) );

		String mode = args.get_value( "mode", "client" );

		if ( mode.equals( "server" ) )
		{
			server = new Server( config );
			server.run();
		}
		else if ( mode.equals( "client" ) )
		{
			try
			{
				System.out.println( "Intializing display" );
				Display.setDisplayMode( new DisplayMode( 1200, 720 ) );
				Display.create();
				Display.setTitle( "Voxicity" );
				System.out.println( "Display created" );
			}
			catch ( LWJGLException e )
			{
				e.printStackTrace();
				System.exit(0);
			}

			System.out.println( "Setting up OpenGL states" );
			GL11.glShadeModel( GL11.GL_SMOOTH );
			GL11.glEnable( GL11.GL_DEPTH_TEST );
			GL11.glEnable( GL11.GL_TEXTURE_2D );
			GL11.glEnable( GL11.GL_CULL_FACE );
			GL11.glEnable( GL11.GL_BLEND );
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glClearColor( 126.0f / 255.0f, 169.0f / 255.0f, 254.0f / 255.0f, 1.0f );

			System.out.println( "Number of texture units: " + GL11.glGetInteger( GL13.GL_MAX_TEXTURE_UNITS ) );
			System.out.println( "Number of image texture units: " + GL11.glGetInteger( GL20.GL_MAX_TEXTURE_IMAGE_UNITS ) );
			System.out.println( "Number of vertex texture units: " + GL11.glGetInteger( GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS ) );
			System.out.println( "Number of combined vertex/image texture units: " + GL11.glGetInteger( GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS ) );

			try
			{
				gui_renderer = new LWJGLRenderer();
				LoginGUI login_gui = new LoginGUI();
				ThemeManager theme = ThemeManager.createThemeManager( Voxicity.class.getResource( "/login.xml" ), gui_renderer );
				GUI gui = new GUI( login_gui, gui_renderer );
				gui.applyTheme( theme );

				while ( !login_gui.is_login_pressed() )
				{
					GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
					gui.update();
					Display.update();

					if ( Display.isCloseRequested() )
					{
						Display.destroy();
						System.exit( 0 );
					}
				}

				Mouse.setGrabbed( true );

				Socket client_s = new Socket( login_gui.get_server_name(), 11000 );
				client = new Client( config, new NetworkConnection( client_s ) );

				get_time_delta();

				client.init();
				camera = client.player.pos;

				setup_camera();

				while ( !is_close_requested )
				{
					System.out.println( "Update at " + Time.get_time_µs() );
					client.update();
					client.input_handler.update( get_time_delta() / 1000.0f, client.world );
					System.out.println( "Check collisions at " + Time.get_time_µs() );
					check_collisions( client.player.last_pos, new Vector3f( client.player.pos ), client.world, client.player );
					System.out.println( "Done checking collisions at " + Time.get_time_µs() );
					client.input_handler.place_loc.set( client.input_handler.calc_place_loc( client.world ) );
					client.input_handler.update_camera();
					update_fps();
					client.fps_counter.update();
					client.hud.set_loc( client.player.pos );
					client.hud.set_chunks( client.renderer.draw_calls, client.renderer.chunks.size(), client.renderer.batch_draw_calls );
					client.hud.set_quads( client.renderer.quads );
					System.out.println( "Render at " + Time.get_time_µs() );
					client.renderer.render();
					client.hud.render();
					Display.update();
					System.out.println( "Loop done" );

					is_close_requested |= Display.isCloseRequested();
				}

				client.shutdown();
				System.out.println( "Destroying display" );
				Display.destroy();
			}

			// Catch exceptions from the game init and main game-loop

			catch ( LWJGLException e )
			{
				System.out.println( e );
				e.printStackTrace();
				System.exit(1);
			}
			catch ( IOException e )
			{
				System.out.println( e );
				e.printStackTrace();
				System.exit(1);
			}
			catch ( Exception e )
			{
				System.out.println( e );
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println( "Invalid mode: " + mode );
		}
	}

	int get_time_delta()
	{
		// Get the time in milliseconds
		long new_time = Time.get_time_ms();
		int delta = (int) ( new_time - last_frame );
		last_frame = new_time;
		return delta;
	}

	void update_fps()
	{
		if ( client.fps_counter.time_delta() > 200 )
		{
			client.hud.set_fps( client.fps_counter.fps() );
			client.fps_counter.reset();
		}
	}


	void setup_camera()
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective( 45.0f, 1200 / 720.0f, 0.1f, 10000f );
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		camera.x = 0;
		camera.y = 3;
		camera.z = 0;

		client.renderer.camera.set_attribs( 45.0f, 1200 / 720.0f, 0.1f, 1000.0f );
	}

	Vector3f[] check_collisions( Vector3f last_pos, Vector3f new_pos, World world, Player p )
	{
		int slice_num = 10;

		boolean collided_x = false;
		boolean collided_y = false;
		boolean collided_z = false;

		Vector3f[] result = { new Vector3f(), new Vector3f(), new Vector3f() };

		AABB player = new AABB( 0.5f, 1.7f, 0.5f );

		Vector3f slice_distance = new Vector3f();
		Vector3f.sub( new_pos, last_pos, slice_distance );
		slice_distance.scale( 1.0f / ( slice_num * 1.0f ) );

		Vector3f slice_pos = new Vector3f( last_pos );
		player.pos = slice_pos;

		Vector3f corrected_pos = new Vector3f( new_pos );

		// Check y axis for collisions
		for ( int i = 0 ; i < slice_num ; i++ )
		{
			
			Vector3f.add( slice_pos, slice_distance, slice_pos );

			AABB above = world.get_hit_box( Math.round( player.pos.x ), Math.round( player.top() ), Math.round( player.pos.z ) );
			if ( above != null )
			{
				if ( player.collides( above ) )
				{
					collided_y = true;
					player.pos.y += above.bottom_intersect( player );
					p.velocity.y = -p.velocity.y;
					p.accel.y = 0;
				}
			}

			AABB beneath = world.get_hit_box( Math.round( player.pos.x), Math.round( player.bottom() - 0.01f ), Math.round(player.pos.z) );
			if ( beneath != null )
			{
				if ( player.collides( beneath ) && p.velocity.y < 0 )
				{
					collided_y = true;
					player.pos.y += beneath.top_intersect( player ) + 0.0001f;
					p.velocity.y = 0;
					p.accel.y = 0;
					p.jumping = false;
				}
			}
			else
			{
				p.jumping = true;
			}

			if ( collided_y )
				break;
		}

		corrected_pos.y = slice_pos.y;
		slice_pos = new Vector3f( last_pos );
		player.pos = slice_pos;

		// Check x axis for collisions
		for ( int i = 0 ; i < slice_num ; i++ )
		{

			Vector3f.add( slice_pos, slice_distance, slice_pos );

			AABB upper_neg_x = world.get_hit_box( Math.round(player.left()), Math.round(player.top()), Math.round(player.pos.z) );
			if ( upper_neg_x != null )
			{
				if ( player.collides( upper_neg_x ) )
				{
					collided_x = true;
					p.velocity.x = 0;
					player.pos.x += upper_neg_x.right_intersect( player ) + 0.0001f;
				}
			}

			AABB upper_pos_x = world.get_hit_box( Math.round(player.right()), Math.round(player.top()), Math.round(player.pos.z) );
			if ( upper_pos_x != null )
			{
				if ( player.collides( upper_pos_x ) )
				{
					collided_x = true;
					p.velocity.x = 0;
					player.pos.x += upper_pos_x.left_intersect( player ) - 0.0001f;
				}
			}

			AABB lower_neg_x = world.get_hit_box( Math.round(player.left()), Math.round(player.bottom()), Math.round(player.pos.z) );
			if ( lower_neg_x != null )
			{
				if ( player.collides( lower_neg_x ) )
				{
					collided_x = true;
					p.velocity.x = 0;

					if ( Math.abs( lower_neg_x.right_intersect( player ) ) < Math.abs( lower_neg_x.top_intersect( player ) ) )
						player.pos.x += lower_neg_x.right_intersect( player ) + 0.0001f;
				}
			}

			AABB lower_pos_x = world.get_hit_box( Math.round(player.right()), Math.round(player.bottom()), Math.round(player.pos.z) );
			if ( lower_pos_x != null )
			{
				if ( player.collides( lower_pos_x ) )
				{
					collided_x = true;
					p.velocity.x = 0;
					if ( Math.abs( lower_pos_x.left_intersect( player ) ) < Math.abs( lower_pos_x.top_intersect( player ) ) )
						player.pos.x += lower_pos_x.left_intersect( player ) - 0.0001f;
				}
			}

			if ( collided_x )
				break;
		}

		corrected_pos.x = slice_pos.x;
		slice_pos = new Vector3f( last_pos );
		player.pos = slice_pos;

		for ( int i = 0 ; i < slice_num ; i++ )
		{
			Vector3f.add( slice_pos, slice_distance, slice_pos );

			AABB upper_neg_z = world.get_hit_box( Math.round(player.pos.x), Math.round(player.top()), Math.round(player.back()) );
			if ( upper_neg_z != null )
			{
				if ( player.collides( upper_neg_z ) )
				{
					collided_z = true;
					p.velocity.z = 0;
					player.pos.z += upper_neg_z.front_intersect( player ) + 0.0001f;
				}
			}

			AABB upper_pos_z = world.get_hit_box( Math.round(player.pos.x), Math.round(player.top()), Math.round(player.front()) );
			if ( upper_pos_z != null )
			{
				if ( player.collides( upper_pos_z ) )
				{
					collided_z = true;
					p.velocity.z = 0;
					player.pos.z += upper_pos_z.back_intersect( player ) - 0.0001f;
				}
			}

			AABB lower_neg_z = world.get_hit_box( Math.round(player.pos.x), Math.round(player.bottom()), Math.round(player.back()) );
			if ( lower_neg_z != null )
			{
				if ( player.collides( lower_neg_z ) )
				{
					collided_z = true;
					p.velocity.z = 0;
					if ( Math.abs( lower_neg_z.front_intersect( player ) ) < Math.abs( lower_neg_z.top_intersect( player ) ) )
						player.pos.z += lower_neg_z.front_intersect( player ) + 0.0001f;
				}
			}

			AABB lower_pos_z = world.get_hit_box( Math.round(player.pos.x), Math.round(player.bottom()), Math.round(player.front()) );
			if ( lower_pos_z != null )
			{
				if ( player.collides( lower_pos_z ) )
				{
					collided_z = true;
					p.velocity.z = 0;
					if ( Math.abs( lower_pos_z.back_intersect( player ) ) < Math.abs( lower_pos_z.top_intersect( player ) ) )
						player.pos.z += lower_pos_z.back_intersect( player ) - 0.0001f;
				}
			}

			if ( collided_z )
				break;
		}

		corrected_pos.z = slice_pos.z;


		if ( collided_x || collided_y || collided_z )
		{
			// Set the player's new position after collision checking/handling
			p.pos.x = corrected_pos.x;
			p.pos.y = corrected_pos.y;
			p.pos.z = corrected_pos.z;
		}

		return result;
	}


	void get_system_info()
	{
		
	}

	static void print_usage()
	{
		System.out.println( "Usage: voxicity [OPTION]..." );
		System.out.println( "The following options are valid\n" );
		System.out.println( "  --mode <value>        Sets the mode of the program( server, client, server-client )" );
		System.out.println( "  --config <filename>   Sets the name of the properties file to be loaded( voxicity.properties )" );
		
	}

	public static void main( String[] args )
	{
		try
		{
			Arguments cmd_args = new Arguments( args );
			File new_out = new File( "voxicity.log" );
			System.setOut( new PrintStream( new_out ) );

			Voxicity voxy = new Voxicity( cmd_args );
			voxy.init();
		}
		catch ( Exception e )
		{
			System.out.println( e );
			e.printStackTrace();
			print_usage();
		}
	}
}
