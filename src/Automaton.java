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
        for (Map.Entry<String, State> currentOtherStatesEntry : other.mStates.entrySet()) {
            mStates.put(currentOtherStatesEntry.getKey(), new State(statePrefix + currentOtherStatesEntry.getValue()));
        }
        for (State currentOtherState : other.mStates.values()) {
            State currentOwnState = mStates.get(currentOtherState.getName());
            for (Map.Entry<Symbol, Set<State>> currentSymbolEntry :
                    currentOtherState.mDepartingTransitions.entrySet()) {
                Symbol currentSymbol = currentSymbolEntry.getKey();
                for (State currentDestination : currentSymbolEntry.getValue()) {
                    currentOwnState.addDepartingTransition(mStates.get(currentDestination.getName()), currentSymbol);
                }
            }
        }
        return mStates.get(statePrefix + other.mStartState.getName());
    }

    /**
     * Creates a garbage state an redirects all dead ends in the automaton to this state.
     *
     * @return the newly created garbage state
     */
    private State addGarbageState() {
        State garbageState;
        String garbageStateName = "garbage";
        if (mStates.get(garbageStateName) != null) {
            int garbageStateNameSuffix = 2;
            while (mStates.get(garbageStateName + garbageStateNameSuffix) != null) {
                garbageStateNameSuffix++;
            }
            garbageStateName += garbageStateNameSuffix;
        }
        garbageState = new State(garbageStateName);
        mStates.put(garbageStateName, garbageState);
        Collection<State> states = mStates.values();
        for (Symbol currentSymbol : ALPHABET.values()) {
            states.stream().filter(
                    currentState -> currentState.getTransitions(currentSymbol).size() < 1).forEach(
                    currentState -> currentState.addDepartingTransition(garbageState, currentSymbol));
        }
        return garbageState;
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
        // TODO: 2015-11-12 implement
        if (accept) {
            return null;
        } else {
            return null;
        }
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
     * Returns a DFA equivalent (accepting the same language) to this automaton.
     *
     * @return a DFA equivalent to this automaton
     */
    private Automaton toDFA() {
        if (mIsDFA) {
            return new Automaton(this);
        }
        Automaton result = new Automaton(this);

        // TODO: 2015-11-16 implement

        result.addGarbageState();
        result.mIsDFA = true;
        return result;
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
         * Creates a copy of the given state.
         *
         * @param other the state to copy
         * @throws NullPointerException if <code>other</code> is a null pointer
         */
        public State(State other) throws NullPointerException {
            if (other == null) {
                throw new NullPointerException();
            }
            mName = other.mName;
            mIsAcceptState = other.mIsAcceptState;
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
        public void addDepartingTransitions(State destination, Set<Symbol> symbols) throws NullPointerException {
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
}
