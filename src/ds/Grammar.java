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
    private Vector<Pair<Character, LinkedList<Character>>> productions;
    private HashMap<Character, HashSet<Character>> firstSet;
    private HashMap<Character, HashSet<Character>> followSet;


    public Grammar(char start){
       terminals = new HashSet<>();
       nonterminals = new HashSet<>();
       productions = new Vector<>();
       this.start = start;

        // Loading from txt.
        String url = System.getProperty("user.dir") + "/src/ds/init-grammar";
        try {
            Scanner scanner = new Scanner(new FileInputStream(url));
            while (scanner.hasNextLine()) {
                String[] ss = scanner.nextLine().split(" ");
                char left = ss[0].charAt(0);
                char[] right = ss[1].toCharArray();

                // Initialize the non-terminals and terminals.
                nonterminals.add(left);
                for (char c: right) {
                    if (Character.isUpperCase(c) && !nonterminals.contains(c))
                        nonterminals.add(c);
                    else if (!Character.isUpperCase(c) && c != 'e')
                        terminals.add(c);
                }

                // Construct the production.
                LinkedList<Character> list = new LinkedList<>();
                for (char c: right)
                    list.add(c);
                Pair<Character, LinkedList<Character>> production = new Pair<>(left, list);
                productions.add(production);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Compute the firstSet and followSet for this grammar.
        setFirstSet();
        setFollowSet();
//
//        // Construct a predictive parsing table.
//        constructParsingTable();
    }


    public Vector<Pair<Character, LinkedList<Character>>> getProductions() {
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

            for (Pair<Character, LinkedList<Character>> production: productions) {
                // epsilon-production
                if (production.getRight().getFirst() == 'e')
                    firstSet.get(production.getLeft()).add('e');
                else {
                    for (char c: production.getRight()) {
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


            for (Pair<Character, LinkedList<Character>> production: productions) {
                LinkedList<Character> right = production.getRight();

                for (int idx = 0; idx < right.size(); idx++) {
                    char c = right.get(idx);
                    if (nonterminals.contains(c)) {
                        try {
                            char nc = right.get(idx+1);
                            followSet.get(c).addAll(firstSet.get(nc));
                            followSet.get(c).remove('e');


                            String str = "";
                            for (int j = idx+1; j < right.size(); j++) {
                                str = str.concat(String.valueOf(right.get(j)));
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




}



