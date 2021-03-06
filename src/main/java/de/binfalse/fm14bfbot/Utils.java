/**
 * Bot for the 7th programming contest of freiesMagazin.
 * Copyright (c) 2014 Martin Scharm -- <software@binfalse.de>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.binfalse.fm14bfbot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



/**
 * @author Martin Scharm
 * 
 */
public class Utils
{
	
	public static final int min (int a, int b)
	{
		return a < b ? a : b;
	}
	
	
	public static final int max (int a, int b)
	{
		return a > b ? a : b;
	}
	
	/**
	 * The Class MyInt representing a virtual pointer to an int.
	 */
	static final class MyInt
	{
		
		int	i;
		
		
		public MyInt ()
		{
			i = 0;
		}
		
		
		public MyInt (int i)
		{
			this.i = i;
		}
		
		
		public void inc ()
		{
			i++;
		}
		
		
		public void add (int j)
		{
			i += j;
		}
		
		
		public int val ()
		{
			return i;
		}
	}
	
	
	/**
	 * Resolves a player's name. First player becomes "A", second becomes "B",
	 * and so on. We expect not more than 26 players. ;-)
	 * 
	 * @param id
	 *          the player's id
	 * @return the name
	 */
	public static final String resolvPlayerName (int id)
	{
		return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring (id - 1, id);
	}
	
	
	/**
	 * Absolute value of an int.
	 * 
	 * @param a
	 *          the int
	 * @return a or -a
	 */
	public static final int abs (int a)
	{
		return a < 0 ? -a : a;
	}
	
	
	/**
	 * Is a move allowed?
	 * 
	 * @param pDir
	 *          the player's direction
	 * @param direction
	 *          the target direction
	 * @return true, if player can move towards direction
	 */
	public static final boolean allowedMove (int pDir, int direction)
	{
		return abs (direction - pDir) <= 1 || (pDir == 3 && direction == 0)
			|| (pDir == 0 && direction == 3);
	}
	
	
	/**
	 * Gets the direction. Standing at <code>from</code>, which direction do we
	 * need to go to arrive at <code>to</code>?
	 * 
	 * @param from
	 *          the start position
	 * @param to
	 *          the target position (expected to be adjacent to from)
	 * @return the direction
	 */
	public static final int getDirection (int from, int to)
	{
		if (from + 1 == to)
			return Direction.EAST;
		else if (from - 1 == to)
			return Direction.WEST;
		else if (from < to)
			return Direction.SOUTH;
		else
			return Direction.NORTH;
	}
	
	
	/**
	 * Translates a move to <code>{LEFT, RIGHT, AHEAD}</code>. We assume that
	 * target is exactly 1 step away.
	 * 
	 * @param p
	 *          the player
	 * @param target
	 *          the target position where p wants to end
	 * @return {LEFT, RIGHT, AHEAD}
	 */
	public static final String translateMove (Player p, int target)
	{
		int targetDir = getDirection (p.getPosition (), target);
		
		int looking = p.getDirection ();
		
		if (targetDir == looking)
			return "AHEAD";
		
		if ( (4 + targetDir - looking) % 4 == 1)
			return "RIGHT";
		return "LEFT";
	}
	
	
	/**
	 * Prints a map, for debugging.
	 * 
	 * @param m
	 *          the map
	 * @param width
	 *          the width
	 */
	/*
	 * public static void printMap (int[] m, int width) { if
	 * (!LOGGER.isDebugEnabled ()) return; StringBuffer sb = new StringBuffer
	 * (); sb.append ("\t"); for (int i = 0; i < width; i++) sb.append
	 * ("#").append (i).append ("#\t"); sb.append ("\n#0#\t"); for (int i = 0; i
	 * < m.length; i++) { if (m[i] == Integer.MAX_VALUE) sb.append ("#\t"); else
	 * sb.append (m[i]).append ("\t"); if ( (i + 1) % width == 0) sb.append
	 * ("\n#").append ((i+1) / width).append ("#\t"); } LOGGER.debug ("\n" +
	 * sb.toString ()); }
	 */
	
	public static final void mapReplace (Map<Integer, Integer> map, int from,
		Integer to)
	{
		Integer otherTo = map.get (from);
		if (otherTo == null)
			map.put (from, to);
		else
		{
			if (to.equals (otherTo))
				return;
			map.put (from, max (to, otherTo));
			mapReplace (map, max (to, otherTo), min (to, otherTo));
		}
	}
	
	
	public static StringBuffer printMap (int[] m, int width, StringBuffer sb,
		Collection<Player> players)
	{
		Map<Integer, String> pos = new HashMap<Integer, String> ();
		
		for (Player p : players)
			pos.put (p.getPosition (), Utils.resolvPlayerName (p.getId ()));
		
		for (int i = 0; i < m.length; i++)
		{
			if (pos.get (i) != null)
				sb.append (pos.get (i));
			else if (m[i] == Integer.MAX_VALUE)
				sb.append ("#");
			else
				sb.append (m[i]);
			if ( (i + 1) % width == 0)
				sb.append ("\n");
		}
		return sb;
	}
	
	
	public static final double dnorm (double x, double sig)
	{
		return 1. / (sig * Math.sqrt (2. * Math.PI))
			* Math.exp (- (x * x) / (2. * sig * sig));
	}
	
	
	public static int dist (int i, int j, int width)
	{
		int dX = abs (i % width - j % width);
		int dY = abs (i / width - j / width);
		return dX + dY;
		
	}


	public static boolean neighbors (int start, int end, int width)
	{
		return (start == end + 1 && start % width != 0) || (start == end - 1 && end % width != 0) || start == end - width || start == end + width;
	}
	
}
