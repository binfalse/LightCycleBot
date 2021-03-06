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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


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
		//LOGGER.setMinLevel (LOGGER.DEBUG);
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
		
		/*if (LOGGER.isDebugEnabled ())
		{
			Utils.printMap (voronoi, gm.getWidth());
		}*/
		
		int [] counter = new int [3];
		int sum = 0;
		for (int i = 0; i < voronoi.length; i++)
			if (voronoi[i] < Integer.MAX_VALUE)
			{
				counter[voronoi[i] - 1]++;
			}
			else
				sum++;
		assertEquals ("unexpected tessilation", 24, counter[0]);
		assertEquals ("unexpected tessilation", 27, counter[1]);
		assertEquals ("unexpected tessilation", 25, counter[2]);
		
		for (int i = 0; i < counter.length; i++)
			sum += counter[i];
		assertEquals ("tessilation failed. map size?", voronoi.length, sum);

		//LOGGER.setMinLevel (LOGGER.WARN);
	}
	
	@Test
	public void magnetsTest ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
  	List<String> map = new ArrayList<String> ();
  	map.add ("#.....##..");
  	map.add ("......#.##");
  	map.add ("....#.#...");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("..........");
  	map.add ("..#.......");
  	map.add ("..##..#...");
  	map.add (".........#");

  	GameMap gm = new GameMap (map);
  	System.out.println(gm.getMagnets());
  	
	}
	
	@Test
	public void floodfillTest ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
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
  	
		int [] m = gm.floodFill (p.getPosition (), p.getDirection (), -1);
		int max = 0;
		for (int i = 0; i < m.length; i++)
			if (max < m[i] && m[i] != Integer.MAX_VALUE)
				max = m[i];
		
		//Utils.printMap (m, gm.getWidth ());
		
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
  	m = gm.floodFill (p.getPosition (), p.getDirection (), -1);
		max = 0;
		for (int i = 0; i < m.length; i++)
			if (max < m[i] && m[i] != Integer.MAX_VALUE)
				max = m[i];
		assertEquals ("floodfill seems to be incorrect", 13, max);
  	

  	int [] m2 = m;
  	m = gm.floodFill (9, null, -1);
		for (int i = 0; i < m.length; i++)
			if (max < m[i] && m[i] != Integer.MAX_VALUE)
				max = m[i];
		assertEquals ("floodfill seems to be incorrect", 15, max);
		
		
		// check that it's same for both directions
  	assertEquals ("floodfill seems to be incorrect", m2[9], m[p.getPosition ()]);
  	assertEquals ("floodfill seems to be incorrect", 13, m[p.getPosition ()]);
		
		//LOGGER.setMinLevel (LOGGER.WARN);
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
  	
  	//LOGGER.setMinLevel (LOGGER.DEBUG);

  	assertEquals ("unexpected number of articulation points in compartment ", 0, gm.getArticulationPoints (1).size ());
  	assertEquals ("unexpected number of articulation points in compartment ", 5, gm.getArticulationPoints (2).size ());
  	assertEquals ("unexpected number of articulation points in compartment ", 0, gm.getArticulationPoints (6).size ());
  	assertEquals ("unexpected number of articulation points in compartment ", 0, gm.getArticulationPoints (7).size ());
  	

	}
	
	@Test
	public void testNorm ()
	{
		assertEquals ("norm is wrong", 0.5135638, ((double)25) * Utils.dnorm(10, 7), .01);
		assertEquals ("norm is wrong", 0.03456725, Utils.dnorm(7, 7), .01);
		assertEquals ("norm is wrong", 0.01079819, Utils.dnorm(10, 5), .01);
	}
	
	
	@Test
	public void testDist ()
	{
		assertEquals ("dist is wrong", 1, Utils.dist(15, 25, 10));
		assertEquals ("dist is wrong", 2, Utils.dist(15, 26, 10));
		assertEquals ("dist is wrong", 2, Utils.dist(15, 24, 10));
		assertEquals ("dist is wrong", 3, Utils.dist(15, 36, 10));
		assertEquals ("dist is wrong", 8, Utils.dist(19, 11, 10));
		assertEquals ("dist is wrong", 9, Utils.dist(19, 21, 10));
	}
	

	@Test
	public void compartmentsTest2 ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
	  	List<String> map = new ArrayList<String> ();
	  	
	  	map.add("#########################");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#.......................#");
	  	map.add("############.############");
	  	map.add("#.......................#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#########################");

	  	

	  	GameMap gm = new GameMap (map);
	  	//System.out.println (gm.dumpCompartments (new StringBuffer ()));
	  	assertEquals ("unexpected number of compartments", gm.getCompartments ().size (), 2);
	  	
	  	
	  	
		//LOGGER.setMinLevel (LOGGER.WARN);
	  	
	}
	

	@Test
	public void compartmentsTest3 ()
	{
		//LOGGER.setMinLevel (LOGGER.DEBUG);
	  	List<String> map = new ArrayList<String> ();
	  	
	  	map.add("#########################");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#######.....#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#.......................#");
	  	map.add("############.############");
	  	map.add("#.....#.................#");
	  	map.add("#.....#.....#...........#");
	  	map.add("#.....#.....#...........#");
	  	map.add("#.....#.....#...........#");
	  	map.add("#.....#.....#...........#");
	  	map.add("#.....#.....#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#...........#...........#");
	  	map.add("#########################");

	  	

	  	GameMap gm = new GameMap (map);
	  	//System.out.println (gm.dumpCompartments (new StringBuffer ()));
		//LOGGER.setMinLevel (LOGGER.WARN);
	  	assertEquals ("unexpected number of compartments", gm.getCompartments ().size (), 2);
	  	
	  	
	  	
	  	
	}
	
}
