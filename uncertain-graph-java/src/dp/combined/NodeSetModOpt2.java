/*
 * Sep 23, 2015
 * 	- copied from NodeSetModOpt, use boolean array for both S and T, much faster
 * Sep 24
 * - replace Grph by EdgeWeightedGraph	
 * - add partitionLouvain(), recursiveLouvain(): apply Louvain to each node, move nodes between S and T
 * Sep 30
 * - copy getSubEgdeLists() from NodeSetMod
 */

package dp.combined;

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
import java.util.Stack;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

import dp.DPUtil;
import algs4.Edge;
import algs4.EdgeWeightedGraph;

public class NodeSetModOpt2{

	//
	public boolean[] ind;	// ind[i] = true -> i in S, false -> i in T
	public int[] ind2node;	// 
	public Map<Integer, Integer> node2ind;
	public int n_s;
	public int n_t;
	//
	public int e_st;
	public int e_s;			// number of edges inside S
	public int e_t;			// number of edges inside T
	public int d_s = 0; 	// total degree of nodes in S
	public int d_t = 0;		// total degree of nodes in T
	public List<Int2> e_list;
	
	public NodeSetModOpt2 parent, left, right;		// for recursive partitioning
	public int id;
	public int level = 0;
	
	////return e_listS, e_listT (they MUST be initialized outside !)
	public static void getSubEgdeLists(NodeSetModOpt2 R, List<Int2> e_listS, List<Int2> e_listT){
		
		int u = 0;
		int v = 0;
		for (Int2 e : R.e_list){
			u = R.node2ind.get(e.val0);
			v = R.node2ind.get(e.val1);
			if (R.ind[u] == true && R.ind[v] == true)
				e_listS.add(e);
			if (R.ind[u] == false && R.ind[v] == false)
				e_listT.add(e);
		}
	}
	
	////
	public static void getSubSet(EdgeWeightedGraph G, NodeSetModOpt2 R, NodeSetModOpt2 ret, boolean val, List<Int2> e_list){
		ret.node2ind = new HashMap<Integer, Integer>();
		int count = 0;
		for (int i = 0; i < R.ind.length; i++)
			if (R.ind[i] == val){
				ret.node2ind.put(R.ind2node[i], count);
				count ++;
			}
		
		ret.ind = new boolean[count];
		ret.ind2node = new int[count];
		count = 0;
		for (int i = 0; i < R.ind.length; i++)
			if (R.ind[i] == val)
				ret.ind2node[count++] = R.ind2node[i];
		
		//
		int n_nodes = ret.ind.length;
		
		//
		for (int i = 0; i < n_nodes/2; i++)
			ret.ind[i] = true;
		for (int i = n_nodes/2; i < n_nodes; i++)
			ret.ind[i] = false;
		
		ret.n_s = n_nodes/2;
		ret.n_t = n_nodes - n_nodes/2;
		//
		int[] node2Set = new int[n_nodes]; // 1:S, 2:T
		
		// d_s, d_t
		ret.d_s = 0;
		ret.d_t = 0;
		for (int i = 0; i < n_nodes; i++)
			if (ret.ind[i] == true){
				ret.d_s += G.degree(ret.ind2node[i]);
				node2Set[i] = 1;
			}else{
				ret.d_t += G.degree(ret.ind2node[i]);
				node2Set[i] = 2;
			}
	
		// e_st, e_s, e_t
		ret.e_st = 0;
		ret.e_s = 0;
		ret.e_t = 0;
		
		int u_id; 
		int v_id;
		for (Int2 e : e_list){
			u_id = ret.node2ind.get(e.val0);
			v_id = ret.node2ind.get(e.val1);
			if (node2Set[u_id] + node2Set[v_id] == 3)	//  
				ret.e_st += 1;
			if (node2Set[u_id] == 1 && node2Set[v_id] == 1)
				ret.e_s += 1;
			if (node2Set[u_id] == 2 && node2Set[v_id] == 2)
				ret.e_t += 1;
		}
	}
	
