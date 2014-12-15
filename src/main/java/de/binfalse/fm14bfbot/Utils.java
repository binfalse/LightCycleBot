/**
 * 
 */
package de.binfalse.fm14bfbot;

import de.binfalse.bflog.LOGGER;


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
		int i;
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
	 * Resolves a player's name. First player becomes "A", second becomes "B", and so on. We expect not more than 26 players. ;-)
	 *
	 * @param id the player's id
	 * @return the name
	 */
	public static final String resolvPlayerName (int id)
	{
		return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring (id - 1, id);
	}
	
	/**
	 * Absolute value of an int.
	 *
	 * @param a the int
	 * @return a or -a
	 */
	public static final int abs (int a)
	{
		return a < 0 ? -a : a;
	}
	
	
	/**
	 * Is a move allowed?
	 *
	 * @param pDir the player's direction
	 * @param direction the target direction
	 * @return true, if player can move towards direction
	 */
	public static final boolean allowedMove (int pDir, int direction)
	{
		return abs (direction - pDir) <= 1;
	}
	
	
	/**
	 * Gets the direction. Standing at <code>from</code>, which direction do we need to go to arrive at <code>to</code>?
	 *
	 * @param from the start position
	 * @param to the target position (expected to be adjacent to from)
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
	 * Translates a move to <code>{LEFT, RIGHT, AHEAD}</code>. We assume that target is exactly 1 step away.
	 *
	 * @param p the player
	 * @param target the target position where p wants to end
	 * @return {LEFT, RIGHT, AHEAD}
	 */
	public static final String translateMove (Player p, int target)
	{
		int targetDir = getDirection (p.getPosition(), target);

		int looking = p.getDirection();
		
		if (targetDir == looking)
			return "AHEAD";
		
		if ((4 + targetDir - looking) % 4 == 1)
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
	public static void printMap (int[] m, int width)
	{
		if (!LOGGER.isDebugEnabled ())
			return;
		StringBuffer sb = new StringBuffer ();
		sb.append ("\t");
		for (int i = 0; i < width; i++)
			sb.append ("#").append (i).append ("#\t");
		sb.append ("\n#0#\t");
		for (int i = 0; i < m.length; i++)
		{
			if (m[i] == Integer.MAX_VALUE)
				sb.append ("#\t");
			else
				sb.append (m[i]).append ("\t");
			if ( (i + 1) % width == 0)
				sb.append ("\n#").append ((i+1) / width).append ("#\t");
		}
		LOGGER.debug ("\n" + sb.toString ());
	}
}
