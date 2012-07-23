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

import java.util.ArrayList;

public class Blocks
{
	static ArrayList< Block > id_index = new ArrayList< Block >()
	{
		{
			add( Constants.Blocks.air, new Air() );
			add( Constants.Blocks.dirt, new Dirt() );
			add( Constants.Blocks.stone, new Stone() );
			add( Constants.Blocks.grass, new Grass() );
		}
	};

	static Block get( int id )
	{
		if ( ( id < 0 ) || ( id >= id_index.size() ) )
			return id_index.get( 0 );
		else
			return id_index.get( id );
	}
}
