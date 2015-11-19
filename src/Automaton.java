import java.util.*;

/**
 * Represents a finite automaton.
 *
 * @author Robin Machiels
 * @author Reinaert Van de Cruys
 */
public class Automaton {
    public static final String ARC = "A";
    public static final String DRAGON = "D";
    public static final String GATE = "G";
    public static final String KEY = "K";
    public static final String RIVER = "R";
    public static final String SWORD = "S";
    public static final String TREASURE = "T";

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
     * the set of states of this automaton, indexed by their name
     */
    private final Map<String, State> mStates = new HashMap<>();
    /**
     * whether this automaton is a DFA or an NFA
     */
    private boolean mIsDFA = false;
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
     * Creates a copy of the given automaton.
     *
     * @param other the automaton to copy
     */
    private Automaton(Automaton other) {
        mStartState = includeAutomaton(other, "");
        mIsDFA = other.mIsDFA;
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
     * Creates a copy of the given automaton in this automaton, prefixing the name of every state with the given prefix.
     *
     * @param other       the automaton to copy
     * @param statePrefix the prefix to add to every state imported from the given automaton
     * @return the start state of the included automaton (which is not marked as the start state of this automaton)
     */
    private State includeAutomaton(Automaton other, String statePrefix) {
        Map<State, State> otherStateToOwnState = new HashMap<>();
        int stateName = 0;
        for (Map.Entry<String, State> currentOtherStatesEntry : other.mStates.entrySet()) {
            String currentOwnStateName = statePrefix + stateName;
            State currentOwnState = new State(currentOwnStateName, currentOtherStatesEntry.getValue());
            mStates.put(currentOwnStateName, currentOwnState);
            otherStateToOwnState.put(currentOtherStatesEntry.getValue(), currentOwnState);
            stateName++;
        }
        for (State currentOtherState : other.mStates.values()) {
            State currentOwnState = otherStateToOwnState.get(currentOtherState);
            for (Map.Entry<Symbol, Set<State>> currentSymbolEntry :
                    currentOtherState.mDepartingTransitions.entrySet()) {
                Symbol currentSymbol = currentSymbolEntry.getKey();
                for (State currentDestination : currentSymbolEntry.getValue()) {
                    currentOwnState.addDepartingTransition(otherStateToOwnState.get(currentDestination), currentSymbol);
                }
            }
        }
        return otherStateToOwnState.get(other.mStartState);
    }

    /**
     * Returns an automaton accepting the complement of the language accepted by this automaton.
     *
     * @return an automaton accepting the complement of the language accepted by this automaton
     */
    public Automaton complement() {
        if (!mIsDFA) {
            return toDFA().complement();
        }
        Automaton complement = new Automaton(this);
        for (State currentState : complement.mStates.values()) {
            currentState.setAcceptState(!currentState.isAcceptState());
        }
        return complement;
    }

    /**
     * Returns the shortest string that is (not) accepted by this automaton.
     *
     * @param accept <code>true</code> to return the shortest accepted string, <code>false</code> to return the
     *               shortest string that is not accepted
     * @return the requested string, or a null pointer if no such string exists
     */
    public String getShortestExample(Boolean accept) {
        toDFA();
        Set<State> reachedStates = new HashSet<>();
        Queue<State> statesToBeChecked = new LinkedList<>();
        Queue<String> stringsMatchingStatesToBeChecked = new LinkedList<>();
        statesToBeChecked.add(mStartState);
        stringsMatchingStatesToBeChecked.add("");
        while (!statesToBeChecked.isEmpty()) {
            State currentState = statesToBeChecked.remove();
            String currentString = stringsMatchingStatesToBeChecked.remove();
            if (currentState.isAcceptState() == accept) {
                return currentString;
            }
            reachedStates.add(currentState);
            for (Symbol currentSymbol : ALPHABET.values()) {
                State currentReachableState = currentState.getTransitions(currentSymbol).toArray(new State[1])[0];
                if (!reachedStates.contains(currentReachableState)) {
                    statesToBeChecked.add(currentReachableState);
                    stringsMatchingStatesToBeChecked.add(currentString + currentSymbol.toString());
                }
            }
        }
        return null;
    }

    /**
     * Returns an automaton accepting the intersection of the languages accepted by this and the given automaton.
     *
     * @param aut the automaton accepting the language to take the intersection with
     * @return an automaton accepting the intersection of the languages accepted by this and the given automaton
     */
    public Automaton intersection(Automaton aut) {
        if (aut == null) {
            return null;
        }
        return (this.complement().union(aut.complement()).complement());
    }

    /**
     * Prints out this automaton.
     */
    private void print() {
        System.out.println("+++  (START)  " + mStartState.getName() + "  +++");
        for (State currentState : mStates.values()) {
            System.out.print(currentState.getName() + "  ");
            if (currentState.isAcceptState()) {
                System.out.print("(FINAL)");
            }
            System.out.print("\n    $  ->  ");
            Set<State> currentReachableStates = currentState.getTransitions(null);
            if (currentReachableStates != null) {
                for (State currentReachableState : currentReachableStates) {
                    System.out.print(currentReachableState.getName() + " ");
                }
            }
            System.out.println();
            for (Symbol currentSymbol : ALPHABET.values()) {
                System.out.print("    " + currentSymbol + "  ->  ");
                currentReachableStates = currentState.getTransitions(currentSymbol);
                if (currentReachableStates != null) {
                    for (State currentReachableState : currentReachableStates) {
                        System.out.print(currentReachableState.getName() + " ");
                    }
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    /**
     * Returns a DFA equivalent to (accepting the same language as) this automaton.
     *
     * @return a DFA equivalent to this automaton
     */
    private Automaton toDFA() {
        return new ToDFAConverter().run(this);
    }

    /**
     * Returns an automaton accepting the union of the languages accepted by this and the given automaton.
     *
     * @param aut the automaton accepting the language to take the union with
     * @return an automaton accepting the union of the languages accepted by this and the given automaton
     */
    public Automaton union(Automaton aut) {
        if (aut == null) {
            return null;
        }
        String thisStatesPrefix = "a";
        String autStatesPrefix = "b";
        String newStartStateName = "s";
        Automaton union = new Automaton();
        State thisStartState = union.includeAutomaton(this, thisStatesPrefix);
        State autStartState = union.includeAutomaton(aut, autStatesPrefix);
        State newStartState = new State(newStartStateName);
        union.mStates.put(newStartStateName, newStartState);
        newStartState.addDepartingTransition(thisStartState, null);
        newStartState.addDepartingTransition(autStartState, null);
        union.mStartState = newStartState;
        return union;
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
         * Adds transitions between the given states on all symbols that do not allow a transition in the source state.
         *
         * @param sourceName      the state from which the transitions depart
         * @param destinationName the state at which the transitions arrive
         * @throws IllegalStateException    if the result of this builder has already been successfully requested
         * @throws NullPointerException     if <code>sourceName</code> or <code>destinationName</code> is a null pointer
         * @throws IllegalArgumentException if <code>sourceName</code> or <code>destinationName</code> is invalid
         */
        public void addTransitionsOnRemainingSymbols(String sourceName, String destinationName) throws
                IllegalStateException, NullPointerException, IllegalArgumentException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            State source = getStateAndAddIfNotFound(sourceName);
            State destination = getStateAndAddIfNotFound(destinationName);
            Set<Symbol> remainingSymbols = new HashSet<>(ALPHABET.values());
            Set<Symbol> usedSymbols = source.mDepartingTransitions.keySet();
            remainingSymbols.removeAll(usedSymbols);
            source.addDepartingTransitions(destination, remainingSymbols);
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
            State state = mResult.mStates.get(name);
            if (state == null) {
                state = new State(name);
                mResult.mStates.put(name, state);
            }
            return state;
        }

        /**
         * Removes all transitions departing from the given source on the given symbol.
         *
         * @param sourceName the name of the state from which the transitions depart
         * @param symbol     the symbol allowing the transitions
         * @throws IllegalStateException    if the result of this builder has already been successfully requested
         * @throws NullPointerException     if <code>sourceName</code> is a null pointer
         * @throws IllegalArgumentException if <code>sourceName</code> or <code>symbol</code> are not valid
         */
        public void removeTransitionsOnSymbol(String sourceName, String symbol) throws IllegalStateException,
                NullPointerException, IllegalArgumentException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            State source = getStateAndAddIfNotFound(sourceName);
            source.removeTransitionsOnSymbol(toSymbol(symbol));
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

    /**
     * Represents a state of an automaton.
     */
    private static class State {
        /**
         * a set of symbols mapped to the set of states they enable the transition to from this state
         */
        private final Map<Symbol, Set<State>> mDepartingTransitions = new HashMap<>();
        /**
         * the name of this state
         */
        private final String mName;
        /**
         * whether this state is an accept state or not
         */
        private boolean mIsAcceptState = false;

        /**
         * Creates a state with the given name that is not an accept state.
         *
         * @param name the name of the state
         * @throws NullPointerException     if <code>name</code> is a null pointer
         * @throws IllegalArgumentException if <code>name</code> is an empty string
         */
        public State(String name) throws NullPointerException, IllegalArgumentException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (name.equals("")) {
                throw new IllegalArgumentException();
            }
            mName = name;
        }

        /**
         * Creates a state with the given name which is a copy of the given state.
         *
         * @param name  the name of the state
         * @param other the state to copy
         * @throws NullPointerException if <code>name</code> or <code>other</code> is a null pointer
         * @throws IllegalArgumentException if <code>name</code> is an empty string
         */
        public State(String name, State other) throws NullPointerException, IllegalArgumentException {
            if (name == null || other == null) {
                throw new NullPointerException();
            }
            if (name.equals("")) {
                throw new IllegalArgumentException();
            }
            mName = name;
            mIsAcceptState = other.mIsAcceptState;
        }

        /**
         * Creates a new state with the given name and makes it an accept state if any of the given states is one.
         *
         * @param name   the name of the state
         * @param states the states to
         * @throws NullPointerException if <code>name</code> or <code>states</code> is a null pointer
         * @throws IllegalArgumentException if <code>name</code> is an empty string
         */
        public State(String name, Set<State> states) throws NullPointerException, IllegalArgumentException {
            if (name == null || states == null) {
                throw new NullPointerException();
            }
            if (name.equals("")) {
                throw new IllegalArgumentException();
            }
            mName = name;
            for (State currentState : states) {
                if (currentState.mIsAcceptState) {
                    mIsAcceptState = true;
                    break;
                }
            }
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
         * Adds departing transitions from this state to the given destination on the given set of symbols.
         *
         * @param destination the state at which the transitions arrive
         * @param symbols     the symbols allowing the transition
         * @throws NullPointerException if <code>destination</code> or <code>symbols</code> is a null pointer
         */
        public void addDepartingTransitions(State destination, Collection<Symbol> symbols) throws NullPointerException {
            if (destination == null || symbols == null) {
                throw new NullPointerException();
            }
            for (Symbol currentSymbol : symbols) {
                Set<State> existingDestinations = mDepartingTransitions.get(currentSymbol);
                if (existingDestinations != null) {
                    existingDestinations.add(destination);
                } else {
                    Set<State> destinations = new HashSet<>();
                    destinations.add(destination);
                    mDepartingTransitions.put(currentSymbol, destinations);
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return mName.equals(state.mName);
        }

        /**
         * Returns a set of symbols mapped to the set of states they enable the transition to from this state.
         *
         * @return a set of symbols mapped to the set of states they enable the transition to from this state
         */
        public Map<Symbol, Set<State>> getDepartingTransitions() {
            return mDepartingTransitions;
        }

        /**
         * Returns the name of this state.
         *
         * @return the name of this state
         */
        public String getName() {
            return mName;
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

        @Override
        public int hashCode() {
            return mName.hashCode();
        }

        /**
         * Removes all transitions departing from this state on the given symbol.
         *
         * @param value the symbol for which to remove all transitions
         */
        public void removeTransitionsOnSymbol(Symbol value) {
            mDepartingTransitions.remove(value);
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
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return mString.hashCode();
        }

        @Override
        public String toString() {
            return mString;
        }
    }

    /**
     * Converts an automaton to a DFA.
     */
    private static class ToDFAConverter {
        /**
         * Builds the epsilon closure of the given state by recursively following epsilon transitions.
         *
         * @param state          the state whose epsilon closure to build
         * @param epsilonClosure the set of states to build the epsilon closure in
         */
        private static void buildEpsilonClosure(State state, Set<State> epsilonClosure) {
            if (epsilonClosure.contains(state)) {
                return;
            }
            epsilonClosure.add(state);
            Set<State> statesDirectlyReachableByEpsilonTransition = state.getTransitions(null);
            if (statesDirectlyReachableByEpsilonTransition == null) {
                return;
            }
            for (State currentState : statesDirectlyReachableByEpsilonTransition) {
                buildEpsilonClosure(currentState, epsilonClosure);
            }
        }

        /**
         * Returns the epsilon closure of the given state.
         *
         * @param state the state to get the epsilon closure of
         */
        private static Set<State> getEpsilonClosure(State state) {
            Set<State> epsilonClosure = new HashSet<>();
            buildEpsilonClosure(state, epsilonClosure);
            return epsilonClosure;
        }

        /**
         * Converts the given automaton to an equivalent where every state has exactly one transition for every symbol
         * in the alphabet, without adding any new epsilon transitions.
         *
         * @param aut the automaton to convert
         */
        private static void convertToEquivalentWithOneTransitionPerSymbol(Automaton aut) {
            Map<Set<State>, State> oldStatesToNewState = new HashMap<>();
            Map<State, Set<State>> newStateToOldStates = new HashMap<>();
            Queue<State> newStatesToCheck = new LinkedList<>();
            State garbageState = new State("garbage");
            garbageState.addDepartingTransitions(garbageState, ALPHABET.values());
            State startState = new State("start");
            startState.setAcceptState(aut.mStartState.isAcceptState());
            newStateToOldStates.put(startState, new HashSet<>(Arrays.asList(new State[]{aut.mStartState})));
            oldStatesToNewState.put(newStateToOldStates.get(startState), startState);
            newStatesToCheck.add(startState);
            int newStateName = 0;
            while (!newStatesToCheck.isEmpty()) {
                State currentNewState = newStatesToCheck.remove();
                Set<State> currentOldStates = newStateToOldStates.get(currentNewState);
                for (Symbol currentSymbol : ALPHABET.values()) {
                    Set<State> currentReachableStates = new HashSet<>();
                    for (State currentOldState : currentOldStates) {
                        Set<State> currentTransitions = currentOldState.getTransitions(currentSymbol);
                        if (currentTransitions != null) {
                            currentReachableStates.addAll(currentTransitions);
                        }
                    }
                    if (!currentReachableStates.isEmpty()) {
                        State currentNewReachableState = oldStatesToNewState.get(currentReachableStates);
                        if (currentNewReachableState == null) {
                            currentNewReachableState =
                                    new State(Integer.toString(newStateName), currentReachableStates);
                            newStateToOldStates.put(currentNewReachableState, currentReachableStates);
                            oldStatesToNewState.put(
                                    newStateToOldStates.get(currentNewReachableState), currentNewReachableState);
                            newStatesToCheck.add(currentNewReachableState);
                            newStateName++;
                        }
                        currentNewState.addDepartingTransition(currentNewReachableState, currentSymbol);
                    } else {
                        currentNewState.addDepartingTransition(garbageState, currentSymbol);
                    }
                }
            }
            aut.mStates.clear();
            for (State currentState : newStateToOldStates.keySet()) {
                aut.mStates.put(currentState.getName(), currentState);
            }
            aut.mStates.put(garbageState.getName(), garbageState);
            aut.mStartState = oldStatesToNewState.get(new HashSet<>(Arrays.asList(new State[]{aut.mStartState})));
        }

        /**
         * Converts the given automaton to an equivalent with all epsilon transitions removed.
         *
         * @param aut the automaton to convert
         */
        private static void convertToEquivalentWithoutEpsilonTransitions(Automaton aut) {
            Map<Set<State>, State> epsilonClosureToMergedState = new HashMap<>();
            Map<State, Set<State>> mergedStateToEpsilonClosure = new HashMap<>();
            Map<State, State> oldStatesToMergedState = new HashMap<>();
            int mergedStateName = 0;
            for (State currentOldState : aut.mStates.values()) {
                Set<State> epsilonClosure = getEpsilonClosure(currentOldState);
                State mergedState = epsilonClosureToMergedState.get(epsilonClosure);
                if (mergedState == null) {
                    mergedState = new State(Integer.toString(mergedStateName), epsilonClosure);
                    epsilonClosureToMergedState.put(epsilonClosure, mergedState);
                    mergedStateToEpsilonClosure.put(mergedState, epsilonClosure);
                    mergedStateName++;
                }
                oldStatesToMergedState.put(currentOldState, mergedState);
            }
            for (Map.Entry<State, State> currentEntry : oldStatesToMergedState.entrySet()) {
                for (State currentOldState : mergedStateToEpsilonClosure.get(currentEntry.getValue()))
                    for (Map.Entry<Symbol, Set<State>> currentTransitions :
                            currentOldState.getDepartingTransitions().entrySet()) {
                        if (currentTransitions.getKey() == null) {
                            continue;
                        }
                        for (State currentReachableState : currentTransitions.getValue()) {
                            currentEntry.getValue().addDepartingTransition(
                                    oldStatesToMergedState.get(currentReachableState), currentTransitions.getKey());
                        }
                    }
            }
            aut.mStates.clear();
            for (State currentState : oldStatesToMergedState.values()) {
                aut.mStates.put(currentState.getName(), currentState);
            }
            aut.mStartState = oldStatesToMergedState.get(aut.mStartState);
        }

        /**
         * Converts the given automaton to a DFA.
         *
         * @param aut the automaton to convert
         * @return a DFA version of the given automaton
         */
        public Automaton run(Automaton aut) {
            if (aut.mIsDFA) {
                return aut;
            }
            convertToEquivalentWithoutEpsilonTransitions(aut);
            convertToEquivalentWithOneTransitionPerSymbol(aut);
            aut.mIsDFA = true;
            return aut;
        }
    }
}
