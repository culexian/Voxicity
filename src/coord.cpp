#include "coord.h"

#include "constants.h"

namespace Coord
{
    std::vector<int> GlobalToChunk( int x, int y, int z )
    {
        using namespace Constants::Chunk;

        return { x / side_length + ( ( ( x >= 0 ) || ( x % side_length == 0 ) ) ? 0 : -1 ),
                y / side_length + ( ( ( y >= 0 ) || ( y % side_length == 0 ) ) ? 0 : -1 ),
                z / side_length + ( ( ( z >= 0 ) || ( z % side_length == 0 ) ) ? 0 : -1 ) };
    }

    std::vector<int> ChunkToGlobal( int x, int y, int z )
    {
        using namespace Constants::Chunk;

        return { x * side_length,
                y * side_length,
                z * side_length };
    }

    std::vector<int> ChunkOffsetToGlobal( const std::vector<int>& chunk, const std::vector<int>& offset )
    {
        using namespace Constants::Chunk;

        return { chunk[0] * side_length + offset[0],
                chunk[1] * side_length + offset[1],
                chunk[2] * side_length + offset[2] };
    }

    std::vector<int> GlobalToChunkBase( int x, int y, int z )
    {
        auto id = GlobalToChunk( x, y, z );
        return ChunkToGlobal( id[0], id[1], id[2] );
    }

}
