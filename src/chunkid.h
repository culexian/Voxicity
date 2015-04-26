#ifndef CHUNKID_H
#define CHUNKID_H

#include "constants.h"

#include <vector>

class ChunkID
{
    public:
    int x;
    int y;
    int z;

    ChunkID( int x, int y, int z );
    ChunkID( float x, float y, float z );

    std::vector<int> coords() const;
    ChunkID get( Direction d ) const;

    bool operator<( const ChunkID& c ) const;
};

#endif // CHUNKID_H
