/*
 * Mar 18, 2016
 * 	- "Link Exchange" problem(s)
 * Mar 27
 * 	- add graphMetric(), sampleLinkNoDup(), linkExchangeNoDup()
 */

package dsn;

import hist.Int2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import dp.PathMetric;
import dp.UtilityMeasure;
import algs4.EdgeInt;
import algs4.EdgeIntGraph;
import algs4.UnweightedGraph;


public class LinkExchange {

	////
	public static void printEdges(List<Int2> list){
		for(Int2 e:list)
			System.out.print("(" + e.val0 + "," + e.val1 + ") ");
		System.out.println();
	}
	////
	public static void graphMetric(String graph_file, int n_nodes) throws IOException{
		UnweightedGraph aG = UnweightedGraph.readEdgeListWithNodes(graph_file, "\t", n_nodes);
		System.out.println("#nodes = " + aG.V());
		System.out.println("#edges = " + aG.E());
		
		PathMetric path = new PathMetric();
		double[] distance_dist;
		
		distance_dist = UtilityMeasure.getDistanceDistr(aG, path);
		
		//
		System.out.println("diameter = " + path.s_Diam);
	}
	
	////
	public static List<Int2> createFalseLink(EdgeIntGraph G, int u, double beta){
		List<Int2> ret = new ArrayList<Int2>();
		
		int n = G.V();
		//
		Random random = new Random();
		for (int i = 0; i < beta*G.degree(u); i++){
			int w = random.nextInt(n);
			while (G.areEdgesAdjacent(u, w))
				w = random.nextInt(n);
			
			ret.add(new Int2(u, w));
			
		}
		
		
		//
		return ret;
	}
	
	////
	public static List<Int2> sampleLink(List<Int2> srcList, double alpha){
		List<Int2> ret = new ArrayList<Int2>();
		
		Random random = new Random();
		for (int i = 0; i < alpha*srcList.size(); i++){
			int k = random.nextInt(srcList.size());
			
			ret.add(srcList.get(k));
			
		}
		
		//
		return ret;
		
	}
	
	////
	public static List<Int2> sampleLinkNoDup(List<Int2> srcList, double alpha){
		List<Int2> ret = new ArrayList<Int2>();
		
		Map<Integer, Integer> dup = new HashMap<Integer, Integer>();
		
		Random random = new Random();
		for (int i = 0; i < alpha*srcList.size(); i++){
			int k = random.nextInt(srcList.size());
			while(dup.containsKey(k) == true)
				k = random.nextInt(srcList.size());
			
			dup.put(k, 1);
			ret.add(srcList.get(k));
			
		}
		
		//
		return ret;
		
	}
	
	////
	public static void linkExchange(EdgeIntGraph G, int round, double alpha, double beta, String count_file) throws IOException{
		int n = G.V();
		
		List<List<Int2>> links = new ArrayList<List<Int2>>();
		
		long start = System.currentTimeMillis();
		// initial stage
		for (int u = 0; u < n; u++){
			List<Int2> temp = new ArrayList<Int2>();
			for (int v:G.adj(u).keySet())
				temp.add(new Int2(u, v));
			
			List<Int2> newLinks = createFalseLink(G, u, beta);
			temp.addAll(newLinks);
			
			links.add(temp);
		}
		
		// loop
		for(int t = 1; t < round+1; t++){
			List<List<Int2>> exLinks = new ArrayList<List<Int2>>();		// new links received at each node
			for (int u = 0; u < n; u++)
				exLinks.add(new ArrayList<Int2>());
			
			// for each pair of nodes (u,v)
			for (EdgeInt e: G.edges()){
				int u = e.either();
				int v = e.other(u);
				
				List<Int2> listU = sampleLink(links.get(u), alpha);
				List<Int2> listV = sampleLink(links.get(v), alpha);
				
				//
				exLinks.get(u).addAll(listV);
				exLinks.get(v).addAll(listU);
				
			}
			// expand lists, accept duplicate links
			for (int u = 0; u < n; u++)
				links.get(u).addAll(exLinks.get(u));
		}
		
		// count true/false/duplicate links
		int[] trueLinks = new int[n];
		int[] falseLinks = new int[n];
		int[] dupLinks = new int[n];
		for (int u = 0; u < n; u++){
			Map<Int2, Integer> dup = new HashMap<Int2, Integer>();
			for(Int2 p : links.get(u)){
				if(p.val0 > p.val1){	// normalize
					int temp = p.val0;
					p.val0 = p.val1;
					p.val1 = temp;
				}
				
				if (dup.containsKey(p)){
					dupLinks[u] += 1;
				}else{
					dup.put(p, 1);
					if (G.areEdgesAdjacent(p.val0, p.val1))
						trueLinks[u] += 1;
					else
						falseLinks[u] += 1;
				}
			}
			
		}
		System.out.println("linkExchange - DONE, elapsed " + (System.currentTimeMillis() - start));
		
		// write to file
		BufferedWriter bw = new BufferedWriter(new FileWriter(count_file));
		for (int u = 0; u < n; u++){
			bw.write(trueLinks[u] + "\t" + falseLinks[u] + "\t" + dupLinks[u] + "\n");
		}
		bw.close();
		System.out.println("Written to count_file.");
		
	}
	
