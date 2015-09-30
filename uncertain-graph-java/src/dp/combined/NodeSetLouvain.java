/*
 * Sep 28, 2015
 * 	- exponential mechanism via MCMC on K groups of nodes (first pass of Louvain method)
 * Sep 29, 2015
 * 	- apply tree structure (not binary)
 */

package dp.combined;

import hist.Int2;
import hist.Int2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import algs4.Edge;
import algs4.EdgeWeightedGraph;

import com.carrotsearch.hppc.cursors.IntCursor;

import dp.DPUtil;
import dp.mcmc.Dendrogram;
import dp.mcmc.Node;


public class NodeSetLouvain {

	public int k = 5;	// 	fan-out
	//
	public int[] part;	//	part[i] = 0 -> K-1 is the partition of the node i 
	public int[] size;	//	number of nodes in partition k
	public int[] lc;	// 	number of intra-edges
	public int[] dc;	// 	
	//
	public List<Int2> e_list;	
	public int[] ind2node;	// 
	public Map<Integer, Integer> node2ind;
	//
	public NodeSetLouvain[] children;
	public NodeSetLouvain parent;
	public int id;
	public int level = 0;
	
	//// for the case number of parts < k
	public int normalizePart(){
		boolean[] mark = new boolean[this.k];
		for (int i = 0; i < this.k; i++)
			mark[i] = false;
		
		for (int p : this.part)
			mark[p] = true;
		
		int[] map = new int[this.k];
		int count = 0;
		for (int i = 0; i < this.k; i++){
			map[i] = count;
			if (mark[i] == true)
				count += 1;
		}
		
		//
		for (int i = 0; i < this.part.length; i++)
			this.part[i] = map[this.part[i]];
		
		//
		return count;
	}
	
	////
	public static void getSubEgdeLists(NodeSetLouvain R, int k, List<List<Int2>> e_list){
		for (int i = 0; i < k; i++)
			e_list.add(new ArrayList<Int2>());
		
		int u = 0;
		int v = 0;
		for (Int2 e : R.e_list){
			u = R.node2ind.get(e.val0);
			v = R.node2ind.get(e.val1);
			if (R.part[u] == R.part[v])
				e_list.get(R.part[u]).add(e);
		}
	}
	
	//// build child node RC (part = val)
	public static void getSubSet(EdgeWeightedGraph G, NodeSetLouvain R, NodeSetLouvain RC, int val, int k){
		int count = 0;
		RC.node2ind = new HashMap<Integer, Integer>();
		for (int i = 0; i < R.part.length; i++)
			if (R.part[i] == val){
				RC.node2ind.put(R.ind2node[i], count);
				count ++;
			}
		
		RC.part = new int[count];
		RC.ind2node = new int[count];
		count = 0;
		for (int i = 0; i < R.part.length; i++)
			if (R.part[i] == val)
				RC.ind2node[count++] = R.ind2node[i];
		
		//
		int n = RC.part.length;
		
		RC.part = new int[n];
		RC.size = new int[k];
		RC.lc = new int[k];
		RC.dc = new int[k];
		//
		for (int i = 0; i < k-1; i++){
			for (int j = 0; j < n/k; j++)
				RC.part[i*(n/k) + j] = i;
			RC.size[i] = n/k;
		}

		int m = n - (k-1)*(n/k);
		for (int j = 0; j < m; j++)
			RC.part[(k-1)*(n/k) + j] = k-1;
		RC.size[k-1] = m;
		
		// lc, dc
		int u_id; 
		int v_id;
		for (Int2 e : RC.e_list){
			u_id = RC.node2ind.get(e.val0);
			v_id = RC.node2ind.get(e.val1);
			if (RC.part[u_id] == RC.part[v_id])
				RC.lc[RC.part[u_id]] += 1;
		}
		
		for (int u : RC.ind2node){
			u_id = RC.node2ind.get(u);
			
			RC.dc[RC.part[u_id]] += G.degree(u);
			
		}
		
	}
	
