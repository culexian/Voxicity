#include "world.h"

#include <cmath>

World::World( Config* config )
{
    this->config = config;
}

// Checks if a chunk is loaded and returns true if so
bool World::is_chunk_loaded( const ChunkID& id ) const
{
    // TODO Synchronize this
    return chunks.count( id ) > 0;
}

bool World::is_chunk_loaded( int x, int y, int z ) const
{
    return is_chunk_loaded( ChunkID( x, y, z ) );
}

// TODO Synchronize
Chunk* World::get_chunk( const ChunkID& id ) const
{
    auto c = chunks.find(id);

    if ( c == chunks.end() )
        return nullptr;
    else
        return c->second;
}

Chunk* World::get_chunk( int x, int y, int z ) const
{
    return get_chunk( ChunkID( x, y, z ) );
}

// Sets a chunk in the world to the given chunk.
// Doesn't care about overwriting existing chunks
// Marks neighbors for update after.
// TODO Synchronize
void World::set_chunk( int x, int y, int z, Chunk* chunk )
{
    if ( chunk == nullptr ) return;

    ChunkID id( x, y, z );
    //chunk.world = this;
    chunks[id] = chunk;
    mark_neighbors( id );
}

// TODO Synchronize
void World::mark_neighbors( ChunkID id )
{
    if ( is_chunk_loaded( id.get( West ) ) )
        get_chunk( id.get( West ) )->update_timestamp();

    if ( is_chunk_loaded( id.get( East ) ) )
        get_chunk( id.get( East ) )->update_timestamp();

    if ( is_chunk_loaded( id.get( North ) ) )
        get_chunk( id.get( North ) )->update_timestamp();

    if ( is_chunk_loaded( id.get( South ) ) )
        get_chunk( id.get( South ) )->update_timestamp();

    if ( is_chunk_loaded( id.get( Up ) ) )
        get_chunk( id.get( Up ) )->update_timestamp();

    if ( is_chunk_loaded( id.get( Down ) ) )
        get_chunk( id.get( Down ) )->update_timestamp();
}

int World::get_block( int x, int y, int z ) const
{
    ChunkID id( x, y, z );

    // If chunk is not loaded, block is air
    if ( is_chunk_loaded( id ) )
        return get_chunk( id )->get_block( x, y, z );
    else
        return Constants::Blocks::air;
}

int World::get_block( float x, float y, float z ) const
{
    return get_block( static_cast<int>(std::lrint( x )),
        static_cast<int>(std::lrint( y )),
        static_cast<int>(std::lrint( z ) ));
}

// Sets the block id of a block in the world if that chunk is
// loaded
void World::set_block( int x, int y, int z, int id )
{
    ChunkID cid( x, y, z );

    // Don't operate on null, otherwise, set the block
    if ( is_chunk_loaded( cid ) )
        get_chunk( cid )->set_block( x, y, z, id );
}

void World::set_block( float x, float y, float z, int id )
{
    set_block( static_cast<int>(std::lrint( x )),
        static_cast<int>(std::lrint( y )),
        static_cast<int>(std::lrint( z )),
        id );
}

AABB World::get_hit_box( int x, int y, int z ) const
{
    int id = get_block( x, y, z );

    if ( id == Constants::Blocks::air )
        return AABB();

    // TODO Port Cube
    //AABB box = Cube.bounds();
    AABB box( 1, 1, 1 );
    box.center_on( x, y, z );
    return box;
}