	////
	public NodeSetModOpt2(){
		this.e_list = new ArrayList<Int2>();
	}
	
	////
	public NodeSetModOpt2(EdgeWeightedGraph G){
		int n_nodes = G.V();
		
		this.ind = new boolean[n_nodes];
		this.ind2node = new int[n_nodes];
		this.node2ind = new HashMap<Integer, Integer>();
		
		//
		for (int i = 0; i < n_nodes; i++){
			this.ind2node[i] = i;
			this.node2ind.put(i, i);
		}
		
		//
		for (int i = 0; i < n_nodes/2; i++)
			this.ind[i] = true;
		for (int i = n_nodes/2; i < n_nodes; i++)
			this.ind[i] = false;
		
		this.n_s = n_nodes/2;
		this.n_t = n_nodes - n_nodes/2;
		//
		int n = G.V();
		int[] node2Set = new int[n]; // 1:S, 2:T
		
		// d_s, d_t
		this.d_s = 0;
		this.d_t = 0;
		for (int u = 0; u < n; u++)
			if (this.ind[this.node2ind.get(u)] == true){
				this.d_s += G.degree(u);
				node2Set[u] = 1;
			}else{
				this.d_t += G.degree(u);
				node2Set[u] = 2;
			}
		
		// e_st, e_s, e_t
		this.e_st = 0;
		this.e_s = 0;
		this.e_t = 0;
		this.e_list = new ArrayList<Int2>();
		
		int u; 
		int v;
		for (Edge e : G.edges()){
			u = e.either();
			v = e.other(u);
			this.e_list.add(new Int2(u, v));
			if (node2Set[u] + node2Set[v] == 3)	//  
				this.e_st += 1;
			if (node2Set[u] == 1 && node2Set[v] == 1)
				this.e_s += 1;
			if (node2Set[u] == 2 && node2Set[v] == 2)
				this.e_t += 1;
		}
		
		// debug
		System.out.println("NodeSetModOpt2 called: e_st = " + this.e_st + " e_s = " + this.e_s + " d_s = " + this.d_s + " e_t = " + this.e_t + " d_t = " + this.d_t);
	}
	
	//// move 1 item u from T to S
	public void add(int u, EdgeWeightedGraph G){
		
		//
		int count_add = 0;
		int count_remove = 0;
		for (int v : G.adj(u).keySet()){
			if (this.node2ind.containsKey(v)){
				if (this.ind[this.node2ind.get(v)] == true)
					count_remove += 1;
				else
					count_add += 1;
			}
		}
		this.n_s += 1;
		this.n_t -= 1;
		
		this.e_st = this.e_st - count_remove + count_add;
		this.e_s = this.e_s + count_remove;
		this.e_t = this.e_t - count_add;
		
		//
		this.ind[this.node2ind.get(u)] = true;
		
		// 
		int deg_u = G.degree(u);
		this.d_s += deg_u;
		this.d_t -= deg_u;
		
		//
		
	}
	
	//// move 1 item u from S to T
	public void remove(int u, EdgeWeightedGraph G){
		
		//
		int count_add = 0;
		int count_remove = 0;
		for (int v : G.adj(u).keySet()){
			if (this.node2ind.containsKey(v)){
				if (this.ind[this.node2ind.get(v)] == true)
					count_add += 1;
				else
					count_remove += 1;
			}
		}
		this.n_s -= 1;
		this.n_t += 1;
		
		this.e_st = this.e_st - count_remove + count_add;
		this.e_s = this.e_s - count_add;
		this.e_t = this.e_t + count_remove;
		
		this.ind[this.node2ind.get(u)] = false;
		
		// 
		int deg_u = G.degree(u);
		this.d_s -= deg_u;
		this.d_t += deg_u;
		
		//
		
	}
	
