/**
 * 
 */
package de.binfalse.fm14bfbot;


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
	 * @param p the player
	 * @param direction the target direction
	 * @return true, if player can move towards direction
	 */
	public static final boolean allowedMove (Player p, int direction)
	{
		return abs (direction - p.getDirection ()) <= 1;
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
		int pPos = p.getPosition();
		int targetDir;
		
		if (pPos + 1 == target)
			targetDir = Direction.WEST;
		else if (pPos - 1 == target)
			targetDir = Direction.EAST;
		else if (pPos < target)
			targetDir = Direction.SOUTH;
		else
			targetDir = Direction.NORTH;

		int looking = p.getDirection();
		
		if (targetDir == looking)
			return "AHEAD";
		
		if ((4 + targetDir - looking) % 4 == 1)
			return "RIGHT";
		return "LEFT";
	}
}
