/*
 * Copyright 2014, Erik Lund
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

#include "fpscounter.h"
#include "util.h"

// Reset the time and counter
void FPSCounter::reset()
{
    start = Util::get_time_ms();
    counter = 0;
}

// Compute the frames per second since the last reset
// 1000 ms * number of frames / ms since start
int FPSCounter::fps()
{
    long delta = time_delta();

    if (delta == 0)
        return 1000 * counter;
    else
        return int (1000 * counter / (time_delta() * 1.0f));
}

// Return the time delta since the last reset
long FPSCounter::time_delta()
{
    return Util::get_time_ms() - start;
}

// Increments the counter of
// frames since last reset
void FPSCounter::update()
{
    counter++;
}
