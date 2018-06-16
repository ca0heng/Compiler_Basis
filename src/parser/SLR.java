package parser;

import ds.Grammar;
import ds.Pair;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;





public class SLR {

    enum action {
        shift,
        reduce,
        accept,
        error
    }




    private Grammar grammar;
    private HashMap<Pair<Integer, Character>, Pair<action, Integer>> actionTable;
    private HashMap<Pair<Integer, Character>, Integer> gotoTable;
    private Vector<Vector<Pair<Character, String>>> collection;

    public SLR(Grammar grammar) {
        this.grammar = grammar;
        computeCollection();
    }


    /**
     * Compilers, p245, Figure 4.32
     */
    private Vector<Pair<Character, String>> CLOSURE(
            Vector<Pair<Character, String>> items) {


        HashMap<Character, Boolean> added = new HashMap<>();
        for (char c: grammar.getNonTerminals())
            added.put(c, false);



        // Initialize the closure.
        Vector<Pair<Character, String>> closure = new Vector<>(items);



        // Computation of closure
        while (true) {
            int i = closure.size();

            for (Pair<Character, String> item: closure) {
                int idxOfDot = item.getRight().indexOf('.');
                if (idxOfDot < item.getRight().length()-1) {
                    char nc = item.getRight().toCharArray()[idxOfDot+1];

                    if (grammar.getNonTerminals().contains(nc) && !added.get(nc)) {
                        // each nc-production
                        for (Pair<Character, String> production: grammar.getProductions()) {
                            if (production.getLeft() == nc) {
                                String rightWithDot = ".".concat(production.getRight());
                                closure.add(new Pair<>(nc, rightWithDot));
                            }
                        }
                        added.put(nc, true);
                        break;
                    }
                }
            }

            int j = closure.size();
            if (i == j)
                break;
        }

        return closure;
    }


    /**
     * Compilers p246, Figure 4.33
     * Computing the canonical collection of sets of LR(0) items.
     */
    private void computeCollection() {
        Vector<Vector<Pair<Character, String>>> collection = new Vector<>();

        // I0
        Vector<Pair<Character, String>> I0 = new Vector<>();
        I0.add(new Pair<>('A', "." + String.valueOf(grammar.getStart())));
        collection.add(CLOSURE(I0));


        while (true) {
            int i = collection.size();
            Vector<Vector<Pair<Character, String>>> t = new Vector<>(collection);
            Vector<Character> symbols = new Vector<>();
            symbols.addAll(grammar.getNonTerminals());
            symbols.addAll(grammar.getTerminals());



            for (Vector<Pair<Character, String>> itemSet: t) {
                for (char symbol: symbols) {
                    Vector<Pair<Character, String>> vector = GOTO(itemSet, symbol);
                    if (!vector.isEmpty() && !collection.contains(vector))
                        collection.add(vector);
                }
            }
            int j = collection.size();
            if (i == j)
                break;
        }

        this.collection = collection;
    }



    /**
     * Compilers p246, GOTO function
     */
    private Vector<Pair<Character, String>> GOTO(Vector<Pair<Character, String>> set, char symbol) {

       Vector<Pair<Character, String>> itemSet = new Vector<>();

       for (Pair<Character, String> item: set) {
           int idxOfDot = item.getRight().indexOf('.');
           if (idxOfDot != item.getRight().length()-1) {
               char[] chars = item.getRight().toCharArray();
               if (chars[idxOfDot+1] == symbol) {
                   chars[idxOfDot] = chars[idxOfDot+1];
                   chars[idxOfDot+1] = '.';
                   itemSet.add(new Pair<>(item.getLeft(), String.valueOf(chars)));
               }
           }
       }

       return CLOSURE(itemSet);
     }





    /**
     * Compilers p253, Algorithm 4.46
     * Constructing an SLR-parsing table.
     */
    private void constructParsingTable() {
        HashMap<Pair<Integer, Character>, Pair<action, Integer>> actionTable = new HashMap<>();
        HashMap<Pair<Integer, Character>, Integer> gotoTable = new HashMap<>();

        for (Vector<Pair<Character, String>> itemSet: collection) {
            int i = collection.indexOf(itemSet);
            for (Pair<Character, String> item: itemSet) {
                int idxOfDot = item.getRight().indexOf('.');


                if (idxOfDot != item.getRight().length()-1) {

                    char c = item.getRight().toCharArray()[idxOfDot+1];
                    Vector<Pair<Character, String>> vector = GOTO(itemSet, c);
                    int j = collection.indexOf(vector);
                    // shift j
                    if (grammar.getTerminals().contains(c)) {
                        actionTable.put(new Pair<>(i, c), new Pair<>(action.shift, j));
                    } else {
                        // GOTO
                        gotoTable.put(new Pair<>(i, c), j);
                    }

                } else if (item.getLeft() != 'A'){
                    // reduce
                    for (char c: grammar.getFollowSet().get(item.getLeft())) {
                        actionTable.put(new Pair<>(i, c), new Pair<>(action.reduce, getIndexOfProduction(item)));
                    }
                } else {
                    // Accept
                    actionTable.put(new Pair<>(i, '$'), new Pair<>(action.accept, 0));
                }
            }
        }
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
    }



    private int getIndexOfProduction(Pair<Character, String> item) {
        String right = item.getRight().replace(".", "");
        Pair<Character, String> production = new Pair<>(item.getLeft(), right);
        return grammar.getProductions().indexOf(production);
    }


    /**
     * Compilers p250, Algorithm 4.44
     * LR-parsing algorithm
     * @param input is end with '$'.
     */
    public void parsing(String input) throws ParsingError {
        constructParsingTable();

        int idx = 0;
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        while (true) {
            char a = input.charAt(idx);

            int s = stack.peek();
            Pair<action, Integer> pair = actionTable.get(new Pair<>(s, a));
            if (pair.getLeft() == action.shift) {
                stack.push(pair.getRight());
                System.out.println("shift");
                idx++;
            } else if (pair.getLeft() == action.reduce) {
                Pair<Character, String> production = grammar.getProductions().get(pair.getRight());
                for (int i = 0; i < production.getRight().length(); i++)
                    stack.pop();

                int t = stack.peek();
                stack.push(gotoTable.get(new Pair<>(t, production.getLeft())));
                System.out.println("reduce by: " + production.getLeft() + " " + production.getRight());
            } else if (actionTable.get(new Pair<>(s, a)).getLeft() == action.accept) {
                System.out.println("accept");
                break;
            }
            else
                throw new ParsingError();
        }
    }



    public static void main(String[] args) throws ParsingError {
        SLR parser = new SLR(new Grammar('E'));
        parser.parsing("i*i+i$");
    }
}
