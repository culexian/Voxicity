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

public class Dirt implements Block
{
	public int id()
	{
		return Constants.Blocks.dirt;
	}

	public String texture_string()
	{
		return "textures/dirt.png";
	}

	public FloatBuffer vertices()
	{
		return Cube.vertices();
	}

	public FloatBuffer texture_coords()
	{
		return Cube.texture_coords();
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