	////move 1 item u from S back to T
	public void reverse_add(int u, EdgeWeightedGraph G, int old_st, int old_s, int old_t){
		int deg_u = G.degree(u);
		this.e_st = old_st;
		this.e_s = old_s;
		this.e_t = old_t;
		
		this.ind[this.node2ind.get(u)] = false;
		
		this.n_s -= 1;
		this.n_t += 1;
		// 
		this.d_s -= deg_u;
		this.d_t += deg_u;
	}
	
	////move 1 item u from T back to S
	public void reverse_remove(int u, EdgeWeightedGraph G, int old_st, int old_s, int old_t){
		int deg_u = G.degree(u);
		this.e_st = old_st;
		this.e_s = old_s;
		this.e_t = old_t;
		
		this.ind[this.node2ind.get(u)] = true;
		
		this.n_s += 1;
		this.n_t -= 1;
		// 
		this.d_s += deg_u;
		this.d_t -= deg_u;
		
	}
	
	
	//// m : number of edges in G
	public double modularity(int m){
		double mod = 0.0;
		
//		System.out.println(" e_st = " + this.e_st + " e_s = " + this.e_s + " e_t = " + this.e_t + " d_s = " + this.d_s + " d_t = " + this.d_t);
		
		mod = (double)this.e_s/m - (this.d_s/(2.0*m)) *(this.d_s/(2.0*m))  +  (double)this.e_t/m - (this.d_t/(2.0*m))*(this.d_t/(2.0*m));	// avoid over flow of int*int
		
		//
		return mod;
	}
	
	////m : number of edges in G
	public double modularitySelf(int m){
		double mod = 0.0;
		
		double lc = this.e_s + this.e_t + this.e_st;
		double dc = this.d_s + this.d_t;
		
		mod = lc/m - (dc/(2.0*m))*(dc/(2.0*m));
		
		//
		return mod;
	}
	
	////m : number of edges in G
	public double modularityAll(int m){
		NodeSetModOpt2 root_set = this;
		while (root_set.parent != null)
			root_set = root_set.parent;
		
		double mod = 0.0;
		
		Queue<NodeSetModOpt2> queue_set = new LinkedList<NodeSetModOpt2>();
		queue_set.add(root_set);
		while (queue_set.size() > 0){
			NodeSetModOpt2 R = queue_set.remove();
			if (R.left == null) // leaf
				mod += R.modularitySelf(m);
			else{
				queue_set.add(R.left);
				queue_set.add(R.right);
			}
				
		}
		
		//
		return mod;
	}
	
	////
	public void print(){
		System.out.print("S : ");
		for (int s = 0; s < this.ind.length; s++)
			if (this.ind[s] == true)
			System.out.print(this.ind2node[s] + " ");
		System.out.println();
		
		System.out.print("T : ");
		for (int t = 0; t < this.ind.length; t++)
			if (this.ind[t] == false)
			System.out.print(this.ind2node[t] + " ");
		System.out.println();
	}
	
	////
	public int pickRandomFromS(Random random){
		int loc = random.nextInt(this.ind.length);
		while (true){
			if (this.ind[loc] == true)
				return this.ind2node[loc];
			loc = random.nextInt(this.ind.length);
		}
	}
	
