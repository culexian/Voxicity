#ifndef NETWORKCONNECTION_H
#define NETWORKCONNECTION_H

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

#include "connection.h"

#include "packetfactory.h"
#include "socket.h"

#include <thread>

class NetworkConnection : public Connection
{
    // The network socket
    Socket* s;

    std::thread sender;
    std::thread receiver;

    // Connection is constructed with a socket that's already been prepared
    NetworkConnection( Socket* s );

    void start_receive_thread();
    void start_send_thread();

    void close();
    bool is_closed() const;

    void wait_send();
};

#endif // NETWORKCONNECTION_H
