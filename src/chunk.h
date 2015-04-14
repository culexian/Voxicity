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
    void update_timestamp();

    public:
    const int x;
    const int y;
    const int z;

    Chunk( int x, int y, int z );
    void get_block( int x, int y, int z, int type );
    void set_block( int x, int y, int z, int type );
    long get_timestamp() const;

};

#endif // CHUNK_H}
