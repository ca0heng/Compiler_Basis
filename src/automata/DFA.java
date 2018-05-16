package automata;


import ds.Pair;
import java.util.HashMap;

/**
 * A DFA object represents Deterministic finite automaton.
 */
public class DFA extends FA{
    private HashMap<Pair<Integer, Character>, Integer> transition;

    public DFA() {
        super();
        transition = new HashMap<>();
    }




    /**
     *
     * @param str an input string
     * @return true if this DFA accepts str, otherwise return false.
     */
    public boolean simulate(String str) {
        int s = start;
        for (char c: str.toCharArray()) {
            try {
                s = move(s, c);
            } catch (TransitionNotFoundException e) {
                e.printStackTrace();
            }
        }
        return finals.contains(s);
    }



    /**
     * Give the state to which there is an edge from state s on input c.
     */
    public int move(int s, char c) throws TransitionNotFoundException{
        Pair pair =  new Pair<>(s, c);
        if (!transition.containsKey(pair)) {
            throw new TransitionNotFoundException();
        } else {
            return transition.get(pair);
        }
    }


    private class TransitionNotFoundException extends Exception {
    }

}
