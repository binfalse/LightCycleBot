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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.binfalse.fm14bfbot.Utils.MyInt;



/**
 * @author Martin Scharm
 * 
 */
public class GameMap
{
	
	private int													width;
	private int[]												map;
	private int[]												compartments;
	private Map<Integer, List<Integer>>	compartmentMapper;
	
	private List<VirtualCompartment>	bestCompartmentList;
	private int												bestScore;
	private Player me;
	private int myPos;
	private int maxApIt;
	private List<Integer> magnets;
	
	/* private int[] voronoi; */
	
	public GameMap (List<String> mapDescr)
	{
		width = mapDescr.get (0).length ();
		map = new int[width * mapDescr.size ()];
		for (int row = 0; row < mapDescr.size (); row++)
		{
			char[] line = mapDescr.get (row).toCharArray ();
			for (int col = 0; col < line.length; col++)
			{
				if (line[col] == '#')
					map[row * width + col] = Integer.MAX_VALUE;
			}
		}
		magnets = new ArrayList<Integer> ();
		updateCompartments ();
		maxApIt = Utils.min (map.length, 500); 
	}
	
	public void setMe (Player me)
	{
		this.me = me;
	}
	
	public List<Integer> getMagnets ()
	{
		return magnets;
	}
	
	private void updateCompartments ()
	{
		magnets.clear();
		compartments = new int[map.length];
		int cmp = 0;
		if (map[0] != 0)
			compartments[0] = Integer.MAX_VALUE;
		else
			compartments[0] = ++cmp;
		
		Map<Integer, Integer> mapper = new HashMap<Integer, Integer> ();
		
		for (int i = 1; i < map.length; i++)
		{
			// calc magnets
			boolean [] mapRestrictions = 
				{
					// left
					i % width != 0,
					// top
					i > width,
					// right
					(i + 1) % width != 0 && i < map.length - 1,
					// bottom
					i < map.length - 1 - width
				};
			/*if (walls[0] && walls[1] && walls[2] && walls[3])
				magnets.add(i);
			else
			{
				int 
			}*/
			
			// top left
			if (mapRestrictions[0] && mapRestrictions[1]
					&& map[i - 1] != 0 && map[i - width] != 0 && map[i - width - 1] != 0)
				magnets.add(i);
			// top right
			else if (mapRestrictions[2] && mapRestrictions[1]
					&& map[i + 1] != 0 && map[i - width] != 0 && map[i - width + 1] != 0)
				magnets.add(i);
			// bottom right
			else if (mapRestrictions[2] && mapRestrictions[3]
					&& map[i + 1] != 0 && map[i + width] != 0 && map[i + width + 1] != 0)
				magnets.add(i);
			// bottom right
			else if (mapRestrictions[0] && mapRestrictions[3]
					&& map[i - 1] != 0 && map[i + width] != 0 && map[i + width - 1] != 0)
				magnets.add(i);
			
			
			
			// are we a wall?
			if (map[i] != 0)
			{
				compartments[i] = Integer.MAX_VALUE;
				continue;
			}
			// check left
			int left = (i % width == 0) ? Integer.MAX_VALUE : compartments[i - 1];
			// check top
			int top = i < width ? Integer.MAX_VALUE : compartments[i - width];
			
			if (left == Integer.MAX_VALUE && top == Integer.MAX_VALUE)
				compartments[i] = ++cmp;
			else if (left == top)
				compartments[i] = left;
			else if (left == Integer.MAX_VALUE)
				compartments[i] = top;
			else if (top == Integer.MAX_VALUE)
				compartments[i] = left;
			else
			{
				// different
				int togo = Utils.min (top, left);
				int toreplace = Utils.max (top, left);
				
				compartments[i] = togo;
				//mapper.put (toreplace, togo);
				Utils.mapReplace(mapper, toreplace, togo);
				// System.out.println (toreplace + " -> " + togo);
			}
		}
		//Utils.printMap(compartments, width);
		//LOGGER.debug("need to replace compartments: ", mapper);
		
		// another run to have it consistent and to fill the compartment mapper
		compartmentMapper = new HashMap<Integer, List<Integer>> ();
		for (int i = 0; i < map.length; i++)
		{
			Integer cur = compartments[i];
			Integer target = mapper.get (cur);
			while (target != null && !cur.equals (target))
			{
				cur = target;
				target = mapper.get (cur);
			}
			//LOGGER.debug("repplacing ", compartments[i], " width ", cur, " - target is ", target, " at ", i);
			compartments[i] = cur;
			List<Integer> cmpEl = compartmentMapper.get (cur);
			if (cmpEl == null)
			{
				cmpEl = new ArrayList<Integer> ();
				compartmentMapper.put (cur, cmpEl);
			}
			cmpEl.add (i);
		}
	}
	
	
	public void updatePlayers (Map<String, Player> players)
	{
		updateCompartments ();
		for (Player p : players.values ())
		{
			map[p.getPosition ()] = p.getId ();
			//compartments[p.getPosition ()] = Integer.MAX_VALUE;
			
			// TODO: doing this every round is stupid.
			
			// since the maps are quite small and we have plenty of time we could
			// do the following at every point in time. but let's at least try to
			// do something intelligent ;-)
			
			// TODO: is there a potential new articulation point?
			// -> recalc aps for this compartment
			// -> cache aps
			
			// TODO: did one of them enter an ap?
			// -> recalc the compartments and of course recalc aps
		}
		myPos = me.getPosition();
	}
	
	
	public int [] getCompartmentMap ()
	{
		return compartments;
	}
	
	
	public Set<Integer> getCompartments ()
	{
		return compartmentMapper.keySet ();
	}
	
	
	public int getCompartmentNumber (int field)
	{
		return compartments[field];
	}
	
	
	public List<Integer> getCompartment (int cmp)
	{
		return compartmentMapper.get (cmp);
	}
	
	
	public List<Integer> getUnusableFields ()
	{
		return compartmentMapper.get (Integer.MAX_VALUE);
	}
	
	
	public int getWidth ()
	{
		return width;
	}
	
	
	public int getHeight ()
	{
		return map.length / width;
	}
	
	
	public int getPos (int index)
	{
		return map[index];
	}
	
	
	public int getPos (int x, int y)
	{
		return map[x + width * y];
	}
	
	
	public boolean sameCompartment (int idxA, int idxB)
	{
		return compartments[idxA] == compartments[idxB];
	}
	