	//// insert link e to a sorted list
	public static boolean insertLink(List<Int2> list, Int2 e){
		// normalize e
		normalizeEdge(e);
		
		//
		int lo = 0;
		int hi = list.size()-1;
		int mid = (lo + hi)/2;
		int comp = 0;
		boolean found = false;
		while (true){
			comp = e.compareTo(list.get(mid));
			
			if (comp < 0){
				hi = mid-1;
			}else if(comp > 0){
				lo = mid+1;
			}else{
				found = true;
				break;
			}
			
			mid = (lo + hi)/2;
			
			if (lo > hi)
				break;
			
		}
		
		if (!found){
			comp = list.get(mid).compareTo(e);
			if (comp < 0)
				list.add(mid+1, e);
			else if (comp > 0)
				list.add(mid, e);
			else
				System.err.println("ERROR in insertLink");
			return true;
		}else
			return false;
		
	}
	
	////
	public static void normalizeEdge(Int2 e){
		if (e.val0 > e.val1){
			int temp = e.val0;
			e.val0 = e.val1;
			e.val1 = temp;
		}
	}
	
	////
	public static void normalizeEdges(List<Int2> list){
		for (Int2 e:list){
			normalizeEdge(e);
		}
		
	}
	
	////
	public static void linkExchangeNoDup(EdgeIntGraph G, int round, double alpha, double beta, double discount, String count_file) throws IOException{
		int n = G.V();
		
		List<List<Int2>> links = new ArrayList<List<Int2>>();
		
		long start = System.currentTimeMillis();
		
		// initial stage
		for (int u = 0; u < n; u++){
			List<Int2> temp = new ArrayList<Int2>();
			for (int v:G.adj(u).keySet())
				temp.add(new Int2(u, v));
			links.add(temp);
			
			// normalize and sort
			normalizeEdges(links.get(u));
			Collections.sort(links.get(u));
			
			// add false links (u,w)
			Random random = new Random();
			for (int i = 0; i < beta*G.degree(u); i++){
				int w = random.nextInt(n);
				
				while (true){
					
					if (w == u || G.areEdgesAdjacent(u, w) == true){
						w = random.nextInt(n);
						continue;
					}
					
					Int2 e = new Int2(u, w);
					boolean isNew = insertLink(links.get(u), e);
					if (isNew == true)
						break;
					else
						w = random.nextInt(n);
					
				}
			}
		}
		
		
		// loop
		for(int t = 1; t < round+1; t++){
			List<List<Int2>> exLinks = new ArrayList<List<Int2>>();		// new links received at each node
			for (int u = 0; u < n; u++)
				exLinks.add(new ArrayList<Int2>());
			
			// for each pair of nodes (u,v)
			for (EdgeInt e: G.edges()){
				int u = e.either();
				int v = e.other(u);
				
				List<Int2> listU = sampleLinkNoDup(links.get(u), alpha);
				List<Int2> listV = sampleLinkNoDup(links.get(v), alpha);
				
				//
				exLinks.get(u).addAll(listV);
				exLinks.get(v).addAll(listU);
				
			}
			// expand lists, do not accept duplicate links
			for (int u = 0; u < n; u++){
				for (Int2 e:exLinks.get(u))
					insertLink(links.get(u), e);
			}
			
			//
			alpha = alpha * discount;
		}
		
		// count true/false/duplicate links
		int[] trueLinks = new int[n];
		int[] falseLinks = new int[n];
		int[] dupLinks = new int[n];
		for (int u = 0; u < n; u++){
			Map<Int2, Integer> dup = new HashMap<Int2, Integer>();
			for(Int2 p : links.get(u)){
				// p is already normalized 
				if (p.val0 > p.val1)
					System.err.println("error");
				
				if (dup.containsKey(p)){
					dupLinks[u] += 1;
				}else{
					dup.put(p, 1);
					if (G.areEdgesAdjacent(p.val0, p.val1))
						trueLinks[u] += 1;
					else
						falseLinks[u] += 1;
				}
			}
			
		}
		System.out.println("linkExchangeNoDup - DONE, elapsed " + (System.currentTimeMillis() - start));
		
		// write to file
		BufferedWriter bw = new BufferedWriter(new FileWriter(count_file));
		for (int u = 0; u < n; u++){
			bw.write(trueLinks[u] + "\t" + falseLinks[u] + "\t" + dupLinks[u] + "\n");
		}
		bw.close();
		System.out.println("Written to count_file.");
		
	}
	
	
	////////////////////////////////////////////////
	public static void main(String[] args) throws Exception{
		String prefix = "";
//		String dataname = "pl_1000_5_01";		// diameter = 5
		String dataname = "pl_10000_5_01";		// diameter = 6,  Dup: round=3 (OutOfMem, 7GB ok), 98s (Acer)
												//				NoDup: round=3 (4.5GB), 376s (Acer)
		
		
		String filename = prefix + "_data/" + dataname + ".gr";
		
		//
		long start = System.currentTimeMillis();
		EdgeIntGraph G = EdgeIntGraph.readEdgeList(filename, "\t");	// "\t" or " "
		System.out.println("readGraph - DONE, elapsed " + (System.currentTimeMillis() - start));
		
		System.out.println("#nodes = " + G.V());
		System.out.println("#edges = " + G.E());

		// compute diameter
//		graphMetric(filename, G.V());
		
		
		//
		int round = 4; // <= diameter
		double alpha = 0.5;
		double discount = 0.5;
		double beta = 1.0;
		// TEST linkExchange()
//		String count_file = prefix + "_out/" + dataname + "-" + round + "_" + String.format("%.1f",alpha) + "_" + String.format("%.1f",beta) + ".cnt";
//		
//		linkExchange(G, round, alpha, beta, count_file);
		
		// TEST normalizeEdges(), insertLink()
//		List<Int2> list = new ArrayList<Int2>();
//		list.add(new Int2(2,3));
//		list.add(new Int2(3,4));
//		list.add(new Int2(4,2));
//		printEdges(list);
//		
//		normalizeEdges(list);
//		printEdges(list);
//		
//		Collections.sort(list);
//		printEdges(list);
//		
//		Int2 e1 = new Int2(2,5);
//		insertLink(list, e1);
//		printEdges(list);
		
		
		// TEST linkExchangeNoDup()
		String count_file = prefix + "_out/" + dataname + "-nodup-" + round + "_" + String.format("%.1f",alpha) + "_" + 
				String.format("%.1f",beta) + "_" + String.format("%.1f",discount) + ".cnt";
		
		linkExchangeNoDup(G, round, alpha, beta, discount, count_file);
		
		
	}

}