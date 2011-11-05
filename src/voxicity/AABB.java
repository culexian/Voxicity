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
