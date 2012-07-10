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

public class RLETree
{
	private class Node
	{
		public int pos;
		public int data;

		public Node next, prev;
		public Node left, right;
		public int balance;

		public Node( int pos, int data )
		{
			this.pos = pos;
			this.data = data;
		}

		public String toString()
		{
			return "(" + pos + " " + data + ")";
		}
	}

	Node head;
	Node root;

	public RLETree()
	{
		head = new Node( 0, 0 );
		root = head;
	}

	int get( int pos )
	{
		if ( pos < 0 )
			return -1;

		return tree_search( root, pos ).data;
	}

	/* Currently seeks through the linked list of runs */
	private Node seek_node( int pos )
	{
		// Stores the current node being looked at
		Node node = head;

		// Until completion
		while ( true )
		{
			// If already at pos, return this one
			if ( node.pos == pos )
				return node;

			// If this is the end of the list, return this one
			if ( node.next == null )
				return node;

			// node.next is not null from here

			// If the next node is greater than pos, return this one
			if ( node.next.pos > pos )
				return node;

			// If the next node is closer to pos, move on there
			if ( node.next.pos <= pos )
				node = node.next;
		}
	}

	// Will always succeed. pos is never less than 0 and runs stretch infinitely if unhindered
	void set( int pos, int data )
	{
		if ( pos < 0 )
		{
			System.out.println( "Error! No runs starting at less than 0 allowed!" );
			return;
		}

		//System.out.println( "Setting " + pos + " " + data );
		Node node = new Node( pos, data );

		// Get the node containing this position
		Node start = tree_search( root, node.pos );
		Node prev = start.prev;
		Node next = start.next;

		// Nothing to do, the position in this run already has that data
		if ( start.data == node.data )
			return;

		// node.data != start.data from here

		// If at the start of the run
		if ( node.pos == start.pos )
		{
			// If the list has just one run
			if ( prev == null && next == null )
			{
				start.pos += 1;
				start.prev = node;
				node.next = start;
				head = node;
				// Insert node to tree
				tree_insert( root, node );
				return;
			}

			// If at the end of the list
			if ( next == null )
			{
				start.pos += 1;
				prev.next = node;
				node.prev = prev;
				node.next = start;
				start.prev = node;
				// Insert node to tree
				tree_insert( root, node );
				// Try collapse prev to node
				collapse( prev, node );
				return;
			}

			// If this run is the first one
			if ( start == head )
			{
				// If a single node at start, just change the data
				if ( next.pos == start.pos + 1 )
				{
					start.data = node.data;
					// Try collapse to next
					collapse( node, next );
					return;
				}
				else // Run is more than 1 in length
				{
					start.pos += 1;
					node.next = start;
					start.prev = node;
					head = node;
					// Insert node to tree
					tree_insert( root, node );
					return;
				}
			}

			// The run is somewhere inside the list

			// If the run is 1 in length
			if ( next.pos == start.pos + 1 )
			{
				start.data = data;
				// Try to collapse start to next
				collapse( start, next );
				// Try to collapse prev to start
				collapse( prev, start );
				return;
			}

			// The run is longer than 1 in length
			start.pos += 1;
			start.prev = node;
			node.next = start;
			node.prev = prev;
			prev.next = node;
			// Insert node to tree
			tree_insert( root, node );
			// Try to collapse prev to node
			collapse( prev, node );
			return;
		}
		else // Somewhere out in a run, by definition in a run longer than 1 length
		{
			// If there is just one run in the list
			if ( prev == null && next == null )
			{
				// Split the run. The old start, the new node and a new tail with same data as start
				Node last = new Node( node.pos + 1, start.data );
				start.next = node;
				node.prev = start;
				node.next = last;
				last.prev = node;
				// Insert node to tree
				tree_insert( root, node );
				// Insert last to tree
				tree_insert( root, last );
				return;
			}

			// If this is the first run
			if ( start == head )
			{
				// If at the end of the run
				if ( next.pos == node.pos + 1 )
				{
					node.next = next;
					node.prev = start;
					next.prev = node;
					start.next = node;
					// Insert node to tree
					tree_insert( root, node );
					// Try to collapse node to next
					collapse( node, next );
					return;
				}
				else // Inside the first run of at least length 3
				{
					Node new_next = new Node( node.pos + 1, start.data );
					start.next = node;
					node.prev = start;
					node.next = new_next;
					new_next.prev = node;
					new_next.next = next;
					next.prev = new_next;
					// Inset node to tree
					tree_insert( root, node );
					// Insert new_next to tree
					tree_insert( root, new_next );
					return;
				}
			}

			// If this is the last run
			if ( next == null )
			{
				Node new_last = new Node( node.pos + 1, start.data );
				node.prev = start;
				start.next = node;
				node.next = new_last;
				new_last.prev = node;
				// Insert node to tree
				tree_insert( root, node );
				// Insert new_last to tree
				tree_insert( root, new_last );
				return;
			}

			// The run is somewhere inside the list

			// If at the end of the run
			if ( next.pos == node.pos + 1 )
			{
				node.next = next;
				next.prev = node;
				node.prev = start;
				start.next = node;
				// Insert node to tree
				tree_insert( root, node );
				// Try to collapse node to next
				collapse( node, next );
				return;
			}

			// node.pos is somewhere inside a run of at least length 3
			Node new_next = new Node( node.pos + 1, start.data );
			node.next = new_next;
			new_next.prev = node;
			start.next = node;
			node.prev = start;
			new_next.next = next;
			next.prev = new_next;
			// Insert node to tree
			tree_insert( root, node );
			// Insert new_next to tree
			tree_insert( root, new_next );
			return;
		}
	}

