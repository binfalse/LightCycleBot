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
		
		if (LOGGER.isDebugEnabled ())
		{
			Utils.printMap (voronoi, gm.getWidth());
		}
		
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

		LOGGER.setMinLevel (LOGGER.WARN);
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
  	
		int [] m = gm.floodFill (p.getPosition (), p.getDirection ());
		int max = 0;
		for (int i = 0; i < m.length; i++)
			if (max < m[i] && m[i] != Integer.MAX_VALUE)
				max = m[i];
		
		Utils.printMap (m, gm.getWidth ());
		
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
  	m = gm.floodFill (p.getPosition (), p.getDirection ());
		max = 0;
		for (int i = 0; i < m.length; i++)
			if (max < m[i] && m[i] != Integer.MAX_VALUE)
				max = m[i];
		assertEquals ("floodfill seems to be incorrect", 13, max);
  	

  	int [] m2 = m;
  	m = gm.floodFill (9, null);
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

		LOGGER.setMinLevel (LOGGER.WARN);
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
}
