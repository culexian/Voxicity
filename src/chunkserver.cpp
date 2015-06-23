#include "chunkserver.h"

ChunkServer::ChunkServer( World* world )
{
    this->world = world;

    // Start this chunk serving thread
    thread = std::thread( [&]{ run(); } );
}

void ChunkServer::quit()
{
    //thread.interrupt();
    quitting = true;
}

void ChunkServer::run()
{
    while ( !quitting )
    {
        // Try to get a new request
        ChunkID id = incoming_requests.take();

        // Don't get a chunk that's already loaded
        if ( world->is_chunk_loaded( id ) )
            continue;

        Chunk* c = new Chunk( id );

        world->set_chunk( id.x, id.y, id.z, c );
    }
}

// Adds a new chunk request to the queue
void ChunkServer::request_chunk( ChunkID id )
{
    incoming_requests.put( id );
}
