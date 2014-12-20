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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.binfalse.fm14bfbot.GameMap.FightStrategy;



/**
 * @author Martin Scharm
 * 
 */
public class TestFight
{
	
	@Test
	public void testFight ()
	{
		// LOGGER.setMinLevel (LOGGER.DEBUG);
		List<String> map = new ArrayList<String> ();
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		map.add ("..........");
		
		GameMap gm = new GameMap (map);
		
		List<Player> enemies = new ArrayList<Player> ();
		Player p = new Player (3);
		String[] line = "POS 3 1,2 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		enemies.add (p);
		
		p = new Player (2);
		line = "POS 2 5,5 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		enemies.add (p);
		
		p = new Player (1);
		line = "POS 1 9,8 EAST".split (" ");
		p.update (gm, line[2], line[3]);
		
		gm.setMe (p);
		gm.fight (enemies);
	}
	
	
	@Test
	public void testGoodnessOfFloods ()
	{
		// LOGGER.setMinLevel (LOGGER.DEBUG);
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
		
		FightStrategy me = new FightStrategy (0, new int[] { 1, 2, 3, 4 });
		List<List<FightStrategy>> theirFloods = new ArrayList<List<FightStrategy>> ();
		for (int i = 0; i < 3; i++)
			theirFloods.add (new ArrayList<FightStrategy> ());
		
		// player 1
		theirFloods.get (0).add (new FightStrategy (0, new int[] { 5, 3, 5, 5 }));
		theirFloods.get (0).add (new FightStrategy (0, new int[] { 5, 1, 5, 5 }));
		// player 2
		theirFloods.get (1).add (new FightStrategy (0, new int[] { 5, 5, 5, 5 }));
		theirFloods.get (1).add (new FightStrategy (0, new int[] { 4, 5, 5, 2 }));
		theirFloods.get (1).add (new FightStrategy (0, new int[] { 5, 7, 5, 5 }));
		// player 3
		theirFloods.get (2).add (new FightStrategy (0, new int[] { 5, 5, 2, 5 }));
		
		// LOGGER.debug(gm.goodnessOfFlood(me, theirFloods));
		// LOGGER.setMinLevel (LOGGER.WARN);
		
		// assertEquals ("goodness of floods seems to be wrong", 13,
		// gm.goodnessOfFlood(me, theirFloods), .1);
		assertEquals ("goodness of floods seems to be wrong", 2.1666666,
			gm.goodnessOfFlood (me, theirFloods), .1);
		
		me = new FightStrategy (0, new int[] { 1, 2, 3, 4 });
		theirFloods = new ArrayList<List<FightStrategy>> ();
		for (int i = 0; i < 1; i++)
			theirFloods.add (new ArrayList<FightStrategy> ());
		
		// player 1
		theirFloods.get (0).add (new FightStrategy (0, new int[] { 5, 3, 2, 5 }));
		theirFloods.get (0).add (new FightStrategy (0, new int[] { 5, 1, 5, 4 }));
		
		// LOGGER.debug(gm.goodnessOfFlood(me, theirFloods));
		// LOGGER.setMinLevel (LOGGER.WARN);
		// assertEquals ("goodness of floods seems to be wrong", 5,
		// gm.goodnessOfFlood(me, theirFloods), .1);
		assertEquals ("goodness of floods seems to be wrong", 1.66666666,
			gm.goodnessOfFlood (me, theirFloods), .1);
		
	}
}
