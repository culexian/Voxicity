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
	Player player;
	World world;

	long last_block_change;
	long last_frame = 0;

	float yaw;
	float pitch;

	float mouse_speed = 2.0f;
	float camera_offset = 0.75f;

	Vector3f place_loc = new Vector3f( 0, 0, 0 );

	public InputHandler( Client client )
	{
		this.client = client;
		this.player = client.player;
		this.world = client.world;
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

		if ( in_state.quit )
			client.quit();

		if ( in_state.toggle_mouse )
			toggle_mouse_grab();

		if ( in_state.toggle_flying )
			toggle_flying();

		update_movement( delta, in_state );

		if ( in_state.use_action )
			place_block();

		if ( in_state.hit_action )
			remove_block();
	}

	void update_movement( float delta, InputState in_state )
	{
		// Set friction factor
		float friction = 0.99f;

		//Store the last_position
		player.last_pos.set( player.pos );

		if ( Mouse.isGrabbed() )
		{
			int x_delta = in_state.x_delta;
			int y_delta = in_state.y_delta;

			Vector3f move = new Vector3f();

			if ( in_state.move_left )
				if ( player.jumping )
					move.x -= 20;
				else
					move.x -= 50;

			if ( in_state.move_right )
				if ( player.jumping )
					move.x += 20;
				else
					move.x += 50;

			if ( in_state.move_forward )
				if ( player.jumping )
					move.z -= 25;
				else
					move.z -= 50;

			if ( in_state.move_backward )
				if ( player.jumping )
					move.z += 25;
				else
					move.z += 50;

			if ( player.flying )
			{
				if ( in_state.ascend )
					player.pos.y += 5 * delta;

				if ( in_state.descend )
					player.pos.y -= 5 * delta;
			}
			else
			{
				if ( in_state.ascend && !player.jumping )
				{
					player.velocity.y = 8.0f;
					player.jumping = true;
				}

				if ( player.jumping )
					player.accel.y = -23f;
			}

			yaw += ( x_delta / 800.0f ) * 45.0f * mouse_speed;
			pitch += ( y_delta / 800.0f ) * 45.0f * mouse_speed;

			// Make sure spinning idiots don't get ludicrously high rotations
			yaw = yaw > 361.0f ? yaw - 360.0f : yaw;
			yaw = yaw < -361.0 ? yaw + 360.0f : yaw;

			// Avoid NaN in the frustum calculations
			pitch = Math.min( pitch, 89.9999f );
			pitch = Math.max( pitch, -89.9999f );

			float cos_yaw = ( float ) Math.cos( Math.toRadians( yaw ) );
			float sin_yaw = ( float ) Math.sin( Math.toRadians( yaw ) );
			float cos_pitch = ( float ) Math.cos( Math.toRadians( pitch ) );
			float sin_pitch = ( float ) Math.sin( Math.toRadians( pitch ) );

			float corr_x = ( move.x * cos_yaw ) + ( move.z * -sin_yaw );
			float corr_z = ( move.x * sin_yaw ) + ( move.z * cos_yaw );

			player.accel.x = corr_x;
			player.accel.z = corr_z;

			player.velocity.x += player.accel.x * delta; 
			player.velocity.y += player.accel.y * delta;
			player.velocity.z += player.accel.z * delta;

			Vector3f friction_vec = new Vector3f( player.velocity );
			friction_vec = friction_vec.normalise( null );
			friction_vec.negate();
			friction_vec.scale( 23 * friction * delta );

			if ( player.velocity.lengthSquared() < friction_vec.lengthSquared() )
			{
				player.velocity.x = 0;
				player.velocity.z = 0;
			}
			else
			{
				if ( !Float.isNaN( friction_vec.lengthSquared() ) )
				{
					player.velocity.x += friction_vec.x;
					player.velocity.z += friction_vec.z;
				}
			}

			if ( player.velocity.lengthSquared() < 0.01 )
			{
				player.velocity.x = 0;
				player.velocity.z = 0;
			}

			Vector3f horiz_vel = new Vector3f( player.velocity.x, 0, player.velocity.z );
			if ( horiz_vel.lengthSquared() > 25.0f )
			{
				float ratio = 5.0f / horiz_vel.length();
				horiz_vel.scale( ratio );
				player.velocity.x = horiz_vel.x;
				player.velocity.z = horiz_vel.z;
			}

			player.pos.x += player.velocity.x * delta;
			player.pos.y += player.velocity.y * delta;
			player.pos.z += player.velocity.z * delta;

			// Set the look vector
			player.look.set( sin_yaw * cos_pitch * 4, sin_pitch * 4, cos_yaw * cos_pitch * -4 );
		}
	}

	void toggle_mouse_grab()
	{
		Mouse.setGrabbed( !Mouse.isGrabbed() );
	}

	void toggle_flying()
	{
		if ( player.flying == false )
		{
			player.flying = true;
			player.accel.y = 0;
			player.velocity.y = 0;
			System.out.println( "Flying is " + player.flying );
		}
		else
		{
			player.flying = false;
			player.jumping = true;
			System.out.println( "Flying is " + player.flying );
		}
	}

	boolean can_change_block()
	{
		return Time.get_time_ms() - last_block_change > ( 1000 / 5 );
	}

	void place_block()
	{
		if ( can_change_block() )
			last_block_change = Time.get_time_ms();
		else
			return;

		int id = world.get_block( place_loc.x, place_loc.y, place_loc.z );
		if ( id == Constants.Blocks.air )
		{
			client.tell_use_action( new BlockLoc( place_loc.x, place_loc.y, place_loc.z, world ), Direction.None );
		}
		else
		{
			Direction collision_side = world.get_hit_box( Math.round(place_loc.x), Math.round(place_loc.y), Math.round(place_loc.z) ).collision_side( new Vector3f( player.pos.x, player.pos.y + camera_offset, player.pos.z ), player.look );

			client.tell_use_action( new BlockLoc( place_loc.x, place_loc.y, place_loc.z, world ), collision_side );
		}
	}

	void remove_block()
	{
		if ( can_change_block() )
			last_block_change = Time.get_time_ms();
		else
			return;

		client.tell_hit_action( Math.round( place_loc.x ), Math.round( place_loc.y ), Math.round( place_loc.z ) );
	}

	void update_camera()
	{
		Vector3f pos = new Vector3f( player.pos );
		pos.y += camera_offset;

		Vector3f look = Vector3f.add( pos, player.look, null );

		Vector3f up = new Vector3f( 0, 1, 0 );

		client.renderer.camera.set_pos( pos, look, up );
	}

	Vector3f calc_place_loc()
	{
		float nearest_distance = Float.POSITIVE_INFINITY;
		BlockLoc nearest_block = new BlockLoc( Math.round( player.pos.x + player.look.x ), Math.round( player.pos.y + camera_offset + player.look.y ), Math.round( player.pos.z + player.look.z ), world );

		int x_incr = (int)Math.signum( player.look.x );
		int y_incr = (int)Math.signum( player.look.y );
		int z_incr = (int)Math.signum( player.look.z );

		for ( int x = Math.round( player.pos.x ) ; x != Math.round( player.pos.x + player.look.x ) + x_incr ; x += x_incr)
			for ( int y = Math.round( player.pos.y + camera_offset ) ; y != Math.round( player.pos.y + camera_offset + player.look.y ) + y_incr ; y += y_incr )
				for ( int z = Math.round( player.pos.z ) ; z != Math.round( player.pos.z + player.look.z ) + z_incr ; z += z_incr )
				{
					AABB box = world.get_hit_box( x, y, z );

					if ( box != null )
					{
						Float distance = box.collision_distance( new Vector3f( player.pos.x, player.pos.y + camera_offset, player.pos.z ), player.look );
						
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
		Vector3f last_pos = player.last_pos;
		Vector3f new_pos = new Vector3f( player.pos );
		Player p = player;

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
