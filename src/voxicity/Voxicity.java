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

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.Color;
import org.lwjgl.Sys;

import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

public class Voxicity
{
	long last_fps_update = 0;
	int fps_count = 0;

	long last_frame = 0;

	float rot_x;
	float rot_y;
	float mouse_speed = 2.0f;
	float camera_offset = 0.75f;

	float camera[] = new float[3];

	Vector3f last_pos = new Vector3f();

	Vector3f accel = new Vector3f( 0, 0, 0 );
	Vector3f move_speed = new Vector3f();

	boolean is_close_requested = false;
	boolean jumping = true;
	boolean flying = false;

	List<Block> block_list = new ArrayList<Block>();

	World world;

	public void init()
	{
		try
		{
			Display.setDisplayMode( new DisplayMode( 800, 600 ) );
			Display.create();
		}
		catch ( LWJGLException e )
		{
			e.printStackTrace();
			System.exit(0);
		}

		last_fps_update = get_time_ms();
		get_time_delta();
		setup_camera();
		Mouse.setGrabbed( true );
		world = new World();

		GL11.glShadeModel( GL11.GL_SMOOTH );
		GL11.glEnable( GL11.GL_DEPTH_TEST );
		GL11.glEnable( GL11.GL_TEXTURE_2D );

		while ( !is_close_requested )
		{
			update( get_time_delta() / 1000.0f );
			render();

			is_close_requested |= Display.isCloseRequested();
		}
			Display.destroy();
	}

	int get_time_delta()
	{
		// Get the time in milliseconds
		long new_time = get_time_ms();
		int delta = (int) ( new_time - last_frame );
		last_frame = new_time;
		return delta;
	}

