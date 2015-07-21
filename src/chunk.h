#ifndef CHUNK_H
#define CHUNK_H

#include "chunkid.h"
#include "constants.h"
#include "util.h"

class Chunk
{
    long modified_time = Util::get_time_ms();
    int blocks[Constants::Chunk::blocks_per_chunk];

    void generate_blocks();

    public:
    const int x;
    const int y;
    const int z;

    Chunk( ChunkID id );
    Chunk( int x, int y, int z );
    int get_block( int x, int y, int z ) const;
    void set_block( int x, int y, int z, int id );
    long get_timestamp() const;
    void update_timestamp();
};

#endif // CHUNK_H}
