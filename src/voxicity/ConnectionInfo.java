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

public class ConnectionInfo
{
	int update_id;
	long last_update = Time.get_time_ms();
	boolean awaiting_update = false;

	public ConnectionInfo()
	{
		update();
	}

	public int get_update_id()
	{
		return update_id;
	}

	public long get_last_update()
	{
		return last_update;
	}

	public long get_next_update()
	{
		return last_update + 10000;
	}

	public void update()
	{
		last_update = Time.get_time_ms();
		update_id = new java.util.Random( last_update ).nextInt();
		awaiting_update = false;
	}
}
