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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;



/**
 * @author Martin Scharm
 * 
 */
public class TestCommunication
{
	
	@Test
	public void positionLineTest ()
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
		
		String[] line = "POS 1 2,5 EAST".split (" ");
		
		Player p = new Player (1);
		
		line = "POS 1 2,5 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		assertEquals ("unexpected player position", 41, p.getPosition ());
		assertEquals ("unexpected player direction", Direction.EAST,
			(int) p.getDirection ());
		
		line = "POS 1 7,5 NORTH".split (" ");
		p.update (gm, line[2], line[3]);
		assertEquals ("unexpected player position", 46, p.getPosition ());
		assertEquals ("unexpected player direction", Direction.NORTH,
			(int) p.getDirection ());
		
		line = "POS 1 8,1 SOUTH".split (" ");
		p.update (gm, line[2], line[3]);
		assertEquals ("unexpected player position", 7, p.getPosition ());
		assertEquals ("unexpected player direction", Direction.SOUTH,
			(int) p.getDirection ());
		
		line = "POS 1 10,9 WEST".split (" ");
		p.update (gm, line[2], line[3]);
		assertEquals ("unexpected player position",
			gm.getWidth () * gm.getHeight () - 1, p.getPosition ());
		assertEquals ("unexpected player direction", Direction.WEST,
			(int) p.getDirection ());
		
	}
	
}