	public boolean freeField (int idx)
	{
		return map[idx] == 0;
	}
	
	
	public int getIdx (int x, int y)
	{
		return x + width * y;
	}
	
	public double goodnessOfFlood (FightStrategy myFlood, List<List<FightStrategy>> theirFloods)
	{
		// this assumes the enemies do smth random.
		// in that case it increases my chances to win
		// but i guess they won't do stupid stuff..
		// however, it's the best i was just able to hack now..
		
		int o = 1;
		for (List<FightStrategy> enemy : theirFloods)
			o *= enemy.size();
		int [][] options = new int [o][myFlood.flood.length];
		int [][] mins = new int [o][myFlood.flood.length];
		for (int i = 0; i < mins.length; i++)
			for (int j = 0; j < mins[i].length; j++)
				mins[i][j] = Integer.MAX_VALUE;
		
		for (int enemy = 0; enemy < theirFloods.size(); enemy++)
		{
			if (theirFloods.get(enemy).size () == 0)
				continue;
			
			int split = mins.length / theirFloods.get(enemy).size();
			//LOGGER.debug("split ", split);
			for (int enemyOption = 0; enemyOption < theirFloods.get(enemy).size(); enemyOption++)
			{
				int [] eO = theirFloods.get(enemy).get(enemyOption).flood;
				for (int i = 0; i < split; i++)
				{
					//
					int field = i + split * enemyOption;
					//LOGGER.debug("field ", field);
					//int [] cur = options[field];
					for (int j = 0; j < myFlood.flood.length; j++)
					{
						if (eO[j] < mins[field][j])
						{
							options[field][j] = enemy;
							mins[field][j] = eO[j];
						}
					}
				}
			}
		}
		int myFields = 0;
		
		Map<Integer, MyInt> theirs = new HashMap<Integer, MyInt> ();
		for (int i = 0; i < mins.length; i++)
			for (int j = 0; j < mins[i].length; j++)
				if (myFlood.flood[j] < mins[i][j])
					myFields++;
				else if (mins[i][j] < Integer.MAX_VALUE)
				{
					MyInt t = theirs.get(options[i][j]);
					if (t == null)
					{
						t = new MyInt ();
						theirs.put(options[i][j], t);
					}
					t.inc();
				}
		
		int best = 1;
		for (MyInt i : theirs.values())
			if (i.val() > best)
				best = i.val ();
		
		return ((double) myFields) / (double) best;
	}
	
