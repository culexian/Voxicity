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

#include <cmath>
#include <ctime>
#include <random>

class Noise
{
private:
	/* Smooth fade in/out factor, expects value [0,1] */
	static double fade( double val )
	{
		return val * val * val * ( val * ( val * 6 - 15 ) + 10 );
	}

	/* Linear interpolation */
	static double lerp( double factor, double start, double end )
	{
		return start + factor * ( end - start );
	}

	/* Apparently figures out the direction of the gradient, due to be replaced
	 * by understandable code for mortals.
	 */
	static double grad( int hash, double x, double y, double z )
	{
		int h = hash & 15;
		double u = h < 8 ? x : y;
		double v = h < 4 ? y : h == 12 || h == 14 ? x : z;

		return ( ( h & 1 ) == 0 ? u : -u ) + ( ( h & 2 ) == 0 ? v : -v );
	}

	/* Gets the first random number for this seed */
	static int first_rand( long seed )
	{
		srand(time(NULL));
		int random = rand() % seed + 1;
		return random; // Temporary, will be fixed later
	}

public:
	static double perlin( long seed, double x, double y, double z )
	{
		/*Extract the significant side of the decimal */
		int X = std::floor( x );
		int Y = std::floor( y );
		int Z = std::floor( z );

		/* Extract the less significant part of the decimal */
		x -= std::floor( x );
		y -= std::floor( y );
		z -= std::floor( z );

		/* Get fade values for each coord */
		double u = fade( x );
		double v = fade( y );
		double w = fade( z );

		/* Hash coords of 6 cube corners */
		int A = first_rand( seed + X ) + Y;
		int AA = first_rand( seed + A ) + Z;
		int AB = first_rand( seed + A + 1 ) + Z;
		int B = first_rand( seed + X + 1 ) + Y;
		int BA = first_rand( seed + B ) + Z;
		int BB = first_rand( seed + B + 1 ) + Z;

		return lerp( w, lerp( v, lerp( u, grad( first_rand(AA), x, y, z),
		                         grad( first_rand(BA), x - 1, y, z ) ),
		              lerp( u, grad( first_rand(AB), x, y - 1, z ),
		                       grad( first_rand(BB), x - 1, y - 1, z ) ) ),
		              lerp( v, lerp( u, grad( first_rand(AA+1), x, y, z - 1 ),
		                       grad( first_rand(BA+1), x - 1, y, z - 1 ) ),
		              lerp( u, grad( first_rand(AB+1), x, y - 1, z - 1),
		                       grad( first_rand(BB+1), x - 1, y - 1, z - 1 ) ) ) );
	}
};