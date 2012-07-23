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

import java.util.HashMap;
import java.util.Map;

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
			air = 0,
			dirt = 1,
			stone = 2,
			grass = 3
			;

		public static final Map< Integer, String > tex_map = new HashMap< Integer, String >()
		{
			{
				put( Blocks.dirt, "textures/dirt.png" );
				put( Blocks.stone, "textures/stone.png" );
				put( Blocks.grass, "textures/grass.png" );
			}
		};
	}

	public enum Direction
	{
		North, East, South, West, Up, Down, All, None
	}

	public static class Packet
	{
		public static final int
		LoadChunk = 0,
		BlockUpdate = 1,
		UseAction = 2,
		HitAction = 3,
		MoveAction = 4
		;
	}
}
