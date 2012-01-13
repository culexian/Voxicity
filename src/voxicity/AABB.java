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

import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

public class AABB
{
	public Vector3f pos = new Vector3f();
	public Vector3f dim = new Vector3f();

	public AABB( float width, float height, float depth )
	{
		dim.x = width / 2;
		dim.y = height / 2;
		dim.z = depth / 2;
	}

	public boolean collides( final AABB rhs )
	{
		if ( ( left() < rhs.right() && right() > rhs.left() ) &&
		     ( bottom() < rhs.top() && top() > rhs.bottom() ) &&
		     ( back() < rhs.front() && front() > rhs.back() ) )
			return true;

		return false; 
	}

	public float collision_distance( final Vector3f start, final Vector3f length )
	{
		/* Multisampling code, maybe replace later */
		AABB tiny_box = new AABB( 0.000005f, 0.000005f, 0.000005f );
		tiny_box.pos.set( start );
		int samples = (int)(length.lengthSquared() * 10);
		Vector3f sample_step = new Vector3f( length );
		sample_step.scale( 1 / ( samples * 1.0f ) );

		for ( int i = 0 ; i < samples ; i++ )
		{
			if ( collides( tiny_box ) )
			{
				Vector3f distance = Vector3f.sub( tiny_box.pos, start, null );
				return (float)Math.sqrt( distance.lengthSquared() );
			}

			Vector3f.add( tiny_box.pos, sample_step, tiny_box.pos );
		}

		return Float.POSITIVE_INFINITY;
	}

	public Constants.Direction collision_side( final Vector3f start, final Vector3f length )
	{
		float nearest_distance = Float.POSITIVE_INFINITY;
		Constants.Direction nearest_dir = Constants.Direction.None;

		Vector3f dir_normal = length.normalise( null );

		for ( Constants.Direction dir : Constants.Direction.values() )
		{
			float distance = collision_side_distance( start, dir_normal, dir );

			if ( distance < Float.POSITIVE_INFINITY )
			{
				Vector3f collide_point = new Vector3f( dir_normal );
				collide_point.scale( distance );
				collide_point = Vector3f.add( start, collide_point, null );

				boolean collides_col = false;

				switch ( dir )
				{
					case West:
					case East:
						collides_col = collides_x_col( collide_point );
					break;

					case North:
					case South:
						collides_col = collides_z_col( collide_point );
					break;

					case Up:
					case Down:
						collides_col = collides_y_col( collide_point );
					break;
				}

				if ( collides_col && distance < nearest_distance )
				{
					nearest_distance = distance;
					nearest_dir = dir;
				}
			}

		}

		System.out.println( "Nearest side found is " + nearest_distance + " away on the " + nearest_dir + " side \n" );

		return nearest_dir;
	}

	public float collision_side_distance( final Vector3f start, final Vector3f length, Constants.Direction dir )
	{
		if ( ( dir == Constants.Direction.All ) || ( dir == Constants.Direction.None ) )
			return Float.POSITIVE_INFINITY;

		float distance = Float.POSITIVE_INFINITY;

		System.out.println( "Checking for collision on side " + dir );
		switch ( dir )
		{
			case North:
				distance = ray_plane_intersect( start, length, new Vector3f( 0.0f, 0.0f, 1.0f ), new Vector3f( pos.x, pos.y, front() ) );
			break;
			case East:
				distance = ray_plane_intersect( start, length, new Vector3f( 1.0f, 0.0f, 0.0f ), new Vector3f( right(), pos.y, pos.z ) );
			break;
			case South:
				distance = ray_plane_intersect( start, length, new Vector3f( 0.0f, 0.0f, -1.0f ), new Vector3f( pos.x, pos.y, back() ) );
			break;
			case West:
				distance = ray_plane_intersect( start, length, new Vector3f( -1.0f, 0.0f, 0.0f ), new Vector3f( left(), pos.y, front() ) );
			break;
			case Up:
				distance = ray_plane_intersect( start, length, new Vector3f( 0.0f, 1.0f, 0.0f ), new Vector3f( pos.x, top(), pos.z ) );
			break;
			case Down:
				distance = ray_plane_intersect( start, length, new Vector3f( 0.0f, -1.0f, 0.0f ), new Vector3f( pos.x, bottom(), pos.z ) );
			break;
		}

		return distance;
	}

	public float ray_plane_intersect( final Vector3f line_start, final Vector3f line_dir, final Vector3f plane_normal, final Vector3f plane_point )
	{

		System.out.println( "Tracing ray to plane" );
		float distance = Float.POSITIVE_INFINITY;

		Vector3f line_dir_norm = line_dir.normalise( null );

		float dot_dir = Vector3f.dot( line_dir_norm, plane_normal );

		System.out.println( "DOT of direction and plane normal " + dot_dir );

		if ( dot_dir == 0.0f || dot_dir == -0.0f )
			return Float.POSITIVE_INFINITY;

		System.out.println( "Found to be non-parallel to plane" );

		Vector3f line_start_plane_delta = Vector3f.sub( plane_point, line_start, null );

		System.out.println( "Distance from ray to plane " + line_start_plane_delta );

		float line_plane_distance = ( Vector3f.dot( plane_normal, line_start_plane_delta ) / dot_dir );

		System.out.println( "Distance from ray to plane " + line_plane_distance );

		if ( line_plane_distance < 0.0f )
			return Float.POSITIVE_INFINITY;

		System.out.println( "Returning " + line_plane_distance );
		return line_plane_distance;
	}

	public boolean collides_point( final Vector3f point )
	{
		AABB tiny_box = new AABB( Float.MIN_VALUE * 8, Float.MIN_VALUE * 8, Float.MIN_VALUE * 8 );
		tiny_box.pos.set( point );
		return collides( tiny_box );
	}

	public boolean collides_x_col( final Vector3f point )
	{
		AABB x_col = new AABB( Float.POSITIVE_INFINITY, dim.y * 2, dim.z * 2 );
		x_col.pos.set( pos );
		return x_col.collides_point( point );
	}

	public boolean collides_y_col( final Vector3f point )
	{
		AABB y_col = new AABB( dim.x * 2, Float.POSITIVE_INFINITY, dim.z * 2 );
		y_col.pos.set( pos );
		return y_col.collides_point( point );
	}

	public boolean collides_z_col( final Vector3f point )
	{
		AABB z_col = new AABB( dim.x * 2, dim.y * 2, Float.POSITIVE_INFINITY );
		z_col.pos.set( pos );
		return z_col.collides_point( point );
	}

	public float left()
	{
		return pos.x - dim.x;
	}

	public float right()
	{
		return pos.x + dim.x;
	}

	public float top()
	{
		return pos.y + dim.y;
	}

	public float bottom()
	{
		return pos.y - dim.y;
	}

	public float front()
	{
		return pos.z + dim.z;
	}

	public float back()
	{
		return pos.z - dim.z;
	}

	public float top_intersect( final AABB rhs )
	{
		return top() - rhs.bottom();
	}

	public float bottom_intersect( final AABB rhs )
	{
		return bottom() - rhs.top();
	}

	public float left_intersect( final AABB rhs )
	{
		return left() - rhs.right();
	}

	public float right_intersect( final AABB rhs )
	{
		return right() - rhs.left();
	}

	public float front_intersect( final AABB rhs )
	{
		return front() - rhs.back();
	}

	public float back_intersect( final AABB rhs )
	{
		return back() - rhs.front();
	}

	public String toString()
	{
		return pos.toString() + dim.toString();
	}
}
