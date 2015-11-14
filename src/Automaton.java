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
        put(ARC, new Symbol(ARC));
        put(DRAGON, new Symbol(DRAGON));
        put(GATE, new Symbol(GATE));
        put(KEY, new Symbol(KEY));
        put(RIVER, new Symbol(RIVER));
        put(SWORD, new Symbol(SWORD));
        put(TREASURE, new Symbol(TREASURE));
    }};

    /**
     * the start state of this automaton
     */
    private State mStartState = null;

    /**
     * the set of states of this automaton, indexed by their name
     */
    private Map<String, State> mStates = new HashMap<>();

    /**
     * Creates an automaton without any states (and thus invalid, since it has no start state). This private
     * constructor is only defined to prevent outer classes from creating instances of this class. Use the automaton
     * builder to create a valid automaton.
     */
    private Automaton() {
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
        return null;
    }

    /**
     * Returns an automaton accepting the intersection of the languages accepted by this and the given automaton.
     *
     * @param aut the automaton accepting the language to take the intersection with
     * @return an automaton accepting the intersection of the languages accepted by this and the given automaton
     */
    public Automaton intersection(Automaton aut) {
        // TODO: 2015-11-12 implement
        return null;
    }

    /**
     * Builds an automaton.
     */
    public static class Builder {
        /**
         * whether the result of this builder has successfully been requested and thus the building is finished
         */
        private boolean mIsFinished = false;

        /**
         * the automaton that is created by this builder
         */
        private Automaton mResult = new Automaton();

        /**
         * Adds a state with the given name as an accept state to the automaton.
         *
         * @param name the name of the accept state
         * @throws IllegalStateException if the result of this builder has already been successfully requested
         * @throws NullPointerException  if <code>name</code> is a null pointer
         */
        public void addAcceptState(String name) throws IllegalStateException, NullPointerException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            if (name == null) {
                throw new NullPointerException();
            }
            State state = mResult.mStates.get(name);
            if (state == null) {
                state = new State(name);
                mResult.mStates.put(name, state);
            }
            state.setAcceptState(true);
        }

        /**
         * Adds a transition between the given states on the given symbol.
         *
         * @param sourceName      the state from which the transition departs
         * @param destinationName the state in which the transition arrives
         * @param symbol          the symbol allowing the transition, or a null pointer to transition without any symbol
         * @throws IllegalStateException if the result of this builder has already been successfully requested
         * @throws NullPointerException if <code>sourceName</code> or <code>destinationName</code> is a null pointer
         * @throws IllegalArgumentException if <code>symbol</code> is not contained in the alphabet
         */
        public void addTransition(String sourceName, String destinationName, String symbol) throws
                IllegalStateException, NullPointerException, IllegalArgumentException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            State source = getStateAndAddIfNotFound(sourceName);
            State destination = getStateAndAddIfNotFound(destinationName);
            Symbol actualSymbol = null;
            if (symbol != null) {
                actualSymbol = ALPHABET.get(symbol);
                if (actualSymbol == null) {
                    throw new IllegalArgumentException();
                }
            }
            source.addDepartingTransition(destination, actualSymbol);
        }

        /**
         * Adds transitions between the given states on all symbols that do not allow a transition in the source state.
         *
         * @param sourceName      the state from which the transitions depart
         * @param destinationName the state at which the transitions arrive
         * @throws IllegalStateException if the result of this builder has already been successfully requested
         * @throws NullPointerException if <code>sourceName</code> or <code>destinationName</code> is a null pointer
         */
        public void addTransitionsOnRemainingSymbols(String sourceName, String destinationName) throws
                IllegalStateException, NullPointerException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            State source = getStateAndAddIfNotFound(sourceName);
            State destination = getStateAndAddIfNotFound(destinationName);
            Set<Symbol> usedSymbols = source.mDepartingTransitions.keySet();
            Set<Symbol> remainingSymbols = new HashSet<>(ALPHABET.values());
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
         * @throws NullPointerException if <code>name</code> is a null pointer
         */
        private State getStateAndAddIfNotFound(String name) throws NullPointerException {
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
         * Makes a state with the given name the start state of the automaton.
         *
         * @param name the name of the start state
         * @throws IllegalStateException if the result of this builder has already been successfully requested
         * @throws NullPointerException  if <code>name</code> is a null pointer
         */
        public void setStartState(String name) throws IllegalStateException, NullPointerException {
            if (mIsFinished) {
                throw new IllegalStateException();
            }
            if (name == null) {
                throw new NullPointerException();
            }
            if (mResult.mStartState != null) {
                mResult.mStartState.setStartState(false);
            }
            State newStartState = getStateAndAddIfNotFound(name);
            newStartState.setStartState(true);
            mResult.mStartState = newStartState;
        }
    }

    /**
     * Represents a state of an automaton.
     */
    private static class State {
        /**
         * a set of symbols mapped to the set of states they enable the transition to from this state
         */
        private Map<Symbol, Set<State>> mDepartingTransitions = new HashMap<>();

        /**
         * whether this state is an accept state or not
         */
        private boolean mIsAcceptState = false;

        /**
         * whether this state is a start state or not
         */
        private boolean mIsStartState = false;

        /**
         * the name of this state
         */
        private String mName;

        /**
         * Creates a state with the given name that is neither an accept state nor a start state.
         *
         * @param name the name of the state
         * @throws NullPointerException if <code>name</code> is a null pointer
         */
        public State(String name) throws NullPointerException {
            if (name == null) {
                throw new NullPointerException();
            }
            mName = name;
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

        /**
         * Returns whether this state is a start state.
         *
         * @return <code>true</code> if this state is a start state, <code>false</code> otherwise
         */
        public boolean isStartState() {
            return mIsStartState;
        }

        /**
         * Sets whether this state is a start state.
         *
         * @param value <code>true</code> to make this state a start state, <code>false</code> otherwise
         */
        public void setStartState(boolean value) {
            mIsStartState = value;
        }
    }

    /**
     * Represents a symbol in the alphabet of an automaton.
     */
    private static final class Symbol {
        /**
         * the textual representation of this symbol
         */
        private final String mValue;

        /**
         * Creates a symbol with the given textual representation.
         *
         * @param value the textual representation of the symbol
         */
        public Symbol(String value) {
            mValue = value;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return mValue.hashCode();
        }

        @Override
        public String toString() {
            return mValue;
        }
    }
}
