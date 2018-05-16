package ds;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;

/**
 * A Grammar Object represents a context-free grammar which contains terminals,
 * non-terminals and productions.
 */
public class Grammar {
    private Vector<Character> terminals;
    private Vector<Character>  nonterminals;
    private char start;
    private Vector<Pair<Character, LinkedList<Character>>> productions;

    public Grammar(char start){
       terminals = new Vector<>();
       nonterminals = new Vector<>();
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
                if (!nonterminals.contains(left))
                    nonterminals.add(left);
                for (char c: right) {
                    if (Character.isUpperCase(c) && !nonterminals.contains(c))
                        nonterminals.add(c);
                    if (Character.isLowerCase(c) && !terminals.contains(c))
                        terminals.add(c);
                }

                // Construct the production.
                Pair<Character, LinkedList<Character>> production = new Pair<>(left, new LinkedList<Character>());


                if (!nonterminals.contains(left))
                    nonterminals.add(left);
                line = line.substring(2);
                ss = line.split(" ");
                Vector<LinkedList<Character>> v = new Vector<>();
                for (String right : ss) {
                    char[] chars = right.toCharArray();
                    LinkedList<Character> l = new LinkedList<>();
                    for (char c : chars) {

                        if (Character.isUpperCase(c) && !nonterminals.contains(c))
                            nonterminals.add(c);
                        if (Character.isLowerCase(c) && !terminals.contains(c))
                            terminals.add(c);
                        l.add(c);
                    }
                    v.add(l);
                }
                productions.put(left, v);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loading from xml file.
        /*
            code
         */
    }


    private HashMap<Pair<Character, Character>, Pair<Character, LinkedList<Character>>> getParsingTable() {

    }

}

