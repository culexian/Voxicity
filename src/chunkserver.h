#ifndef CHUNKSERVER_H
#define CHUNKSERVER_H

/*
 * Copyright 2011, Erik Lund
 *
 * This file is part of Voxicity.
 *
 *  Voxicity is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Voxicity is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Voxicity.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "blockingqueue.h"
#include "chunk.h"
#include "world.h"

#include <thread>

class ChunkServer
{
    bool quitting = false;

    // The thread running this object
    std::thread thread;

    // The world to supply finished chunks to
    World* world;

    // The queue of incoming requests for chunk serving
    BlockingQueue<ChunkID> incoming_requests;

    public:
    ChunkServer( World* world );

    void quit();
    void run();

    // Adds a new chunk request to the queue
    void request_chunk( ChunkID id );
};

#endif // CHUNKSERVER_H
