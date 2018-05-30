package parser;

import ds.Grammar;
import ds.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

public class SLR {
    private Grammar grammar;

    public SLR(Grammar grammar) {
        this.grammar = grammar;
    }


    /**
     * Compilers, p245, Figure 4.32
     */
    public Vector<Pair<Character, LinkedList<Character>>> getClosureOfItem(
            Vector<Pair<Character, LinkedList<Character>>> items) {


        HashMap<Character, Boolean> added = new HashMap<>();
        for (char c: grammar.getNonTerminals())
            added.put(c, false);



        // Initialize the closure.
        Vector<Pair<Character, LinkedList<Character>>> closure = new Vector<>(items);

        // Computation of closure
        while (true) {
            int i = closure.size();

            for (Pair<Character, LinkedList<Character>> item: closure) {
                int idxOfDot = item.getRight().indexOf('.');
                if (idxOfDot < item.getRight().size()-1) {
                    char nc = item.getRight().get(idxOfDot+1);
                    if (grammar.getNonTerminals().contains(nc) && !added.get(nc)) {
                        // each nc-production
                        for (Pair<Character, LinkedList<Character>> production: grammar.getProductions()) {
                            if (production.getLeft() == nc) {
                                 LinkedList<Character> rightWithDot = new LinkedList<>(production.getRight());
                                 rightWithDot.addFirst('.');
                                 closure.add(new Pair<>(nc, rightWithDot));
                            }
                        }
                        added.put(nc, true);
                    }
                }
            }

            int j = closure.size();
            if (i == j)
                break;
        }

        return closure;
    }

    public static void main(String[] args) {
        SLR parser = new SLR(new Grammar('E'));


        Vector<Pair<Character, LinkedList<Character>>> items = new Vector<>();
        LinkedList<Character> linkedList = new LinkedList<>();
        linkedList.add('(');
        linkedList.add('.');
        linkedList.add(('E'));
        linkedList.add(')');
        items.add(new Pair<>('F', linkedList));
        Vector<Pair<Character, LinkedList<Character>>> closure = parser.getClosureOfItem(items);
        System.out.println();
    }

}
