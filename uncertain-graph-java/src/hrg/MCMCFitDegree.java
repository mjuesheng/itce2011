/**
 * 20 Mar, 2015
 * conversion from Python, run 30 times faster !
 * 
 */
package hrg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import algs4.EdgeIntGraph;
import toools.io.file.RegularFile;
import toools.set.IntSet;
import grph.VertexPair;
import grph.algo.AdjacencyMatrix;
import grph.io.EdgeListReader;
import grph.io.EdgeListWriter;


//////////////////////////////////
public class MCMCFitDegree {

	public static void main(String[] args) throws Exception{
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
		System.out.println("MCMCFitDegree");
		
		// TOY GRAPH
//		EdgeIntGraph G = new InMemoryEdgeIntGraph();
//		G.addNVertices(7);
//		for (int v = 0; v < 6; v++)
//			G.addSimpleEdge(v, v+1, false);
		
		
		// load graph
		String dataname = "polbooks";		// (105, 441)
//		String dataname = "polblogs";		// (1224,16715) 	1324k fitting (1069s)
//		String dataname = "as20graph";		// (6474,12572)		build_dendrogram 0.16s, 75k fitting (16s), 750k(234s), 10 samples (68s)
//		String dataname = "wiki-Vote";		// (7115,100762) 	81k fitting (37s)
//		String dataname = "ca-HepPh";		// (12006,118489) 	22k fitting (8.8s)
//		String dataname = "ca-AstroPh";		// (18771,198050) 	28.7k fitting (19.6s)
		
		
		String filename = "_data/" + dataname + ".grph";
		String node_file = "_out/" + dataname + "_deg_hrg";
	    String out_file = "_sample/" + dataname + "_deg_fit";    		

	    //
	    long start = System.currentTimeMillis();
		EdgeIntGraph G = EdgeIntGraph.readEdgeList(filename, "\t");	// "\t" or " "
		System.out.println("readGraph - DONE, elapsed " + (System.currentTimeMillis() - start));
		
		System.out.println("#nodes = " + G.V());
		System.out.println("#edges = " + G.E());
			
//		AdjacencyMatrix A = G.getAdjacencyMatrix();
		
		//
		HRGDeg T = new HRGDeg();
		T.initByGraph(G);
		
//		T.initByInternalNodes(G, new Int4[]{new Int4(-5,-2,1,2), new Int4(-6,-3,3,4), new Int4(-7,-3,5,6), 
//				new Int4(-2,-1,0,-5), new Int4(-3,-1,-6,-7), new Int4(-1,Node.ROOT_NODE,-2,-3)});
		
//		System.out.println("TEST lowestCommonAncestor");
//		System.out.println("lowestCommonAncestor = " + HRGDeg.lowestCommonAncestor(T.node_dict.get(0), T.node_dict.get(2)));
		
//		System.out.println("inOrderPrint");
//		HRGDeg.inOrderPrint(T.root_node, true, true);
		
		System.out.println("logLK = " + T.logLK());
		System.out.println("degDiffL1 = " + T.degDiffL1());
		
//		List<Integer> u_list = HRGDeg.findChildren(T.root_node);
//		System.out.println(u_list.size());
//		for (int id:u_list)
//			System.out.print(id + " ");
		
		// TEST config_2(), config_3()
//		Node r_node = T.node_dict.get(-2);  // node_dict[-2], [-5]
//		    
////	    Node p_node = T.config_2(G, r_node);
////	    System.out.println("config_2: DONE");
//	    r_node = T.config_3(G, r_node);
//	    System.out.println("config_3: DONE");
//
//	    System.out.println("logLK = " + T.logLK());
//	    
//	    System.out.println("--root_node.id =" + T.root_node.id);
//	    HRGDeg.inOrderPrint(T.root_node, true, true);
		
		// TEST 
		int n_samples = 50;
		int sample_freq = 1000;
		int burn_factor = G.V();
		
		System.out.println("burn_factor = " + burn_factor + " sample_freq = " + sample_freq);
	    start = System.currentTimeMillis();
	    List<HRGDeg> list_T = HRGDeg.dendrogramFitting(T, G, burn_factor*G.V(), n_samples, sample_freq);      
	    System.out.println("dendrogramFitting - DONE, elapsed " + (System.currentTimeMillis() - start));

//	    //check T
////	    int nNode = HRGDeg.inOrderPrint(T.root_node, false, false);		// 12947 = 6474*2-1 OK
////	    System.out.println("#nodes in T : " + nNode);
	    
	    for (HRGDeg T2 : list_T)
	    	System.out.println("logLK = " + T2.logLK() + " degDiffL1 = " + T2.degDiffL1());
	    
	    // TEST writeInternalNodes(), readInternalNodes()
	    HRGDeg.writeInternalNodes(list_T, node_file);
	    System.out.println("writeInternalNodes - DONE");
		
		
//		System.out.println("readInternalNodes");
//	    ArrayList<HRGDeg> list_T2 = new ArrayList<HRGDeg>();
//	    for (int i = 0; i < n_samples; i++)
//	    	list_T2.add(new HRGDeg());
//	    
//	    start = System.currentTimeMillis();
//	    HRGDeg.readInternalNodes(G, list_T2, node_file, n_samples);
//	    for (HRGDeg T2 : list_T2)
//	    	System.out.println("logLK = " + T2.logLK());
//	    System.out.println("readInternalNodes - DONE, elapsed " + (System.currentTimeMillis() - start));
	    
	    
//	    // TEST generateSanitizedSample()
//	    start = System.currentTimeMillis();
//	    int count = 0;
//	    for (HRGDeg T2 : list_T){
//	    	EdgeIntGraph aG = T2.generateSanitizedSample(G.getNumberOfVertices(), false);	// list_T not list_T2 (T2 not contains .noisy_value)
//	    	f = new RegularFile(out_file + "." + count);
////	    	f.setContent(aG.toEdgeIntGraphText().getBytes());
//	    	writer.writeGraph(aG, f);
//	    	count ++;
//	    }
//	    System.out.println("generateSanitizedSample - DONE, elapsed " + (System.currentTimeMillis() - start));
	    
	}

}
