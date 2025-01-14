/*
 * Sep 21, 2015
 * 	- use short weight
 */

/*************************************************************************
 *  Compilation:  javac Edge.java
 *  Execution:    java Edge
 *
 *  Immutable weighted edge.
 *
 *************************************************************************/

package algs4;

/**
 *  The <tt>Edge</tt> class represents a weighted edge in an 
 *  {@link EdgeWeightedGraph}. Each edge consists of two shortegers
 *  (naming the two vertices) and a real-value weight. The data type
 *  provides methods for accessing the two endposhorts of the edge and
 *  the weight. The natural order for this data type is by
 *  ascending order of weight.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class EdgeShort implements Comparable<EdgeShort> { 

    private final short v;
    private final short w;
//    private final double weight;
    private short weight;	// hiepnh Sep 21, 2015

    /**
     * Initializes an edge between vertices <tt>v/tt> and <tt>w</tt> of
     * the given <tt>weight</tt>.
     * param v one vertex
     * param w the other vertex
     * param weight the weight of the edge
     * @throws IndexOutOfBoundsException if either <tt>v</tt> or <tt>w</tt> 
     *    is a negative shorteger
     * @throws IllegalArgumentException if <tt>weight</tt> is <tt>NaN</tt>
     */
    public EdgeShort(short v, short w, short weight) {
        if (v < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative shorteger");
        if (w < 0) throw new IndexOutOfBoundsException("Vertex name must be a nonnegative shorteger");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    /**
     * Returns the weight of the edge.
     * @return the weight of the edge
     */
    public short weight() {
        return weight;
    }

    /**
     * Returns either endposhort of the edge.
     * @return either endposhort of the edge
     */
    public short either() {
        return v;
    }

    /**
     * Returns the endposhort of the edge that is different from the given vertex
     * (unless the edge represents a self-loop in which case it returns the same vertex).
     * @param vertex one endposhort of the edge
     * @return the endposhort of the edge that is different from the given vertex
     *   (unless the edge represents a self-loop in which case it returns the same vertex)
     * @throws java.lang.IllegalArgumentException if the vertex is not one of the endposhorts
     *   of the edge
     */
    public short other(short vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("Illegal endposhort");
    }

    /**
     * Compares two edges by weight.
     * @param that the other edge
     * @return a negative shorteger, zero, or positive shorteger depending on whether
     *    this edge is less than, equal to, or greater than that edge
     */
    public int compareTo(EdgeShort that) {
        if      (this.weight() < that.weight()) return -1;
        else if (this.weight() > that.weight()) return +1;
        else                                    return  0;
    }

    /**
     * Returns a string representation of the edge.
     * @return a string representation of the edge
     */
    public String toString() {
        return String.format("%d-%d %.5f", v, w, weight);
    }

	////////////////////////////////////////
	// hiepnh - Sep 14, 2015
    public void setWeight(short new_weight){
    	this.weight = new_weight;
    }
    
    
    /**
     * Unit tests the <tt>Edge</tt> data type.
     */
    public static void main(String[] args) {
        EdgeShort e = new EdgeShort((short)12, (short)23, (short)3);
        StdOut.println(e);
    }
}
