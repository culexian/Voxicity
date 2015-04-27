#include "chunk.h"

#include <cmath>
#include <iostream>

Chunk::Chunk( int x, int y, int z ):
    x(x), y(y), z(z)
{
    generate_blocks();
    std::printf("Created chunk at %d, %d, %d\n", x, y, z );
}

void Chunk::generate_blocks()
{
    using namespace Constants;
    using namespace Constants::Chunk;

    long start = Util::get_time_ns();

    double heightmap[side_length][side_length];

    // Make a heightmap for this chunk
    for ( int x = 0 ; x < side_length ; x++ )
        for ( int z = 0 ; z < side_length ; z++ )
            //heightmap[x][z] = Noise.perlin( 0, ( this.x + x ) / 600.0f, 0, ( this.z + z ) / 600.0f );
            heightmap[x][z] = 0.0;

    for ( int x = 0 ; x < side_length ; x++ )
    {
        for ( int y = 0 ; y < side_length ; y++ )
        {
            for ( int z = 0 ; z < side_length ; z++ )
            {
                //double noise = Noise.perlin( 0, (this.x + x) / 10.0f, (this.y + y) / 20.0f, (this.z + z) / 10.0f );
                double noise = 0;

                //System.out.println( "Height factor: " + height_factor );
                int ground_level = static_cast<int>(std::nearbyint(heightmap[x][z] * 2 + noise * 3));
                //System.out.println( "x: " + ( this.x + x ) + " y: " + ( this.y + y ) + " z: " + ( this.z + z ) );
                //System.out.println( "Ground level: " + ground_level );

                if ( ( this->y + y ) > ground_level )
                    set_block( x, y, z, Blocks::air );
                else if ( ( this->y + y ) == ground_level )
                    set_block( x, y, z, Blocks::grass );
                else
                {
                //System.out.println( "Ground level: " + ground_level + " y-coord: " + ( this.y + y ) );
                    //System.out.println( "ground_level - ( y-coord ) = " + ( ground_level - ( this.y + y ) ) );
                    if ( ground_level - ( this->y + y ) <= 1 )
                        set_block( x, y, z, Blocks::dirt );
                    else
                        set_block( x, y, z, Blocks::stone );
                }
            }
        }
    }
    long end = Util::get_time_ns();
}

int Chunk::get_block( int x, int y, int z ) const
{

}

void Chunk::set_block( int x, int y, int z, int type )
{

}

void Chunk::update_timestamp()
{
    modified_time = Util::get_time_ms();
}

long Chunk::get_timestamp() const
{
    return modified_time;
}