	static class FightStrategy
	{
		int start;
		double score;
		int [] flood;
		
		public FightStrategy (int start, int [] flood)
		{
			this.start = start;
			this.score = 1;
			this.flood = flood;
		}
	}
	
	public int[] fightStrategy (List<Player> enemies, Player me)
	{
		List<FightStrategy> myFloods = calcPossibleFloods (me);
		List<List<FightStrategy>> theirFloods = new ArrayList<List<FightStrategy>> ();
		for (Player enemy : enemies)
			theirFloods.add(calcPossibleFloods (enemy));
		
		double best = -1;
		List<FightStrategy> take = new ArrayList<FightStrategy> ();
		
		for (FightStrategy myFlood : myFloods)
		{
			double cur = goodnessOfFlood (myFlood, theirFloods);
			if (cur == best)
				take.add(myFlood);
			if (cur > best)
			{
				best = cur;
				take.clear();
				take.add(myFlood);
			}
		}

		// from this we can take the field with a 1
		return takeBestTake (take);
	}
	
	public int [] takeBestTake (List<FightStrategy> takes)
	{
		if (takes.size() == 0)
			return null;
		
		// let's go along the wall
		int best = 0;
		double bestScore = -1;
		

		for (int j = 0; j < takes.size(); j++)
		{
			FightStrategy strategy = takes.get(j);
			int start = strategy.start;
			for (int i : magnets)
				strategy.score *= Utils.dnorm(Utils.dist (start, i, width), 7.) * 17.;
			strategy.score /= (double) getAdjacentAvailable (strategy.start).size();
			if (strategy.score > bestScore)
			{
				bestScore = strategy.score;
				best = j;
			}
		}
		return takes.get(best).flood;
	}
	
	public String fight (List<Player> enemies)
	{
		String out = "AHEAD";
		int [] strategy = fightStrategy (enemies, me);
		if (strategy != null)
		{
			for (int i = 0; i < strategy.length; i++)
				if (strategy[i] == 1)
					out = Utils.translateMove (me, i);
		}
					
		return out;
	}

	
	public List<FightStrategy> calcPossibleFloods (Player p)
	{
		List<FightStrategy> floods = new ArrayList<FightStrategy> ();
		List<Integer> adj = getAdjacentAvailable(p.getPosition(), p.getDirection());
		
		for (int i : adj)
		{
			floods.add(new FightStrategy(i, floodFill (i, Utils.getDirection(p.getPosition (), i), p.getPosition())));
		}
		return floods;
	}
	
	public int[] calcVoronoi (List<Player> enemies, Player me)
	{
		// TODO: avoid actual crashes -> enemy wants to get same field as our bot..
		// TODO: test all available moves of our bot and enemies
		// and count the number of fields we would get
		
		// my flood:
		int[] flood = floodFill (me.getPosition (), me.getDirection (), -1);
		
		// their floods
		List<int[]> floods = new ArrayList<int[]> ();
		for (Player enemy : enemies)
			floods.add (floodFill (enemy.getPosition (), enemy.getDirection (), -1));
		
		// voronoi
		int[] voronoi = new int[flood.length];
		for (int i = 0; i < voronoi.length; i++)
		{
			int min = flood[i];
			if (map[i] == 0)
				voronoi[i] = me.getId ();
			else
				voronoi[i] = Integer.MAX_VALUE;
			
			for (int j = 0; j < enemies.size (); j++)
			{
				if (floods.get (j)[i] < min)
				{
					voronoi[i] = enemies.get (j).getId ();
					min = floods.get (j)[i];
				}
			}
		}
		
		return voronoi;
	}
	
	
	public List<VirtualCompartment> getVirtualCompartments (int start,
		int[] floodFilled, List<Integer> aps)
	{
		boolean[] ap = new boolean[floodFilled.length];
		boolean[] done = new boolean[floodFilled.length];
		for (Integer i : aps)
			ap[i] = true;
		
		List<VirtualCompartment> virtualCompartments = new ArrayList<VirtualCompartment> ();
		VirtualCompartment vc = new VirtualCompartment (start);
		virtualCompartments.add (vc);
		int curVc = 0;
		List<Integer> toVisit = new ArrayList<Integer> ();
		toVisit.add (start);
		done[start] = true;
		
		while (!toVisit.isEmpty () || curVc < virtualCompartments.size () - 1)
		{
			if (toVisit.isEmpty ())
			{
				curVc++;
				vc = virtualCompartments.get (curVc);
				if (vc == null)
					break;
				toVisit.add (vc.input);
			}
			else
			{
				int here = toVisit.remove (0);
				List<Integer> neighbors = getAdjacentAvailable (here);
				for (int n : neighbors)
				{
					if (!done[n])
					{
						done[n] = true;
						vc.nodes.add (n);
						if (ap[n])
						{
							// this is end and a start for a new vc
							VirtualCompartment vcn = new VirtualCompartment (n);
							virtualCompartments.add (vcn);
							vc.outputs.put (n, vcn);
						}
						else
						{
							toVisit.add (n);
						}
					}
				}
			}
		}
		
		return virtualCompartments;
	}
	
