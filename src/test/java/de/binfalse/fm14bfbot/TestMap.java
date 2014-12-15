/**
 * Copyright (c) 2007-2014 Martin Scharm -- <software@binfalse.de>
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted (subject to the limitations in the
 * disclaimer below) provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * 
 * * Neither the name of <Owner Organization> nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 * 
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
 * GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.binfalse.fm14bfbot;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.binfalse.bflog.LOGGER;
import de.binfalse.fm14bfbot.GameMap.VirtualCompartment;


/**
 * Test the LOGGER.
 */
public class TestMap
{
	@Test
	public void testCompartmentSequenceFinding ()
	{
		GameMap.VirtualCompartment vc = new GameMap.VirtualCompartment(0);
		GameMap.VirtualCompartment vc2 = new GameMap.VirtualCompartment(0);
		GameMap.VirtualCompartment vc3 = new GameMap.VirtualCompartment(0);
		GameMap.VirtualCompartment vc4 = new GameMap.VirtualCompartment(0);
		GameMap.VirtualCompartment vc5 = new GameMap.VirtualCompartment(0);
		GameMap.VirtualCompartment vc6 = new GameMap.VirtualCompartment(0);
		GameMap.VirtualCompartment vc7 = new GameMap.VirtualCompartment(0);
		
		vc.outputs.put(0, vc2);
		vc.outputs.put(1, vc3);
		vc.outputs.put(2, vc4);
		
		vc2.outputs.put(3, vc5);
		vc2.outputs.put(4, vc6);
		
		vc4.outputs.put(5, vc7);
		
		class dummy
		{
			public void addNodesToCompartment (int num, GameMap.VirtualCompartment vc)
			{
				for (int i = 0; i < num; i++)
					vc.nodes.add(i);
			}
		}
		
		dummy d = new dummy ();

		d.addNodesToCompartment(15, vc);
		d.addNodesToCompartment(5, vc2);
		d.addNodesToCompartment(7, vc3);
		d.addNodesToCompartment(2, vc4);
		d.addNodesToCompartment(4, vc5);
		d.addNodesToCompartment(6, vc6);
		d.addNodesToCompartment(35, vc7);
		
		

	  	List<String> map = new ArrayList<String> ();
	  	map.add ("#.....#...");

	  	GameMap gm = new GameMap (map);
	  	
	  	List<GameMap.VirtualCompartment> all = new ArrayList<GameMap.VirtualCompartment> ();
	  	all.add(vc);
	  	all.add(vc2);
	  	all.add(vc3);
	  	all.add(vc4);
	  	all.add(vc5);
	  	all.add(vc6);
	  	all.add(vc7);
	  	gm.chooseBestCompartmentPath(all);
	  	List<GameMap.VirtualCompartment> best = gm.getBestCompartmentSequence();
	  	//System.out.println(best.size());

		assertEquals("expected a virtual compartment path of size 3.", 3, best.size());

	  	int sum = 0;
	  	int sum2 = 0;
	  	for (int i = 0 ; i < best.size(); i++)
		  	for (int j = 0 ; j < all.size(); j++)
		  		if (best.get(i) == all.get(j))
		  		{
		  			//System.out.println(i + " -- " + j + " -> " + best.get(i).nodes.size());
		  			sum += best.get(i).nodes.size();
		  			sum2 += j;
		  		}

		assertEquals("wrong virtual compartment path.", 52, sum);
		assertEquals("wrong virtual compartment path.", 9, sum2);
	}
	
	@Test
	public void voronoiTest ()
	{
		LOGGER.setMinLevel (LOGGER.DEBUG);
	  	List<String> map = new ArrayList<String> ();
	  	map.add ("#.....#...");
	  	map.add ("......#.##");
	  	map.add ("....#.#...");
	  	map.add ("....#####.");
	  	map.add ("..........");
	  	map.add ("..........");
	  	map.add ("..........");
	  	map.add ("......#...");
	  	map.add (".........#");
	  	
	  	GameMap gm = new GameMap (map);
	  	
	  	List<Player> enemies = new ArrayList<Player> ();
	  	Player p = new Player (3);
		String [] line = "POS 3 1,2 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		enemies.add(p);
	  	
	  	p = new Player (2);
		line = "POS 2 5,5 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		enemies.add(p);
	  	
	  	p = new Player (1);
		line = "POS 1 9,8 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		
		int [] voronoi = gm.calcVoronoi(enemies, p);
		
		if (LOGGER.isDebugEnabled ())
		{
			
			printMap (voronoi, gm.getWidth());
		}

		LOGGER.setMinLevel (LOGGER.WARN);
	}
	
