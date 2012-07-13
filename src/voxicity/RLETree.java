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
		public int height;

		public Node( int pos, int data )
		{
			this.pos = pos;
			this.data = data;
			this.height = 0;
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

		//System.out.println( "Search found " + prev + "|" + start + "|" + next + " for pos " + pos );	
		//System.out.println( "Root " + root + "\n" + this );

		if ( next != null && next.pos < start.pos )
			System.out.println( "LOOK HERE!" );

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
				root = tree_insert( root, node );
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
				root = tree_insert( root, node );
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
					root = tree_insert( root, node );
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
			root = tree_insert( root, node );
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
				root = tree_insert( root, node );
				// Insert last to tree
				root = tree_insert( root, last );
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
					root = tree_insert( root, node );
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
					root = tree_insert( root, node );
					// Insert new_next to tree
					root = tree_insert( root, new_next );
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
				root = tree_insert( root, node );
				// Insert new_last to tree
				root = tree_insert( root, new_last );
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
				root = tree_insert( root, node );
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
			root = tree_insert( root, node );
			// Insert new_next to tree
			root = tree_insert( root, new_next );
			return;
		}
	}

	// Removes a Node from the linked list
	// Sets the head if needed
	private void list_remove( Node n )
	{
		// Don't operate on null
		if ( n == null )
			return;

		// If we're removing the head
		if ( n == head )
		{
			// New head has no previous node
			n.next.prev = null;
			// Set the new head
			head = n.next;
		}
		else if ( n.next == null ) // Removing the tail
		{
			// Link the previous to the next( null )
			n.prev.next = null;
		}
		else // Inside the list
		{
			// Link the previous and next nodes to eachother
			n.prev.next = n.next;
			n.next.prev = n.prev;
		}
	}

	private void collapse( Node a, Node b )
	{
		// Check that the links work( shouldn't ever fail if all else is done right )
		// Also check that a and b share the same data value
		if ( ( a.next == b ) && ( b.prev == a ) && ( a.data == b.data ) )
		{
			// Link a to b's next
			a.next = b.next;

			// Don't try to set null's prev
			if ( b.next != null )
				b.next.prev = a;

			// Remove b from tree
			root = tree_delete( root, b );
		}
	}

	// Insert a new node into the tree at the first available position
	// that fits its pos attribute starting from _root
	// Return the new _root
	private static Node tree_insert( Node _root, Node node )
	{
		// If node is null, keep the root
		if ( node == null )
			return _root;

		// If _root is null, return the new _root
		if ( _root == null )
			return node;

		// If a node with this pos already exists, return without doing anything
		if ( node.pos == _root.pos )
		{
			System.out.println( "LOOK HERE 2!" );
			return _root;
		}

		// If node.pos is less than this root, add it to the left subtree
		if ( node.pos < _root.pos )
			_root.left = tree_insert( _root.left, node );
		else // node.pos is greater than _root.pos
			_root.right = tree_insert( _root.right, node );

		// Balance the tree
		return tree_balance( _root );
	}

	// Remove a node from the tree
	// and chooses a successor if needed
	// Return the new root
	private static Node tree_delete( Node _root, Node node )
	{
		// If either _root or node is invalid, return
		if ( _root == null || node == null )
			return _root;

		// Once we're at the node to remove
		if ( _root.pos == node.pos )
		{
			// No children nodes, return null to remove node
			if ( ( _root.left == null ) && ( _root.right == null ) )
				return null;

			// Just a left child, return that as the new node
			if ( _root.right == null )
				return _root.left;

			// Just a right child, return that as the new node
			if ( _root.left == null )
				return _root.right;

			// _root has two children, find an heir
			Node heir = find_heir( _root );

			// Remove the heir from the tree
			_root = tree_delete( _root, heir );

			// Search and replace node with heir in _root's tree
			_root = tree_replace( _root, node, heir );

			// _root should be balanced. Balance, just in case
			return tree_balance( _root );
		}

		// Keep searching down the tree

		if ( node.pos < _root.pos )
			_root.left = tree_delete( _root.left, node );
		else if ( node.pos > _root.pos )
			_root.right = tree_delete( _root.right, node );

		// Balance tree
		return tree_balance( _root );
	}

	// Find an heir for the root node, return that.
	// _root node has 2 children at this point
	// Returns the right-most node in the left subtree i.e. _root.prev
	// or the left-most node in the right subtree i.e. _root.next
	// return node will always have one or no children.
	private static Node find_heir( Node _root )
	{
		if ( _root.prev != null )
			return _root.prev;
		else
			return _root.next;
	}

	// Searches in tree _root for Node old and replaces it with Node heir
	private static Node tree_replace( Node _root, Node old, Node heir )
	{
		// If something is invalid, do nothing
		if ( _root == null || old == null || heir == null )
			return _root;

		// If the old node has been found, copy links from old to heir
		// Then return heir as the new _root
		if ( _root.pos == old.pos )
		{
			heir.left = old.left;
			heir.right = old.right;
			update_height( heir );
			return heir;
		}

		// Not found, search subtrees

		// If old is in the left subtree, search there
		if ( old.pos < _root.pos )
			return tree_replace( _root.left, old, heir );
		else // It's in the right subtree
			return tree_replace( _root.right, old, heir );
	}

	// Search the tree from root to a run using binary search
	// and returning the Node containing the requested pos in its run
	private static Node tree_search( Node _root, int pos )
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

	// Rebalances the tree at _root
	// Returns the new _root
	private static Node tree_balance( Node _root )
	{

		// Nothing to do, return null
		if ( _root == null )
			return _root;

		// _root has no children, is at height 0
		if ( _root.left == null && _root.right == null )
		{
			update_height( _root );
			return _root;
		}

		// Check for a right-heavy tree
		if ( compare_height( _root.right, _root.left ) > 1 )
		{
			// Check for a left-leaning, right subtree.
			// If so, do Right-rotation on right subtree
			if ( compare_height( _root.right.left, _root.right.right ) > 0 )
				_root.right = tree_right_rotate( _root.right );

			// Then do Left-rotation on _root
			//_root = tree_left_rotate( _root );
		}
		// Check for a left-heavy tree
		else if ( compare_height( _root.left, _root.right ) > 1 )
		{
			// Check for a right-leaning, left subtree.
			// If so, do Left-rotation left subtree
			if ( compare_height( _root.left.right, _root.left.left ) > 0 )
				_root.left = tree_left_rotate( _root.left );

			// Then do Right-rotation on _root
			_root = tree_right_rotate( _root );
		}

		update_height( _root );

		return _root;
	}

	// Perform a tree rotation to the left
	// Update heights and return new root
	private static Node tree_left_rotate( Node _root )
	{
		//System.out.println( "Left rotate on " + _root + "\n" + tree_traverse( _root ) );
		Node new_root = _root.right;
		_root.right = new_root.left;
		new_root.left = _root;
		update_height( _root );
		update_height( new_root );
		//System.out.println( "Left rotate end on " + _root + "\n" + tree_traverse( new_root ) );
		return new_root;
	}

	// Perform a tree rotation to the right
	// Update heights and return new root
	private static Node tree_right_rotate( Node _root )
	{
		//System.out.println( "Right rotate on " + _root + "\n" + tree_traverse( _root ) );
		Node new_root = _root.left;
		_root.left = new_root.right;
		new_root.right = _root;
		update_height( _root );
		update_height( new_root );
		//System.out.println( "Right rotate end on " + _root + "\n" + tree_traverse( new_root ) );
		return new_root;
	}

	// Return difference in Node a and Node b's heights
	private static int compare_height( Node a, Node b )
	{
		return get_height( a ) - get_height( b );
	}

	// Return the height of a node or 0
	private static int get_height( Node a )
	{
		return a == null ? 0 : a.height;
	}

	// Set the height of the this node to the height of its highest child + 1
	private static void update_height( Node _root )
	{
		_root.height = 1 + Math.max( get_height( _root.left ), get_height( _root.right ) );
	}

	static String tree_traverse( Node _root )
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
