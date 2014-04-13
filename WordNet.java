/**
 *
 * @author Webber Huang
 */

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class WordNet {
    private final List<String> origList;
    private final Map<String, Bag<Integer>> nounIdMap;
    private final Set<Integer> ids;
    private final Digraph G;
    private final SAP sap;
    
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        ids = new HashSet<>();
        origList = new ArrayList<>();
        nounIdMap = new HashMap<>();
        
        readSynsets(synsets);          

        // build digraph
        G = new Digraph(origList.size());
        buildDigraph(G, buildHynMap(hypernyms));   
        
        // check is rooted DAG
        isRootedDAG();
        
        // create sap instance
        sap = new SAP(G);
    }
    
    private void readSynsets(String filepath) {
        In in = new In(filepath);
        
        while (in.hasNextLine()) {      
            String[] fields = in.readLine().split(","); 
            int id = Integer.parseInt(fields[0]);
            ids.add(id);
            origList.add(fields[1]);
            
            for (String n : fields[1].split(" ")) {
                if (!nounIdMap.containsKey(n))
                    nounIdMap.put(n, new Bag<Integer>());                
                nounIdMap.get(n).add(id);
            }
        }
    }
    
    private Map<Integer, Bag<Integer>> buildHynMap(String filepath) {
        In in = new In(filepath);
        Map<Integer, Bag<Integer>> map = new HashMap<>();
        
        while (in.hasNextLine()) {  
            String[] fields = in.readLine().split(",");
            int id = Integer.parseInt(fields[0]);
            ids.remove(id);

            if (!map.containsKey(id))
                map.put(id, new Bag<Integer>());

            for (int i = 1; i < fields.length; i++) {
                int val = Integer.parseInt(fields[i]);
                map.get(id).add(val);
            }
        }
        return map;
    }
    
    private void buildDigraph(Digraph graph, Map<Integer, Bag<Integer>> map) {
        for (Map.Entry<Integer, Bag<Integer>> entry : map.entrySet()) {
            for (int val : entry.getValue())
                graph.addEdge(entry.getKey(), val);
        }
    }
    
    private void isRootedDAG() {
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle() || ids.size() > 1) 
            throw new java.lang.IllegalArgumentException();
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
        checkNouns(nounA, nounB);

        Bag<Integer> idA = nounIdMap.get(nounA);
        Bag<Integer> idB = nounIdMap.get(nounB);        
        return sap.length(idA, idB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor 
    // of nounA and nounB in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNouns(nounA, nounB);
        
        Bag<Integer> idA = nounIdMap.get(nounA);
        Bag<Integer> idB = nounIdMap.get(nounB);
        int idAn = sap.ancestor(idA, idB);        
        return origList.get(idAn);
    }
    
    private void checkNouns(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) 
            throw new java.lang.IllegalArgumentException();
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
