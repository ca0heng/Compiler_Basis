package parser;

import ds.Grammar;
import ds.Pair;

import java.util.HashMap;
import java.util.Stack;

public class PredictiveParser {
    private Grammar grammar;
    private HashMap<Pair<Character, Character>, Pair<Character, String>> parsingTable;

    public PredictiveParser(Grammar grammar) {
        this.grammar = grammar;
        constructParsingTable();
    }


    /**
     * Construction of a predictive parsing table
     * Compilers, p224, Algorithm 4.31
      */
    private void constructParsingTable() {
        HashMap<Pair<Character, Character>, Pair<Character, String>> parsingTable = new HashMap<>();
        for (Pair<Character, String> production: grammar.getProductions()) {
            String str = "";
            for (char c: production.getRight().toCharArray())
                str = str.concat(String.valueOf(c));

            for (char c: grammar.getFirstSetOfString(str)) {
                if (grammar.getTerminals().contains(c))
                    parsingTable.put(new Pair<>(production.getLeft(), c), production);
            }

            if (grammar.getFirstSetOfString(str).contains('e'))
                for (char c: grammar.getFollowSet().get(production.getLeft()))
                    if (!grammar.getNonTerminals().contains(c))
                        parsingTable.put(new Pair<>(production.getLeft(), c), production);
        }

        this.parsingTable = parsingTable;
    }


    /**
     * Compilers, p226, Algorithm 4.34
     * @param input is end with '$'.
     */
    public void parsing(String input) throws ParsingError {
        // stack
        Stack<Character> stack = new Stack<>();
        stack.push('$');
        stack.push(grammar.getStart());

        // Table-driven predictive parsing
        char[] chars = input.toCharArray();
        int idx = 0;
        char a = chars[idx];
        char x = stack.peek();
        while (x != '$') {
            if (x == a) {
                stack.pop();
                // Let a be the next symbol of input
                if (idx < chars.length - 1)
                    a = chars[++idx];
            } else if (grammar.getTerminals().contains(x)) {
                throw new ParsingError();
            } else if (!parsingTable.containsKey(new Pair<>(x, a))) {
                throw new ParsingError();
            } else {
                // output
                Pair<Character, String> production = parsingTable.get(new Pair<>(x, a));
                System.out.println(production.getLeft() + " " + production.getRight());
                stack.pop();


                // Push the production onto the stack.
                if (production.getRight().toCharArray()[0] != 'e')
                    for (int i = production.getRight().length() - 1; i >= 0; i--)
                        stack.push(production.getRight().toCharArray()[i]);

            }

            x = stack.peek();
        }
    }



    public static void main(String[] args) throws ParsingError {
        PredictiveParser parser = new PredictiveParser(new Grammar('E'));
        parser.parsing("i+i*i$");

    }
}


