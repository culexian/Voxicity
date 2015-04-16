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
}