	@Test
	public void floodfillTest ()
	{
  	List<String> map = new ArrayList<String> ();
  	map.add ("#.....#...");
  	map.add ("......#.##");
  	map.add ("....#.#...");
  	map.add ("....#####.");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("......#...");
  	map.add (".........#");

  	GameMap gm = new GameMap (map);
  	Player p = new Player (1);
		String [] line = "POS 1 2,5 EAST".split (" ");
		p.update (gm, line[2], line[3]);
  	
		int [] m = gm.floodFill (p.getPosition ());
		int max = 0;
		for (int i = 0; i < m.length; i++)
			if (max < m[i] && m[i] != Integer.MAX_VALUE)
				max = m[i];
		assertEquals ("floodfill seems to be incorrect", 17, max);
		
		
		// let's try compartments
		map = new ArrayList<String> ();
  	map.add ("....#.....");
  	map.add ("....#.....");
  	map.add ("####..####");
  	map.add ("......#..#");
  	map.add ("......#..#");
  	map.add (".........#");
  	map.add ("##########");
  	map.add ("......#...");
  	map.add ("......#...");
  	
  	gm = new GameMap (map);
  	m = gm.floodFill (p.getPosition ());
		max = 0;
		for (int i = 0; i < m.length; i++)
			if (max < m[i] && m[i] != Integer.MAX_VALUE)
				max = m[i];
		assertEquals ("floodfill seems to be incorrect", 13, max);
  	

  	int [] m2 = m;
  	m = gm.floodFill (9);
		for (int i = 0; i < m.length; i++)
			if (max < m[i] && m[i] != Integer.MAX_VALUE)
				max = m[i];
		assertEquals ("floodfill seems to be incorrect", 15, max);
		
		
		// check that it's same for both directions
  	assertEquals ("floodfill seems to be incorrect", m2[9], m[p.getPosition ()]);
  	assertEquals ("floodfill seems to be incorrect", 13, m[p.getPosition ()]);
		
  	/*m=m2;
		StringBuffer sb = new StringBuffer ();
		for (int i = 0; i < m.length; i++)
		{
			if (m[i] == Integer.MAX_VALUE)
				sb.append ("#\t");
			else
				sb.append (m[i]).append ("\t");
			if ((i + 1) % gm.getWidth () == 0)
				sb.append ("\n");
		}
		System.out.println (sb.toString ());*/
		
	}
	
	
	@Test
	public void simpleTest ()
	{
  	List<String> map = new ArrayList<String> ();
  	map.add ("#.........");
  	map.add ("..........");
  	map.add ("....#.....");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("......#...");
  	map.add (".........#");
  	
  	
  	GameMap gm = new GameMap (map);
  	
  	int height = gm.getHeight ();
  	int width = gm.getWidth ();

  	assertEquals ("unexpected width", width, map.get (0).length ());
  	assertEquals ("unexpected height", height, map.size ());
  	
  	int sum = 0;
  	for (int i = 0; i < height * width; i++)
  		if (gm.getPos (i) == Integer.MAX_VALUE)
  			sum += i;
  	

  	assertEquals ("unexpected walls", sum, 189);
  	
  	
  	// test translations
  	assertEquals ("unexpected index", 0, gm.getIdx (0, 0));
  	assertEquals ("unexpected index", 1, gm.getIdx (1, 0));
  	assertEquals ("unexpected index", 10, gm.getIdx (0, 1));
  	assertEquals ("unexpected index", 23, gm.getIdx (3, 2));
  	
  	
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
			
			int [] flood = gm.floodFill (p.getPosition());

			printMap (flood, gm.getWidth());
		
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
			
			printMap (walking, gm.getWidth());
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
			
		int [] flood = gm.floodFill (p.getPosition());
		
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
		
		walkPath = gm.findGoodPath(flood, bestVCPath.get(0), bestVCPath.get(1).input);
		if (LOGGER.isDebugEnabled ())
		{
			LOGGER.debug (walkPath);
		
			int [] walking = new int [flood.length];
			//Arrays.fill (walking, 0)
			int n = 0;
			for (int i : walkPath)
				walking[i] = ++n;
			
			printMap (walking, gm.getWidth());
			LOGGER.debug(walkPath);
		}
		
		assertEquals ("walk path not optimal", 19, walkPath.size ());
		assertEquals ("walk starts at bad position", 41, (int) walkPath.get (0));
		assertEquals ("walk arrives at bad position", 25, (int) walkPath.get (walkPath.size () - 1));

		LOGGER.setMinLevel (LOGGER.WARN);
		//gm.findGoodPath(new ArrayList<Integer> (), flood, bestVCPath.get(0), bestVCPath.get(0).outputs.keySet().iterator().next());
	}
	
	
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
			