	////
	public NodeSetLouvain(int k){
		this.k = k;
		this.children = new NodeSetLouvain[this.k];
	}
	
	////
	public NodeSetLouvain(EdgeWeightedGraph G, int k){
		int n = G.V();
		
		// ind2node, node2ind
		this.ind2node = new int[n];
		this.node2ind = new HashMap<Integer, Integer>();
		
		//
		for (int i = 0; i < n; i++){
			this.ind2node[i] = i;
			this.node2ind.put(i, i);
		}
		
		//
		this.k = k;
		this.part = new int[n];
		this.size = new int[k];
		this.lc = new int[k];
		this.dc = new int[k];
		//
		for (int i = 0; i < k-1; i++){
			for (int j = 0; j < n/k; j++)
				this.part[i*(n/k) + j] = i;
			this.size[i] = n/k;
		}

		int m = n - (k-1)*(n/k);
		for (int j = 0; j < m; j++)
			this.part[(k-1)*(n/k) + j] = k-1;
		this.size[k-1] = m;
		
		// lc, dc, e_list
		this.e_list = new ArrayList<Int2>();
		int u; 
		int v;
		for (Edge e : G.edges()){
			u = e.either();
			v = e.other(u);
			this.e_list.add(new Int2(u, v));
			
			if (this.part[u] == this.part[v])
				this.lc[this.part[u]] += 1;
			this.dc[this.part[u]] += 1;
			this.dc[this.part[v]] += 1;
		}
		
		// check sum(lc), sum(dc)
		int sum_dc = 0;
		for (int val : this.dc)
			sum_dc += val;
		System.out.println("sum_dc = " + sum_dc);
		
		
	}
	
	////m : number of edges in G
	public double modularity(int m){
		double mod = 0.0;
		
		for (int i = 0; i < lc.length; i++)
			mod += (double)this.lc[i]/m - (this.dc[i]/(2.0*m)) *(this.dc[i]/(2.0*m));
		//
		return mod;
	}
	
	////m : number of edges in G
	public double modularitySelf(int m){
		double mod = 0.0;
		double lc = this.e_list.size();
		double dc = 0;
		for (int val : this.dc)
			dc += val;
		
		mod = lc/m - (dc/(2.0*m))*(dc/(2.0*m));
		
		//
		return mod;
	}
	
	////m : number of edges in G
	public double modularityAll(int m){
		NodeSetLouvain root_set = this;
		while (root_set.parent != null)
			root_set = root_set.parent;
		
		double mod = 0.0;
		
		Queue<NodeSetLouvain> queue_set = new LinkedList<NodeSetLouvain>();
		queue_set.add(root_set);
		while (queue_set.size() > 0){
			NodeSetLouvain R = queue_set.remove();
			if (R.children[0] == null) // leaf
				mod += R.modularitySelf(m);
			else{
				for (int i = 0; i < R.children.length; i++)
					queue_set.add(R.children[i]);
			}
				
		}
		
		//
		return mod;
	}
	
