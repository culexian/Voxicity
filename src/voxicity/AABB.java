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
	final Vector3f min = new Vector3f();
	final Vector3f max = new Vector3f();

	// Construct the AABB volume.
	// Select the min and max points
	// from the two vectors supplied
	public AABB( Vector3f a, Vector3f b )
	{
		this.min.set( Math.min( a.x, b.x ),
		              Math.min( a.y, b.y ),
		              Math.min( a.z, b.z ) );

		this.max.set( Math.max( a.x, b.x ),
		              Math.max( a.y, b.y ),
		              Math.max( a.z, b.z ) );
	}

	// Construct an AABB from dimensions of this vector
	public AABB( Vector3f dimensions )
	{
		this( new Vector3f( 0, 0, 0 ), dimensions );
	}

	// Alternate version of AABB dimensional specification
	// by floats
	public AABB( float x, float y, float z )
	{
		this( new Vector3f( x, y, z ) );
	}

	// Copy contructor for AABB
	public AABB( AABB rhs )
	{
		this( rhs.min(), rhs.max() );
	}

	// Construct the AABB of two AABBs
	// Uses the min and max values of both
	public AABB( AABB a, AABB b )
	{
		Vector3f a_min = a.min();
		Vector3f a_max = a.max();
		Vector3f b_min = b.min();
		Vector3f b_max = b.max();

		this.min.set( Math.min( a.min.x, b.min.x ),
		              Math.min( a.min.y, b.min.y ),
		              Math.min( a.min.z, b.min.z ) );

		this.max.set( Math.max( a.max.x, b.max.x ),
		              Math.max( a.max.y, b.max.y ),
		              Math.max( a.max.z, b.max.z ) );
	}

	// Returns a copy of the minimum corner
	public Vector3f min()
	{
		return new Vector3f( min );
	}

	// Returns a copy of the maximum corner
	public Vector3f max()
	{
		return new Vector3f( max );
	}

	// Return the maximum x-extent
	public float max_x()
	{
		return max.x;
	}

	// Return the minimum x-extent
	public float min_x()
	{
		return min.x;
	}

	// Return the maximum y-extent
	public float max_y()
	{
		return max.y;
	}

	// Return the minimum y-extent
	public float min_y()
	{
		return min.y;
	}

	// Return the maximum z-extent
	public float max_z()
	{
		return max.z;
	}

	// Return the minimum z-extent
	public float min_z()
	{
		return min.z;
	}

	// Returns the vector to the center point of the volume
	public Vector3f position()
	{
		return new Vector3f( ( min.x + max.x ) / 2,
		                     ( min.y + max.y ) / 2,
		                     ( min.z + max.z ) / 2 );
	}

	// Return a vector of the distance from the center point
	// to the maximum corner. X is width, Y is height, z is depth.
	public Vector3f dimensions()
	{
		return new Vector3f( ( max.x - min.x ) / 2,
		                     ( max.y - min.y ) / 2,
		                     ( max.z - min.z ) / 2 );
	}

	public void translate( Vector3f v )
	{
		Vector3f.add( min, v, min );
		Vector3f.add( max, v, max );
	}

	public void translate( float x, float y, float z )
	{
		translate( new Vector3f( x, y, z ) );
	}

	public void scale( float x, float y, float z )
	{
		Vector3f center = position();
		Vector3f dim = dimensions();

		dim.x *= x;
		dim.y *= y;
		dim.z *= z;

		min.set( -dim.x, -dim.y, -dim.z );
		max.set( dim.x, dim.y, dim.z );

		center_on( center );
	}

	public void center_on( Vector3f v )
	{
		Vector3f delta = Vector3f.sub( v, position(), null );
		translate( delta );
	}

	public void center_on( float x, float y, float z )
	{
		center_on( new Vector3f( x, y, z ) );
	}

	// Return true if 
	public boolean intersects( final AABB rhs )
	{
		Vector3f rhs_min = rhs.min();
		// Check each axis for collision in standard AABB style
		if ( max_x() < rhs.min_x() ||
		     min_x() > rhs.max_x() ||
		     max_y() < rhs.min_y() ||
		     min_y() > rhs.max_y() ||
		     max_z() < rhs.min_z() ||
		     min_z() > rhs.max_z() )
			return false;

		// If all the checks fail, the AABBs collide
		return true; 
	}

	public float collision_distance( final Vector3f start, final Vector3f length )
	{
		/* Multisampling code, maybe replace later */
		AABB tiny_box = new AABB( 0.000005f, 0.000005f, 0.000005f );
		tiny_box.center_on( start );
		int samples = (int)(length.lengthSquared() * 10);
		Vector3f sample_step = new Vector3f( length );
		sample_step.scale( 1 / ( samples * 1.0f ) );

		for ( int i = 0 ; i < samples ; i++ )
		{
			if ( intersects( tiny_box ) )
			{
				Vector3f distance = Vector3f.sub( tiny_box.position(), start, null );
				return (float)Math.sqrt( distance.lengthSquared() );
			}

			tiny_box.translate( sample_step );
		}

		return Float.POSITIVE_INFINITY;
	}

	public Direction collision_side( final Vector3f start, final Vector3f length )
	{
		float nearest_distance = Float.POSITIVE_INFINITY;
		Direction nearest_dir = Direction.None;

		Vector3f dir_normal = length.normalise( null );

		for ( Direction dir : Direction.values() )
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

	public float collision_side_distance( final Vector3f start, final Vector3f length, Direction dir )
	{
		if ( ( dir == Direction.All ) || ( dir == Direction.None ) )
			return Float.POSITIVE_INFINITY;

		float distance = Float.POSITIVE_INFINITY;

		System.out.println( "Checking for collision on side " + dir );
		switch ( dir )
		{
			case North:
				distance = ray_plane_intersect( start, length, new Vector3f( 0.0f, 0.0f, 1.0f ), new Vector3f( position().x, position().y, max_z() ) );
			break;
			case East:
				distance = ray_plane_intersect( start, length, new Vector3f( 1.0f, 0.0f, 0.0f ), new Vector3f( max_x(), position().y, position().z ) );
			break;
			case South:
				distance = ray_plane_intersect( start, length, new Vector3f( 0.0f, 0.0f, -1.0f ), new Vector3f( position().x, position().y, min_z() ) );
			break;
			case West:
				distance = ray_plane_intersect( start, length, new Vector3f( -1.0f, 0.0f, 0.0f ), new Vector3f( min_x(), position().y, max_z() ) );
			break;
			case Up:
				distance = ray_plane_intersect( start, length, new Vector3f( 0.0f, 1.0f, 0.0f ), new Vector3f( position().x, max_y(), position().z ) );
			break;
			case Down:
				distance = ray_plane_intersect( start, length, new Vector3f( 0.0f, -1.0f, 0.0f ), new Vector3f( position().x, min_y(), position().z ) );
			break;
		}

		return distance;
	}

	public float ray_plane_intersect( final Vector3f line_start, final Vector3f line_dir, final Vector3f plane_normal, final Vector3f plane_point )
	{
		float distance = Float.POSITIVE_INFINITY;

		Vector3f line_dir_norm = line_dir.normalise( null );

		float dot_dir = Vector3f.dot( line_dir_norm, plane_normal );

		if ( dot_dir == 0.0f || dot_dir == -0.0f )
			return Float.POSITIVE_INFINITY;

		Vector3f line_start_plane_delta = Vector3f.sub( plane_point, line_start, null );

		float line_plane_distance = ( Vector3f.dot( plane_normal, line_start_plane_delta ) / dot_dir );

		if ( line_plane_distance < 0.0f )
			return Float.POSITIVE_INFINITY;

		System.out.println( "Returning " + line_plane_distance );
		return line_plane_distance;
	}

	public Vector3f get_vert( int i )
	{
		float x_coord = ( i - 1 ) / 4 < 1 ? min_x() : max_x();
		float y_coord = ( i - 1 ) % 4 / 2 < 1 ? min_y() : max_y();
		float z_coord = ( i - 1 ) % 2 < 1 ? min_z() : max_z();

		return new Vector3f( x_coord, y_coord, z_coord );
	}

	// Test an AABB against the X-axis
	public boolean intersects_x( AABB rhs )
	{
		return !( min_x() >= rhs.max_x() || max_x() <= rhs.min_x() );
	}

	// Test and AABB against the Y-axis
	public boolean intersects_y( AABB rhs )
	{
		return !( min_y() >= rhs.max_y() || max_y() <= rhs.min_y() );
	}

	// Test an AABB against the Z-axis
	public boolean intersects_z( AABB rhs )
	{
		return !( min_z() >= rhs.max_z() || max_z() <= rhs.min_z() );
	}

	// Test an AABB against the XY-corridor
	public boolean intersects_xy( AABB rhs )
	{
		return intersects_x( rhs ) && intersects_y( rhs );
	}

	// Test an AABB against the XZ-corridor
	public boolean intersects_xz( AABB rhs )
	{
		return intersects_x( rhs ) && intersects_z( rhs );
	}

	// Test an AABB against the YZ-corridor
	public boolean intersects_yz( AABB rhs )
	{
		return intersects_y( rhs ) && intersects_z( rhs );
	}

	public boolean collides_point( final Vector3f point )
	{
		AABB tiny_box = new AABB( Float.MIN_VALUE * 8, Float.MIN_VALUE * 8, Float.MIN_VALUE * 8 );
		tiny_box.center_on( point );
		return intersects( tiny_box );
	}

	public boolean collides_x_col( final Vector3f point )
	{
		AABB x_col = new AABB( Float.POSITIVE_INFINITY, dimensions().y * 2, dimensions().z * 2 );
		x_col.center_on( position() );
		return x_col.collides_point( point );
	}

	public boolean collides_y_col( final Vector3f point )
	{
		AABB y_col = new AABB( dimensions().x * 2, Float.POSITIVE_INFINITY, dimensions().z * 2 );
		y_col.center_on( position() );
		return y_col.collides_point( point );
	}

	public boolean collides_z_col( final Vector3f point )
	{
		AABB z_col = new AABB( dimensions().x * 2, dimensions().y * 2, Float.POSITIVE_INFINITY );
		z_col.center_on( position() );
		return z_col.collides_point( point );
	}

	public String toString()
	{
		return min + " " + max;
	}
}
