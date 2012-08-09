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

import java.util.List;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class WorldUtil
{
	// Get the full list of intersecting volumes for this AABB in this World
	public static List<AABB> get_intersecting_volumes( World world, AABB box )
	{
		if ( world == null || box == null )
			throw new NullPointerException();

		ArrayList<AABB> list = new ArrayList<AABB>();
		Vector3f min = box.min();
		Vector3f max = box.max();

		// Check from the floored minima up to and including the
		// up-rounded maxima of the bounding volume
		for ( int i = (int)Math.floor(min.x) ; i <= Math.ceil( max.x ) ; i++ )
			for ( int j = (int)Math.floor(min.y) ; j <= Math.ceil( max.y ) ; j++ )
				for ( int k = (int)Math.floor(min.z) ; k <= Math.ceil( max.z ) ; k++ )
				{
					AABB hit = world.get_hit_box( i, j, k );

					// Only add non-null volumes to the list
					if ( hit != null )
						list.add( hit );
				}

		return list;
	}
}