	private void collapse( Node a, Node b )
	{
		// Check that the links work( shouldn't ever fail if all else is done right )
		if ( a.next == b && b.prev == a )
		{
			// a and b have the same data, merge to one run by removing b
			if ( a.data == b.data )
			{
				a.next = b.next;

				// Don't try to set null's prev
				if ( b.next != null )
					b.next.prev = a;

				// Remove b from tree
				root = tree_delete( root, b );
			}
		}
	}

	// Insert a new node into the tree at the first available position
	// that fits its pos attribute starting from _root
	private Node tree_insert( Node _root, Node node )
	{
		// If any of the two nodes are invalid, return
		if ( node == null || _root == null )
			return _root;

		// If a node with this pos already exists, return without doing anything
		if ( node.pos == _root.pos )
			return _root;

		// If node.pos is less than this root, add it to the left subtree
		if ( node.pos < _root.pos )
		{
			// No left subtree, this node is it now
			if ( _root.left == null )
				_root.left = node;
			else // Call insert on the left subtree
				tree_insert( _root.left, node );
		}
		else // node.pos is greater than _root.pos
		{
			// No right subtree, this node is it now
			if ( _root.right == null )
				_root.right = node;
			else // Call insert on the right subtree
				tree_insert( _root.right, node );
		}

		// Balance the tree
		return _root;
	}

	private Node tree_delete( Node _root, Node node )
	{
		// If either _root or node is invalid, return
		if ( _root == null || node == null )
			return _root;

		// Once we're at the node to remove
		if ( _root.pos == node.pos )
		{
			// No children nodes, return null to remove node
			if ( _root.left == null && _root.right == null )
				return null;

			// Just a left child, return that as the new node
			if ( _root.left != null && _root.right == null )
				return _root.left;

			// Just a right child, return that as the new node
			if ( _root.left == null && _root.right != null )
				return _root.right;

			// _root has two children, find an heir
			Node heir = find_heir( _root );

			// Remove the heir from the tree
			tree_delete( _root, heir );

			// Fix links
			heir.left = _root.left;
			heir.right = _root.right;

			// Balance tree
			return heir;
		}

		// Keep searching down the tree

		if ( node.pos < _root.pos )
			_root.left = tree_delete( _root.left, node );
		else if ( node.pos > _root.pos )
			_root.right = tree_delete( _root.right, node );

		// Balance tree
		return _root;
	}

	// Find an heir for the root node, return that.
	// _root node has 2 children at this point
	// Returns the right-most node in the left subtree,
	// return node will always have a left child or no children.
	private Node find_heir( Node _root )
	{
		// Start in the left subtree of the root
		Node cur = _root.left;

		// Find the right-most( closest to root ) node
		while ( cur.right != null )
			cur = cur.right;

		// Return the right-most node
		return cur;
	}

	// Search the tree from root to a run using binary search
	// and returning the Node containing the requested pos in its run
	private Node tree_search( Node _root, int pos )
	{
		if ( pos < 0 )
		{
			System.out.println( "No positions less than 0 allowed." );
			return null;
		}

		// If the _root is null, return null
		if ( _root == null )
			return null;

		// If _root.pos is equal to to pos, return _root
		if ( pos == _root.pos )
			return _root;

		// If pos is less than _root.pos, search the left subtree
		// If there is no left subtree, return the previous list node
		if ( pos < _root.pos )
		{
			if ( _root.left == null )
				return _root.prev;
			else
				return tree_search( _root.left, pos );
		}
		else // pos > _root.pos, work through the right subtree
		{
			// Search the right node if it exists
			if ( _root.right == null )
				return _root;
			else // Right node exists, check it
				return tree_search( _root.right, pos );
		}
	}

	String tree_traverse( Node _root )
	{
		if ( _root == null )
			return "";
		else
			return "(" + tree_traverse( _root.left ) + " " + _root.pos + " " + _root.data + " " + tree_traverse( _root.right ) + ")";
	}

	public String toString()
	{
		Node node = head;
		int count = 0;
		String out = new String();

		while ( node != null )
		{
			
			out += node.toString();
			node = node.next;
			count++;
		}

		String tree_out = tree_traverse( root );

		out += " " + count + " runs.\n";

		out += tree_out;

		return out;
	}
}
