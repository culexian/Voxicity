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
	long last_frame = 0;

	float rot_x;
	float rot_y;

	float mouse_speed = 2.0f;
	float camera_offset = 0.75f;

	Vector3f place_loc = new Vector3f( 0, 0, 0 );

	public InputHandler( Client client )
	{
		this.client = client;
		get_time_delta();
	}

	void init()
	{
		Mouse.setGrabbed( true );
	}

	void update( World world )
	{
		// Get the number of seconds as a float since the last update
		float delta = get_time_delta() / 1000.0f;

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

	int get_time_delta()
	{
		// Get the time in milliseconds
		long new_time = Time.get_time_ms();
		int delta = (int) ( new_time - last_frame );
		last_frame = new_time;
		return delta;
	}

	Vector3f[] check_collisions()
	{
		Vector3f last_pos = client.player.last_pos;
		Vector3f new_pos = new Vector3f( client.player.pos );
		World world = client.world;
		Player p = client.player;

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
}
