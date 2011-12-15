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

import java.util.list;

class Node
{
	List<Node> children = new ArrayList<Node>();

	boolean dirty = true;

	public void clean()
	{
		if ( dirty )
			; // Do stuff
	}

	public void render()
	{
		for( Node child : children )
			child.render();
	}

	public boolean has_child( Node child )
	{
		for ( Node node : children )
			if ( node == child )
				return true;

		return false;
	}

	public void add_child( Node child )
	{
		if ( !has_child( child ) )
			children.add( child );
	}

	public void remove_child( Node child )
	{
		Iterator<Node> iter = children.iterator();

		while ( iter.hasNext() )
			if ( iter.next() == child )
				iter.remove();
	}
}
