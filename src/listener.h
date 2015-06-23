#ifndef LISTENER_H
#define LISTENER_H

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
#include "config.h"
#include "connection.h"
#include "socket.h"

#include <thread>

class Listener
{
    BlockingQueue< Connection* > connections;
    Socket socket;
    std::thread thread;
    bool quitting = false;

    public:
    Listener( Config* config );
    void run();
    void quit();
    Connection* get_new_connection();
};

#endif // LISTENER_H