	////
	public int pickRandomFromT(Random random){
		int loc = random.nextInt(this.ind.length);
		while (true){
			if (this.ind[loc] == false)
				return this.ind2node[loc];
			loc = random.nextInt(this.ind.length);
		}
	}
	
	
	//// MODULARITY partition, using modularity()
	public static void partitionMod(NodeSetModOpt2 R, EdgeWeightedGraph G, int n_steps, int n_samples, int sample_freq, boolean print_out, int lower_size){
		if (print_out)
			System.out.println("NodeSetMod.partitionMod called");
		
//		int n_nodes = G.V();
		int n_nodes = R.ind.length;
		int n_edges = G.E();
		
//		if (print_out)
//			System.out.println("#steps = " + (n_steps + n_samples * sample_freq));

		int out_freq = (n_steps + n_samples * sample_freq) / 10;
		//
		long start = System.currentTimeMillis();
		boolean is_add = true;			// add or remove
		Random random = new Random();
		int n_accept = 0;
		int n_accept_positive = 0;
		int u = -1;
		
		double modT = R.modularity(n_edges);
		double modT2;
		int old_st = R.e_st;
		int old_s = R.e_s;
		int old_t = R.e_t;
		
		for (int i = 0; i < n_steps + n_samples * sample_freq; i++) {
			// decide add or remove
			if (R.n_s < n_nodes/2 && R.n_s > lower_size){	// add or remove
				int rand_val = random.nextInt(2);
				if (rand_val == 0)
					is_add = true;
				else
					is_add = false;	
			}else if (R.n_s <= lower_size){			// only add
				is_add = true;
			}else{								// only remove (R.S.size() >= n_nodes/2)
				is_add = false;
			}
			
			// perform add or remove
			if (is_add){
				// randomly pick an item from T
				u = R.pickRandomFromT(random);
				R.add(u, G);
				
			}else{
				// randomly pick an item from S
				u = R.pickRandomFromS(random);
				R.remove(u, G);
			}
			
			// MCMC
			modT2 = R.modularity(n_edges);
			
			if (modT2 > modT){
				n_accept += 1;
//				n_accept_positive += 1;
				modT = modT2;
				//
				old_st = R.e_st;
				old_s = R.e_s;
				old_t = R.e_t;
			}else{
				// reverse
				if (is_add)
					R.reverse_add(u, G, old_st, old_s, old_t);
				else
					R.reverse_remove(u, G, old_st, old_s, old_t);
			}
			
//			if (i % out_freq == 0 && print_out)
//				System.out.println("i = " + i + " n_accept = " + n_accept + " mod = " + R.modularityAll(n_edges)
//						+ " n_accept_positive = " + n_accept_positive
//						+ " time : " + (System.currentTimeMillis() - start));
		}
		
		//
//		System.out.println("n_accept = " + n_accept);
		
	}
	
	
	//////////////////////////////
	// limit_size = 32: i.e. for NodeSet having size <= limit_size, call 
	public static NodeSetModOpt2 recursiveMod(EdgeWeightedGraph G, int burn_factor, int limit_size, int lower_size, int max_level){
		int n_nodes = G.V();
		int n_edges = G.E();
		int id = -1;
		
		// root node
		NodeSetModOpt2 root = new NodeSetModOpt2(G);
		root.id = id--;
		root.level = 0;
		// 
		Queue<NodeSetModOpt2> queue = new LinkedList<NodeSetModOpt2>();
		queue.add(root);
		while(queue.size() > 0){
			NodeSetModOpt2 R = queue.remove();
			System.out.println("R.level = " + R.level + " R.S.size() + R.T.size() = " + R.ind.length);
			
			// USE limit_size
//			boolean check_mod = false;
//			if (R.parent != null)
//				if (R.parent.modularity(n_edges) > R.parent.left.modularity(n_edges) + R.parent.right.modularity(n_edges))
//					check_mod = true;
			
			if (R.ind.length <= limit_size || R.level == max_level){
				continue;
			}
			
			long start = System.currentTimeMillis();
			NodeSetModOpt2.partitionMod(R, G, burn_factor* R.ind.length, 0, 0, false, lower_size);
			System.out.println("elapsed " + (System.currentTimeMillis() - start));
			
			NodeSetModOpt2 RS = new NodeSetModOpt2();
			NodeSetModOpt2 RT = new NodeSetModOpt2();
			
			getSubEgdeLists(R, RS.e_list, RT.e_list);
			
			getSubSet(G, R, RS, true, RS.e_list);
			getSubSet(G, R, RT, false, RT.e_list);
					
			RS.id = id--;
			R.left = RS;
			RS.parent = R;
			RS.level = R.level + 1;
			
			RT.id = id--;
			R.right = RT;
			RT.parent = R;
			RT.level = R.level + 1;
			
			//
			queue.add(RS);
		
			queue.add(RT);
			
		}
		
		//
		return root;
	}
	
