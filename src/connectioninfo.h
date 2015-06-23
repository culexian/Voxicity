#ifndef CONNECTION_INFO_H
#define CONNECTION_INFO_H

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

#include "util.h"

class ConnectionInfo
{
    int update_id;
    long last_update = Util::get_time_ms();
    bool awaiting_update = false;

    public:
    ConnectionInfo();

    int get_update_id() const;
    long get_last_update() const;
    long get_next_update() const;
    void update();
};

#endif // CONNECTION_INFO_H
