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

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class InputHandler
{
	Client client;

	long last_block_change;

	float rot_x;
	float rot_y;

	float mouse_speed = 2.0f;
	float camera_offset = 0.75f;

	Vector3f place_loc = new Vector3f( 0, 0, 0 );

	public InputHandler( Client client )
	{
		this.client = client;
	}

	void update( float delta, World world )
	{
		// Get the input state
		InputState in_state = new InputState();

		//Store the last_position
		client.player.last_pos.set( client.player.pos );

		if ( in_state.quit )
			client.quit();

		if ( in_state.toggle_mouse )
			toggle_mouse_grab();

		if ( in_state.toggle_flying )
			toggle_flying();

		if ( Mouse.isGrabbed() )
		{
			float x_move = 0;
			float z_move = 0;

			if ( in_state.move_left )
				x_move -= 5;

			if ( in_state.move_right )
				x_move += 5;

			if ( in_state.move_forward )
				z_move -= 5;

			if ( in_state.move_backward )
				z_move += 5;

			if ( client.player.flying )
			{
				if ( in_state.start_jumping )
					client.player.pos.y += 5 * delta;

				if ( in_state.fly_down )
					client.player.pos.y -= 5 * delta;
			}
			else
			{
				if ( in_state.start_jumping && !client.player.jumping )
				{
					client.player.velocity.y = 8.0f;
					client.player.jumping = true;
				}

				if ( client.player.jumping )
					client.player.accel.y = -23f;
			}

			if ( in_state.use_action )
				place_block( world );

			if ( in_state.hit_action )
				remove_block( world );

			int x_delta = in_state.x_delta;
			int y_delta = in_state.y_delta;

			rot_x += ( x_delta / 800.0f ) * 45.0f * mouse_speed;
			rot_y += ( y_delta / 800.0f ) * 45.0f * mouse_speed;

			// Make sure spinning idiots don't get ludicrously high rotations
			rot_x = rot_x > 361.0f ? rot_x - 360.0f : rot_x;
			rot_x = rot_x < -361.0 ? rot_x + 360.0f : rot_x;

			// Avoid NaN in the frustum calculations
			rot_y = Math.min( rot_y, 89.9999f );
			rot_y = Math.max( rot_y, -89.9999f );

			float cos_rot_x = ( float ) Math.cos( Math.toRadians( rot_x ) );
			float sin_rot_x = ( float ) Math.sin( Math.toRadians( rot_x ) );
			float cos_rot_y = ( float ) Math.cos( Math.toRadians( rot_y ) );
			float sin_rot_y = ( float ) Math.sin( Math.toRadians( rot_y ) );

			float corr_x = ( x_move * cos_rot_x ) - ( z_move * sin_rot_x );
			float corr_z = ( x_move * sin_rot_x ) + ( z_move * cos_rot_x );

			client.player.accel.x = corr_x;
			client.player.accel.z = corr_z;

			client.player.velocity.x = client.player.accel.x; 
			client.player.velocity.y += client.player.accel.y * delta;
			client.player.velocity.z = client.player.accel.z;

			client.player.pos.x += client.player.velocity.x * delta;
			client.player.pos.y+= client.player.velocity.y * delta;
			client.player.pos.z += client.player.velocity.z * delta;

			// Set the look vector
			client.player.look.set( sin_rot_x * cos_rot_y * 4, sin_rot_y * 4, cos_rot_x * cos_rot_y * -4 );
		}
	}

	void toggle_mouse_grab()
	{
		Mouse.setGrabbed( !Mouse.isGrabbed() );
	}

	void toggle_flying()
	{
		if ( client.player.flying == false )
		{
			client.player.flying = true;
			client.player.accel.y = 0;
			client.player.velocity.y = 0;
			System.out.println( "Flying is " + client.player.flying );
		}
		else
		{
			client.player.flying = false;
			client.player.jumping = true;
			System.out.println( "Flying is " + client.player.flying );
		}
	}

	boolean can_change_block()
	{
		return Time.get_time_ms() - last_block_change > ( 1000 / 5 );
	}

	void place_block( World world )
	{
		if ( can_change_block() )
			last_block_change = Time.get_time_ms();
		else
			return;

		int id = world.get_block( place_loc.x, place_loc.y, place_loc.z );
		if ( id == Constants.Blocks.air )
		{
			client.tell_use_action( new BlockLoc( place_loc.x, place_loc.y, place_loc.z, client.world ), Direction.None );
		}
		else
		{
			Direction collision_side = world.get_hit_box( Math.round(place_loc.x), Math.round(place_loc.y), Math.round(place_loc.z) ).collision_side( new Vector3f( client.player.pos.x, client.player.pos.y + camera_offset, client.player.pos.z ), client.player.look );

			client.tell_use_action( new BlockLoc( place_loc.x, place_loc.y, place_loc.z, client.world ), collision_side );
		}
	}

	void remove_block( World world )
	{
		if ( can_change_block() )
			last_block_change = Time.get_time_ms();
		else
			return;

		client.tell_hit_action( Math.round( place_loc.x ), Math.round( place_loc.y ), Math.round( place_loc.z ) );
	}

	void update_camera()
	{
		Vector3f pos = new Vector3f( client.player.pos );
		pos.y += camera_offset;

		Vector3f look = Vector3f.add( pos, client.player.look, null );

		Vector3f up = new Vector3f( 0, 1, 0 );

		client.renderer.camera.set_pos( pos, look, up );
	}

	Vector3f calc_place_loc( World world )
	{
		float nearest_distance = Float.POSITIVE_INFINITY;
		BlockLoc nearest_block = new BlockLoc( Math.round( client.player.pos.x + client.player.look.x ), Math.round( client.player.pos.y + camera_offset + client.player.look.y ), Math.round( client.player.pos.z + client.player.look.z ), world );

		int x_incr = (int)Math.signum( client.player.look.x );
		int y_incr = (int)Math.signum( client.player.look.y );
		int z_incr = (int)Math.signum( client.player.look.z );

		for ( int x = Math.round( client.player.pos.x ) ; x != Math.round( client.player.pos.x + client.player.look.x ) + x_incr ; x += x_incr)
			for ( int y = Math.round( client.player.pos.y + camera_offset ) ; y != Math.round( client.player.pos.y + camera_offset + client.player.look.y ) + y_incr ; y += y_incr )
				for ( int z = Math.round( client.player.pos.z ) ; z != Math.round( client.player.pos.z + client.player.look.z ) + z_incr ; z += z_incr )
				{
					AABB box = world.get_hit_box( x, y, z );

					if ( box != null )
					{
						Float distance = box.collision_distance( new Vector3f( client.player.pos.x, client.player.pos.y + camera_offset, client.player.pos.z ), client.player.look );
						
						if ( distance < nearest_distance )
						{
							nearest_distance = distance;
							nearest_block.x = x;
							nearest_block.y = y;
							nearest_block.z = z;
						}
					}
				}

		return new Vector3f( nearest_block.x, nearest_block.y, nearest_block.z );
	}
}