	public static class VirtualCompartment
	{
		
		List<Integer>											nodes;
		int																input;
		Map<Integer, VirtualCompartment>	outputs;
		
		
		public VirtualCompartment (int start)
		{
			input = start;
			outputs = new HashMap<Integer, VirtualCompartment> ();
			nodes = new ArrayList<Integer> ();
		}
	}
	
	
	public void chooseBestCompartmentPath (List<VirtualCompartment> compartments)
	{
		bestCompartmentList = new ArrayList<VirtualCompartment> ();
		List<VirtualCompartment> cur = new ArrayList<VirtualCompartment> ();
		bestScore = 0;
		chooseBestCompartmentPath (compartments.get (0), cur, 0);
	}
	
	
	public List<VirtualCompartment> getBestCompartmentSequence ()
	{
		return bestCompartmentList;
	}
	
	
	public void chooseBestCompartmentPath (VirtualCompartment vc,
		List<VirtualCompartment> cur, int curScore)
	{
		if (bestCompartmentList == null)
		{
			bestCompartmentList = new ArrayList<VirtualCompartment> ();
			bestScore = 0;
		}
		cur.add (vc);
		curScore += vc.nodes.size ();
		for (VirtualCompartment c : vc.outputs.values ())
		{
			chooseBestCompartmentPath (c, cur, curScore);
			
		}
		if (vc.outputs.size () == 0)
		{
			// this is leaf... score?
			if (curScore > bestScore)
			{
				bestScore = curScore;
				bestCompartmentList.clear ();
				for (VirtualCompartment c : cur)
					bestCompartmentList.add (c);
			}
		}
		curScore -= vc.nodes.size ();
		cur.remove (cur.size () - 1);
	}
	
	
	public List<Integer> optimalFill ()
	{
		// List<Integer> toGo = new ArrayList<Integer> ();
		
		int[] flood = floodFill (me.getPosition (), me.getDirection (), -1);
		
		List<Integer> articulationPoints = getArticulationPoints (getCompartmentNumber (me
			.getPosition ()));
		
		// ich muss nur einmal flood fillen -> articulation points sind natuerlich
		// die 1 in den neuen compartments..
		
		// create virtual compartments
		List<VirtualCompartment> virtualCompartments = getVirtualCompartments (
			me.getPosition (), flood, articulationPoints);
		
		
		// choose a path through compartments
		// List<VirtualCompartment> finalCompartmentPath =
		bestCompartmentList = new ArrayList<VirtualCompartment> ();
		bestScore = 0;
		chooseBestCompartmentPath (virtualCompartments.get (0),
			new ArrayList<VirtualCompartment> (), 0);
		
		List<Integer> walkPath = new ArrayList<Integer> ();
		
		// foreach virtual compartment do
		
		
		for (int i = 0; i < bestCompartmentList.size (); i++)
		{
			walkPath
				.addAll (findGoodPath (
					flood,
					bestCompartmentList.get (i),
					i < bestCompartmentList.size () - 1 ? bestCompartmentList.get (i + 1).input
						: -1, me.getDirection ()));
		}
		
		
		return dropDouble (walkPath);
	}
	
