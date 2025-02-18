/*
 * Apr 3, 2015
 * 	- created: sort noisy cells of A in ascending order and map to array of [m] 1-cells and [n(n-1)/2-m] 0-cells
 * Apr 5
 * 	- 
 * Sep 28
 * 	- add filterLaplaceAndLouvain(), use Louvain.java
 * Oct 11
 * 	- COMMAND-LINE
 */

package naive;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import algs4.Edge;
import algs4.EdgeWeightedGraph;
import dp.DPUtil;
import dp.combined.Louvain;
import grph.Grph;
import grph.VertexPair;
import grph.in_memory.InMemoryGrph;
import grph.io.EdgeListReader;
import grph.io.EdgeListWriter;
import grph.io.GrphTextReader;
import hist.DegreeSeqHist;
import hist.GraphIntSet;
import toools.io.file.RegularFile;
import toools.set.IntSet;

public class GreedyReconstruct {

	
	////
	public static Tuple3Double[] addLaplaceNoise(Grph G, double eps){
		int n_nodes = G.getNumberOfVertices();
		Tuple3Double[] result = new Tuple3Double[n_nodes*(n_nodes-1)/2];
		
		int count = 0;
		for (int u = 0; u < n_nodes; u++){
			IntSet u_neighbors = G.getNeighbours(u);
			for (int v = u+1; v < n_nodes; v++){
				if (u_neighbors.contains(v))
					result[count++] = new Tuple3Double(u, v, 1 + DPUtil.laplaceMechanism(eps));
				else
					result[count++] = new Tuple3Double(u, v, DPUtil.laplaceMechanism(eps));
			}
		}
		
		//
		return result;
	}
	
	////
	public static Grph greedyMapLaplace(Tuple3Double[] result, int n_nodes, int n_edges){
		Grph aG = new InMemoryGrph();
		aG.addNVertices(n_nodes);
		
		Arrays.sort(result);	// ascending
		
		int last = n_nodes*(n_nodes-1)/2;
		for (int i = last-n_edges; i < last; i++){		// add n_edges
			int u = result[i].r;
			int v = result[i].c;
			aG.addSimpleEdge(u, v, false);
		}
			
		//
		return aG;
	}
	
	////////////
	////
	public static Tuple3Int[] addGeometricNoise(Grph G, double eps){
		int n_nodes = G.getNumberOfVertices();
		Tuple3Int[] result = new Tuple3Int[n_nodes*(n_nodes-1)/2];
		
		double alpha = Math.exp(-eps);
		int count = 0;
		for (int u = 0; u < n_nodes; u++){
			IntSet u_neighbors = G.getNeighbours(u);
			for (int v = u+1; v < n_nodes; v++){
				if (u_neighbors.contains(v))
					result[count++] = new Tuple3Int(u, v, 1 + DPUtil.geometricMechanism(alpha));
				else
					result[count++] = new Tuple3Int(u, v, DPUtil.geometricMechanism(alpha));
			}
		}
		
		//
		return result;
	}
	
	////
	public static Grph greedyMapGeometric(Tuple3Int[] result, int n_nodes, int n_edges){
		Grph aG = new InMemoryGrph();
		aG.addNVertices(n_nodes);
		
		Arrays.sort(result);		// ascending
		
		int last = n_nodes*(n_nodes-1)/2;
		for (int i = last-n_edges; i < last; i++){		// add n_edges
			int u = result[i].r;
			int v = result[i].c;
			aG.addSimpleEdge(u, v, false);
		}
			
		//
		return aG;
	}
	
	//////// FOR large graphs ///////////
	public static int editScore(Grph aG, Grph G){
		GraphIntSet I = new GraphIntSet(G);
		
		int dist = 0;
		for (VertexPair p : aG.getEdgePairs()){
	    	int u = p.first;
	       	int v = p.second;
	       	if (!I.hasEdge(u, v))
	       		dist++;
		}
		
		//
		return dist;
	}
	
