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

import org.lwjgl.util.vector.Vector3f;

public class Frustum
{
	Vector3f pos   = new Vector3f( 0, 0, 0 );
	Vector3f right = new Vector3f( 1, 0, 0 );
	Vector3f up    = new Vector3f( 0, 1, 0 );
	Vector3f look  = new Vector3f( 0, 0, 1 );

	float ratio;
	float vert_angle_rads;
	float near_dist;
	float far_dist;

	float tan_vert_angle;
	float near_width, near_height;
	float far_width, far_height;

	void set_attribs( float vert_angle, float ratio, float near_dist, float far_dist )
	{
		this.ratio = ratio;
		this.vert_angle_rads = (float)Math.toRadians( vert_angle );
		this.near_dist = near_dist;
		this.far_dist = far_dist;

		this.tan_vert_angle = (float)Math.tan( this.vert_angle_rads );

		this.near_height = near_dist * tan_vert_angle;
		this.near_width = near_height * ratio;

		this.far_height = far_dist * tan_vert_angle;
		this.far_width = far_height * ratio;
	}

	void set_pos( Vector3f pos, Vector3f look, Vector3f up, Vector3f forward )
	{
		this.pos.set( pos );

		this.look = new Vector3f( look );
		this.look = this.look.normalise( null );

		this.right = Vector3f.cross( forward, up, null );
		this.right = this.right.normalise( null );

		this.up = Vector3f.cross( this.right, this.look, null );

		Vector3f near_center = new Vector3f( this.look );
		near_center.scale( near_dist );

		Vector3f far_center = new Vector3f( this.look );
		far_center.scale( far_dist );
	}

	public boolean contains_point( Vector3f point )
	{
		Vector3f v = Vector3f.sub( point, pos, null );


		float x_dot = Vector3f.dot( v, right );
		float y_dot = Vector3f.dot( v, up );
		float z_dot = Vector3f.dot( v, look );


		if ( z_dot > far_dist || z_dot < near_dist )
			return false;

		float y_extreme = z_dot * tan_vert_angle;
		if ( y_dot > y_extreme || y_dot < -y_extreme )
			return false;

		float x_extreme = y_extreme * ratio;
		if ( x_dot > x_extreme || x_dot < -x_extreme )
			return false;

		return true;
	}


	// Check if an AABB collides with this frustum by checking all the points
	// in turn against each set of planes
	public boolean collides( AABB box )
	{
		// Create an array of all the points, their x_dot, y_dot and z_dot values
		Vector3f[] points = new Vector3f[8];//{ new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };
		float[][] dots = new float[8][3];

		points[0] = Vector3f.sub( new Vector3f( box.min_x(), box.max_y(), box.max_z() ), pos, null );
		points[1] = Vector3f.sub( new Vector3f( box.max_x(), box.max_y(), box.max_z() ), pos, null );
		points[2] = Vector3f.sub( new Vector3f( box.min_x(), box.min_y(), box.max_z() ), pos, null );
		points[3] = Vector3f.sub( new Vector3f( box.max_x(), box.min_y(), box.max_z() ), pos, null );
		points[4] = Vector3f.sub( new Vector3f( box.min_x(), box.max_y(), box.min_z() ), pos, null );
		points[5] = Vector3f.sub( new Vector3f( box.max_x(), box.max_y(), box.min_z() ), pos, null );
		points[6] = Vector3f.sub( new Vector3f( box.min_x(), box.min_y(), box.min_z() ), pos, null );
		points[7] = Vector3f.sub( new Vector3f( box.max_x(), box.min_y(), box.min_z() ), pos, null );

		for ( int i = 0 ; i < 8 ; i++ )
		{
			dots[i][0] = Vector3f.dot( points[i], right );
			dots[i][1] = Vector3f.dot( points[i], up );
			dots[i][2] = Vector3f.dot( points[i], look );
		}

		boolean z_check = false;

		for ( int i = 0 ; i < 8 ; i++ )
			z_check |= ( dots[i][2] < far_dist );

		if ( !z_check )
			return false;

		for ( int i = 0 ; i < 8 ; i++ )
			z_check |= ( dots[i][2] > near_dist );

		if ( !z_check )
			return false;

		boolean y_check = false;

		for ( int i = 0 ; i < 8 ; i++ )
		{
			float y_extreme = dots[i][2] * tan_vert_angle;
			y_check |= ( dots[i][1] < y_extreme );
		}

		if ( !y_check )
			return false;

		for ( int i = 0 ; i < 8 ; i++ )
		{
			float y_extreme = dots[i][2] * tan_vert_angle;
			y_check |= ( dots[i][1] > -y_extreme );
		}

		if ( !y_check )
			return false;

		boolean x_check = false;

		for ( int i = 0 ; i < 8 ; i++ )
		{
			float x_extreme = ( dots[i][2] * tan_vert_angle ) * ratio;
			x_check |= ( dots[i][0] < x_extreme );
		}

		if ( !x_check )
			return false;

		for ( int i = 0 ; i < 8 ; i++ )
		{
			float x_extreme = ( dots[i][2] * tan_vert_angle ) * ratio;
			x_check |= ( dots[i][0] > -x_extreme );
		}

		if ( !x_check )
			return false;

		return true;
	}
}