	public List<Integer> dropDouble (List<Integer> list)
	{
		List<Integer> toGo = new ArrayList<Integer> ();
		for (int i : list)
			if (toGo.isEmpty () || !toGo.get (toGo.size () - 1).equals (i))
				toGo.add (i);
		return toGo;
	}
	
	
	public List<Integer> findGoodPath (int[] flood, VirtualCompartment vc, int end, int dir)
	{
		int start = vc.input;
		if (end < 0)
		{
			// we need to decide where to go to!? maybe just search for the field with
			// the biggest flood value!?
			// throw new RuntimeException("not yet implemented");
			int highest = 0;
			for (int i : vc.nodes)
				if (flood[i] > highest)
				{
					end = i;
					highest = flood[i];
				}
		}
		List<Integer> walkPath = new ArrayList<Integer> ();
		boolean[] visited = new boolean[flood.length];
		//Utils.printMap(flood, width);
		walkPath.addAll (shortestPath (flood, visited, start, end));
		
		extendPath (walkPath, visited, vc, dir);
		
		if ( ((double) (walkPath.size ())) / (double) vc.nodes.size () < .8)
		{
			// let's try a different approach
			// there is a big region we were not able to reach.
			// add another waypoint into unvisited..
			
			int sum = 0, num = 0;
			
			for (int i : vc.nodes)
				if (!visited[i])
				{
					sum += i;
					num++;
				}
			
			sum = sum / num;
			while (sum < 1 || visited[sum] || !vc.nodes.contains (sum))
			{
				sum++;
				if (sum > visited.length)
					while (sum < 1 || visited[sum] || !vc.nodes.contains (sum))
						sum--;
			}
			
			// sum is our waypoint
			
			List<Integer> walkPath2 = new ArrayList<Integer> ();
			visited = new boolean[flood.length];
			// shortest path start-sum
			walkPath2.addAll (shortestPath (flood, visited, start, sum));
			
			// we need to temporarily change the compartments map
			// and make set the first part of the short path to unavailable
			// otherwise the second part of our shortest path will probably
			// cross it.
			int [] tmp = compartments;
			compartments = new int [tmp.length];
			for (int i = 0; i < tmp.length; i++)
				compartments[i] = tmp[i];
			for (int i : walkPath2)
				if (i != sum) compartments[i] = Integer.MAX_VALUE;
			//Utils.printMap(compartments, width);
			
			// shortest path sum-end
			int[] flood2 = floodFill (sum, null, -1);
			compartments = tmp;
			
			visited[sum] = false;
			walkPath2.addAll (shortestPath (flood2, visited, sum, end));

			
			extendPath (walkPath2, visited, vc, dir);
			if (walkPath.size () < walkPath2.size ())
				walkPath = walkPath2;
		}

		return dropDouble (walkPath);
		}
	
	
	public List<Integer> shortestPath (int[] floodFromStart, boolean[] visited,
		int start, int end)
	{
		List<Integer> walkPath = new ArrayList<Integer> ();
		
		walkPath.add (end);
		int insertAt = walkPath.size () - 1;
		// walkPath.add(end);
		int cur = end;
		visited[end] = true;
		int it = 0;
		while (cur != start)
		{
			if (it++ > 1000)
				break;
			List<Integer> neighbors = getAdjacentAvailable (cur);
			for (int i : neighbors)
			{
				if (!visited[i] && floodFromStart[i] < floodFromStart[cur])
				{
					cur = i;
					visited[i] = true;
					walkPath.add (insertAt, i);
					break;
				}
			}
		}
		
		return walkPath;
	}
	
	
	public void extendPath (List<Integer> walkPath, boolean[] visited,
		VirtualCompartment vc, int dir)
	{
		
		// ok, now we have the shortest path ;-)
		// let's extend it
		
		// transform the following patterns:
		
		// 2x2
		// XO -> XX
		// XO -> XX
		
		// OO -> XX
		// XX -> XX
		
		// OX -> XX
		// OX -> XX
		
		// XX -> XX
		// OO -> XX
		
		// for every two seq try to go through their side
		// while (((double) (walkPath - insertAt)) / (double) vc.nodes.size() < .8)
		// 80% would be ok for me.
		boolean didsmth = true, didnow = false;
		int its = 0;
		while (didsmth && its < 10)
		{
			didsmth = false;
			its++;
			int c = 0;
			while (c < walkPath.size () - 1)
			{
				int p1 = walkPath.get (c);
				int p2 = walkPath.get (c + 1);
				didnow = false;
				if (p1 + 1 == p2 || p1 - 1 == p2)
				{
					// p1 left or right of p2
					if ((c != 0 || Utils.allowedMove (dir, Utils.getDirection (p1, p1 - width))) && tryExtend (walkPath, c, visited, p1, p2, p1 - width, p2 - width))
					{
						// extended above
						didsmth = true;
						didnow = true;
					}
					else if ((c != 0 || Utils.allowedMove (dir, Utils.getDirection (p1, p1 + width))) && tryExtend (walkPath, c, visited, p1, p2, p1 + width, p2 + width))
					{
						// extended below
						didsmth = true;
						didnow = true;
					}
				}
				else
				{
					// p1 above or below p2
					if (p1 % width != 0 && (c != 0 || Utils.allowedMove (dir, Utils.getDirection (p1, p1 - 1))) && tryExtend (walkPath, c, visited, p1, p2, p1 - 1, p2 - 1))
					{
						// extended left
						didsmth = true;
						didnow = true;
					}
					else if ((p1 + 1) % width != 0 && (c != 0 || Utils.allowedMove (dir, Utils.getDirection (p1, p1 + 1))) && tryExtend (walkPath, c, visited, p1, p2, p1 + 1, p2 + 1))
					{
						// extended right
						didsmth = true;
						didnow = true;
					}
				}
				if (!didnow)
					c++;
			}
		}
		
		/*
		 * --------XXX
		 * --------XXX
		 * XXXXXXXXXXX
		 * -----------
		 */
		
		/*
		 * that artifact would kill us
		 * 9----
		 * 87---
		 * -65--
		 * --43-
		 * ---21
		 * 
		 * so maybe search for
		 * 
		 * X-
		 * XX
		 * 
		 * and replace it with
		 * 
		 * XX
		 * -X
		 * 
		 * ? and retry the first thing
		 */
		
		// return walkPath;
	}
	
	
	private boolean tryExtend (List<Integer> walkPath, int c, boolean[] visited,
		int p1, int p2, int p1ext, int p2ext)
	{
		if (p1ext >= 0 && p2ext >= 0 && p1ext < visited.length
			&& p2ext < visited.length && !visited[p1ext] && !visited[p2ext]
			&& map[p1ext] == 0 && map[p2ext] == 0)
		{
			walkPath.add (c + 1, p1ext);
			walkPath.add (c + 2, p2ext);
			visited[p1ext] = true;
			visited[p2ext] = true;
			return true;
		}
		return false;
	}
	
	
	public int[] floodFill (int idx, Integer dir, int forbidden)
	{
		int[] reached = new int[map.length];
		
		List<Integer> todo = new ArrayList<Integer> ();
		Arrays.fill (reached, Integer.MAX_VALUE);
		
		reached[idx] = 1;
		todo.add (idx);
		
		while (!todo.isEmpty ())
		{
			int next = todo.remove (0);
			int r = reached[next];
			
			List<Integer> adj = next == idx && dir != null ? getAdjacentAvailable (next, dir) : getAdjacentAvailable (next);
			for (int n : adj)
				if (map[n] == 0 && n != forbidden && reached[n] == Integer.MAX_VALUE)
				{
					reached[n] = r + 1;
					todo.add (n);
				}
			
		}
		return reached;
	}
	
	
	public StringBuffer dump (StringBuffer sb, Collection<Player> players)
	{
		Map<Integer, String> pos = new HashMap<Integer, String> ();
		
		for (Player p : players)
			pos.put (p.getPosition (), Utils.resolvPlayerName (p.getId ()));
		
		for (int i = 0; i < map.length; i++)
		{
			if (pos.get (i) != null)
				sb.append (pos.get (i));
			else if (map[i] == Integer.MAX_VALUE)
				sb.append ("#");
			else
				sb.append (map[i]);
			if ( (i + 1) % width == 0)
				sb.append ("\n");
		}
		return sb;
	}
	
	

