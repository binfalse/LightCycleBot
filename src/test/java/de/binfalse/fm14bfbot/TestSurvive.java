/**
 * 
 */
package de.binfalse.fm14bfbot;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.binfalse.bflog.LOGGER;
import de.binfalse.fm14bfbot.GameMap.VirtualCompartment;


/**
 * @author Martin Scharm
 *
 */
public class TestSurvive
{
	
	
	@Test
	public void testPathFinderArtefact1 ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
		List<String> map = new ArrayList<String> ();
		map.add ("..........");
		map.add ("..........");
		map.add (".########.");
		map.add ("......#...");
		map.add ("......#...");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		
		GameMap gm = new GameMap (map);
	
	  	Player p = new Player (1);
			String [] line = "POS 1 1,1 W".split (" ");
			p.update (gm, line[2], line[3]);
			
		int [] flood = gm.floodFill (p.getPosition(), p.getDirection ());
		
		List<Integer> articulationPoints = gm.getArticulationPoints (gm.getCompartmentNumber (p.getPosition()));
		
		//System.out.println ("aps: " + articulationPoints);
		
		// ich muss nur einmal flood fillen -> articulation points sind natuerlich die 1 in den neuen compartments..
		
		// create virtual compartments
		List<VirtualCompartment> virtualCompartments = gm.getVirtualCompartments (p.getPosition(), flood, articulationPoints);
		
		// choose a path through compartments
		//List<VirtualCompartment> finalCompartmentPath = 
		gm.chooseBestCompartmentPath (virtualCompartments.get(0), new ArrayList<VirtualCompartment> (), 0);
		
		List<VirtualCompartment> bestVCPath = gm.getBestCompartmentSequence();
		List<Integer> walkPath = new ArrayList<Integer> ();
		
		//System.out.println (bestVCPath.get(0).input + " -> " +  bestVCPath.get(1).input);
		
		walkPath = gm.findGoodPath(flood, bestVCPath.get(0), 9, p.getDirection ());
		//System.out.println(walkPath);
		
		
		if (LOGGER.isDebugEnabled ())
		{
			int [] walking = new int [flood.length];
			//Arrays.fill (walking, 0)
			int n = 0;
			for (int i : walkPath)
				walking[i] = ++n;
			
			Utils.printMap (walking, gm.getWidth());
		}
		
		
		assertEquals ("walk path not optimal", 70, walkPath.size ());
		assertEquals ("walk starts at bad position", 0, (int) walkPath.get (0));
		assertEquals ("walk arrives at bad position", 9, (int) walkPath.get (walkPath.size () - 1));
		
		//gm.findGoodPath(new ArrayList<Integer> (), flood, bestVCPath.get(0), bestVCPath.get(0).outputs.keySet().iterator().next());
	}

	@Test
	public void testWholePathFinder ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
		List<String> map = new ArrayList<String> ();
		map.add ("....#.....");
		map.add ("....#.....");
		map.add ("####..####");
		map.add ("......#..#");
		map.add ("......#..#");
		map.add (".........#");
		map.add ("##########");
		map.add ("......#...");
		map.add ("......#...");
		
		GameMap gm = new GameMap (map);
	
	  	Player p = new Player (1);
			String [] line = "POS 1 1,5 W".split (" ");
			p.update (gm, line[2], line[3]);
		
		List<Integer> path = gm.optimalFill(p);
		
		
		if (LOGGER.isDebugEnabled ())
		{
			LOGGER.debug(path);
			
			int [] flood = gm.floodFill (p.getPosition(), p.getDirection ());

			Utils.printMap (flood, gm.getWidth());
		
			int [] walking = new int [flood.length];
			//Arrays.fill (walking, Integer.MAX_VALUE);
			for (int i = 0; i < walking.length; i++)
				if (flood[i] < Integer.MAX_VALUE)
					walking[i] = 0;
				else
					walking[i] = flood[i];
			
			int n = 0;
			for (int i : path)
				walking[i] = ++n;
			
			
			for (int i = 0; i < walking.length; i++)
				if (flood[i] == Integer.MAX_VALUE)
					walking[i] = -1;
			
			Utils.printMap (walking, gm.getWidth());
		}
		LOGGER.setMinLevel (LOGGER.WARN);
		
		assertEquals ("walk path not optimal", 28, path.size ());
		assertEquals ("walk starts at bad position", 40, (int) path.get (0));
		assertEquals ("walk arrives at bad position", 9, (int) path.get (path.size () - 1));
	}


	@Test
	public void testPathFinder ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
		List<String> map = new ArrayList<String> ();
		map.add ("....#.....");
		map.add ("....#.....");
		map.add ("####..####");
		map.add ("......#..#");
		map.add ("......#..#");
		map.add (".........#");
		map.add ("##########");
		map.add ("......#...");
		map.add ("......#...");
		
		GameMap gm = new GameMap (map);
	
	  	Player p = new Player (1);
			String [] line = "POS 1 2,5 W".split (" ");
			p.update (gm, line[2], line[3]);
			
		int [] flood = gm.floodFill (p.getPosition(), p.getDirection ());
		
		List<Integer> articulationPoints = gm.getArticulationPoints (gm.getCompartmentNumber (p.getPosition()));
		
		LOGGER.debug ("aps: ", articulationPoints);
		
		// ich muss nur einmal flood fillen -> articulation points sind natuerlich die 1 in den neuen compartments..
		
		// create virtual compartments
		List<VirtualCompartment> virtualCompartments = gm.getVirtualCompartments (p.getPosition(), flood, articulationPoints);
		
		// choose a path through compartments
		//List<VirtualCompartment> finalCompartmentPath = 
		gm.chooseBestCompartmentPath (virtualCompartments.get(0), new ArrayList<VirtualCompartment> (), 0);
		
		List<VirtualCompartment> bestVCPath = gm.getBestCompartmentSequence();
		List<Integer> walkPath = new ArrayList<Integer> ();
		
		LOGGER.debug (bestVCPath.get(0).input + " -> " +  bestVCPath.get(1).input);
		
		walkPath = gm.findGoodPath(flood, bestVCPath.get(0), bestVCPath.get(1).input, p.getDirection ());
		if (LOGGER.isDebugEnabled ())
		{
			LOGGER.debug (walkPath);
		
			int [] walking = new int [flood.length];
			//Arrays.fill (walking, 0)
			int n = 0;
			for (int i : walkPath)
				walking[i] = ++n;
			
			Utils.printMap (walking, gm.getWidth());
			LOGGER.debug(walkPath);
		}
		LOGGER.setMinLevel (LOGGER.WARN);
		
		assertEquals ("walk path not optimal", 19, walkPath.size ());
		assertEquals ("walk starts at bad position", 41, (int) walkPath.get (0));
		assertEquals ("walk arrives at bad position", 25, (int) walkPath.get (walkPath.size () - 1));

		//gm.findGoodPath(new ArrayList<Integer> (), flood, bestVCPath.get(0), bestVCPath.get(0).outputs.keySet().iterator().next());
	}
}
