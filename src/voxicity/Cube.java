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

import java.nio.IntBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public class Cube
{
	public static FloatBuffer vertices()
	{
		// Make an array with the vertices for 6 faces of a cube
		float[] verts = new float[]{
		                   // Left
		                   -0.5f,  0.5f,  0.5f,
		                   -0.5f,  0.5f, -0.5f,
		                   -0.5f, -0.5f,  0.5f,
		                   -0.5f, -0.5f, -0.5f,

		                   // Right
		                    0.5f,  0.5f,  0.5f,
		                    0.5f,  0.5f, -0.5f,
		                    0.5f, -0.5f,  0.5f,
		                    0.5f, -0.5f, -0.5f,

		                   // Top
		                    0.5f,  0.5f,  0.5f,
		                    0.5f,  0.5f, -0.5f,
		                   -0.5f,  0.5f,  0.5f,
		                   -0.5f,  0.5f, -0.5f,

		                   // Bottom
		                    0.5f, -0.5f,  0.5f,
		                    0.5f, -0.5f, -0.5f,
		                   -0.5f, -0.5f,  0.5f,
		                   -0.5f, -0.5f, -0.5f,

		                   // Front
		                    0.5f,  0.5f,  0.5f,
		                    0.5f, -0.5f,  0.5f,
		                   -0.5f,  0.5f,  0.5f,
		                   -0.5f, -0.5f,  0.5f,

		                   // Back
		                    0.5f,  0.5f, -0.5f,
		                    0.5f, -0.5f, -0.5f,
		                   -0.5f,  0.5f, -0.5f,
		                   -0.5f, -0.5f, -0.5f,
		                  };

		// Store the vertices in a properly sized buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( verts.length );
		buf.put( verts );

		// Rewind buffer to start
		buf.rewind();

		// Return the buffer
		return buf;
	}
	
	static FloatBuffer offset_vertices( Vector3f pos )
	{
		FloatBuffer verts = vertices();

		// Offset the vertices by the amount in pos
		for ( int i = 0 ; i < verts.limit() ; i++ )
		{
			if ( i % 3 == 0 ) verts.put( i, verts.get(i) + pos.x );
			if ( i % 3 == 1 ) verts.put( i, verts.get(i) + pos.y );
			if ( i % 3 == 2 ) verts.put( i, verts.get(i) + pos.z );
		}

		// Rewind verts to start of buffer
		verts.rewind();

		// Return the offset buffer
		return verts;
	}

	static IntBuffer indices()
	{
		// Return array with indices for a cube, 6 sides, 1 quad each
		int[] indices = new int[]{
		                  0, 1, 3, 2,     // Left
		                  6, 7, 5, 4,     // Right
		                  8, 9, 11, 10,   // Top
		                  14, 15, 13, 12, // Bottom
		                  17, 16, 18, 19, // Front
		                  23, 22, 20, 21, // Back
		                };

		// Store the indices in a buffer
		IntBuffer buf = BufferUtils.createIntBuffer( indices.length );
		buf.put( indices );

		// Rewind the buffer to start
		buf.rewind();

		// Return the buffer
		return buf;
	}

	static FloatBuffer texture_coords()
	{
		// Return texture coords for 6 sides of 4 vertices that make a cube
		float[] coords = new float[]{
		                   // Left
		                   1, 0,
		                   0, 0,
		                   1, 1,
		                   0, 1,

		                   // Right
		                   0, 0,
		                   1, 0,
		                   0, 1,
		                   1, 1,

		                   // Top
		                   1, 0,
		                   1, 1,
		                   0, 0,
		                   0, 1,

		                   // Bottom
		                   0, 0,
		                   0, 1,
		                   1, 0,
		                   1, 1,

		                   // Front
		                   0, 0,
		                   0, 1,
		                   1, 0,
		                   1, 1,

		                   // Back
		                   1, 0,
		                   1, 1,
		                   0, 0,
		                   0, 1,
		                  };

		// Store the coords in a buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( coords.length );
		buf.put( coords );
		buf.rewind();

		return buf;
	}

	static AABB bounds()
	{
		return new AABB( 1, 1, 1 );
	}
}
