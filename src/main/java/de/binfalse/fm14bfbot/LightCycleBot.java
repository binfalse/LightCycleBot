package de.binfalse.fm14bfbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.binfalse.bflog.LOGGER;

/**
 * Hello world!
 *
 */
public class LightCycleBot 
{
    private BufferedReader in;
    private PrintStream out;
    private GameMap map;
    private Player me;
    private Map<String, Player> players;
    
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
    			for (Player p : players.values ())
    				LOGGER.debug ("player: ", p.getId (), p == me ? " (me)" : "", " -- ", p.getPosition (), " -- ", p.getDirection ());
    			
    			// do something
    			
    			map.updatePlayers (players);
    			
    			LOGGER.debug ("\n", Utils.printMap (map.getCompartmentMap (), map.getWidth (), new StringBuffer (), players.values ()));
    			
    			// is this fight or endgame?
    			List<Player> enemies = new ArrayList<Player> ();
    			for (Player p : players.values ())
    			{
    				if (p == me)
    					continue;
    				if (me.sameCompartment (map, p))
    				{
    					// ok, let's kick him out of our compartment.
    					enemies.add(p);
    				}
    			}
    			
    			if (enemies.isEmpty())
    				//	ok, no enemy. task is to survive as long as possible
    				survive ();
    			else
    				fight (enemies);

    			LOGGER.debug ("-------------------------------------------------");
    		}
    		
    		
    		
    		else
    			throw new RuntimeException ("do not understand " + line);
    	}
    }
    
    private void fight (List<Player> enemies)
    {
    	// there is some enemy in our territory. get rid of him.
    	LOGGER.debug ("need to fight");
    	if (LOGGER.isDebugEnabled ())
    		LOGGER.debug (Arrays.toString (map.getMap ()), enemies);
    	sayServer (map.fight (enemies));
    }
    
    private void survive ()
    {
    	// try to be as efficient as possible in filling our compartment.
    	// and let's hope the enemies are sillier than our hack
    	LOGGER.debug ("need to survive");
    	if (path == null)
    	{
    		path = map.optimalFill();
    		LOGGER.debug("found optimal path: ", path);
    		pathPosition = 1;
    	}
    	
    	if (pathPosition < path.size())
    		sayServer (Utils.translateMove(me, path.get(pathPosition++)));
    	else
    		sayServer ("AHEAD");
    	
    }
    
    private List<Integer> path;
    private int pathPosition;
    
    
    
    
    
    
    private void parsePositionLine (String line)
    {
    	String [] tokens = line.split (" ");
			Player player = players.get (tokens[1]);
			if (player == null)
			{
				player = new Player (Integer.parseInt (tokens[1]));
  			players.put (tokens[1], player);
			}
    	LOGGER.debug ("updating player ", player.getId ());
			
			player.update (map, tokens[2], tokens[3]);
    	LOGGER.debug ("player: ", player);
    }
    
    public void done () throws IOException
    {
    	in.close ();
    }
    
    
    public void init () throws IOException
    {
    	LOGGER.debug ("starting init");
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
    			map.setMe(me);
    			players.put (myNumber, me);
    			break;
    		}
    	}
    	LOGGER.debug ("finished init, map:");
    	Utils.printMap (map.getMap (), map.getWidth ());
    	map.debugPos ();
    	LOGGER.debug ("me: ", me.getId ());
    }
    
    private void sayServer (String s)
    {
    	LOGGER.debug (" < ", s);
    	out.println (s);
    }
    
    private String readServer () throws IOException
    {
    	String s = in.readLine ();
    	LOGGER.debug (" > ", s);
    	return s;
    }
    
    public static void main (String[] args) throws IOException
    {
    	
    	String process = ManagementFactory.getRuntimeMXBean().getName();
    	File f = new File (("/tmp/lightCycleBot-" + new Date () + process).replaceAll ("\\s", ""));
    	f.createNewFile ();
    	//LOGGER.setLogFile (f);
    	//LOGGER.setLogToFile (true);
    	LOGGER.setLogToStdErr (false);
    	//LOGGER.setMinLevel (LOGGER.DEBUG);
    	
    	
    	LightCycleBot bot = new LightCycleBot (new BufferedReader(new InputStreamReader(System.in)), System.out);
    	bot.win ();
    	bot.done ();
    	
    	
    	LOGGER.closeLogger ();
    }
}
