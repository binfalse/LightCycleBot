/**
 * 
 */
package de.binfalse.fm14bfbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.binfalse.bflog.LOGGER;



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
		updateCompartments ();
	}
	
	
	private void updateCompartments ()
	{
		compartments = new int[map.length];
		int cmp = 0;
		if (map[0] == Integer.MAX_VALUE)
			compartments[0] = Integer.MAX_VALUE;
		else
			compartments[0] = ++cmp;
		
		Map<Integer, Integer> mapper = new HashMap<Integer, Integer> ();
		
		for (int i = 1; i < map.length; i++)
		{
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
				mapper.put (toreplace, togo);
				// System.out.println (toreplace + " -> " + togo);
			}
		}
		
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
		for (Player p : players.values ())
		{
			map[p.getPosition ()] = p.getId ();
			compartments[p.getPosition ()] = Integer.MAX_VALUE;
			
			// since the maps are quite small and we have plenty of time we could
			// do the following at every point in time. but let's at least try to
			// do something intelligent ;-)
			
			// TODO: is there a potential new articulation point?
			// -> recalc aps for this compartment
			
			// TODO: did one of them enter an ap?
			// -> recalc the compartments and of course recalc aps
		}
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
	
	
	public int getIdx (int x, int y)
	{
		return x + width * y;
	}
	
	
	public StringBuffer dump (StringBuffer sb)
	{
		for (int i = 0; i < map.length; i++)
		{
			if (map[i] == Integer.MAX_VALUE)
				sb.append ("#");
			else
				sb.append (map[i]);
			if ( (i + 1) % width == 0)
				sb.append ("\n");
		}
		return sb;
	}
	
	
	public int[] calcVoronoi (List<Player> enemies, Player me)
	{
		// TODO: avoid actual crashes -> enemy wants to get same field as our bot..
		// TODO: test all available moves of our bot and enemies
		// and count the number of fields we would get
		
		// my flood:
		int[] flood = floodFill (me.getPosition ());
		
		// their floods
		List<int[]> floods = new ArrayList<int[]> ();
		for (Player enemy : enemies)
			floods.add (floodFill (enemy.getPosition ()));
		
		// voronoi
		int[] voronoi = new int[flood.length];
		for (int i = 0; i < voronoi.length; i++)
		{
			int min = flood[i];
			if (map[i] < Integer.MAX_VALUE)
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
	
	
	private List<Integer> getAvailableMoves (GameMap map, Player p)
	{
		List<Integer> moves = getAdjacentAvailable (p.getPosition ());
		int i = 0;
		while (i < moves.size ())
			if (moves.get (i))
				
				return moves;
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
	
	private List<VirtualCompartment>	bestCompartmentList;
	private int												bestScore;
	
	
	public void chooseBestCompartmentPath (VirtualCompartment vc,
		List<VirtualCompartment> cur, int curScore)
	{
		bestCompartmentList = new ArrayList<VirtualCompartment> ();
		bestScore = 0;
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
	
	
	public List<Integer> optimalFill (Player p)
	{
		// List<Integer> toGo = new ArrayList<Integer> ();
		
		int[] flood = floodFill (p.getPosition ());
		
		List<Integer> articulationPoints = getArticulationPoints (getCompartmentNumber (p
			.getPosition ()));
		
		// ich muss nur einmal flood fillen -> articulation points sind natuerlich
		// die 1 in den neuen compartments..
		
		// create virtual compartments
		List<VirtualCompartment> virtualCompartments = getVirtualCompartments (
			p.getPosition (), flood, articulationPoints);
		
		// choose a path through compartments
		// List<VirtualCompartment> finalCompartmentPath =
		chooseBestCompartmentPath (virtualCompartments.get (0),
			new ArrayList<VirtualCompartment> (), 0);
		
		List<Integer> walkPath = new ArrayList<Integer> ();
		// System.out.println(p.getPosition());
		// walkPath.add(p.getPosition());
		
		// foreach virtual compartment do
		
		for (int i = 0; i < bestCompartmentList.size (); i++)
		{
			// System.out.println("pre: " + walkPath);
			walkPath
				.addAll (findGoodPath (
					flood,
					bestCompartmentList.get (i),
					i < bestCompartmentList.size () - 1 ? bestCompartmentList.get (i + 1).input
						: -1));
			// System.out.println("post: " + walkPath);
		}
		
		// run floodfill from start // i guess we do not need another flood?
		// best: just go from end to start by finding neighbors with smaller values
		
		// take shortest path from start to end
		// and extend this path
		
		List<Integer> toGo = new ArrayList<Integer> ();
		for (int i : walkPath)
			if (toGo.isEmpty () || !toGo.get (toGo.size () - 1).equals (i))
				toGo.add (i);
		return toGo;
	}
	
	
	public List<Integer> findGoodPath (int[] flood, VirtualCompartment vc, int end)
	{
		int start = vc.input;
		if (end < 0)
		{
			// we need to decide where to go to!? maybe just search for the field with
			// the biggest flood value!?
			// throw new RuntimeException("not yet implemented");
			int highest = 0;
			for (int i : vc.nodes)
			{
				if (flood[i] > highest)
					end = i;
			}
		}
		List<Integer> walkPath = new ArrayList<Integer> ();
		boolean[] visited = new boolean[flood.length];
		walkPath.addAll (shortestPath (flood, visited, start, end));
		
		LOGGER.debug ("shortest path: ", walkPath);
		
		extendPath (walkPath, visited, vc);
		
		if ( ((double) (walkPath.size ())) / (double) vc.nodes.size () < .8)
		{
			LOGGER.debug ("need to add waypoint...");
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
			
			// shortest path sum-end
			int[] flood2 = floodFill (sum);
			walkPath2.addAll (shortestPath (flood2, visited, sum, end));
			
			extendPath (walkPath2, visited, vc);
			if (walkPath.size () < walkPath2.size ())
				walkPath = walkPath2;
		}
		
		return walkPath;
	}
	
	
	public List<Integer> shortestPath (int[] floodFromStart, boolean[] visited,
		int start, int end, int dir)
	{
		List<Integer> walkPath = new ArrayList<Integer> ();
		
		walkPath.add (end);
		int insertAt = walkPath.size () - 1;
		// walkPath.add(end);
		int cur = end;
		visited[end] = true;
		while (cur != start)
		{
			List<Integer> neighbors = getAdjacentAvailable (cur);
			for (int i : neighbors)
				if (floodFromStart[i] < floodFromStart[cur])
				{
					cur = i;
					visited[i] = true;
					walkPath.add (insertAt, i);
				}
		}
		
		
		
		return walkPath;
	}
	
	
	public void extendPath (List<Integer> walkPath, boolean[] visited,
		VirtualCompartment vc)
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
		boolean didsmth = true;
		int its = 0;
		while (didsmth && its < 5)
		{
			didsmth = false;
			its++;
			int c = 0;
			while (c < walkPath.size () - 1)
			{
				int p1 = walkPath.get (c);
				int p2 = walkPath.get (c + 1);
				
				if (p1 + 1 == p2 || p1 - 1 == p2)
				{
					// p1 left or right of p2
					if (tryExtend (walkPath, c, visited, p1, p2, p1 - width, p2 - width))
						// extended above
						didsmth = true;
					if (tryExtend (walkPath, c, visited, p1, p2, p1 + width, p2 + width))
						// extended below
						didsmth = true;
				}
				else
				{
					// p1 above or below p2
					if (tryExtend (walkPath, c, visited, p1, p2, p1 - 1, p2 - 1))
						// extended left
						didsmth = true;
					if (tryExtend (walkPath, c, visited, p1, p2, p1 + 1, p2 + 1))
						// extended right
						didsmth = true;
				}
				
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
			&& map[p1ext] < Integer.MAX_VALUE && map[p2ext] < Integer.MAX_VALUE)
		{
			if (LOGGER.isDebugEnabled ())
				LOGGER.debug ("extending: ", p1, "/", p2, " -> ", p1, "-", p1ext, "-",
					p2ext, "-", p2);
			walkPath.add (c + 1, p1ext);
			walkPath.add (c + 2, p2ext);
			visited[p1ext] = true;
			visited[p2ext] = true;
			return true;
		}
		return false;
	}
	
	
	public int[] floodFill (int idx)
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
			
			for (int n : getAdjacentAvailable (next))
				if (reached[n] == Integer.MAX_VALUE)
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
	
	
	public StringBuffer dumpCompartments (StringBuffer sb)
	{
		for (int i = 0; i < compartments.length; i++)
		{
			if (compartments[i] == Integer.MAX_VALUE)
				sb.append ("#");
			else
				sb.append (compartments[i]);
			if ( (i + 1) % width == 0)
				sb.append ("\n");
		}
		return sb;
	}
	
	
	public StringBuffer dumpCompartments (StringBuffer sb,
		Collection<Player> players)
	{
		Map<Integer, String> pos = new HashMap<Integer, String> ();
		
		for (Player p : players)
			pos.put (p.getPosition (), Utils.resolvPlayerName (p.getId ()));
		
		for (int i = 0; i < compartments.length; i++)
		{
			if (pos.get (i) != null)
				sb.append (pos.get (i));
			else if (compartments[i] == Integer.MAX_VALUE)
				sb.append ("#");
			else
				sb.append (compartments[i]);
			if ( (i + 1) % width == 0)
				sb.append ("\n");
		}
		return sb;
	}
	
	
	public List<Integer> getAdjacentAvailable (int idx)
	{
		List<Integer> adj = new ArrayList<Integer> ();
		int comp = compartments[idx];
		// left?
		if (idx % width != 0 && compartments[idx - 1] == comp)
			adj.add (idx - 1);
		// top
		if (idx >= width && compartments[idx - width] == comp)
			adj.add (idx - width);
		// right
		if ( (idx + 1) % width != 0 && compartments[idx + 1] == comp)
			adj.add (idx + 1);
		// bottom
		if (idx + width < compartments.length && compartments[idx + width] == comp)
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
		int parent[], boolean ap[], Utils.MyInt time)
	{
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
				APUtil (v, visited, disc, low, parent, ap, time);
				
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
				APUtil (i, visited, disc, low, parent, artPoints, new Utils.MyInt ());
		
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
	
}
