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

package voxicity;

public class FPSCounter
{
	long start;
	int counter;

	// Reset the time and counter
	public void reset()
	{
		start = Time.get_time_ms();
		counter = 0;
	}

	// Compute the frames per second since the last reset
	// 1000 ms * number of frames / ms since start
	public int fps()
	{
		long delta = time_delta();

		if ( delta == 0 )
			return 1000 * counter;
		else
			return 1000 * counter / (int)time_delta();
	}

	// Return the time delta since the last reset
	public long time_delta()
	{
		return Time.get_time_ms() - start;
	}

	// Increments the counter of
	// frames since last reset
	public void update()
	{
		counter++;
	}
}
