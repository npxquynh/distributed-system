/**
 * http://algs4.cs.princeton.edu/43mst/Edge.java.html
 * 
 */

package mst;

public class Edge {
	private final int v;
	private final int w;
	private final double weight;
	public enum SE { BASIC, BRANCH, REJECTED };
	private SE stateEdge;
	
    public SE getStateEdge() {
		return stateEdge;
	}

	public void setStateEdge(SE stateEdge) {
		System.out.println(this.toString());
		this.stateEdge = stateEdge;
		System.out.println(this.toString());
	}

	/**
     * Initializes an edge between vertices <tt>v/tt> and <tt>w</tt> of
     * the given <tt>weight</tt>.
     * param v one vertex
     * param w the other vertex
     * param weight the weight of the edge
     */
	public Edge(int v, int w, double weight) {
		this.v = v;
		this.w = w;
		this.weight = weight;
		this.stateEdge = SE.BASIC;
	}
	
    /**
     * Returns the weight of the edge.
     * @return the weight of the edge
     */
    public double weight() {
        return weight;
    }

    /**
     * Returns either endpoint of the edge.
     * @return either endpoint of the edge
     */
	public int either() {
		return v;
	}
	
    /**
     * Returns the endpoint of the edge that is different from the given vertex
     * (unless the edge represents a self-loop in which case it returns the same vertex).
     * @param vertex one endpoint of the edge
     * @return the endpoint of the edge that is different from the given vertex
     *   (unless the edge represents a self-loop in which case it returns the same vertex)
     * @throws java.lang.IllegalArgumentException if the vertex is not one of the endpoints
     *   of the edge
     */
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint");
    }
    
    /**
     * Compares two edges by weight.
     * @param that the other edge
     * @return a negative integer, zero, or positive integer depending on whether
     *    this edge is less than, equal to, or greater than that edge
     */
    public int compareTo(Edge that) {
        if      (this.weight() < that.weight()) return -1;
        else if (this.weight() > that.weight()) return +1;
        else                                    return  0;
    }
    
    /**
     * Returns a string representation of the edge.
     * @return a string representation of the edge
     */
    public String toString() {
        return String.format("%d-%d %.5f\t%s", v, w, weight, String.valueOf(stateEdge));
    }
}