	////
	public static void printSetIds(NodeSetModOpt2 root_set, int m){
		System.out.println("printSetIds");
		
		Queue<NodeSetModOpt2> queue_set = new LinkedList<NodeSetModOpt2>();
		queue_set.add(root_set);
		while (queue_set.size() > 0){
			NodeSetModOpt2 R = queue_set.remove();
			if (R.left != null){
//				System.out.println("R.id = " + R.id + " left.id = " + R.left.id + " right.id = " + R.right.id + 
//						" left.size = " + R.S.size() + " right.size = " + R.T.size() + " mod = " + R.modularity(m) + " modSelf = " + R.modularitySelf(m));
				System.out.println("R.id = " + R.id + " left.id = " + R.left.id + " right.id = " + R.right.id + 
					" left.size = " + R.n_s + " right.size = " + R.n_t + " (" + R.e_st + "," + R.e_s + "," + R.e_t + "," + R.d_s + "," + R.d_t + "," + m + ")" + 
					" mod = " + String.format("%.4f", R.modularity(m)) + " modSelf = " + String.format("%.4f", R.modularitySelf(m)) );
			}else{
				System.out.println("LEAF R.id = " + R.id + " modSelf = " + R.modularitySelf(m)); // + " left.size = " + R.S.size() + " right.size = " + R.T.size());
				//
//				System.out.print("LEAF R.id = " + R.id + " : {");
//				for (IntCursor t : R.S)
//					System.out.print(t.value + " ");
//				System.out.print(" ** ");
//				for (IntCursor t : R.T)
//					System.out.print(t.value + " ");
//				System.out.println("}");
			}
			if (R.left != null){
				queue_set.add(R.left);
				queue_set.add(R.right);
			}
		}
	}
	
