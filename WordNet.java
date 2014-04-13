/**
 *
 * @author Webber Huang
 */

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class WordNet {
    private final ArrayList<String> origList;
    private final Map<String, ArrayList<Integer>> nounIdMap;
    private int count;
    private final Set<Integer> ids;
    private final Digraph G;
    private final SAP sap;
    
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        count = 0;
        ids = new HashSet<>();
        origList = new ArrayList<>();
        nounIdMap = buildSynMap(synsets);          

        // build digraph
        G = new Digraph(count);
        buildDigraph(G, buildHynMap(hypernyms));   
        
        // check is rooted DAG
        isRootedDAG();
        
        // create sap instance
        sap = new SAP(G);
    }
    
    private void isRootedDAG() {
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle()) throw new java.lang.IllegalArgumentException();
        if (ids.size() > 1) throw new java.lang.IllegalArgumentException();
    }
    
    private Map<String, ArrayList<Integer>> buildSynMap(String synsets) {
        In in = new In(synsets);
        Map<String, ArrayList<Integer>> map = new HashMap<>();
        
        while (in.hasNextLine()) {      
            String[] fields = in.readLine().split(","); 
            int id = Integer.parseInt(fields[0]);
            ids.add(id);
            origList.add(fields[1]);
            
            for (String n : fields[1].split(" ")) {
                if (!map.containsKey(n))
                    map.put(n, new ArrayList<Integer>());                
                map.get(n).add(id);
            }
            count++;
        }
        return map;
    }
    
    private Map<Integer, ArrayList<Integer>> buildHynMap(String hypernyms) {
        In in = new In(hypernyms);
        Map<Integer, ArrayList<Integer>> map = new HashMap<>();
        
        while (in.hasNextLine()) {  
            String[] fields = in.readLine().split(",");
            int id = Integer.parseInt(fields[0]);
            ids.remove(id);

            if (!map.containsKey(id))
                map.put(id, new ArrayList<Integer>());

            for (int i = 1; i < fields.length; i++) {
                int val = Integer.parseInt(fields[i]);
                map.get(id).add(val);
            }
        }
        return map;
    }
    
    private void buildDigraph(Digraph graph, Map<Integer, ArrayList<Integer>> map) {
        for (Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
            for (int val : entry.getValue())
                graph.addEdge(entry.getKey(), val);
        }
    }

    // the set of nouns (no duplicates), returned as an Iterable
    public Iterable<String> nouns() {
        return nounIdMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return  nounIdMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA)) throw new java.lang.IllegalArgumentException();
        if (!isNoun(nounB)) throw new java.lang.IllegalArgumentException();

        ArrayList<Integer> idA = nounIdMap.get(nounA);
        ArrayList<Integer> idB = nounIdMap.get(nounB);        
        return sap.length(idA, idB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor 
    // of nounA and nounB in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA)) throw new java.lang.IllegalArgumentException();
        if (!isNoun(nounB)) throw new java.lang.IllegalArgumentException();
        
        ArrayList<Integer> idA = nounIdMap.get(nounA);
        ArrayList<Integer> idB = nounIdMap.get(nounB);
        int idAn = sap.ancestor(idA, idB);        
        return origList.get(idAn);
    }

    // for unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        
        while (!StdIn.isEmpty()) {
            String a = StdIn.readString();
            String b = StdIn.readString();
            int length = wn.distance(a, b);
            String ancestor = wn.sap(a, b);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }    
}
