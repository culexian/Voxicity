#ifndef WORLD_H
#define WORLD_H

#include "aabb.h"
#include "config.h"
#include "chunk.h"
#include "chunkid.h"

#include <map>

class World
{
    Config* config;

    // Chunk lookup map
    std::map< ChunkID, Chunk* > chunks;

    public:
    World( Config* config );

    // Checks if a chunk is loaded and returns true if so
    bool is_chunk_loaded( const ChunkID& id ) const;
    bool is_chunk_loaded( int x, int y, int z ) const;
    Chunk* get_chunk( const ChunkID& id );
    Chunk* get_chunk( int x, int y, int z );

    // Sets a chunk in the world to the given chunk.
    // Doesn't care about overwriting existing chunks
    // Marks neighbors for update after.
    // TODO Synchronize
    void set_chunk( int x, int y, int z, Chunk* chunk );

    // TODO Synchronize
    void mark_neighbors( ChunkID id );
    int get_block( int x, int y, int z );
    int get_block( float x, float y, float z );

    // Sets the block id of a block in the world if that chunk is
    // loaded
    void set_block( int x, int y, int z, int id );
    void set_block( float x, float y, float z, int id );
    AABB get_hit_box( int x, int y, int z );
};

#endif // WORLD_H
