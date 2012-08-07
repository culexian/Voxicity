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

public class MathUtil
{
	public static int clamp( int lower, int upper, int value )
	{
		if ( lower > upper )
			return value;

		if ( value >= upper )
			return upper;

		if ( value <= lower )
			return lower;

		return value;
	}

	public static float clamp( float lower, float upper, float value )
	{
		if ( lower > upper )
			return value;

		if ( value > upper )
			return upper;

		if ( value < lower )
			return lower;

		return value;
	}

	public static boolean within_range( float lower, float upper, float value )
	{
		if ( lower > upper )
			return false;

		if ( value < lower || value > upper )
			return false;

		return true;
	}
}
