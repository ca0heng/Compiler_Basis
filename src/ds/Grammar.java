package ds;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * A Grammar Object represents a context-free grammar which contains terminals,
 * non-terminals and productions.
 */
public class Grammar {
    private HashSet<Character> terminals;
    private HashSet<Character>  nonterminals;
    private char start;
    private Vector<Pair<Character, String>> productions;
    private HashMap<Character, HashSet<Character>> firstSet;
    private HashMap<Character, HashSet<Character>> followSet;


    public Grammar(char start){
       terminals = new HashSet<>();
       nonterminals = new HashSet<>();
       productions = new Vector<>();
       this.start = start;

        // Loading from txt.
        String url = System.getProperty("user.dir") + "/src/ds/config.txt";
        try {
            Scanner scanner = new Scanner(new FileInputStream(url));
            while (scanner.hasNextLine()) {
                String[] ss = scanner.nextLine().split(" ");
                char left = ss[0].charAt(0);


                // Initialize the non-terminals and terminals.
                nonterminals.add(left);
                for (char c: ss[1].toCharArray()) {
                    if (Character.isUpperCase(c))
                        nonterminals.add(c);
                    else if (!Character.isUpperCase(c) && c != 'e')
                        terminals.add(c);
                }

                // Construct the production.
                Pair<Character, String> production = new Pair<>(left, ss[1]);
                productions.add(production);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Compute the firstSet and followSet for this grammar.
        setFirstSet();
        setFollowSet();

    }


    public Vector<Pair<Character, String>> getProductions() {
        return productions;
    }

    public HashSet<Character> getTerminals() {
        return terminals;
    }



    public HashSet<Character> getNonTerminals() {
        return nonterminals;
    }


    public HashMap<Character, HashSet<Character>> getFirstSet() {
        return firstSet;
    }


    public HashMap<Character, HashSet<Character>> getFollowSet() {
        return followSet;
    }


    public char getStart() {
        return start;
    }


    private void setFirstSet() {
        // Initialize the firstSet.
        HashMap<Character, HashSet<Character>> firstSet = new HashMap<>();
        for (char c: nonterminals)
            firstSet.put(c, new HashSet<Character>());
        for (char c: terminals)
            firstSet.put(c, new HashSet<Character>());


        firstSet.put('e', new HashSet<Character>());
        firstSet.get('e').add('e');

        // Compute the firstSet of terminals.
        for (char c: terminals)
            firstSet.get(c).add(c);

        // Compute the firstSet of non-terminals.
        while (true) {
            int i = 0;
            for (HashSet<Character> set: firstSet.values())
                i += set.size();

            for (Pair<Character, String> production: productions) {
                // epsilon-production
                if (production.getRight().toCharArray()[0] == 'e')
                    firstSet.get(production.getLeft()).add('e');
                else {
                    for (char c: production.getRight().toCharArray()) {
                        HashSet<Character> firstOfC = firstSet.get(c);
                        firstSet.get(production.getLeft()).addAll(firstOfC);
                        if (!firstOfC.contains('e'))
                            break;
                    }
                }
            }

            int j = 0;
            for (HashSet<Character> set: firstSet.values())
                j+= set.size();
            if (i == j)
                break;
        }
        this.firstSet = firstSet;
    }


    /**
     * @return the firstSet for any string.
     */
    public HashSet<Character> getFirstSetOfString(String str) {
        HashSet<Character> firstSetOfString = new HashSet<>();
        for (char c: str.toCharArray()) {

            HashSet<Character> set = firstSet.get(c);
            firstSetOfString.addAll(set);
            firstSetOfString.remove('e');
            if (!set.contains('e'))
                break;
            firstSetOfString.add('e');
        }

        return firstSetOfString;
    }

    private void setFollowSet() {
        // Initialize the followSet
        HashMap<Character, HashSet<Character>> followSet = new HashMap<>();
        for (char c: nonterminals)
            followSet.put(c, new HashSet<Character>());

        followSet.get(start).add('$');

        // Compute the followSet.
        while (true) {
            int i = 0;
            for (HashSet<Character> set: followSet.values())
                i += set.size();


            for (Pair<Character, String> production: productions) {
                char[] right = production.getRight().toCharArray();

                for (int idx = 0; idx < right.length; idx++) {
                    char c = right[idx];
                    if (nonterminals.contains(c)) {
                        try {
                            char nc = right[idx+1];
                            followSet.get(c).addAll(firstSet.get(nc));
                            followSet.get(c).remove('e');



                            String str = "";
                            for (int j = idx+1; j < right.length; j++) {
                                str = str.concat(String.valueOf(right[j]));
                            }
                            if (getFirstSetOfString(str).contains('e'))
                                followSet.get(c).addAll(followSet.get(production.getLeft()));
                        } catch (IndexOutOfBoundsException ex) {
                            followSet.get(production.getLeft());
                            followSet.get(c).addAll(followSet.get(production.getLeft()));
                        }
                    }

                }

            }


            int j = 0;
            for (HashSet<Character> set: followSet.values())
                j+= set.size();
            if (i == j)
                break;
        }
        this.followSet = followSet;
    }


    public boolean isLL1() {
        for (char c: nonterminals) {
            // two distinct productions
            Vector<Pair<Character, String>> vector = new Vector<>();
            for (Pair<Character, String> production: productions) {
                if (production.getLeft() == c)
                    vector.add(production);
            }
            if (vector.size() == 2) {
                // Condition 1
                String str1 = vector.get(0).getRight();
                String str2 = vector.get(1).getRight();
                HashSet<Character> firstOfStr1 = getFirstSetOfString(str1);
                HashSet<Character> firstOfStr2 = getFirstSetOfString(str2);
                if (!Collections.disjoint(firstOfStr1, firstOfStr2)) {
                    return false;
                }
                // Condition 2
                if (firstOfStr1.contains('e') && !Collections.disjoint(followSet.get(c), firstOfStr2))
                    return false;
                if (firstOfStr2.contains('e') && !Collections.disjoint(followSet.get(c), firstOfStr1))
                    return false;
            }
        }

        return true;
    }




    /**
     * Compilers p213, Algorithm 4.19, Eliminating left recursion
     * Input grammar G with no cycles or epsilon-productions.
     */
    public void eliminateLeftRecursion() {

    }


    /**
     * Compilers p214, Algorithm 4.21, Left factoring a grammar
     */
    public void leftFactor() {

    }


    public static void main(String[] args) {
        Grammar grammar = new Grammar('S');
        System.out.println(grammar.isLL1());
    }
}



