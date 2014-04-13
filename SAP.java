/**
 *
 * @author Webber Huang
 */
public class SAP {
    private final Digraph G;
    private BreadthFirstDirectedPaths bfs1, bfs2;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }

    // length of shortest ancestral bfs between v and w; -1 if no such bfs
    public int length(int v, int w) {
        checkInput(v, w);
        int an = ancestor(v, w);
        if (an == -1) return -1;
        return bfs1.distTo(an) + bfs2.distTo(an);        
    }

    // a common ancestor of v and w that participates in a shortest 
    // ancestral bfs; -1 if no such bfs
    public int ancestor(int v, int w) {
        checkInput(v, w);
        int result = -1;
        int shortestLength = Integer.MAX_VALUE;
        bfs1 = new BreadthFirstDirectedPaths(G, v);
        bfs2 = new BreadthFirstDirectedPaths(G, w);
        for (int i = 0; i < G.V(); i++) {
            if (bfs1.hasPathTo(i) && bfs2.hasPathTo(i)) {
                int dist = bfs1.distTo(i) + bfs2.distTo(i);
                if (dist < shortestLength) {
                    shortestLength = dist;
                    result = i;
                }
            }                
        }
        return result;
    }

    // length of shortest ancestral bfs between any vertex in v and any 
    // vertex in w; -1 if no such bfs
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkInput(v, w);
        int an = ancestor(v, w);
        if (an == -1) return -1;
        return bfs1.distTo(an) + bfs2.distTo(an);     
    }

    // a common ancestor that participates in shortest ancestral bfs; 
    // -1 if no such bfs
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkInput(v, w);
        int result = -1;
        int shortestLength = Integer.MAX_VALUE;
        bfs1 = new BreadthFirstDirectedPaths(G, v);
        bfs2 = new BreadthFirstDirectedPaths(G, w);
        for (int i = 0; i < G.V(); i++) {
            if (bfs1.hasPathTo(i) && bfs2.hasPathTo(i)) {
                int dist = bfs1.distTo(i) + bfs2.distTo(i);
                if (dist < shortestLength) {
                    shortestLength = dist;
                    result = i;
                }
            }                
        }
        return result;
    }
    
    // Helper methods for validate input argument
    private void checkInput(int v, int w) {
        if (v < 0 || v > G.V()-1 || w < 0 || w > G.V()-1) 
            throw new java.lang.IndexOutOfBoundsException();
    }
    
    private void checkInput(Iterable<Integer> v, Iterable<Integer> w) {
        for (int x : v)
            if (x < 0 || x > G.V()-1) 
                throw new java.lang.IndexOutOfBoundsException();
        for (int y : w)
            if (y < 0 || y > G.V()-1) 
                throw new java.lang.IndexOutOfBoundsException();
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