		int [] flood = gm.floodFill (p.getPosition());
		
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
		
		walkPath = gm.findGoodPath(flood, bestVCPath.get(0), 9);
		//System.out.println(walkPath);
		
		
		if (LOGGER.isDebugEnabled ())
		{
			int [] walking = new int [flood.length];
			//Arrays.fill (walking, 0)
			int n = 0;
			for (int i : walkPath)
				walking[i] = ++n;
			
			printMap (walking, gm.getWidth());
		}
		
		
		assertEquals ("walk path not optimal", 71, walkPath.size ());
		assertEquals ("walk starts at bad position", 0, (int) walkPath.get (0));
		assertEquals ("walk arrives at bad position", 9, (int) walkPath.get (walkPath.size () - 1));
		
		//gm.findGoodPath(new ArrayList<Integer> (), flood, bestVCPath.get(0), bestVCPath.get(0).outputs.keySet().iterator().next());
	}

public static void printMap (int [] m, int width)
{
	if (!LOGGER.isDebugEnabled ())
		return;
StringBuffer sb = new StringBuffer ();

for (int i = 0; i < m.length; i++)
{
	if (m[i] == Integer.MAX_VALUE)
		sb.append ("#\t");
	else
		sb.append (m[i]).append ("\t");
	if ((i + 1) % width == 0)
		sb.append ("\n");
}
LOGGER.debug ("\n" + sb.toString ());
}
	@Test
	public void compartmentsTest ()
	{
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
  	//System.out.println (gm.dumpCompartments (new StringBuffer ()));
  	assertEquals ("unexpected number of compartments", gm.getCompartments ().size (), 5);
  	assertEquals ("unexpected number of elements in compartment 1", gm.getCompartment (1).size (), 8);
  	assertEquals ("unexpected number of elements in compartment 2", gm.getCompartment (2).size (), 37);
  	assertEquals ("unexpected number of elements in compartment 5", gm.getCompartment (6).size (), 12);
  	assertEquals ("unexpected number of elements in compartment 6", gm.getCompartment (7).size (), 6);
  	int sum = 0;
  	for (Integer i : gm.getCompartments ())
  		sum += gm.getCompartment (i).size ();
  	assertEquals ("unexpected number of fields in map", gm.getWidth () * gm.getHeight (), sum);

  	assertTrue ("points should be same compartment", gm.sameCompartment (gm.getIdx (0, 0), gm.getIdx (1, 0)));
  	assertFalse ("points shouldn't be same compartment", gm.sameCompartment (gm.getIdx (0, 0), gm.getIdx (4, 0)));
  	assertFalse ("points shouldn't be same compartment", gm.sameCompartment (gm.getIdx (0, 0), gm.getIdx (5, 0)));
  	assertTrue ("points should be same compartment", gm.sameCompartment (gm.getIdx (5, 0), gm.getIdx (6, 0)));
  	assertTrue ("points should be same compartment", gm.sameCompartment (gm.getIdx (0, 0), gm.getIdx (0, 1)));
  	assertTrue ("points should be same compartment", gm.sameCompartment (gm.getIdx (6, 0), gm.getIdx (8, 5)));
  	assertFalse ("points shouldn't be same compartment", gm.sameCompartment (gm.getIdx (8, 5), gm.getIdx (8, 7)));
  	
  	LOGGER.setMinLevel (LOGGER.DEBUG);

  	assertEquals ("unexpected number of articulation points in compartment ", 0, gm.getArticulationPoints (1).size ());
  	assertEquals ("unexpected number of articulation points in compartment ", 5, gm.getArticulationPoints (2).size ());
  	assertEquals ("unexpected number of articulation points in compartment ", 0, gm.getArticulationPoints (6).size ());
  	assertEquals ("unexpected number of articulation points in compartment ", 0, gm.getArticulationPoints (7).size ());
  	
  	
	}
}
