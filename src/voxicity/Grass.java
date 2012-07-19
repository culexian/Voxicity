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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Grass implements Block
{
	public int id()
	{
		return Constants.Blocks.grass;
	}

	public String texture_string()
	{
		return "textures/grass.png";
	}

	public FloatBuffer texture_coords()
	{
		// Return texture coords for 6 sides of 4 vertices that make a cube
		float[] coords = new float[]{
		                   // Left
		                   1f, 0.75f,
		                   0f, 0.75f,
		                   1f, 0.5f,
		                   0f, 0.5f,

		                   // Right
		                   1f, 0.75f,
		                   0f, 0.75f,
		                   1f, 0.5f,
		                   0f, 0.5f,

		                   // Top
		                   1f, 1f,
		                   1f, 0.75f,
		                   0f, 1f,
		                   0f, 0.75f,

		                   // Bottom
		                   1f, 0.25f,
		                   1f, 0.5f,
		                   0f, 0.25f,
		                   0f, 0.5f,

		                   // Front
		                   1f, 0.75f,
		                   1f, 0.5f,
		                   0f, 0.75f,
		                   0f, 0.5f,

		                   // Back
		                   1f, 0.75f,
		                   1f, 0.5f,
		                   0f, 0.75f,
		                   0f, 0.5f,
		                  };

		// Put the coords in a properly sized buffer
		FloatBuffer buf = BufferUtils.createFloatBuffer( coords.length );
		buf.put( coords );

		// Rewind to start of buffer
		buf.rewind();

		// Return the buffer
		return buf;
	}

	public FloatBuffer vertices()
	{
		return Cube.vertices();
	}

	public IntBuffer indices()
	{
		return Cube.indices();
	}

	public AABB bounds()
	{
		return Cube.bounds();
	}
}