	//// move node u (id) to group 'dest'
	public void move(int u_id, int dest, EdgeWeightedGraph G){
		
		int u = this.ind2node[u_id];
		
		int deg_u = G.degree(u);
		this.dc[part[u_id]] -= deg_u;
		this.dc[dest] += deg_u;
		
		for (int v : G.adj(u).keySet()){
			if (this.node2ind.containsKey(v)){
				int v_id = this.node2ind.get(v);
				if (this.part[v_id] == this.part[u_id])
					this.lc[part[u_id]] -= 1;
				if (this.part[v_id] == dest)
					this.lc[dest] += 1;
			}
		}
		
		//
		this.size[part[u_id]] -= 1;
		this.size[dest] += 1;
		
		this.part[u_id] = dest;
		
	}
	
	
	////
	public static void partitionMod(NodeSetLouvain R, EdgeWeightedGraph G, double eps_p, int n_steps, int n_samples, int sample_freq){
		
		int n_nodes = R.part.length;
		int n_edges = G.E();
		int k = R.lc.length;
		
		// compute dU
	    double dU = 8.0/n_edges;
	    
	    System.out.println("#steps = " + (n_steps + n_samples * sample_freq));
		//
	    Random random = new Random();
		int n_accept = 0;
		int n_accept_positive = 0;
		int u = -1;
		int dest = -1;
		int old_dest = -1;
		
		double modT = R.modularity(n_edges);
		double modT2;
		
		for (int i = 0; i < n_steps + n_samples * sample_freq; i++) {
			
			// decide (u, dest)
			u = random.nextInt(n_nodes);
			dest = random.nextInt(k);
			while (dest == R.part[u])
				dest = random.nextInt(k);
			
			old_dest = R.part[u];
			R.move(u, dest, G);
			
			// MCMC
			modT2 = R.modularity(n_edges);
			
			if (modT2 > modT){
				n_accept += 1;
				n_accept_positive += 1;
				modT = modT2;
				
			}else{
				double prob = Math.exp(eps_p/(2*dU)*(modT2 - modT));			// prob ~ 1.0
				// System.out.println("prob = " + prob);
				double prob_val = random.nextDouble();
				
				if (prob_val > prob){
					// reverse
					R.move(u, old_dest, G);
					
				}else {
					n_accept += 1;
					modT = modT2;
					//
				}
			}
			
			
		}
	}
	
	////
	public static NodeSetLouvain recursiveMod(EdgeWeightedGraph G, double eps1, int burn_factor, int limit_size, int lower_size, int max_level, double ratio, int k){
		int n_nodes = G.V();
		int n_edges = G.E();
		int id = -1;
		
		//
		double[] epsArr = DPUtil.epsilonByLevel(eps1, max_level, ratio); 
		
		// root node
		NodeSetLouvain root = new NodeSetLouvain(G, k);
		root.id = id--;
		root.level = 0;
		
		// 
		Queue<NodeSetLouvain> queue = new LinkedList<NodeSetLouvain>();
		queue.add(root);
		while(queue.size() > 0){
			NodeSetLouvain R = queue.remove();
			System.out.println("R.level = " + R.level + " R.size = " + R.part.length);
			
			if (R.part.length <= limit_size || R.level == max_level){
				continue;
			}
			
			long start = System.currentTimeMillis();
			NodeSetLouvain.partitionMod(R, G, epsArr[R.level], burn_factor*R.part.length, 0, 0);
			System.out.println("elapsed " + (System.currentTimeMillis() - start));
			
			int count = R.normalizePart();
			
			List<List<Int2>> e_list = new ArrayList<List<Int2>>();
			getSubEgdeLists(R, count, e_list);
			
			R.children = new NodeSetLouvain[count];
			for (int i = 0; i < count; i++){
				NodeSetLouvain RC = new NodeSetLouvain(k);
				
				RC.e_list = e_list.get(i);
				getSubSet(G, R, RC, i, k);
				
				RC.id = id--;
				RC.level = R.level + 1;
				RC.parent = R;
				R.children[i] = RC;
				
				
				queue.add(RC);
			}
			
		}
		
		//
		return root;
	}
	
	////
	public static void printSetIds(NodeSetLouvain root_set, int m){
		System.out.println("printSetIds");
		
		Queue<NodeSetLouvain> queue_set = new LinkedList<NodeSetLouvain>();
		queue_set.add(root_set);
		while (queue_set.size() > 0){
			NodeSetLouvain R = queue_set.remove();
			if (R.children[0] != null){
//				System.out.print("{");
//				for (int i = 0; i < R.part.length; i++)
//					System.out.print(R.ind2node[i] + " ");
//				System.out.println("}");
				
				System.out.print("R.id = " + R.id + " modSelf = " + String.format("%.4f", R.modularitySelf(m)) + " children (id, size) = ");
				for (int i = 0; i < R.children.length; i++)
					System.out.print(" (" + R.children[i].id + "," + R.size[i] + ") mod = " + 
							String.format("%.4f", R.children[i].modularity(m)) );
				System.out.println();
			}else{
				System.out.println("LEAF R.id = " + R.id + " modSelf = " + String.format("%.4f", R.modularitySelf(m)) );
			}
			
			if (R.children[0] != null){
				for (int i = 0; i < R.children.length; i++)
					queue_set.add(R.children[i]);
			}
		}
	}
	
