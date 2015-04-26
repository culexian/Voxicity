#ifndef CHUNK_H
#define CHUNK_H

#include "constants.h"
#include "util.h"

class Chunk
{
    static const int side_length = Constants::Chunk::side_length;

    long modified_time = Util::get_time_ms();
    int blocks[side_length*side_length*side_length];

    void generate_blocks();

    public:
    const int x;
    const int y;
    const int z;

    Chunk( int x, int y, int z );
    int get_block( int x, int y, int z ) const;
    void set_block( int x, int y, int z, int id );
    long get_timestamp() const;
    void update_timestamp();
};

#endif // CHUNK_H}
