#ifndef COORD_H
#define COORD_H

#include <vector>

namespace Coord
{
    std::vector<int> GlobalToChunk( int x, int y, int z );
    std::vector<int> ChunkToGlobal( int x, int y, int z );
    std::vector<int> ChunkOffsetToGlobal( const std::vector<int>& chunk, const std::vector<int>& offset );
    std::vector<int> GlobalToChunkBase( int x, int y, int z );
}

#endif // COORD_H
