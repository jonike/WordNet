/**
 *
 * @author Webber Huang
 */
public class Outcast {
    private final WordNet wn;
    //private final ArrayList<String> nounList;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        wn = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDist = -1;
        String out = "";
        for (String n : nouns) {
            int d = 0;
            //StdOut.println(n);
            for (String m : nouns) {
                d = d + wn.distance(n, m);
            }
            //StdOut.println(d);
            if (d > maxDist) {
                maxDist = d;
                out = n;
            }
        }
        return out;
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
