package automata;

import ds.Pair;
import java.util.*;

/**
 * A NFA object represents a Nondeterministic finite automaton.
 */
public class NFA extends FA {
    private HashMap<Pair<Integer, Character>, Vector<Integer>> transition;

    public NFA() {
        super();
        transition = new HashMap<>();
    }




    public DFA getDFA() {

    }


    /**
     * @return the set of NFA states reachable from NFA state s on epsilon-transitions alone.
     */
    private Vector<Integer> getEpsilonClosure(int s) {

    }

    /**
     * @return the set of NFA states reachable from some NFA state s in set states
     * on epsilon-transitions alone.
     */
    private Vector<Integer> getEpsilonClosure(Vector<Integer> states) {

    }
}