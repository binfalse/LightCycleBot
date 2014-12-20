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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @author Martin Scharm
 * 
 */
public class LightCycleBot
{
	
	private BufferedReader			in;
	private PrintStream					out;
	private GameMap							map;
	private Player							me;
	private Map<String, Player>	players;
	
	
	public LightCycleBot (BufferedReader in, PrintStream out)
	{
		this.in = in;
		this.out = out;
		players = new HashMap<String, Player> ();
	}
	
	
	public void win () throws IOException
	{
		init ();
		
		while (true)
		{
			String line = readServer ();
			
			if (line.startsWith ("POS"))
				parsePositionLine (line);
			
			else if (line.startsWith ("OUT"))
				players.get (line.substring (4)).setOut ();
			
			else if (line.startsWith ("END"))
				return;
			
			else if (line.startsWith ("ROUND"))
			{
				
				// do something
				
				map.updatePlayers (players);
				
				// is this fight or endgame?
				List<Player> enemies = new ArrayList<Player> ();
				for (Player p : players.values ())
				{
					if (p == me)
						continue;
					if (me.sameCompartment (map, p))
					{
						// ok, let's kick him out of our compartment.
						enemies.add (p);
					}
				}
				
				if (enemies.isEmpty ())
					// ok, no enemy. task is to survive as long as possible
					survive ();
				else
					fight (enemies);
			}
			
			else
				throw new RuntimeException ("do not understand " + line);
		}
	}
	
	
	private void fight (List<Player> enemies)
	{
		// there is some enemy in our territory. get rid of him.
		sayServer (map.fight (enemies));
	}
	
	
	private void survive ()
	{
		// try to be as efficient as possible in filling our compartment.
		// and let's hope the enemies are sillier than our hack
		if (path == null)
		{
			path = map.optimalFill ();
			pathPosition = 1;
		}
		
		if (pathPosition < path.size ())
			sayServer (Utils.translateMove (me, path.get (pathPosition++)));
		else
			sayServer ("AHEAD");
		
	}
	
	private List<Integer>	path;
	private int						pathPosition;
	
	
	private void parsePositionLine (String line)
	{
		String[] tokens = line.split (" ");
		Player player = players.get (tokens[1]);
		if (player == null)
		{
			player = new Player (Integer.parseInt (tokens[1]));
			players.put (tokens[1], player);
		}
		
		player.update (map, tokens[2], tokens[3]);
	}
	
	
	public void done () throws IOException
	{
		in.close ();
	}
	
	
	public void init () throws IOException
	{
		List<String> mapDescr = new ArrayList<String> ();
		boolean mapStart = false;
		while (true)
		{
			String line = readServer ();
			if (line.startsWith ("GAMEBOARDSTART"))
			{
				mapStart = true;
				continue;
			}
			if (line.startsWith ("GAMEBOARDEND"))
			{
				mapStart = false;
				map = new GameMap (mapDescr);
				continue;
			}
			if (mapStart)
			{
				mapDescr.add (line);
			}
			
			if (line.startsWith ("SET"))
			{
				String myNumber = line.substring (4);
				me = new Player (Integer.parseInt (myNumber));
				map.setMe (me);
				players.put (myNumber, me);
				break;
			}
		}
		// Utils.printMap (map.getMap (), map.getWidth ());
		// map.debugPos ();
	}
	
	
	private void sayServer (String s)
	{
		out.println (s);
	}
	
	
	private String readServer () throws IOException
	{
		String s = in.readLine ();
		return s;
	}
	
	
	public static void main (String[] args) throws IOException
	{
		LightCycleBot bot = new LightCycleBot (new BufferedReader (
			new InputStreamReader (System.in)), System.out);
		bot.win ();
		bot.done ();
	}
}