	////
	public static Grph filterLaplace(Grph G, double eps){
		int n = G.getNumberOfVertices();
		int m = G.getNumberOfEdges();
		double eps2 = 0.5;
		int m_noisy = m + DPUtil.geometricMechanism(Math.exp(-eps2));
		eps = eps - eps2;
		
		Grph aG = new InMemoryGrph();
		aG.addNVertices((int)n);
		
		//
		double eps_threshold = Math.log((double)n*(n-1)/(2.0*m_noisy) - 1);			// 2.0 to avoid rounding, (double)
		double theta = 0;
		long n1 = 0;		// number of passed 1-cells
		long n2 = 0;		// number of passed 0-cells
		if (eps > eps_threshold){
			theta = 1/(2*eps)*Math.log((double)n*(n-1)/(2.0*m_noisy) - 1) + 0.5;		// 2.0 to avoid rounding
			n1 = Math.round(m_noisy/2 * (2-Math.exp(-eps*(1-theta))));	
		}else{
			theta = 1/eps*Math.log( ((double)n*(n-1)/2 + m_noisy*(Math.exp(eps) - 1)) / (2*m_noisy));
			n1 = Math.round(m_noisy/2 * Math.exp(-eps*(theta-1)));
		}
//		n2 = m - n1;
		n2 = m_noisy-n1;
		
		// gamma = (m-1)/m
		double upper_eps1 = Math.log((double)n*(n-1)*m_noisy/8 - (double)m_noisy*m_noisy/4);
		// gamma = 0.9
		double upper_eps2 = Math.log(((double)n*(n-1)/2 - m_noisy)/(0.04*m_noisy) );
		System.out.println("upper_eps1 = " + upper_eps1);
		System.out.println("upper_eps2 = " + upper_eps2);
		
//		System.out.println("eps = " + eps);
		System.out.println("eps_threshold = " + eps_threshold);
		System.out.println("theta = " + theta);
		System.out.println("n1 = " + n1);
		
		// 1. for 1-cells
		int n1cells = 0;
		for (VertexPair p : G.getEdgePairs()){
	    	int u = p.first;
	       	int v = p.second;
	       	if (1 + DPUtil.laplaceMechanism(eps) > theta){
	       		aG.addSimpleEdge(u, v, false);
	       		n1cells ++;
	       	}
		}
		System.out.println("n1cells = " + n1cells);
		
		// 2. for 0-cells
		GraphIntSet I = new GraphIntSet(G);
		int count = 0;
		Random random = new Random();
//		while (count < m-n1cells){
		while (count < m_noisy-n1cells){
			int u = random.nextInt(n);
			int v = random.nextInt(n);
			if (!I.hasEdge(u, v)){		// 0-cell
				aG.addSimpleEdge(u, v, false);
				count++;
			}
		}
		
		
		//
		return aG;
	}

	//// Sep 28
	public static EdgeWeightedGraph convertGraph(Grph G){
		EdgeWeightedGraph aG = new EdgeWeightedGraph(G.getNumberOfVertices());
		
		for (VertexPair p : G.getEdgePairs()){
			aG.addEdge(new Edge(p.first, p.second, 1.0));
		}
		
		//
		return aG;
	}
	
	////
	public static double modularity(Grph G, Map<Integer, Integer> part){
		double mod = 0.0;
		
		int k = 0;
		for (int com : part.values())
			if (k < com)
				k = com;
		k += 1;
		
		double[] lc = new double[k];
		double[] dc = new double[k];
		
		//
		for (int u = 0; u < G.getNumberOfVertices(); u++)
			dc[part.get(u)] += G.getVertexDegree(u);
		
		for (VertexPair p : G.getEdgePairs()){
			int u = p.first;
			int v = p.second;
			
			if (part.get(u) == part.get(v))
				lc[part.get(u)] += 1;
		}
		
		//
		int m = G.getNumberOfEdges();
		for (int i = 0; i < k; i++)
			mod += lc[i]/m - (dc[i]/(2*m))*(dc[i]/(2*m));
		
		//
		return mod;
	}
	
