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

public class ConnectionGlue implements Runnable
{
	Connection a, b;
	boolean quitting = false;

	public ConnectionGlue( Connection a, Connection b )
	{
		this.a = a;
		this.b = b;
	}

	public void quit()
	{
		quitting = true;
	}

	public void run()
	{
		while( !quitting )
		{
			try
			{
				while( a.outgoing.peek() != null )
					b.incoming.put( a.outgoing.take() );

				while( b.outgoing.peek() != null )
					a.incoming.put( b.outgoing.take() );

				Thread.currentThread().sleep( 20 );
			}
			catch ( Exception e )
			{
				System.out.println( e );
				e.printStackTrace();
			}
		}
	}
}