	public List<Integer> getAdjacentAvailable (int idx, int dir)
	{
		List<Integer> all = getAdjacentAvailable (idx);
		List<Integer> allowed = new ArrayList<Integer> ();
		for (int i : all)
			if (Utils.allowedMove (Utils.getDirection (idx, i), dir))
				allowed.add (i);
		return allowed;
	}
	
	public List<Integer> getAdjacentAvailable (int idx)
	{
		List<Integer> adj = new ArrayList<Integer> ();
		int comp = compartments[idx];
		// left?
		
		
		if (idx % width != 0 && (map[idx - 1] == 0 || idx - 1 == myPos)  && ((comp == Integer.MAX_VALUE) || compartments[idx - 1] == comp))
			adj.add (idx - 1);
		// top
		if (idx >= width && (map[idx - width] == 0 || idx - width == myPos) && ((comp == Integer.MAX_VALUE) || compartments[idx - width] == comp))
			adj.add (idx - width);
		// right
		if ( (idx + 1) % width != 0 && (map[idx + 1]  == 0 || idx + 1 == myPos) && ((comp == Integer.MAX_VALUE) || compartments[idx + 1] == comp))
			adj.add (idx + 1);
		// bottom
		if (idx + width < compartments.length && (map[idx + width] == 0 || idx + width == myPos) && ((comp == Integer.MAX_VALUE) || compartments[idx + width] == comp))
			adj.add (idx + width);

		return adj;
	}
	
	
	// A recursive function that find articulation points using DFS traversal
	// u --> The vertex to be visited next
	// visited[] --> keeps tract of visited vertices
	// disc[] --> Stores discovery times of visited vertices
	// parent[] --> Stores parent vertices in DFS tree
	// ap[] --> Store articulation points
	private void APUtil (int u, boolean visited[], int disc[], int low[],
		int parent[], boolean ap[], Utils.MyInt time, int it)
	{
		if (++it > maxApIt)
			return;
		int children = 0;
		visited[u] = true;
		time.inc ();
		disc[u] = low[u] = time.val ();
		
		List<Integer> adj = getAdjacentAvailable (u);
		
		// vertices adjacent to this
		for (int v : adj)
		{
			if (!visited[v])
			{
				children++;
				parent[v] = u;
				APUtil (v, visited, disc, low, parent, ap, time, it);
				
				// subtree connected to ancestors?
				low[u] = Utils.min (low[u], low[v]);
				
				if (parent[u] == -1 && children > 1)
					ap[u] = true;
				if (parent[u] != -1 && low[v] >= disc[u])
					ap[u] = true;
			}
			
			else if (v != parent[u])
				low[u] = Utils.min (low[u], disc[v]);
		}
	}
	
	
	public List<Integer> getArticulationPoints (int compartment)
	{
		List<Integer> comp = getCompartment (compartment);
		int nV = map.length; // sure, that's too much mem, but who cares..
		boolean[] visited = new boolean[nV];
		int[] disc = new int[nV];
		int[] low = new int[nV];
		int[] parent = new int[nV];
		boolean[] artPoints = new boolean[nV];
		
		for (int i = 0; i < nV; i++)
		{
			parent[i] = -1;
			visited[i] = false;
			artPoints[i] = false;
		}
		
		for (int i : comp)
			if (visited[i] == false)
				APUtil (i, visited, disc, low, parent, artPoints, new Utils.MyInt (), 0);
		
		List<Integer> articulationPoints = new ArrayList<Integer> ();
		for (int i : comp)
			if (artPoints[i] == true)
				articulationPoints.add (i);
		return articulationPoints;
	}
	
	
	public int getFieldNum ()
	{
		return map.length;
	}


	public int [] getMap ()
	{
		return map;
	}
/*
	public void debugPos()
	{
		StringBuffer sb = new StringBuffer ();
		sb.append ("\t");
		for (int i = 0; i < width; i++)
			sb.append ("#").append (i).append ("#\t");
		sb.append ("\n#0#\t");
		for (int i = 0; i < map.length; i++)
		{
			sb.append (i).append ("\t");
			if ( (i + 1) % width == 0)
				sb.append ("\n#").append ((i+1) / width).append ("#\t");
		}
		
	}*/
	
}
