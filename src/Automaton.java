import javafx.util.Pair;

import java.util.*;

/**
 * Represents a finite automaton.
 *
 * @author Robin Machiels
 * @author Reinaert Van de Cruys
 */
public class Automaton {
    private static final String ARC = "A";
    private static final String DRAGON = "D";
    private static final String GATE = "G";
    private static final String KEY = "K";
    private static final String RIVER = "R";
    private static final String SWORD = "S";
    private static final String TREASURE = "T";

    /**
     * the alphabet containing all symbols, indexed by their textual representation
     */
    private static final Map<String, Symbol> ALPHABET = new HashMap<String, Symbol>() {{
        put(ARC, Symbol.getSymbol(ARC));
        put(DRAGON, Symbol.getSymbol(DRAGON));
        put(GATE, Symbol.getSymbol(GATE));
        put(KEY, Symbol.getSymbol(KEY));
        put(RIVER, Symbol.getSymbol(RIVER));
        put(SWORD, Symbol.getSymbol(SWORD));
        put(TREASURE, Symbol.getSymbol(TREASURE));
    }};
    /**
     * the set of states of this automaton
     */
    private final Set<State> mStates = new HashSet<>();
    /**
     * the start state of this automaton
     */
    private State mStartState = null;

    /**
     * Creates an automaton without any states (and thus invalid, since it has no start state). Use the automaton
     * builder to create a valid automaton.
     */
    private Automaton() {
    }

    /**
     * Converts the given string to its corresponding symbol.
     *
     * @param string the string representation to get the symbol for
     * @return the requested symbol
     * @throws IllegalArgumentException if no symbol exists matching the given string
     */
    private static Symbol toSymbol(String string) throws IllegalArgumentException {
        if (string == null) {
            return null;
        }
        Symbol symbol = ALPHABET.get(string);
        if (symbol == null) {
            throw new IllegalArgumentException();
        }
        return symbol;
    }

    /**
     * Returns the shortest string that is (not) accepted by this automaton.
     *
     * @param accept <code>true</code> to return the shortest accepted string, <code>false</code> to return the
     *               shortest string that is not accepted
     * @return the requested string, or a null pointer if no such string exists
     */
    public String getShortestExample(boolean accept) {
        return new PathFinder(this).getShortestExample(accept);
    }

    /**
     * Returns an automaton accepting the intersection of the languages accepted by this and the given automaton.
     *
     * @param aut the automaton accepting the language to take the intersection with
     * @return an automaton accepting the intersection of the languages accepted by this and the given automaton
     */
    public Automaton intersection(Automaton aut) {
        return new IntersectionTaker(this).intersection(aut);
    }

    /**
     * Builds an automaton.
     */
    public static class Builder {
        /**
         * the automaton that is created by this builder
         */
        private final Automaton mResult = new Automaton();
        /**
         * the states of the automaton created by this builder, indexed by their name
         */
        private final Map<String, State> mNameToState = new HashMap<>();
        /**
         * whether the result of this builder has successfully been requested and thus the building is finished
         */
        private boolean mIsFinished = false;

        /**
         * Adds a state with the given name as an accept state to the automaton.
         *
         * @param name the name of the accept state
         * @throws IllegalStateException    if the result of this builder has already been successfully requested
         * @throws NullPointerException     if <code>name</code> is a null pointer
         * @throws IllegalArgumentException if <code>name</code> is an empty string
         */
        public void addAcceptState(String name) throws IllegalStateException, NullPointerException,
                IllegalArgumentException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            State state = getStateAndAddIfNotFound(name);
            state.setAcceptState(true);
        }