	////
	public static void filterLaplaceAndLouvain(Grph G, double eps, String sample_file, String part_file, int n_samples) throws IOException{
		
		RegularFile f;
		Map<Integer, Integer> part;
		
		for (int i = 0; i < n_samples; i++){
			Grph aG = filterLaplace(G, eps);
			
			f = new RegularFile(sample_file + "." + i);
			EdgeListWriter writer = new EdgeListWriter();
	    	writer.writeGraph(aG, f);
	    	System.out.println("writeGraph - DONE");
			
			EdgeWeightedGraph G2 = convertGraph(aG);
			System.out.println("convertGraph - DONE");
			
			
			Louvain lv = new Louvain();
			
			long start = System.currentTimeMillis();
			part = lv.best_partition(G2, null);
			System.out.println("best_partition - DONE, elapsed " + (System.currentTimeMillis() - start));
			
			// compute modularity using G and part
			System.out.println("real modularity = " + modularity(G, part));		// test part on G not G2
			
			//
			Louvain.writePart(part, part_file + "-" + i + ".part");
			System.out.println("writePart - DONE");
			
		}
		
	}
	
	
	///////////////////////////////////////////////////////
	public static void main(String[] args) throws Exception{
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
		System.out.println("GreedyReconstruct");
		
		// TOY GRAPH
//		Grph G = new InMemoryGrph();
//		G.addNVertices(8);
//		G.addSimpleEdge(0, 5, false); G.addSimpleEdge(0, 6, false); G.addSimpleEdge(0, 7, false);
//		G.addSimpleEdge(1, 5, false); G.addSimpleEdge(1, 6, false); G.addSimpleEdge(1, 7, false);
//		G.addSimpleEdge(2, 6, false); G.addSimpleEdge(2, 7, false); G.addSimpleEdge(2, 4, false);
//		G.addSimpleEdge(3, 4, false); // example in the paper
		
		
		// load graph
//		String dataname = "polbooks";		// (105, 441)		15ms/9ms 		edit.dist=358 (eps=1.0), 261(2.0), 99(4.0), 9(8.0), 0(16.0)
																// geometric	edit.dist=345 (eps=1.0), 246(2.0), 57(4.0), 5(8.0), 0(16.0)
		
//		String dataname = "polblogs";		// (1224,16715) 	145ms/487ms 	edit.dist=15748 (1.0), 14376(2.0), 7370(4.0), 1048(8.0), 20(16.0)
																// geometric	edit.dist=15590 (1.0), 14241(2.0), 6732(4.0), 247(8.0), 0(16.0)
		
//		String dataname = "as20graph";		// (6474,12572)		9.2s/11.3s		edit.dist=12556(1.0), 12518(2.0), 12197(4.0), 4659 (8.0), 75 (16.0)
																// geometric	edit.dist=12560(1.0), 12537(2.0), 12340(4.0), 5729(8.0), 1(16.0)		
//		String dataname = "wiki-Vote";		// (7115,100762)
//		String dataname = "ca-HepPh";		// (12006,118489) 	46s/47s (score:71s)	4GB mem				26806(8.0), 519(16.0)
//		String dataname = "ca-AstroPh";		// (18771,198050) 	
		// LARGE
//		String dataname = "com_amazon_ungraph";		// (334863,925872) 2s	edit.dist=915854(1.0), 899454(2.0), 759465(4.0), 133714 (8.0), 2505(16.0)
//		String dataname = "com_dblp_ungraph";		// (,) 
		String dataname = "com_youtube_ungraph";	// (1134890,2987624) 9s	edit.dist=2987591(1.0), 2987519(2.0), 2986873(4.0), 2946598(8.0), 232651(16.0)
													//				edit.score: ?s(8.0), 8s (16.0)	upper_eps1 = 40.71, upper_eps2 = 15.49		
		
		// COMMAND-LINE <prefix> <dataname> <n_samples> <eps>
		String prefix = "";
	    int n_samples = 10;
	    double eps = 1.0;
		
		if(args.length >= 4){
			prefix = args[0];
			dataname = args[1];
			n_samples = Integer.parseInt(args[2]);
			eps = Double.parseDouble(args[3]);
		}
		System.out.println("dataname = " + dataname);
		
		System.out.println("n_samples = " + n_samples);
		System.out.println("eps_c = " + eps);
		
		String filename = prefix + "_data/" + dataname + ".gr";
		String sample_file = prefix + "_sample/" + dataname + "_tmf_" + String.format("%.1f", eps);		// Oct 11, _filter_ --> _tmf_
		System.out.println("sample_file = " + sample_file);

		
//		GrphTextReader reader = new GrphTextReader();
		EdgeListReader reader = new EdgeListReader();
		Grph G;
		RegularFile f = new RegularFile(filename);
		
		long start = System.currentTimeMillis();
		G = reader.readGraph(f);
		System.out.println("readGraph - DONE, elapsed " + (System.currentTimeMillis() - start));
		
		System.out.println("#nodes = " + G.getNumberOfVertices());
		System.out.println("#edges = " + G.getNumberOfEdges());

		// TEST addLaplaceNoise(), greeadyMapLaplace()
//		int n_nodes = G.getNumberOfVertices();
//		
//		double eps = 8.0;
//		int n_edges = G.getNumberOfEdges();
//		
//		long start = System.currentTimeMillis();
//		Tuple3Double[] result = addLaplaceNoise(G, eps);
//		System.out.println("addLaplaceNoise - DONE, elapsed " + (System.currentTimeMillis() - start));
//		start = System.currentTimeMillis();
//		Grph aG = greedyMapLaplace(result, n_nodes, n_edges);
//		System.out.println("greedyMapLaplace - DONE, elapsed " + (System.currentTimeMillis() - start));
		
		// TEST addGeometricNoise(), greeadyMapGeometric()
//		int n_nodes = G.getNumberOfVertices();
//		
//		double eps = 16.0;
//		int n_edges = G.getNumberOfEdges();
//		
//		long start = System.currentTimeMillis();
//		Tuple3Int[] result = addGeometricNoise(G, eps);
//		System.out.println("addGeometricNoise - DONE, elapsed " + (System.currentTimeMillis() - start));
//		start = System.currentTimeMillis();
//		Grph aG = greedyMapGeometric(result, n_nodes, n_edges);
//		System.out.println("greedyMapGeometric - DONE, elapsed " + (System.currentTimeMillis() - start));
//		
//		System.out.println("#nodes = " + aG.getNumberOfVertices());
//		System.out.println("#edges = " + aG.getNumberOfEdges());
//		start = System.currentTimeMillis();
//		System.out.println("edit distance (aG, G) = " + DegreeSeqHist.editScore(aG, G));
//		System.out.println("editScore - DONE, elapsed " + (System.currentTimeMillis() - start));
		
		
		// TEST filterLaplace()
		for (int i = 0; i < n_samples; i++){
			System.out.println("sample i = " + i);
		
			start = System.currentTimeMillis();
			Grph aG = filterLaplace(G, eps);
			System.out.println("filterLaplace - DONE, elapsed " + (System.currentTimeMillis() - start));
			System.out.println("#nodes = " + aG.getNumberOfVertices());
			System.out.println("#edges = " + aG.getNumberOfEdges());
			
//			start = System.currentTimeMillis();
//			System.out.println("edit distance (aG, G) = " + editScore(aG, G));
//			System.out.println("editScore - DONE, elapsed " + (System.currentTimeMillis() - start));
			
			f = new RegularFile(sample_file + "." + i);
			EdgeListWriter writer = new EdgeListWriter();
	    	writer.writeGraph(aG, f);
		
		}
		
		
		// TEST filterLaplaceAndLouvain()
//		int n_samples = 1;
//		String[] dataname_list = new String[]{"com_amazon_ungraph"}; //com_amazon_ungraph, "com_dblp_ungraph", "com_youtube_ungraph"};
////		double[][] eps_list = new double[][]{{5.0, 10.0, 20.0, 30.0}, {5.0, 10.0, 20.0, 30.0}, {10.0, 20.0, 30.0, 50.0}};
//	    double[][] eps_list = new double[][]{{0.5*12.72, 12.72, 1.5*12.72, 2*12.72}, {5.0, 10.0}, {5.0, 10.0}};
//	    
//	    Grph G;
//	    
//	    for (int i = 0; i < dataname_list.length; i++){
//	    	dataname = dataname_list[i];
//	    	
//	    	System.out.println("dataname = " + dataname);
//	    	
//	    	String filename = "_data/" + dataname + ".gr";
//	    	
//			EdgeListReader reader = new EdgeListReader();
//			RegularFile f = new RegularFile(filename);
//			long start = System.currentTimeMillis();
//			G = reader.readGraph(f);
//			System.out.println("readGraph - DONE, elapsed " + (System.currentTimeMillis() - start));
//			
//			System.out.println("#nodes = " + G.getNumberOfVertices());
//			System.out.println("#edges = " + G.getNumberOfEdges());    	
//			
//	    	for (double eps : eps_list[i]){
//	    		
//	    		String sample_file = "_sample/" + dataname + "_filter_" + String.format("%.1f", eps);
//	    		String part_file = "_out/" + dataname + "_filter_" + String.format("%.1f", eps);
//	    		
//	    		System.out.println("eps = " + eps);
//	    		
//	    		filterLaplaceAndLouvain(G, eps, sample_file, part_file, n_samples);
//	    	}
//	    }
		
	}

}