	void update( float delta )
	{
		//Store the new last position
		last_pos.set( camera[0], camera[1], camera[2] );

		if ( Keyboard.isKeyDown( Keyboard.KEY_ESCAPE ) )
			is_close_requested = true;

		if ( Keyboard.isKeyDown( Keyboard.KEY_Q ) )
			is_close_requested = true;

		while ( Keyboard.next() )
		{
			if ( Keyboard.getEventKey() == Keyboard.KEY_E && Keyboard.getEventKeyState() )
				toggle_mouse_grab();

			if ( Keyboard.getEventKey() == Keyboard.KEY_G && Keyboard.getEventKeyState() )
			{
				toggle_flying();
			}
		}

		if ( Mouse.isGrabbed() )
		{
			float x_move = 0;
			float z_move = 0;

			if ( Keyboard.isKeyDown( Keyboard.KEY_A ) )
				x_move -= 3;

			if ( Keyboard.isKeyDown( Keyboard.KEY_D ) )
				x_move += 3;

			if ( Keyboard.isKeyDown( Keyboard.KEY_W ) )
				z_move -= 3;

			if ( Keyboard.isKeyDown( Keyboard.KEY_S ) )
				z_move += 3;

			if ( flying )
			{
				if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) ) camera[1] += 5 * delta;
				if ( Keyboard.isKeyDown( Keyboard.KEY_C ) ) camera[1] -= 5 * delta;
			}
			else
			{
				if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) && !jumping )
				{
					move_speed.y = 8.0f;
					jumping = true;
				}

				if ( jumping )
					accel.y = -23f;

			}

			int x_delta = Mouse.getDX();
			int y_delta = Mouse.getDY();

			rot_x += ( x_delta / 800.0f ) * 45.0f * mouse_speed;
			rot_y += ( y_delta / 800.0f ) * 45.0f * mouse_speed;

			rot_x = rot_x > 360.0f ? rot_x - 360.0f : rot_x;
			rot_x = rot_x < -360.0 ? rot_x + 360.0f : rot_x;

			rot_y = Math.min( rot_y, 90.0f );
			rot_y = Math.max( rot_y, -90.0f );

			float cos_rot_x = ( float ) Math.cos( Math.toRadians( rot_x ) );
			float sin_rot_x = ( float ) Math.sin( Math.toRadians( rot_x ) );

			float corr_x = ( x_move * cos_rot_x ) - ( z_move * sin_rot_x );
			float corr_z = ( x_move * sin_rot_x ) + ( z_move * cos_rot_x );

			accel.x = corr_x;
			accel.z = corr_z;

			move_speed.x = accel.x; 
			move_speed.y += accel.y * delta;
			move_speed.z = accel.z;

			camera[0] += move_speed.x * delta;
			camera[1] += move_speed.y * delta;
			camera[2] += move_speed.z * delta;
		}

		check_collisions();

		update_fps();
	}

	void render()
	{
		GL11.glLoadIdentity();
		GL11.glRotatef( -rot_y, 1, 0, 0 );
		GL11.glRotatef( rot_x, 0, 1, 0 );
		GLU.gluLookAt( camera[0], camera[1] + camera_offset, camera[2], camera[0], camera[1] + camera_offset, camera[2] - 10, 0,1,0 );


		// Clear the screen and depth buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		world.render();

		Display.update();
	}

	void update_fps()
	{
		if ( get_time_ms() - last_fps_update > 1000 )
		{
			Display.setTitle( "FPS: " + fps_count );
			fps_count = 0;
			last_fps_update += 1000;
		}
		fps_count++;
	}

	long get_time_ms()
	{
		return (Sys.getTime() * 1000)  / Sys.getTimerResolution();
	}

	void setup_camera()
	{
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective( 45.0f, 1.333f, 0.1f, 10000f );
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		camera[0] = 5;
		camera[1] = 20;
		camera[2] = 5;
		rot_x = 180;
		rot_y = 0;
	}

	void toggle_mouse_grab()
	{
		Mouse.setGrabbed( !Mouse.isGrabbed() );
	}

	void toggle_flying()
	{
		if ( flying == false )
		{
			flying = true;
			accel.y = 0;
			move_speed.y = 0;
			System.out.println( "Flying is " + flying );
		}
		else
		{
			flying = false;
			jumping = true;
			System.out.println( "Flying is " + flying );
		}
	}

	void check_collisions()
	{
		int slice_num = 10;

		boolean collided_x = false;
		boolean collided_y = false;
		boolean collided_z = false;

		AABB player = new AABB( 0.5f, 1.7f, 0.5f );

		Vector3f new_pos = new Vector3f( camera[0], camera[1], camera[2] );

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

			Block above = world.get_block( Math.round( player.pos.x), Math.round(player.top()), Math.round( player.pos.z) );
			if ( above != null )
			{
				AABB above_box = above.get_bounds();

				if ( player.collides( above_box ) )
				{
					collided_y = true;
					player.pos.y += above_box.bottom_intersect( player );
					move_speed.y = -move_speed.y;
					accel.y = 0;
				}
			}

			Block beneath = world.get_block( Math.round( player.pos.x), Math.round( player.bottom() - 0.01f ), Math.round(player.pos.z) );
			if ( beneath != null )
			{
				AABB beneath_box = beneath.get_bounds();

				if ( player.collides( beneath_box ) && move_speed.y < 0 )
				{
					collided_y = true;
					player.pos.y += beneath_box.top_intersect( player ) + 0.0001f;
					move_speed.y = 0;
					accel.y = 0;
					jumping = false;
				}
			}
			else
			{
				jumping = true;
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

			Block upper_neg_x = world.get_block( Math.round(player.left()), Math.round(player.top()), Math.round(player.pos.z) );
			if ( upper_neg_x != null )
			{
				AABB upper_neg_x_box = upper_neg_x.get_bounds();

				if ( player.collides( upper_neg_x_box ) )
				{
					collided_x = true;
					move_speed.x = 0;
					player.pos.x += upper_neg_x_box.right_intersect( player ) + 0.0001f;
				}
			}

			Block upper_pos_x = world.get_block( Math.round(player.right()), Math.round(player.top()), Math.round(player.pos.z) );
			if ( upper_pos_x != null )
			{
				AABB upper_pos_x_box = upper_pos_x.get_bounds();

				if ( player.collides( upper_pos_x_box ) )
				{
					collided_x = true;
					move_speed.x = 0;
					player.pos.x += upper_pos_x_box.left_intersect( player ) - 0.0001f;
				}
			}

			Block lower_neg_x = world.get_block( Math.round(player.left()), Math.round(player.bottom()), Math.round(player.pos.z) );
			if ( lower_neg_x != null )
			{
				AABB lower_neg_x_box = lower_neg_x.get_bounds();

				if ( player.collides( lower_neg_x_box ) )
				{
					collided_x = true;
					move_speed.x = 0;

					if ( Math.abs( lower_neg_x_box.right_intersect( player ) ) < Math.abs( lower_neg_x_box.top_intersect( player ) ) )
						player.pos.x += lower_neg_x_box.right_intersect( player ) + 0.0001f;
				}
			}

			Block lower_pos_x = world.get_block( Math.round(player.right()), Math.round(player.bottom()), Math.round(player.pos.z) );
			if ( lower_pos_x != null )
			{
				AABB lower_pos_x_box = lower_pos_x.get_bounds();

				if ( player.collides( lower_pos_x_box ) )
				{
					collided_x = true;
					move_speed.x = 0;
					if ( Math.abs( lower_pos_x_box.left_intersect( player ) ) < Math.abs( lower_pos_x_box.top_intersect( player ) ) )
						player.pos.x += lower_pos_x_box.left_intersect( player ) - 0.0001f;
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

			Block upper_neg_z = world.get_block( Math.round(player.pos.x), Math.round(player.top()), Math.round(player.back()) );
			if ( upper_neg_z != null )
			{
				AABB upper_neg_z_box = upper_neg_z.get_bounds();

				if ( player.collides( upper_neg_z_box ) )
				{
					collided_z = true;
					move_speed.z = 0;
					player.pos.z += upper_neg_z_box.front_intersect( player ) + 0.0001f;
				}
			}

			Block upper_pos_z = world.get_block( Math.round(player.pos.x), Math.round(player.top()), Math.round(player.front()) );
			if ( upper_pos_z != null )
			{
				AABB upper_pos_z_box = upper_pos_z.get_bounds();

				if ( player.collides( upper_pos_z_box ) )
				{
					collided_z = true;
					move_speed.z = 0;
					player.pos.z += upper_pos_z_box.back_intersect( player ) - 0.0001f;
				}
			}

			Block lower_neg_z = world.get_block( Math.round(player.pos.x), Math.round(player.bottom()), Math.round(player.back()) );
			if ( lower_neg_z != null )
			{
				AABB lower_neg_z_box = lower_neg_z.get_bounds();

				if ( player.collides( lower_neg_z_box ) )
				{
					collided_z = true;
					move_speed.z = 0;
					if ( Math.abs( lower_neg_z_box.front_intersect( player ) ) < Math.abs( lower_neg_z_box.top_intersect( player ) ) )
						player.pos.z += lower_neg_z_box.front_intersect( player ) + 0.0001f;
				}
			}

			Block lower_pos_z = world.get_block( Math.round(player.pos.x), Math.round(player.bottom()), Math.round(player.front()) );
			if ( lower_pos_z != null )
			{
				AABB lower_pos_z_box = lower_pos_z.get_bounds();

				if ( player.collides( lower_pos_z_box ) )
				{
					collided_z = true;
					move_speed.z = 0;
					if ( Math.abs( lower_pos_z_box.back_intersect( player ) ) < Math.abs( lower_pos_z_box.top_intersect( player ) ) )
						player.pos.z += lower_pos_z_box.back_intersect( player ) - 0.0001f;
				}
			}

			if ( collided_z )
				break;
		}

		corrected_pos.z = slice_pos.z;


		if ( collided_x || collided_y || collided_z )
		{
			// Set the player's new position after collision checking/handling
			camera[0] = corrected_pos.x;
			camera[1] = corrected_pos.y;
			camera[2] = corrected_pos.z;
		}
	}

	public static void main( String[] args )
	{
		try
		{
			File new_out = new File( "voxicity.log" );
			//System.setOut( new PrintStream( new_out ) );

			Voxicity voxy = new Voxicity();
			voxy.init();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
