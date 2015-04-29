#include "noise.h"

#include <cmath>
#include <random>

namespace Noise
{
    /* Smooth fade in/out factor, expects value [0,1] */
    double fade( double val )
    {
        return val * val * val * ( val * ( val * 6 - 15 ) + 10 );
    }

    /* Linear interpolation */
    double lerp( double factor, double start, double end )
    {
        return start + factor * ( end - start );
    }

    /* Apparently figures out the direction of the gradient, due to be replaced
     * by understandable code for mortals.
     */
    double grad( int hash, double x, double y, double z )
    {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : z;

        return ( ( h & 1 ) == 0 ? u : -u ) + ( ( h & 2 ) == 0 ? v : -v );
    }

    /* Gets the first random number for this seed */
    int first_rand( long seed )
    {
        std::minstd_rand0 random( seed );
        return random(); // Temporary, will be fixed later
    }

    // TODO Convert to template for generic types? ( Better specific performance )
    double perlin( long seed, double x, double y, double z )
    {
        /* Extract the significant side of the decimal */
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
}
