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
    private HashMap<Pair<Character, Character>, Pair<Character, LinkedList<Character>>> parsingTable;

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
                    else if (!Character.isUpperCase(c))
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

        // Construct a predictive parsing table.
        constructParsingTable();
    }


    private void setFirstSet() {
        // Initialize the firstSet.
        HashMap<Character, HashSet<Character>> firstSet = new HashMap<>();
        for (char c: nonterminals)
            firstSet.put(c, new HashSet<Character>());
        for (char c: terminals)
            firstSet.put(c, new HashSet<Character>());

        // Compute the firstSet of terminals.
        for (char c: terminals)
            firstSet.get(c).add(c);

        // Compute the firstSet of non-terminals.
        while (true) {
            int i = 0;
            for (HashSet<Character> set: firstSet.values())
                i += set.size();

            for (Pair<Character, LinkedList<Character>> production: productions) {
                for (char c: production.getRight()) {
                    HashSet<Character> firstOfC = firstSet.get(c);
                    firstSet.get(production.getLeft()).addAll(firstOfC);
                    if (!firstOfC.contains('e'))
                        break;
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
    private HashSet<Character> getFirstSetOfString(String str) {
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
                LinkedList<Character> list = production.getRight();

                for (int m = 0; m < list.size(); m++) {
                    char a = list.get(m);
                    if (nonterminals.contains(a)) {
                        try {
                            char b = list.get(m+1);
                            followSet.get(a).addAll(firstSet.get(b));
                            followSet.get(a).remove('e');

                            String str = "";
                            for (int n = m+1; n < list.size(); n++)
                                str = str.concat(String.valueOf(list.get(n)));
                            if (getFirstSetOfString(str).contains('e'))
                                followSet.get(a).addAll(followSet.get(production.getLeft()));

                        } catch (IndexOutOfBoundsException e) {
                            followSet.get(production.getLeft());
                            followSet.get(a).addAll(followSet.get(production.getLeft()));
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


    private void constructParsingTable() {
        HashMap<Pair<Character, Character>, Pair<Character, LinkedList<Character>>> parsingTable = new HashMap<>();
        for (Pair<Character, LinkedList<Character>> production: productions) {
            String str = "";
            for (char c: production.getRight())
                str = str.concat(String.valueOf(c));
            for (char c: getFirstSetOfString(str)) {
                if (terminals.contains(c) && c != 'e')
                    parsingTable.put(new Pair<>(production.getLeft(), c), production);
            }

            if (getFirstSetOfString(str).contains('e'))
                for (char c: followSet.get(production.getLeft()))
                    if (!nonterminals.contains(c))
                        parsingTable.put(new Pair<>(production.getLeft(), c), production);

        }
        this.parsingTable = parsingTable;
    }

    public static void main(String[] args) {
        Grammar grammar = new Grammar('E');
        System.out.println();
    }


}



