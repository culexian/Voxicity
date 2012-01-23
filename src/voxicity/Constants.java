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

public class Constants
{
	public static class Chunk
	{
		public static final Integer
			side_length = 16,
			block_number = side_length * side_length * side_length;
	}

	public static class Blocks
	{
		public static final Integer
			dirt = 0,
			stone = 1,
			grass = 2
			;
	}

	public enum Direction
	{
		North, East, South, West, Up, Down, All, None
	}
}