	////
	public static void writePart(NodeSetModOpt2 root_set, String part_file) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(part_file));
		
		Queue<NodeSetModOpt2> queue_set = new LinkedList<NodeSetModOpt2>();
		queue_set.add(root_set);
		while (queue_set.size() > 0){
			NodeSetModOpt2 R = queue_set.remove();
			
			if (R.left != null){
				queue_set.add(R.left);
				queue_set.add(R.right);
			}else{	// leaf
				for (int s = 0; s < R.ind.length; s++)
					if (R.ind[s] == true)
						bw.write(R.ind2node[s] + ",");
				for (int s = 0; s < R.ind.length; s++)
					if (R.ind[s] == false)
						bw.write(R.ind2node[s] + ",");
				bw.write("\n");
			}
		}
		
		bw.close();
	}
	
	////dynamic programming: opt(R) = max{mod(R), opt(R.left) + opt(R.right)}
	public static List<NodeSetModOpt2> bestCut(NodeSetModOpt2 root_set, int m){
		
		List<NodeSetModOpt2> ret = new ArrayList<NodeSetModOpt2>();
		Map<Integer, CutNode> sol = new HashMap<Integer, CutNode>();	// best solution node.id --> CutNode info
		
		Queue<NodeSetModOpt2> queue = new LinkedList<NodeSetModOpt2>();
		Stack<NodeSetModOpt2> stack = new Stack<NodeSetModOpt2>();
		
		// fill stack using queue
		queue.add(root_set);
		while (queue.size() > 0){
			NodeSetModOpt2 R = queue.remove();
			stack.push(R);
			if (R.left != null){
				queue.add(R.left);
				queue.add(R.right);
			}
		}
		
		// 
		while (stack.size() > 0){
			NodeSetModOpt2 R = stack.pop();
			
			double mod = R.modularitySelf(m);			// non-private, need modularitySelfDP() !
			boolean self = true;
			if (R.left == null){	// leaf nodes
				sol.put(R.id, new CutNode(mod, true));
			}else{
				//
				double mod_opt = sol.get(R.left.id).mod + sol.get(R.right.id).mod;
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
		queue = new LinkedList<NodeSetModOpt2>();
		queue.add(root_set);
		while (queue.size() > 0){
			NodeSetModOpt2 R = queue.remove();
			
			if (sol.get(R.id).self == true){
				ret.add(R);
				System.out.print(R.id + " ");
			}else if (R.left != null){
				queue.add(R.left);
				queue.add(R.right);
			}
		}
		System.out.println();
		
		//
		return ret;
	}
	
	////
	public static void writeBestCut(List<NodeSetModOpt2> best_cut, String part_file) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(part_file));

		for (NodeSetModOpt2 R : best_cut){
//				for (int i = 0; i < R.k; i++){
//					for (int s = 0; s < R.part.length; s++)
//						if (R.part[s] == i)
//							bw.write(R.ind2node[s] + ",");
//					bw.write("\n");
//				}
			
			for (int s = 0; s < R.ind.length; s++)
				bw.write(R.ind2node[s] + ",");
			bw.write("\n");
		}
		
		bw.close();
	}
	
	
	//// see Louvain.one_level()
	public static void partitionLouvain(NodeSetModOpt2 R, EdgeWeightedGraph G, int burn_factor){
		int n_edges = G.E();
		double modT, modT2;
		
		int old_st = R.e_st;
		int old_s = R.e_s;
		int old_t = R.e_t;
		modT = R.modularity(n_edges);
		
		boolean modif = true;
		int nb_pass_done = 0;
		
		while (modif && nb_pass_done != burn_factor){
	        modif = false;
	        nb_pass_done += 1;
		
			for (int i = 0; i < R.ind.length; i++){
				int u = R.ind2node[i];
				boolean is_add = false;
				
				if (R.ind[i] == true){	// u in S
					R.remove(u, G);
					is_add = false;
				}else{
					R.add(u,G);
					is_add = true;
				}
				modT2 = R.modularity(n_edges);
				
				if (modT2 > modT){ 
					modif = true;
					modT = modT2;
					old_st = R.e_st;
					old_s = R.e_s;
					old_t = R.e_t;
				}else{ 		// reverse
					if (is_add)
						R.reverse_add(u, G, old_st, old_s, old_t);
					else
						R.reverse_remove(u, G, old_st, old_s, old_t);
				}
			}
		}
		//
		System.out.println("nb_pass_done < burn_factor : " + (nb_pass_done < burn_factor));
	}
	
	
	public static NodeSetModOpt2 recursiveLouvain(EdgeWeightedGraph G, int burn_factor, int limit_size, int max_level){
		int id = -1;
		
		// root node
		NodeSetModOpt2 root = new NodeSetModOpt2(G);
		root.id = id--;
		root.level = 0;
		// 
		Queue<NodeSetModOpt2> queue = new LinkedList<NodeSetModOpt2>();
		queue.add(root);
		while(queue.size() > 0){
			NodeSetModOpt2 R = queue.remove();
			System.out.println("R.level = " + R.level + " R.S.size() + R.T.size() = " + R.ind.length);
			
			if (R.ind.length <= limit_size || R.level == max_level){
				continue;
			}
			
			long start = System.currentTimeMillis();
			NodeSetModOpt2.partitionLouvain(R, G, burn_factor);
			System.out.println("elapsed " + (System.currentTimeMillis() - start));
			
			NodeSetModOpt2 RS = new NodeSetModOpt2();
			NodeSetModOpt2 RT = new NodeSetModOpt2();
			
			getSubEgdeLists(R, RS.e_list, RT.e_list);
			
			getSubSet(G, R, RS, true, RS.e_list);
			getSubSet(G, R, RT, false, RT.e_list);
					
			RS.id = id--;
			R.left = RS;
			RS.parent = R;
			RS.level = R.level + 1;
			
			RT.id = id--;
			R.right = RT;
			RT.parent = R;
			RT.level = R.level + 1;
			
			//
			queue.add(RS);
		
			queue.add(RT);
			
		}
		
		//
		return root;
	}
	
}
