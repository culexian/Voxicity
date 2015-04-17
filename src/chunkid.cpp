#include "chunkid.h"

#include "coord.h"

#include <cmath>

ChunkID::ChunkID( int _x, int _y, int _z )
{
    auto coords = Coord::GlobalToChunkBase( _x, _y, _z );
    x = coords[0];
    y = coords[1];
    z = coords[2];
}

ChunkID::ChunkID( float x, float y, float z ):
    ChunkID( static_cast<int>(std::lrint(x)),
            static_cast<int>(std::lrint(y)),
            static_cast<int>(std::lrint(z)) )
{}

std::vector<int> ChunkID::coords() const
{
    return { x, y, z };
}

ChunkID ChunkID::get( Direction d ) const
{
    using namespace Constants::Chunk;

    switch( d )
    {
        case East:  return ChunkID( x + side_length, y, z );
        case West:  return ChunkID( x - side_length, y, z );
        case Up:    return ChunkID( x, y + side_length, z );
        case Down:  return ChunkID( x, y - side_length, z );
        case North: return ChunkID( x, y, z + side_length );
        case South: return ChunkID( x, y, z - side_length );
        default: return *this;
    }
}