	////
	public void writePart(String part_file) throws IOException{

		List<List<Integer>> com = new ArrayList<List<Integer>>();
		int k = this.lc.length;
		for (int i = 0; i < k; i++)
			com.add(new ArrayList<Integer>());
		
		for (int i = 0; i < this.part.length; i++)
			com.get(this.part[i]).add(i);
		
		//
		BufferedWriter bw = new BufferedWriter(new FileWriter(part_file));
		for (List<Integer> list:com){
			for (int u : list)
				bw.write(u + ",");
			bw.write("\n");
		}
		
		bw.close();
	}
	
	////dynamic programming: opt(R) = max{mod(R), opt(R.left) + opt(R.right)}
	public static List<NodeSetLouvain> bestCut(NodeSetLouvain root_set, int m){
		
		List<NodeSetLouvain> ret = new ArrayList<NodeSetLouvain>();
		Map<Integer, CutNode> sol = new HashMap<Integer, CutNode>();	// best solution node.id --> CutNode info
		
		Queue<NodeSetLouvain> queue = new LinkedList<NodeSetLouvain>();
		Stack<NodeSetLouvain> stack = new Stack<NodeSetLouvain>();
		
		// fill stack using queue
		queue.add(root_set);
		while (queue.size() > 0){
			NodeSetLouvain R = queue.remove();
			stack.push(R);
			if (R.children[0] != null)
				for (int i = 0; i < R.children.length; i++)
					queue.add(R.children[i]);
		}
		
		// 
		while (stack.size() > 0){
			NodeSetLouvain R = stack.pop();
			
			double mod = R.modularitySelf(m);			// non-private, need modularitySelfDP() !
			boolean self = true;
			if (R.children[0] == null){	// leaf nodes
				sol.put(R.id, new CutNode(mod, true));
				
			}else{
				//
				double mod_opt = 0.0;
				for (int i = 0; i < R.children.length; i++)
					mod_opt += sol.get(R.children[i].id).mod;
				if (mod < mod_opt){
					mod = mod_opt;
					self = false;
				}
					
				sol.put(R.id, new CutNode(mod, self));
			}
		}
		
		System.out.println("sol.size = " + sol.size());
		System.out.println("best modularity = " + sol.get(-1).mod);
		
		// compute ret
		queue = new LinkedList<NodeSetLouvain>();
		queue.add(root_set);
		while (queue.size() > 0){
			NodeSetLouvain R = queue.remove();
			
			if (sol.get(R.id).self == true){
				ret.add(R);
				System.out.print(R.id + " ");
			}else if (R.children[0] != null)
				for (int i = 0; i < R.children.length; i++)
					queue.add(R.children[i]);
		}
		System.out.println();
		
		//
		return ret;
	}
	
	////
	public static void writeBestCut(List<NodeSetLouvain> best_cut, String part_file) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(part_file));

		for (NodeSetLouvain R : best_cut){
//			for (int i = 0; i < R.k; i++){
//				for (int s = 0; s < R.part.length; s++)
//					if (R.part[s] == i)
//						bw.write(R.ind2node[s] + ",");
//				bw.write("\n");
//			}
			
			for (int s = 0; s < R.part.length; s++)
				bw.write(R.ind2node[s] + ",");
			bw.write("\n");
		}
		
		bw.close();
	}
	
}