        /**
         * Adds a transition between the given states on the given symbol.
         *
         * @param sourceName      the state from which the transition departs
         * @param destinationName the state in which the transition arrives
         * @param symbol          the symbol allowing the transition, or a null pointer to transition without any symbol
         * @throws IllegalStateException    if the result of this builder has already been successfully requested
         * @throws NullPointerException     if <code>sourceName</code> or <code>destinationName</code> is a null pointer
         * @throws IllegalArgumentException if any of the parameters is invalid
         */
        public void addTransition(String sourceName, String destinationName, String symbol) throws
                IllegalStateException, NullPointerException, IllegalArgumentException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            State source = getStateAndAddIfNotFound(sourceName);
            State destination = getStateAndAddIfNotFound(destinationName);
            source.addDepartingTransition(destination, toSymbol(symbol));
        }

        /**
         * Returns the automaton built by this builder and blocks any further building.
         *
         * @return the automaton built by this builder
         * @throws IllegalStateException if the automaton has no start state yet
         */
        public Automaton getResult() throws IllegalStateException {
            if (mResult.mStartState == null) {
                throw new IllegalStateException();
            }
            mIsFinished = true;
            return mResult;
        }

        /**
         * Returns the state with the given name, adding it first if it is not found.
         *
         * @param name name of the state to get
         * @return the state with the given name
         * @throws NullPointerException     if <code>name</code> is a null pointer
         * @throws IllegalArgumentException if <code>name</code> is an empty string
         */
        private State getStateAndAddIfNotFound(String name) throws NullPointerException, IllegalArgumentException {
            if (name == null) {
                throw new NullPointerException();
            }
            State state = mNameToState.get(name);
            if (state == null) {
                state = new State();
                mNameToState.put(name, state);
                mResult.mStates.add(state);
            }
            return state;
        }

        /**
         * Makes the state with the given name the start state of the automaton.
         *
         * @param name the name of the start state
         * @throws IllegalStateException    if the result of this builder has already been successfully requested
         * @throws NullPointerException     if <code>name</code> is a null pointer
         * @throws IllegalArgumentException if <code>name</code> is not a valid state name
         */
        public void setStartState(String name) throws IllegalStateException, NullPointerException,
                IllegalArgumentException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            mResult.mStartState = getStateAndAddIfNotFound(name);
        }
    }

    private static class IntersectionTaker {
        /**
         * the automaton on which this intersection taker works
         */
        private final Automaton mAutomaton;
        /**
         * the automaton with which the intersection is taken
         */
        private Automaton mOther;
        /**
         * the automaton containing the result of this intersection taker
         */
        private Automaton mIntersection;
        /**
         * the states of the intersection, indexed by the two states from which they are created
         */
        private Map<State, Map<State, State>> mOldStatesToNewState;

        /**
         * Creates a new intersection taker that works on the given automaton.
         *
         * @param aut the automaton to work on
         * @throws NullPointerException if <code>aut</code> is a null pointer
         */
        public IntersectionTaker(Automaton aut) throws NullPointerException {
            mAutomaton = aut;
        }

        /**
         * Creates the states of the cross product in the intersection automaton.
         */
        private void createCrossProductStates() {
            for (State thisState : mAutomaton.mStates) {
                Map<State, State> map = new HashMap<>();
                mOldStatesToNewState.put(thisState, map);
                for (State otherState : mOther.mStates) {
                    State newState = new State(thisState, otherState);
                    map.put(otherState, newState);
                    mIntersection.mStates.add(newState);
                }
            }
        }

        /**
         * Creates the transitions of the cross product in the intersection automaton.
         */
        private void createCrossProductTransitions() {
            // create the transitions of the cross product in the new automaton
            for (State thisState : mAutomaton.mStates) {
                for (State otherState : mOther.mStates) {
                    State newState = mOldStatesToNewState.get(thisState).get(otherState);
                    // create the transitions on all symbols in the alphabet
                    createCrossProductSymbolTransitions(newState, thisState, otherState);
                    // create the epsilon transitions
                    createCrossProductEpsilonTransitions(newState, thisState, otherState);
                    createCrossProductEpsilonTransitions(newState, otherState, thisState);
                }
            }
        }

        private void createCrossProductSymbolTransitions(State newState, State state1, State state2) {
            for (Symbol symbol : ALPHABET.values()) {
                for (State thisReachableState : state1.getTransitions(symbol)) {
                    for (State otherReachableState : state2.getTransitions(symbol)) {
                        newState.addDepartingTransition(
                                mOldStatesToNewState.get(thisReachableState).get(otherReachableState), symbol);
                    }
                }
            }
        }

        private void createCrossProductEpsilonTransitions(State newState, State state1, State state2) {
            for (State thisReachableState : state1.getTransitions(null)) {
                newState.addDepartingTransition(mOldStatesToNewState.get(thisReachableState).get(state2), null);
            }
        }

        /**
         * Returns an automaton accepting the intersection of the languages accepted by the given automaton and the
         * automaton on which this intersection taker is working.
         *
         * @param aut the automaton accepting the language to take the intersection with
         * @return an automaton accepting the intersection of the languages
         */
        public Automaton intersection(Automaton aut) {
            if (aut == null) {
                return null;
            }
            mOther = aut;
            mOldStatesToNewState = new HashMap<>();
            mIntersection = new Automaton();
            // create the cross product in the intersection automaton
            createCrossProductStates();
            createCrossProductTransitions();
            // set the start state of the intersection automaton
            mIntersection.mStartState = mOldStatesToNewState.get(mAutomaton.mStartState).get(aut.mStartState);
            // return the finished automaton
            return mIntersection;
        }
    }

    /**
     * Finds the shortest string that is (not) accepted by an automaton.
     */
    private static class PathFinder {
        /**
         * the automaton on which this path finder works
         */
        private final Automaton mAutomaton;
        /**
         * the set of states that have been visited during the current trace
         */
        private Set<State> mReachedStates;
        /**
         * the queue of states to be checked next during the current trace
         */
        private Queue<Pair<State, String>> mStatesToCheck;

        /**
         * Creates a new path finder that works on the given automaton.
         *
         * @param aut the automaton to work on
         * @throws NullPointerException if <code>aut</code> is a null pointer
         */
        public PathFinder(Automaton aut) throws NullPointerException {
            mAutomaton = aut;
        }

        /**
         * Checks whether the given collection of states contains at least one state that is (not) an accept state.
         *
         * @param states the states to check
         * @param accept <code>true</code> to check if at least one accept state is present, <code>false</code> to look
         *               for a state that is not an accept state
         * @return <code>true</code> if the given collection of states contains at least one of the requested kind of
         * state, <code>false</code> otherwise
         */
        private static boolean containsState(Collection<State> states, boolean accept) {
            for (State currentState : states) {
                if (currentState.isAcceptState() == accept) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the shortest string that is (not) accepted by the automaton.
         *
         * @param accept <code>true</code> to return the shortest accepted string, <code>false</code> to return the
         *               shortest string that is not accepted
         * @return the requested string, or a null pointer if no such string exists
         */
        public String getShortestExample(boolean accept) {
            mReachedStates = new HashSet<>();
            mStatesToCheck = new LinkedList<>();
            // start on the start state with an empty string
            mReachedStates.add(mAutomaton.mStartState);
            mStatesToCheck.add(new Pair<>(mAutomaton.mStartState, ""));
            // keep taking states from the front of the queue until it is empty
            while (!mStatesToCheck.isEmpty()) {
                Pair<State, String> currentPair = mStatesToCheck.remove();
                // if the epsilon closure of the current state contains the kind of state we're looking for, return the
                // current string
                if (checkEpsilonClosure(currentPair.getKey(), currentPair.getValue(), accept)) {
                    return currentPair.getValue();
                }
            }
            // no matching state was found, return null
            return null;
        }

        /**
         * Checks whether the epsilon closure of the given state contains the given kind of state, and if not, adds all
         * states directly reachable to the queue of states to be checked if they haven't been checked already.
         *
         * @param state         the state whose epsilon closure to analyze
         * @param currentString the string used to reach the given state
         * @param accept        <code>true</code> to look for an accept state, <code>false</code> to look for a state
         *                      that is not an accept state
         * @return <code>true</code> if the epsilon closure of the given state contains at least one state of the given
         * kinde, <code>false</code> otherwise
         */
        private boolean checkEpsilonClosure(State state, String currentString, boolean accept) {
            // if we can reach the kind of state we're looking for using only epsilon transitions, return true
            Set<State> epsilonClosure = state.getEpsilonClosure();
            if (containsState(epsilonClosure, accept)) {
                return true;
            }
            addToQueueIfNotYetReached(epsilonClosure, currentString);
            // otherwise, add all states directly reachable from the current state to the list of states to be checked
            for (Symbol currentSymbol : ALPHABET.values()) {
                String newString = currentString + currentSymbol;
                // add the symbol used to make the transition to the current string when storing it in the queue
                addToQueueIfNotYetReached(state.getTransitions(currentSymbol), newString);
            }
            // the given state or its epsilon closure did not contain the kind of state we're looking for, return false
            return false;
        }

        /**
         * For every state in the given collection, add it to the queue of states to be checked, together with the
         * given string, if the state has not yet been reached.
         *
         * @param states the collection of states to add to the queue of states to check
         * @param string the string used to get to the states
         */
        private void addToQueueIfNotYetReached(Collection<State> states, String string) {
            for (State currentState : states) {
                if (!mReachedStates.contains(currentState)) {
                    mReachedStates.add(currentState);
                    mStatesToCheck.add(new Pair<>(currentState, string));
                }
            }
        }
    }

    /**
     * Represents a state of an automaton.
     */
    private static class State {
        /**
         * a set of symbols mapped to the set of states they enable the transition to from this state
         */
        private final Map<Symbol, Set<State>> mDepartingTransitions = new HashMap<>();
        /**
         * whether this state is an accept state or not
         */
        private boolean mIsAcceptState = false;

        /**
         * Creates a state that is not an accept state.
         */
        public State() {
        }

        /**
         * Creates a new state from the given pair of states.
         *
         * @param state1 one of the states to merge
         * @param state2 the other state to merge
         * @throws NullPointerException if any of the parameters is a null pointer
         */
        public State(State state1, State state2) throws NullPointerException {
            // the new state is an accept state if both of the given states are accept states
            setAcceptState(state1.isAcceptState() && state2.isAcceptState());
        }

        /**
         * Adds a departing transition from this state to the given destination on the given symbol.
         *
         * @param destination the state at which the transition arrives
         * @param symbol      the symbol allowing the transition, or a null pointer to transition without any symbol
         * @throws NullPointerException if <code>destination</code> is a null pointer
         */
        public void addDepartingTransition(State destination, Symbol symbol) throws NullPointerException {
            if (destination == null) {
                throw new NullPointerException();
            }
            Set<State> states = mDepartingTransitions.get(symbol);
            if (states == null) {
                states = new HashSet<>();
                mDepartingTransitions.put(symbol, states);
            }
            states.add(destination);
        }

        /**
         * Returns the set of states reachable from this state using only epsilon transitions (which always includes
         * this state itself).
         *
         * @return the set of states reachable from this state using only epsilon transitions
         */
        public Set<State> getEpsilonClosure() {
            Set<State> epsilonClosure = new HashSet<>();
            Queue<State> statesToCheck = new LinkedList<>();
            // start with this state
            statesToCheck.add(this);
            // keep taking states from the front of the queue until it is empty and add them to the epsilon closure
            while (!statesToCheck.isEmpty()) {
                State currentState = statesToCheck.remove();
                epsilonClosure.add(currentState);
                // for every state reachable via an epsilon transition, if it hasn't been visited yet, add it to the
                // queue of states to be checked
                for (State currentReachableState : currentState.getTransitions(null)) {
                    if (!epsilonClosure.contains(currentReachableState)) {
                        statesToCheck.add(currentReachableState);
                    }
                }
            }
            // return the finished epsilon closure
            return epsilonClosure;
        }

        /**
         * Returns a set of all the states reachable from this state on the given symbol
         *
         * @param symbol the transition symbol, or null for transitions allowed without any symbol
         * @return a set of all the states reachable from this state on the given symbol
         */
        public Set<State> getTransitions(Symbol symbol) {
            Set<State> result = mDepartingTransitions.get(symbol);
            if (result == null) {
                result = new HashSet<>();
            }
            return result;
        }

        /**
         * Returns whether this state is an accept state.
         *
         * @return <code>true</code> if this state is an accept state, <code>false</code> otherwise
         */
        public boolean isAcceptState() {
            return mIsAcceptState;
        }

        /**
         * Sets whether this state is an accept state.
         *
         * @param value <code>true</code> to make this state an accept state, <code>false</code> otherwise
         */
        public void setAcceptState(boolean value) {
            mIsAcceptState = value;
        }
    }

    /**
     * Represents a symbol in the alphabet of an automaton.
     */
    private static final class Symbol {
        /**
         * a set of all created symbols, indexed by their textual representation
         */
        private static final Map<String, Symbol> mSymbols = new HashMap<>();
        /**
         * the textual representation of this symbol
         */
        private final String mString;

        /**
         * Creates a symbol with the given textual representation.
         *
         * @param string the textual representation of the symbol
         */
        private Symbol(String string) {
            mString = string;
        }

        /**
         * Returns a symbol with the given textual representation.
         *
         * @param string the textual representation of the symbol
         * @throws NullPointerException if <code>string</code> is a null pointer
         */
        public static Symbol getSymbol(String string) throws NullPointerException {
            if (string == null) {
                throw new NullPointerException();
            }
            Symbol symbol = mSymbols.get(string);
            if (symbol == null) {
                symbol = new Symbol(string);
                mSymbols.put(string, symbol);
            }
            return symbol;
        }

        @Override
        public String toString() {
            return mString;
        }
    }
}
